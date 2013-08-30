package com.example.ayaai;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener{
	private Button bt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		bt = (Button)findViewById(R.id.to_screen2button);
		bt.setOnClickListener(this);
		Log.v("onCreate","testestes");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onClick(View v) {
		
		if(v==bt){
			Log.v("test!","testes");
			Intent intent_scr2 = new Intent(this,Screen2Activity.class);//ƒ‹[ƒ‹‰æ–Ê‚Ö‚Ì“¹‚ğì¬
			startActivity(intent_scr2);
		}
		else{Log.v("else","else");}
			
		
	}

}
