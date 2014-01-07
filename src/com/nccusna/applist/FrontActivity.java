package com.nccusna.applist;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.*;

import com.facebook.*;
import com.facebook.model.*;


public class FrontActivity extends Activity {
	private Button selectButt;
	private Button serviceButt;
	private TextView statText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	    selectButt = (Button)findViewById(R.id.select);
	    serviceButt = (Button)findViewById(R.id.service);
	    statText = (TextView)findViewById(R.id.stat);
	    statText.setText("");
		selectButt.setOnClickListener(jumppage);
		serviceButt.setOnClickListener(startCounter);
	    
		// start Facebook Login
	    Session.openActiveSession(this, true, new Session.StatusCallback() {

	      // callback when session changes state
	      @Override
	      public void call(Session session, SessionState state, Exception exception) {
	        if (session.isOpened()) {

	          // make request to the /me API
	          Request.newMeRequest(session, new Request.GraphUserCallback() {

	            // callback after Graph API response with user object
	            @Override
	            public void onCompleted(GraphUser user, Response response) {
	              if (user != null) {
	                statText.setText("Hello!!\n" + user.getName() + "!");
	              }
	              else {
	            	statText.setText("Please login your Facebook to continue!");
	              }
	            }
	          }).executeAsync();;
	        }
	      }
	    });
	}
	
	private Button.OnClickListener jumppage = new Button.OnClickListener(){
		public void onClick(View v){
			Intent intent = new Intent(FrontActivity.this, SelectActivity.class);
			startActivity(intent);
		}
	};
	
	private Button.OnClickListener startCounter = new Button.OnClickListener(){
		public void onClick(View v){
			Intent intent = new Intent(FrontActivity.this, MonitorService.class);
			startService(intent);
		}
	};
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}
} // FrontActivity

