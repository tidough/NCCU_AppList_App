package com.nccusna.applist;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class SelectActivity extends Activity {
	private ListView lView;
	private String statFN = "stat_file";
	private String jsonFN = "json_file";
	private String uID ;
	private List<String> lv_items ;
	private List<String> pastList ;
	private boolean[] result ;
	private PackageManager pM ;
	private final String MY_ACCESS_KEY_ID = "" ; // removed
	private final String MY_SECRET_KEY = "" ; // removed
	private final String MY_BUCKET = "social-apps-tse";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select);
		Intent intent = getIntent();
		uID = intent.getStringExtra("uID") ;
		lv_items = new ArrayList<String>();
		pM = getApplicationContext().getPackageManager();
		addListItem();
		result = new boolean[pastList.size()];
		lView = (ListView) findViewById(R.id.ListView01);
		lView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, lv_items));
		lView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		lView.setOnItemClickListener(checker);
		Button selectAll = (Button)findViewById(R.id.SelectAll);
		Button submit = (Button)findViewById(R.id.Submit);
		selectAll.setOnClickListener(markAll);
		submit.setOnClickListener(upload);
	}

	private AdapterView.OnItemClickListener checker = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
			result[position] = !result[position];
	    }
	};
	
	private Button.OnClickListener markAll = new Button.OnClickListener(){
		public void onClick(View v){
			for ( int i=0; i < pastList.size(); i++) {
				   lView.setItemChecked(i, true);
				   result[i] = true ;
				}
		}
	};
	
	private Button.OnClickListener upload = new Button.OnClickListener(){
		public void onClick(View v){
			String outputString = new String();
			Boolean started = false ;
			outputString = outputString.concat("{\"data\":{") ;
			for ( int x = 0 ; x < pastList.size() ; x++ ) {
				if ( result[x] ) {
					ApplicationInfo appInfo ;
					try {
						appInfo = pM.getApplicationInfo(pastList.get(x).split(",")[0], 0);
	    			} catch (final NameNotFoundException e) {
	    				appInfo = null ;
	    			}
					if ( appInfo != null ) {
						if ( started ) outputString = outputString.concat(",\n");
						outputString = outputString.concat("\""+pastList.get(x).split(",")[0]+"\":[\"");
						outputString = outputString.concat( (String)pM.getApplicationLabel(appInfo) );
						outputString = outputString.concat( "\"," );
						outputString = outputString.concat( pastList.get(x).split(",")[1] + "]") ;
						started = true ;
					}
				}
			}
			outputString = outputString.concat( "\n}}" ) ;
	        FileOutputStream fos;
			try {
				fos = openFileOutput(jsonFN, Context.MODE_PRIVATE);
		        fos.write(outputString.getBytes());
		        fos.close();
		        Log.i("Files:", outputString );
			} catch ( IOException e) {
				// Auto-generated catch block
				e.printStackTrace();
			}

			new UploadTask().execute(uID);
		}
	};
    
    private void addListItem() {
    	String input = readFile() ;
    	Log.i("Input", input);
    	pastList = new ArrayList<String>( Arrays.asList( input.split(";") ) ) ;
    	List<PackageInfo> installedApps = pM.getInstalledPackages(0);
    	for ( PackageInfo app : installedApps )
    		if ( !input.contains(app.packageName) )
    			pastList.add( app.packageName.concat(",0") ) ;

    	Iterator<String> iter = pastList.iterator() ;
    	while ( iter.hasNext() ) {
    		String one = iter.next();
    		String[] pair = one.split(",") ;
    		if( pair[0].compareTo("start") == 0 ) {
    			setTitle("Submit your applist\t(start from "+pair[1]+")");
    			iter.remove();
    		}
    		else {
    			ApplicationInfo applicationInfo;
    			try {
    			    applicationInfo = pM.getApplicationInfo(pair[0], 0);
    			} catch (final NameNotFoundException e) {
    				applicationInfo = null ;
    			}
    			if ( pair[0].compareTo("com.nccusna.applist") == 0 ) {
    				iter.remove();
    				continue;
    			}
    			if ( applicationInfo != null && ( applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM ) == 0 ) {
    				String title = (String)pM.getApplicationLabel(applicationInfo);
    				lv_items.add( title.concat("\t(times:"+pair[1]+")") );
    			}
    			else
    				iter.remove();
    		}
    	}
    }
    
    private String readFile() {
    	int readed;
    	String content="";
    	byte[] buff = new byte[512];
    	try {
    		FileInputStream fis = openFileInput( statFN );
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
    
    private class UploadTask extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... params) {
        	try {
        		AmazonS3Client s3Client = new AmazonS3Client( new BasicAWSCredentials( MY_ACCESS_KEY_ID, MY_SECRET_KEY ) );
        		PutObjectRequest por = new PutObjectRequest( MY_BUCKET, params[0], new java.io.File( getFilesDir().getPath()+"/"+jsonFN ) );  
        		s3Client.putObject( por );
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
			return null ;
        }
    }
}
