package com.sam_chordas.android.stockhawk.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.TaskParams;
import com.sam_chordas.android.stockhawk.R;

/**
 * Created by sam_chordas on 10/1/15.
 */
public class StockIntentService extends IntentService {

  public StockIntentService(){
    super(StockIntentService.class.getName());
  }

  public StockIntentService(String name) {
    super(name);
  }

  @Override protected void onHandleIntent(Intent intent) {
    Log.d(StockIntentService.class.getSimpleName(), "Stock Intent Service");
    StockTaskService stockTaskService = new StockTaskService(this);
    Bundle args = new Bundle();
    if (intent.getStringExtra("tag").equals("add")){
      args.putString("symbol", intent.getStringExtra("symbol"));
    }
    // We can call OnRunTask from the intent service to force it to run immediately instead of
    // scheduling a task.
    // when user enters invalid symbol, YQL returns null in almost every filed and that throws
    // exception, see below URL for example JSON response
    //Valid : https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quote%20where%20symbol%20in%20(%22MSFT%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=
    //inValid : https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quote%20where%20symbol%20in%20(%22PHUB%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=

    try {
      stockTaskService.onRunTask(new TaskParams(intent.getStringExtra("tag"), args));
    } catch (NumberFormatException e) {
      new Handler(Looper.getMainLooper()).post(new Runnable() {
        @Override
        public void run() {
          Toast.makeText(getApplicationContext(), "Invalid SYMBOL for Stock Request", Toast.LENGTH_LONG).show();
        }
      });
    }
  }
}
