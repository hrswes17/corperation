package com.example.fastjudge2;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener{
	
	private String search_word="";
	
	private Button bt1;
	private Button bt2;
	private Button bt3;
	private Button bt4;
	private Button bt5;
	private Button bt6;
	private Button bt7;
	private Button bt8;
	private Button bt9;
	private Button bt10;
	private Button bt11;
	private Button bt12;
	private TextView question;

	Intent next_intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		question=(TextView)findViewById(R.id.quest_text);
		question.setText(R.string.now_feeling);
		
		bt1=(Button)findViewById(R.id.button1);
		bt2=(Button)findViewById(R.id.button2);
		bt3=(Button)findViewById(R.id.button3);
		bt4=(Button)findViewById(R.id.button4);
		bt5=(Button)findViewById(R.id.button5);
		bt6=(Button)findViewById(R.id.button6);
		bt7=(Button)findViewById(R.id.button7);
		bt8=(Button)findViewById(R.id.button8);
		bt9=(Button)findViewById(R.id.button9);
		bt10=(Button)findViewById(R.id.button10);
		bt11=(Button)findViewById(R.id.button11);
		bt12=(Button)findViewById(R.id.button12);
			
		
		bt1.setText(R.string.feel1);
		bt2.setText(R.string.feel2);
		bt3.setText(R.string.feel3);
		bt4.setText(R.string.feel4);
		bt5.setText(R.string.feel5);
		bt6.setText(R.string.feel6);
		bt7.setText(R.string.feel7);
		bt8.setText(R.string.feel8);
		bt9.setText(R.string.feel9);
		bt10.setText(R.string.feel10);
		bt11.setText(R.string.feel11);
		bt12.setText(R.string.back);
		
		
		bt1.setOnClickListener(this);		
		bt2.setOnClickListener(this);		
		bt3.setOnClickListener(this);		
		bt4.setOnClickListener(this);		
		bt5.setOnClickListener(this);		
		bt6.setOnClickListener(this);		
		bt7.setOnClickListener(this);		
		bt8.setOnClickListener(this);		
		bt9.setOnClickListener(this);		
		bt10.setOnClickListener(this);		
		bt11.setOnClickListener(this);		
		bt12.setOnClickListener(this);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onClick(View view){
		
		next_intent = new Intent(this,FoodQuest2.class);
		
		if(view==bt1){
			search_word+="お祝い";
		}
		else if(view==bt2){
			search_word+="明るい";
		}
		else if(view==bt3){
			search_word+="癒やし";
			finish();
		}
		else if(view==bt4){
			search_word+="癒やし";
		}
		else if(view==bt5){
			search_word+="おもしろい";
		}
		else if(view==bt6){
			search_word+="癒やし";
		}
		else if(view==bt7){
			search_word+="明るい";
		}
		else if(view==bt8){
			search_word+="";//あとで "こわい"
		}
		else if(view==bt9){
			search_word+="おもしろい";
		}
		else if(view==bt10){
			search_word+="きれい";
		}
		else if(view==bt11){//ほか
			search_word+="";
		}
		else{//bt12...戻るボタン 一個前の情報(空白区切りで一単語消す)
			search_word+="";
		}
	
		startActivity(next_intent);
		next_intent.putExtra("search", search_word);
		startActivity(next_intent);
//		finish();
	}

}
