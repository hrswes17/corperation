package com.example.fingergolf;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;



public class MainActivity extends Activity implements OnClickListener {

	private Button open_rule_button;
	private Button game_start_button;
	private Button option_button;
	private int mSound;
	private SoundPool soundPool= new SoundPool(1, AudioManager.STREAM_MUSIC,0);//1�Ȃ̂�

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		 mSound=soundPool.load(getApplicationContext(),R.raw.botton,1);
		
		open_rule_button = (Button) findViewById(R.id.rule_button);
		open_rule_button.setOnClickListener(this);
		
		game_start_button = (Button) findViewById(R.id.start_button);
		game_start_button.setOnClickListener(this);	
		
		option_button = (Button) findViewById(R.id.option_button);
		option_button.setOnClickListener(this);			
	}
	
	@Override
	public void onClick(View v){
		if(v==open_rule_button){
		Intent intent_rule = new Intent(this,ActivityRule.class);//���[����ʂւ̓����쐬
		soundPool.play(mSound, 1.0f, 1.0f, 0, 0, 1.0f);
		startActivity(intent_rule);}
		if(v==game_start_button){
			Intent intent_start = new Intent(this,ActivityStart.class);
			intent_start.putExtra("stage", 1);
			intent_start.putExtra("total score",0);
			soundPool.play(mSound, 1.0f, 1.0f, 0, 0, 1.0f);
		startActivity(intent_start);}//�Q�[����ʂ��N��
		if(v==option_button){
		Intent intent_option = new Intent(this,VolumeActivity.class);
		soundPool.play(mSound, 1.0f, 1.0f, 0, 0, 1.0f);
		startActivity(intent_option);
		}	
		
	}
	
	
	
	 //�o�b�N�L�[�����������ɃA�v�����I������悤�ɂ���
/*	 
	public void onPause() {
		super.onPause();
		//�g��Ȃ��Ȃ����T�E���h��unload
//for(int i=0;i<mSound.length;i++)
		soundPool.unload(mSound);

	//SoundPool���g�p���Ă��郊�\�[�X��S�ĉ��
	soundPool.release();
		finish();
	}*/
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.v("MainActivity","onDestroy()");
				//�g��Ȃ��Ȃ����T�E���h��unload
		//for(int i=0;i<mSound.length;i++)
				soundPool.unload(mSound);
		
			//SoundPool���g�p���Ă��郊�\�[�X��S�ĉ��
			soundPool.release();
	}
	
	/*
	@Override
	protected void onDestroy() {
		super.onDestroy();
						//�g��Ȃ��Ȃ����T�E���h��unload
		for(int i=0;i<mSound.length;i++)
				soundPool.unload(mSound[i]);
		
			//SoundPool���g�p���Ă��郊�\�[�X��S�ĉ��
			soundPool.release();
	}
	}*/
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
