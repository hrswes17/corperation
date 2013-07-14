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
		
		nexts=get_intent.getIntExtra("stage", 0);//ステージ取得
		Log.v("GameOverActivity","onCreate start 3");
		if(nexts!=0)
			num_of_hole.setText(""+(nexts-1));
		else//デフォルト値のままのとき
			num_of_hole.setText("err"); 
		
		not=get_intent.getIntExtra("number of touch", 1000);//タッチ数取得
		//ボーナスも作る
		
		total_score=get_intent.getIntExtra("total score", 0);//前ステージまでの総合点数
		total_score=total_score+bounus_score;//トータルスコアはnホール目の点数とボーナス点を加える
		Log.v("GameOverActivity","onCreate start 4");
		if(!over_clear){//クリアー時
			if(nexts==0){	//ゲームクリアしたが、次のステージの情報がなかったときゲームオーバー処理
				over_clear_tv.setText(R.string.game_over);
				nextStageButton.setVisibility(TextView.INVISIBLE);
			}
			else{
				lt=get_intent.getIntExtra("left time", 0);//点数も取得する
				left_time.setText(""+lt);
				total_score=total_score+lt/not;//トータルスコアはnホール目の点数とボーナス点を加える
				n_hole_score.setText(""+(lt/not));//nホール目のゲームスコアの設定
			}
			Log.v("GameOverActivity","onCreate start 5");
		}
		else{//ゲームオーバー時
			over_clear_tv.setText(R.string.game_over);//ゲームオーバー
			nextStageButton.setVisibility(TextView.INVISIBLE);//次のステージへのボタンを非表示
			n_hole_score.setText("0");//クリアできなかったのでホール点数なし
			Log.v("GameOverActivity","onCreate start 6");
		}
		score_tv.setText(" "+total_score);//全ホールのスコアを設定
		if(not!=1000){num_of_touch.setText(""+not);}//タッチ数がデフォルトじゃなかったらそのまま表示
		else{num_of_touch.setText("0");}//1000->0に変更
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
			Intent returnStart = new Intent(this,ActivityStart.class);//ゲーム画面のインテント
			returnStart.putExtra("stage", nexts);//次のステージ数を渡す
			returnStart.putExtra("total score", total_score);
			startActivity(returnStart);  		
			finish();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.v("GameOverActivity","onDestroy()");
				//使わなくなったサウンドをunload
	//	for(int i=0;i<mSound.length;i++)
				soundPool.unload(mSound);
		
			//SoundPoolが使用しているリソースを全て解放
			soundPool.release();
	}

}
