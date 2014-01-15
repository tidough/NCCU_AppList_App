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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;


public class MonitorService extends Service {

	private Handler handler = new Handler();
	private String filename = "stat_file";
	private ActivityManager mActivityManager ;
	private String latestAppName = "";
	private Map<String, String> records;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// Auto-generated method stub
		return null;
	}
	    
    @Override
    public void onStart(Intent intent, int startId) {
    	
    	records = new HashMap<String, String>();
    	String[] past = readFile().split(";") ;
    	for ( String one : past ) {
    		String[] pair = one.split(",") ;
    		records.put(pair[0], pair[1]);
    	}
    	Log.i("Records:", records.toString() );
    	mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        handler.postDelayed(recordApps, 1000);
        //super.onStartCommand(intent, 0, startId);
    }

    @Override
    public void onDestroy() {
        Log.i("Stats:", "Destorying" );
        String outputString = new String();
        for ( Map.Entry<String, String> entry : records.entrySet() ) {
            outputString = outputString.concat(entry.getKey());
            outputString = outputString.concat(",");
            outputString = outputString.concat(entry.getValue());
            outputString = outputString.concat(";");
        }
        FileOutputStream fos;
		try {
			fos = openFileOutput(filename, Context.MODE_PRIVATE);
	        fos.write(outputString.getBytes());
	        fos.close();
	        Log.i("Files:", outputString );
		} catch ( IOException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		handler.removeCallbacks(recordApps);
        super.onDestroy();
    }
    
    private Runnable recordApps = new Runnable() {
        public void run() {
        	List<ActivityManager.RecentTaskInfo> apps = mActivityManager.getRecentTasks(10,ActivityManager.RECENT_WITH_EXCLUDED);
        	if ( !apps.isEmpty() )
        		if ( apps.get(0).baseIntent.getComponent().getPackageName().compareTo(latestAppName) != 0 ) {
        			latestAppName = apps.get(0).baseIntent.getComponent().getPackageName() ;
        			if ( latestAppName.compareTo("com.android.launcher") != 0 ) {
	        			if ( records.containsKey(latestAppName) ) {
	        				int count = Integer.parseInt( records.remove(latestAppName) ) + 1 ;
	        				records.put( latestAppName, String.valueOf(count) ) ;
	        			}
	        			else
	        				records.put( latestAppName, "1" ) ;
        			}
        		}
            handler.postDelayed(this, 500);
        }
    };
    
    private String readFile() {
    	int readed;
    	String content="";
    	byte[] buff = new byte[512];
    	try {
    		FileInputStream fis = openFileInput( filename );
    		while( ( readed = fis.read(buff) )!= -1 ) {
    			content += new String(buff).trim();
    		}
    	} catch (IOException e) {
    		Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+8"));
    		content = "start,";
    		content = content.concat( cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US) );
    		content = content.concat(String.valueOf( cal.get(Calendar.DATE) ));
    		e.printStackTrace();
    	}
    	Log.i("Files:", content );
    	return content ;
    }
}
