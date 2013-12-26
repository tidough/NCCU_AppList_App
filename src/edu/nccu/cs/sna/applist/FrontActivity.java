package edu.nccu.cs.sna.applist;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

import com.facebook.*;
import com.facebook.model.*;

public class FrontActivity extends Activity {
	private Button selectButt;
	private TextView statText;
	private Session.StatusCallback statusCallback = new SessionStatusCallback();
/*
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	    selectButt = (Button)findViewById(R.id.select);
	    statText = (TextView)findViewById(R.id.stat);
		selectButt.setOnClickListener(jumppage);
	    
		// start Facebook Login
	    Session.openActiveSession(this, true, new Session.StatusCallback() {

	      // callback when session changes state
	      @Override
	      public void call(Session session, SessionState state, Exception exception) {
	        if (session.isOpened()) {

	          // make request to the /me API
	          Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

	            // callback after Graph API response with user object
	            @Override
	            public void onCompleted(GraphUser user, Response response) {
	              if (user != null) {
	                statText.setText("Hello " + user.getName() + "!");
	              }
	            }
	          });
	        }
	      }
	    });
	}
	private Button.OnClickListener jumppage = new Button.OnClickListener(){
		public void onClick(View v){
			Intent intent = new Intent();
			intent.setClass(FrontActivity.this, SelectActivity.class);
			startActivity(intent);
		}
	};
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.front, menu);
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}
} // FrontActivity
*/
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);
	    selectButt = (Button)findViewById(R.id.select);
	    statText = (TextView)findViewById(R.id.stat);
	
	    Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
	
	    Session session = Session.getActiveSession();
	    if (session == null) {
	        if (savedInstanceState != null) {
	            session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
	        }
	        if (session == null) {
	            session = new Session(this);
	        }
	        Session.setActiveSession(session);
	        if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
	            session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
	        }
	    }
	
	    updateView();
	}
	
	@Override
	public void onStart() {
	    super.onStart();
	    Session.getActiveSession().addCallback(statusCallback);
	}
	
	@Override
	public void onStop() {
	    super.onStop();
	    Session.getActiveSession().removeCallback(statusCallback);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    Session session = Session.getActiveSession();
	    Session.saveSession(session, outState);
	}
	
	private void updateView() {
	    Session session = Session.getActiveSession();
	    if (session.isOpened()) {
          Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

	            // callback after Graph API response with user object
	            @Override
	            public void onCompleted(GraphUser user, Response response) {
	              if (user != null) {
	                statText.setText("Hello " + user.getName() + "!");
	              }
	            }
		  });
	        selectButt.setText("Click to logout");
	        selectButt.setOnClickListener(new OnClickListener() {
	            public void onClick(View view) { onClickLogout(); }
	        });
	    } else {
	        statText.setText("");
	        selectButt.setText("Click to login");
	        selectButt.setOnClickListener(new OnClickListener() {
	            public void onClick(View view) { onClickLogin(); }
	        });
	    }
	}
	
	private void onClickLogin() {
	    Session session = Session.getActiveSession();
	    if (!session.isOpened() && !session.isClosed()) {
	        session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
	    } else {
	        Session.openActiveSession(this, true, statusCallback);
	    }
	}
	
	private void onClickLogout() {
	    Session session = Session.getActiveSession();
	    if (!session.isClosed()) {
	        session.closeAndClearTokenInformation();
	    }
	}
	
	private class SessionStatusCallback implements Session.StatusCallback {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        updateView();
	    }
	}
} // FrontActivity
