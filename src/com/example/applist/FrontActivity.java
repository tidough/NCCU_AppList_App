package com.example.applist;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class FrontActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button button = (Button)findViewById(R.id.select);
		button.setOnClickListener(jumppage);
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

}
