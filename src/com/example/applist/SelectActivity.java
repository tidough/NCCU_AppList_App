package com.example.applist;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class SelectActivity extends Activity {
	private ListView lView;
	private String lv_items[] = { "LINE DOZER 推幣遊戲", "LINE", "LINE Pokopang",
	 "Facebook 手機即時通", "FINAL FANTASY V", "Dokuro", "導航PAPAGO! Taiwan", "FINAL FANTASY IV",
	 "神魔之塔", "全民打棒球2013", "Where's My warter", "Candy Crush Saga" };

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
