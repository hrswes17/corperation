package com.example.fingergolf;

import android.app.Activity;
//import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class GameOverActivity extends Activity implements OnClickListener {

	private Button returnButton;
	private Button nextStageButton;
//	private Context cont;
	private SoundPool soundPool= new SoundPool(1, AudioManager.STREAM_MUSIC,0);
	private int mSound;
	private int nexts;
	private TextView over_clear_tv;
//	private int score;
	private TextView score_tv;
	private boolean over_clear;
	private TextView num_of_touch;
	private TextView left_time;
	private TextView bounus;
	private TextView n_hole_score;
	private TextView num_of_hole;
	private int lt;
	private int not;
	private int total_score;
	private int bounus_score=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Log.v("GameOverActivity","onCreate start");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.game_over);		
		
		Intent get_intent =getIntent();
		over_clear=get_intent.getBooleanExtra("over", false);//over is true
		
		over_clear_tv=(TextView)findViewById(R.id.game_over);
		score_tv=(TextView)findViewById(R.id.score);
		num_of_touch=(TextView)findViewById(R.id.num_of_touch);
		left_time=(TextView)findViewById(R.id.left_time);
		bounus=(TextView)findViewById(R.id.bounus);
		n_hole_score=(TextView)findViewById(R.id.n_hole_score);
		num_of_hole=(TextView)findViewById(R.id.num_of_hole);
		Log.v("GameOverActivity","onCreate start 2");
		
		returnButton = (Button) findViewById(R.id.over_to_home);
		findViewById(R.id.over_to_home).setOnClickListener(this);
		nextStageButton = (Button) findViewById(R.id.next_stage);
		findViewById(R.id.next_stage).setOnClickListener(this);
		
		nexts=get_intent.getIntExtra("stage", 0);//�X�e�[�W�擾
		Log.v("GameOverActivity","onCreate start 3");
		if(nexts!=0)
			num_of_hole.setText(""+(nexts-1));
		else//�f�t�H���g�l�̂܂܂̂Ƃ�
			num_of_hole.setText("err"); 
		
		not=get_intent.getIntExtra("number of touch", 1000);//�^�b�`���擾
		//�{�[�i�X�����
		
		total_score=get_intent.getIntExtra("total score", 0);//�O�X�e�[�W�܂ł̑����_��
		total_score=total_score+bounus_score;//�g�[�^���X�R�A��n�z�[���ڂ̓_���ƃ{�[�i�X�_��������
		Log.v("GameOverActivity","onCreate start 4");
		if(!over_clear){//�N���A�[��
			if(nexts==0){	//�Q�[���N���A�������A���̃X�e�[�W�̏�񂪂Ȃ������Ƃ��Q�[���I�[�o�[����
				over_clear_tv.setText(R.string.game_over);
				nextStageButton.setVisibility(TextView.INVISIBLE);
			}
			else{
				lt=get_intent.getIntExtra("left time", 0);//�_�����擾����
				left_time.setText(""+lt);
				total_score=total_score+lt/not;//�g�[�^���X�R�A��n�z�[���ڂ̓_���ƃ{�[�i�X�_��������
				n_hole_score.setText(""+(lt/not));//n�z�[���ڂ̃Q�[���X�R�A�̐ݒ�
			}
			Log.v("GameOverActivity","onCreate start 5");
		}
		else{//�Q�[���I�[�o�[��
			over_clear_tv.setText(R.string.game_over);//�Q�[���I�[�o�[
			nextStageButton.setVisibility(TextView.INVISIBLE);//���̃X�e�[�W�ւ̃{�^�����\��
			n_hole_score.setText("0");//�N���A�ł��Ȃ������̂Ńz�[���_���Ȃ�
			Log.v("GameOverActivity","onCreate start 6");
		}
		score_tv.setText(" "+total_score);//�S�z�[���̃X�R�A��ݒ�
		if(not!=1000){num_of_touch.setText(""+not);}//�^�b�`�����f�t�H���g����Ȃ������炻�̂܂ܕ\��
		else{num_of_touch.setText("0");}//1000->0�ɕύX
		Log.v("GameOverActivity","onCreate start 7");
		mSound=soundPool.load(getApplicationContext(),R.raw.botton,1);
		Log.v("GameOverActivity","onCreate fin");
	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if(arg0==returnButton){
//			Intent selectIntent = new Intent();
//			selectIntent.setClassName("com.example.fingergolf", "com.example.fingergolf.MainActivity");
//			cont.startActivity(selectIntent);
			soundPool.play(mSound, 1.0f, 1.0f, 0, 0, 1.0f);
			finish();
		}
		if(arg0==nextStageButton){
//			Intent selectIntent = new Intent();
//			selectIntent.setClassName("com.example.fingergolf", "com.example.fingergolf.MainActivity");
//			cont.startActivity(selectIntent);
			soundPool.play(mSound, 1.0f, 1.0f, 0, 0, 1.0f);
			Intent returnStart = new Intent(this,ActivityStart.class);//�Q�[����ʂ̃C���e���g
			returnStart.putExtra("stage", nexts);//���̃X�e�[�W����n��
			returnStart.putExtra("total score", total_score);
			startActivity(returnStart);  		
			finish();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.v("GameOverActivity","onDestroy()");
				//�g��Ȃ��Ȃ����T�E���h��unload
	//	for(int i=0;i<mSound.length;i++)
				soundPool.unload(mSound);
		
			//SoundPool���g�p���Ă��郊�\�[�X��S�ĉ��
			soundPool.release();
	}

}
