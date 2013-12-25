package com.example.applist;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class SelectActivity extends Activity {
	private ListView lView;
	private String lv_items[] = { "LINE DOZER �����C��", "LINE", "LINE Pokopang",
	 "Facebook ����Y�ɳq", "FINAL FANTASY V", "Dokuro", "�ɯ�PAPAGO! Taiwan", "FINAL FANTASY IV",
	 "���]����", "�������βy2013", "Where's My warter", "Candy Crush Saga" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select);
		
		lView = (ListView) findViewById(R.id.ListView01);
		lView.setAdapter(new ArrayAdapter<String>(this, 
				android.R.layout.simple_list_item_multiple_choice, lv_items));
		lView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		Button SelectAll = (Button)findViewById(R.id.SelectAll);
		Button Submit = (Button)findViewById(R.id.Submit);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.select, menu);
		return true;
	}

}
