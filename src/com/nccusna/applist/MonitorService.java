package com.nccusna.applist;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Handler;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class MonitorService extends Service {

	private Handler handler = new Handler();
	private int counter = 0;
	private String filename = "stat_file.json";
	private ActivityManager mActivityManager ;
	private String latestAppName = "";
	private Map<String, String> records = new HashMap<String, String>();
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	    
    @Override
    public void onStart(Intent intent, int startId) {
    	int readed;
    	String content="";
    	byte[] buff = new byte[256];
    	/*
    	try {
    		FileInputStream fis = openFileInput( filename );
    		while( ( readed = fis.read(buff) )!= -1 ) {
    			content += new String(buff).trim();
    		}
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	counter = Integer.parseInt(content);
    	counter = 0 ;
    	*/
    	mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        handler.postDelayed(showTime, 1000);
        //super.onStartCommand(intent, 0, startId);
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(showTime);
        FileOutputStream fos;
        String outputString = "";
        Iterator<Map.Entry<String,String>> iter = records.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String,String> entry = (Map.Entry<String,String>) iter.next();
            String key = entry.getKey();
            String val = entry.getValue();
            outputString.concat("{"+key+":"+val+"}\n");
        } 
		try {
			fos = openFileOutput(filename, Context.MODE_PRIVATE);
	        fos.write(outputString.getBytes());
	        fos.close();
	        Log.i("Files:", outputString );
		} catch ( IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        super.onDestroy();
    }
    
    private Runnable showTime = new Runnable() {
        public void run() {
        	counter ++ ;
        	List<ActivityManager.RecentTaskInfo> apps = mActivityManager.getRecentTasks(10,ActivityManager.RECENT_WITH_EXCLUDED);
        	if ( !apps.isEmpty() )
        		if ( !apps.get(0).baseIntent.getComponent().getPackageName().matches(latestAppName) ) {
        			latestAppName = apps.get(0).baseIntent.getComponent().getPackageName() ;
        			if ( records.containsKey(latestAppName) ) {
        				int count = Integer.parseInt( records.remove(latestAppName) ) + 1 ;
        				records.put( latestAppName, String.valueOf(count) ) ;
        				Log.i("Record:", latestAppName );
        			}
        			else
        				records.put( latestAppName, "1" ) ;
        		}
            Log.i("times:", Integer.toString(counter) );
            if ( counter > 20 )
            	stopSelf();
            handler.postDelayed(this, 3000);
        }
    };
}
