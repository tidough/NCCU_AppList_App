package com.nccusna.applist;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import android.os.Handler;
import android.util.Log;
import java.util.Date;

public class MonitorService extends Service {

	private Handler handler = new Handler();
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	    
    @Override
    public void onStart(Intent intent, int startId) {
        handler.postDelayed(showTime, 1000);
        super.onStartCommand(intent, 0, startId);
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(showTime);
        super.onDestroy();
    }
    
    private Runnable showTime = new Runnable() {
        public void run() {
            //log目前時間
            Log.i("time:", new Date().toString());
            handler.postDelayed(this, 1000);
        }
    };
}
