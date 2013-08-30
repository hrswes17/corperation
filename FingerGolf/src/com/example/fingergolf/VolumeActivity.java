package com.example.fingergolf;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class VolumeActivity extends Activity implements OnClickListener,
		OnSeekBarChangeListener {
	SeekBar seek;
	TextView volume_tv;
	Button sound_bt;
	Button return_to;
	int[] mSound = new int[2];
	SoundPool soundPool= new SoundPool(2, AudioManager.STREAM_MUSIC,0);
	AudioManager am;
	int max_volume;
	int current_volume;
	
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.volume);

		//�e�v�f��ID��U�蕪����
		seek =  (SeekBar)findViewById(R.id.volume_regulation);
		volume_tv = (TextView)findViewById(R.id.volume_text);
		sound_bt = (Button)findViewById(R.id.test_sound_button);
		return_to=(Button)findViewById(R.id.option_to_start);
		
		
		mSound[0]=soundPool.load(getApplicationContext(),R.raw.botton,1);
		mSound[1]=soundPool.load(getApplicationContext(),R.raw.cup_in,2);
		
		am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		max_volume=am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		current_volume=am.getStreamVolume(AudioManager.STREAM_MUSIC);
		
		//�V�[�N�o�[�̍ő�l�ƁA�ŏ��l�̐ݒ�
		seek.setMax(max_volume);
	    seek.setProgress(current_volume);
	    seek.setOnSeekBarChangeListener(this);

	    Log.v("current_volume",""+current_volume);	    
		am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
	    volume_tv.setText("����: "+current_volume); //TextView�ɐݒ�l��\��
	    am.setStreamVolume(AudioManager.STREAM_MUSIC, current_volume, 0);//0->flag??
	
	    //�{�^�������������ʂ�J��
	    sound_bt.setOnClickListener(this);
	    return_to.setOnClickListener(this);
	    
	}


	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
	//	TextView volume_text;

	       volume_tv.setText("����: "+progress);//TextView�ɐݒ�l��\��
           am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0); //���ʐݒ�
         
           Log.v("1","");
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		Log.v("2","");
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		Log.v("3","");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Log.v("4","");
		
		if(v==sound_bt){
			soundPool.play(mSound[1], 1.0f, 1.0f, 0, 0, 1.0f);}//�e�X�g��
		if(v==return_to){
			soundPool.play(mSound[0], 1.0f, 1.0f, 0, 0, 1.0f);
			finish();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.v("VolumeActivity","onDestroy()");
				//�g��Ȃ��Ȃ����T�E���h��unload
		for(int i=0;i<mSound.length;i++)
				soundPool.unload(mSound[i]);
		
			//SoundPool���g�p���Ă��郊�\�[�X��S�ĉ��
			soundPool.release();
	}

}
