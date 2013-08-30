package com.example.ayaai;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Screen2Activity extends Activity implements OnClickListener{
	private Button bt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_screen2);
		
		bt = (Button)findViewById(R.id.to_screen3button);
		bt.setOnClickListener(this);
		Log.v("onCreate","testestes");
	}

	
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onClick(View v) {
		if(v==bt){
			Log.v("test!","testes");
			Intent intent_scr3 = new Intent(this,Screen3Activity.class);
			startActivity(intent_scr3);
		}
		else{Log.v("else","else");}
		
	}

}
