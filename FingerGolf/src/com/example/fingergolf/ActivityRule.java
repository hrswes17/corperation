package com.example.fingergolf;

import android.app.Activity;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ActivityRule extends Activity implements OnClickListener {
	
	private SoundPool soundPool= new SoundPool(1, AudioManager.STREAM_MUSIC,0);
	private int mSound;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.rule);
		Button closeButton = (Button) findViewById(R.id.rule_to_home);
		closeButton.setOnClickListener(this);
		mSound=soundPool.load(getApplicationContext(),R.raw.botton,1);
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		soundPool.play(mSound, 1.0f, 1.0f, 0, 0, 1.0f);
		finish();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.v("ActivityRule","onDestroy()");
				//使わなくなったサウンドをunload
	//	for(int i=0;i<mSound.length;i++)
				soundPool.unload(mSound);
		
			//SoundPoolが使用しているリソースを全て解放
			soundPool.release();
	}

}
