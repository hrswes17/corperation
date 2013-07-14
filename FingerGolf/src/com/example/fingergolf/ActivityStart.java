package com.example.fingergolf;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.media.AudioManager;
//import android.media.MediaPlayer;
import android.media.SoundPool;

public class ActivityStart extends Activity{

	Display display;//画面サイズ取得　APIレベルが高い場合にoverrideGetDisplayを呼び出す
	Point size = new Point();//画面サイズを格納
//	private Intent to_over;
	private ViewDisplay view;//Viewクラス　実際の描写
	
	private RefreshHandler mRedrawHandler;//スレッド代わり　ハンドラ

	private float touch_start_x;//触れた場所x
	private float touch_start_y;//以下同様y
	private float touch_finish_x;//離れた場所x
	private float touch_finish_y;//以下同様y
	private float distance_x;//触れた距離x
	private float distance_y;//以下同様y
	
	private float touch_time;//指が触れていた時間
	private long touch_start_time;//指が触れた時刻
	private long touch_finish_time;//指が離れた時刻
	private int move_time;//ボールが止まるまでの時間を格納
	private double acceleration_x;//x方向加速度
	private double acceleration_y;//y方向加速度
	private int second=0;//加速度から距離を得るために用意した時間変数　　move_timeを初期値とし、0になるまで減少する　
	private boolean for_second = true;//描写終了後、secondを初期化するために用意した変数
	private int X;//clear_flagが立ってから、ボールの方向を変えるために穴とボールの中心座標のx距離(+-符号付)を格納
	private int Y;//上記同様
	private float distance_center_x;//穴とボールの中心座標のx距離　絶対値でいれてあるから+
	private float distance_center_y;//上記同様y距離
	private float dcx;//distance_center_xの旧情報格納
	private float dcy;
	
	private boolean finger_up=false;//指が離れている時で、描写しているときtrueになる
	private boolean game_clear_flag=false;//外周円を描くためのもの、穴に接近したときにtrue　ここから30*100msec描写
	private boolean game_clear=false;//Intentとfinish用
	private Point hole_center;//穴の中心座標
	private Point ball_center=new Point();//ボールの中心座標
	private boolean first=true;//game_clear_flag内に入ってから行う最初の処理のための変数1度しか使わない
	//MediaPlayer se = MediaPlayer.create(getContext(), R.raw.tap);
	//private SoundPool tap_sound;
	//private SoundPool wall_sound;
	private final static int MAX_STREAMS = 5;//音楽曲数
	private SoundPool soundPool= new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC,0);
	private int[] mSound = new int[MAX_STREAMS];
	private final int TESTC=10;
	
	private int currentStage;//現在のステージ数
	private int numOfTouch;//タッチ回数
	private int totalScore;//全ステージスコア
	
	private Point wall_start;//障害壁始まりの点
	private Point wall_finish;//障害壁終わりの点
	private double wall_angle;//x軸に対して障害壁の角度
	private Point wall_ball_contact1=new Point(0,0);//障害壁とボールの接点その１...ボールが障害壁にぶつかるとしたらの点
	private Point wall_ball_contact2=new Point(0,0);//障害壁とボールの接点その2...同上
	private boolean contact_judge1;//障害壁にぶつかったか判定その１
//	private boolean contact_judge2;//障害壁にぶつかったか判定その２
	double rotAftAx;//障害壁にぶつかった後のx方向加速度
	double rotAftAy;//障害壁にぶつかった後のy方向加速度
	
	double wall_a;//障害壁傾き
	double wall_c;//障害壁定数
	
	final int JUDGE_RANGE=0;//障害壁判定範囲
	private boolean sand_sound=true;
	
	private boolean current_region_high1;//     /◯　  または    ◯\のとき true
	private boolean previous_region_high1;// 同上
	private boolean current_region_high2;//     /◯　  または    ◯\のとき true
	private boolean previous_region_high2;// 同上
	
	private double acceleration_angle;//加速度方向
	
	private Point pre_contact1 = new Point();
	private Point pre_contact2 = new Point();
	boolean up_down_sf=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		display = getWindowManager().getDefaultDisplay();
		
		Intent get_intent= getIntent();
		currentStage=get_intent.getIntExtra("stage", 0);//ステージ取得
		totalScore=get_intent.getIntExtra("total score", 0);

		overrideGetSize(display, size);//Windowサイズの取得Point sizeに格納 ->できね->method変更再挑戦->でけた！
		 
		numOfTouch=0;
		
		 view = new ViewDisplay(getApplication(),size);
		 view.setDrawText(true);//テキスト表示
		 setContentView(view);
		 
		 size.y=(int)view.getWindowSizeUnder();
		 
		 mSound[0]=soundPool.load(getApplicationContext(),R.raw.cup_in,1);//0->CUP IN
		 mSound[1]=	soundPool.load(getApplicationContext(),R.raw.wall,1);//1->WALL
		 mSound[2]=soundPool.load(getApplicationContext(),R.raw.tap,1);//2->TAP
		 mSound[3]=soundPool.load(getApplicationContext(),R.raw.sand,1);//3->SAND
		 mSound[4]=soundPool.load(getApplicationContext(),R.raw.syougai_wall,1);//4->syougai_wall

		 hole_center=new Point((int)(view.hole_x_s+(view.hole_x_f-view.hole_x_s)/2.0), (int)(view.hole_y_s+(view.hole_y_f-view.hole_y_s)/2));
		 
		 
		 if(view.getWallPointStartY()<view.getWallPointFinishY()){//X軸に近い点を始点とする//わからん
			 wall_start=new Point((int)view.getWallPointStartX(), (int)view.getWallPointStartY());
			 wall_finish=new Point((int)view.getWallPointFinishX(),(int)view.getWallPointFinishY());
			 }
		 else{
			 wall_start=new Point((int)view.getWallPointFinishX(), (int)view.getWallPointFinishY());
			 wall_finish=new Point((int)view.getWallPointStartX(),(int)view.getWallPointStartY());
		 }
			 
		wall_angle=Math.atan((double)(wall_finish.y-wall_start.y)/(double)(wall_finish.x-wall_start.x));

		wall_a=((double)(wall_finish.y-wall_start.y)/(double)(wall_finish.x-wall_start.x));
		wall_c=(double)wall_start.y-wall_a*(double)wall_start.x;
		
		previous_region_high1=true;
		previous_region_high2=true;
		current_region_high1=true;
		current_region_high2=true;
		
		pre_contact1.set((int)(view.ball_Point.x-(view.ball_radius/2)*Math.sin(wall_angle)), (int)(view.ball_Point.y+(view.ball_radius/2)*Math.cos(wall_angle)));   //***ball_radius/2!!!
		pre_contact2.set((int)(view.ball_Point.x+(view.ball_radius/2)*Math.sin(wall_angle)), (int)(view.ball_Point.y-(view.ball_radius/2)*Math.cos(wall_angle)));
		update();
	}
	
	@Override	
	protected void onPause(){
		super.onPause();
		mRedrawHandler=null;
//		soundPool.release();
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		mRedrawHandler = new RefreshHandler();
		mRedrawHandler.sleep(0);
	}

	private void update(){
		//TODO update処理
		if(finger_up){
			
			distance_x=touch_finish_x-touch_start_x;//実測距離x
			distance_y=touch_finish_y-touch_start_y;//実測距離y
			distance_x/=touch_time;//ボーナス距離x (m/s)
			distance_y/=touch_time;//ボーナス距離y (m/s)
			
			if(Math.abs(distance_x)>Math.abs(distance_y)){
				move_time=(int)distance_x/TESTC;//距離的なものをTESTCで割ってボールが動く秒数をmove_timeとした
			}
			else{
				move_time=(int)distance_y/TESTC;
			}
			if(move_time<0)
				move_time=-move_time;
				
			if(for_second){
				second=move_time;for_second=false;//描写に使われる時間の記録
				acceleration_x=(distance_x/move_time)/((double)move_time/2.0);//加速度 x
				acceleration_y=(distance_y/move_time)/((double)move_time/2.0);//加速度 y
				soundPool.play(mSound[2], 1.0f, 1.0f, 0, 0, 1.0f);//TAP sound	
			}
			
		
			if(second>0){//ボール転がし中
				view.ball_Point.x=view.initial_ball_Point.x+(int)((acceleration_x*second*second)/100);//ボールの位置x
				view.ball_Point.y=view.initial_ball_Point.y+(int)((acceleration_y*second*second)/100);//ボールの位置y
				
				/*ホールイン処理*/
				if(view.hole_y_s<view.ball_Point.y+view.ball_radius && view.ball_Point.y+view.ball_radius<view.hole_y_f){
					if(view.hole_x_s<view.ball_Point.x+view.ball_radius/2 && view.ball_Point.x+view.ball_radius/2 <view.hole_x_f){
						game_clear_flag=true;//クリアフラグ
					}
				}

				if(game_clear_flag){
					ball_center.set((int)(view.ball_Point.x+view.ball_radius/2.0),(int)(view.ball_Point.y+view.ball_radius/2.0));
					distance_center_x=Math.abs(ball_center.x-hole_center.x);
					distance_center_y=Math.abs(ball_center.y-hole_center.y);
					if(first){
					X=-(ball_center.x-hole_center.x);
					Y=ball_center.y-hole_center.y;
					acceleration_y=Y/100.0;
					acceleration_x=X/100.0;
					first=false;
					dcx=distance_center_x;
					dcy=distance_center_y;
					second=50;
					soundPool.play(mSound[0], 1.0f, 1.0f, 0, 0, 1.0f);//CUP IN Sound
					}
					
					if((view.hole_x_s+view.ball_radius>ball_center.x || view.hole_x_f-view.ball_radius/2<ball_center.x) &&dcx<distance_center_x){
						acceleration_x=-acceleration_x/1.01;
					}
					if((view.hole_y_s+view.hole_height/3>ball_center.y || view.hole_y_f-view.ball_radius/2< ball_center.y)&&dcy<distance_center_y){
						acceleration_y=-acceleration_y/1.01;
					}

					dcx=distance_center_x;
					dcy=distance_center_y;
				}
				
				/*ホールイン処理終了*/
				
				
				else if(!game_clear_flag){/*まだゲームは終わらないよ*/
				/*壁衝突時処理*/
				if((int)(view.ball_Point.x-(view.ball_radius/2))<=0){/*ここからx方向処理*/
					view.ball_Point.x=-(int)(view.ball_Point.x-(view.ball_radius));//変だけどこれじゃないとボールが振動する
					acceleration_x=-acceleration_x;	
//					Log.v("wall test","this "+view.ball_Point.x+"　　"+view.ball_radius);
					soundPool.play(mSound[1], 1.0f, 1.0f, 0, 0, 1.0f);//WALL sound
				}//方向処理もやる->ok?
				if(view.ball_Point.x+view.ball_radius/2>=size.x){
					view.ball_Point.x=size.x-(view.ball_Point.x+view.ball_radius-size.x);
					acceleration_x=-acceleration_x;	
					soundPool.play(mSound[1], 1.0f, 1.0f, 0, 0, 1.0f);//WALL sound
				}//方向処理もやる->ok?/*ここまでx方向処理*/
				
				if(view.ball_Point.y-view.ball_radius/2<=0){/*ここからy方向処理*/
					view.ball_Point.y=-view.ball_Point.y+view.ball_radius;
					acceleration_y=-acceleration_y;
					soundPool.play(mSound[1], 1.0f, 1.0f, 0, 0, 1.0f);//WALL sound
				}
				if(view.ball_Point.y+view.ball_radius/2>=size.y){
					view.ball_Point.y=size.y-(view.ball_Point.y+view.ball_radius-size.y);
					acceleration_y=-acceleration_y;
					soundPool.play(mSound[1], 1.0f, 1.0f, 0, 0, 1.0f);//WALL sound
				}/*ここまでy方向処理*/
				/*壁衝突時処理終了*/
				
				/*障害壁処理*/
				//壁にぶつかるとしたらの点
				
				pre_contact1.set(wall_ball_contact1.x,wall_ball_contact1.y);
				pre_contact2.set(wall_ball_contact2.x,wall_ball_contact2.y);
				wall_ball_contact1.set((int)(view.ball_Point.x-(view.ball_radius/2)*Math.sin(wall_angle)), (int)(view.ball_Point.y+(view.ball_radius/2)*Math.cos(wall_angle)));   //***ball_radius/2!!!
				wall_ball_contact2.set((int)(view.ball_Point.x+(view.ball_radius/2)*Math.sin(wall_angle)), (int)(view.ball_Point.y-(view.ball_radius/2)*Math.cos(wall_angle)));				

				previous_region_high1=current_region_high1;
				if(wall_ball_contact1.y>wall_a*wall_ball_contact1.x+wall_c)
					current_region_high1=true;
				else
					current_region_high1=false;
					
				previous_region_high2=current_region_high2;
				if(wall_ball_contact2.y>wall_a*wall_ball_contact2.x+wall_c)
					current_region_high2=true;
				else
					current_region_high2=false;
				
				
			if(acceleration_x!=0)	
				acceleration_angle=Math.atan(acceleration_y/acceleration_x);
			else
				acceleration_angle=Math.PI/2;
			
			if(acceleration_x<=0&&acceleration_y<=0)
				acceleration_angle=acceleration_angle+Math.PI;
			else if(acceleration_x>0&&acceleration_y<0)
				acceleration_angle=acceleration_angle+2*Math.PI;
			else if(acceleration_x<0&&acceleration_y>0)
				acceleration_angle=acceleration_angle+Math.PI;
			else
				acceleration_angle=acceleration_angle+0;
			
Log.v("pre,cur","pre_c.x: "+pre_contact1.x+" cur_c.x"+wall_ball_contact1.x+"  pre_c.y: "+pre_contact1.y+ " cur_c.y: "+wall_ball_contact1.y);
			if(wall_angle>0){
				if((wall_angle<acceleration_angle && acceleration_angle < wall_angle+Math.PI)){//    \◯ con1
					if(regionHigh(pre_contact1,wall_ball_contact1,wall_start)!=regionHigh(pre_contact1,wall_ball_contact1,wall_finish))
						up_down_sf=true;
					else
						up_down_sf=false;
				}
				else{ //   ◯\
					if(regionHigh(pre_contact2,wall_ball_contact2,wall_start)!=regionHigh(pre_contact2,wall_ball_contact2,wall_finish))
						up_down_sf=true;
					else
						up_down_sf=false;
				}
			}
			else{
				if((wall_angle+Math.PI<acceleration_angle && acceleration_angle < 2*Math.PI+wall_angle)){//  /◯ con2
					if(regionHigh(pre_contact2,wall_ball_contact2,wall_start)!=regionHigh(pre_contact2,wall_ball_contact2,wall_finish))
						up_down_sf=true;
					else
						up_down_sf=false;
				}
				else{
					if(regionHigh(pre_contact1,wall_ball_contact1,wall_start)!=regionHigh(pre_contact1,wall_ball_contact1,wall_finish))
						up_down_sf=true;
					else
						up_down_sf=false;
				}
			}
			
			Log.v("up_down_sf","up_down_sf: "+up_down_sf);
			
				if(wall_angle>0){//-PI/2~PI/2
					if(((current_region_high1!=current_region_high2)||(current_region_high1 != previous_region_high1))&&up_down_sf
									&& ((wall_angle<acceleration_angle && acceleration_angle < wall_angle+Math.PI))){  //    \◯
						contact_judge1=wallFunction(wall_start,wall_finish,wall_ball_contact1,Math.PI/2-wall_angle);
						if(contact_judge1)
							soundPool.play(mSound[4], 1.0f, 1.0f, 0, 0, 1.0f);
						}//障害壁に接した判定->ボールの向きを見て反射する方向をきめる
					else if(((current_region_high1!=current_region_high2)||(current_region_high2 != previous_region_high2))&&up_down_sf
									&&( (0<=acceleration_angle && acceleration_angle<=wall_angle)||(wall_angle+Math.PI<=acceleration_angle && acceleration_angle <= 2*Math.PI))){//   ◯\
						contact_judge1=wallFunction(wall_start,wall_finish,wall_ball_contact2, -wall_angle);
						if(contact_judge1)
							soundPool.play(mSound[4], 1.0f, 1.0f, 0, 0, 1.0f);
						}
					else
						contact_judge1=false;		
					}
				else{
					if(((current_region_high1!=current_region_high2)||(current_region_high2 != previous_region_high2))&&up_down_sf
									&& (wall_angle+Math.PI<acceleration_angle && acceleration_angle < 2*Math.PI+wall_angle)){//  /◯ con2
						Log.v("/◯ con2","/◯ con2");
						contact_judge1=wallFunction(wall_start,wall_finish,wall_ball_contact2,-wall_angle);
						if(contact_judge1)
							soundPool.play(mSound[4], 1.0f, 1.0f, 0, 0, 1.0f);	
					}//障害壁に接した判定->ボールの向きを見て反射する方向をきめる
					else if(((current_region_high1!=current_region_high2)||(current_region_high1 != previous_region_high1))&&up_down_sf
							&& ((0<=acceleration_angle&&acceleration_angle<=Math.PI+wall_angle)||(2*Math.PI+wall_angle <= acceleration_angle && acceleration_angle <=2*Math.PI))){// ◯/ con1
						contact_judge1=wallFunction(wall_start,wall_finish,wall_ball_contact1,-(Math.PI/2+wall_angle));
						Log.v("◯/ con2","◯/ con2");
						if(contact_judge1)
							soundPool.play(mSound[4], 1.0f, 1.0f, 0, 0, 1.0f);
					}
					else
						contact_judge1=false;
					}
				
				/**
				if(wall_angle>0){//-PI/2~PI/2
					if((wall_ball_contact1.y>=wall_a*wall_ball_contact1.x+wall_c+JUDGE_RANGE)&&(wall_start.x <= wall_ball_contact1.x && wall_ball_contact1.x<=wall_finish.x && wall_start.y <= wall_ball_contact1.y && wall_ball_contact1.y<=wall_finish.y)&& (wall_ball_contact2.y<=wall_a*wall_ball_contact2.x+wall_c-JUDGE_RANGE) && !(acceleration_x>0&&acceleration_y<0)){  //    \◯
						contact_judge1=wallFunction(wall_start,wall_finish,wall_ball_contact1,Math.PI/2-wall_angle);
						if(contact_judge1)
							soundPool.play(mSound[4], 1.0f, 1.0f, 0, 0, 1.0f);
						}//障害壁に接した判定->ボールの向きを見て反射する方向をきめる
					else if((wall_ball_contact1.y>=wall_a*wall_ball_contact1.x+wall_c+JUDGE_RANGE)&&(wall_start.x <= wall_ball_contact2.x && wall_ball_contact2.x<=wall_finish.x && wall_start.y <= wall_ball_contact2.y && wall_ball_contact2.y<=wall_finish.y)&& (wall_ball_contact2.y<=wall_a*wall_ball_contact2.x+wall_c-JUDGE_RANGE) && !(acceleration_x<0 && acceleration_y>0)){//   ◯\
						contact_judge1=wallFunction(wall_start,wall_finish,wall_ball_contact2, -wall_angle);
						if(contact_judge1)
							soundPool.play(mSound[4], 1.0f, 1.0f, 0, 0, 1.0f);
						}
					else
						contact_judge1=false;		
					}
				else{
					if((wall_ball_contact1.y>=wall_a*wall_ball_contact1.x+wall_c+JUDGE_RANGE)&&(wall_finish.x<=wall_ball_contact1.x&&wall_ball_contact1.x<=wall_start.x && wall_start.y<=wall_ball_contact1.y&&wall_ball_contact1.y<=wall_finish.y)&& (wall_ball_contact2.y<=wall_a*wall_ball_contact2.x+wall_c-JUDGE_RANGE) && !(acceleration_x>0 && acceleration_y>0)){//  /◯ con1
						contact_judge1=wallFunction(wall_start,wall_finish,wall_ball_contact1,-wall_angle);
						if(contact_judge1)
							soundPool.play(mSound[4], 1.0f, 1.0f, 0, 0, 1.0f);	
					}//障害壁に接した判定->ボールの向きを見て反射する方向をきめる
					else if((wall_ball_contact1.y>=wall_a*wall_ball_contact1.x+wall_c+JUDGE_RANGE)&&(wall_finish.x<=wall_ball_contact2.x&&wall_ball_contact2.x<=wall_start.x && wall_start.y<=wall_ball_contact2.y&&wall_ball_contact2.y<=wall_finish.y)&& (wall_ball_contact2.y<=wall_a*wall_ball_contact2.x+wall_c-JUDGE_RANGE)&& !(acceleration_y<0 && acceleration_x<0)){// ◯/ con2
						contact_judge1=wallFunction(wall_start,wall_finish,wall_ball_contact2,-(Math.PI/2+wall_angle));
						if(contact_judge1)
							soundPool.play(mSound[4], 1.0f, 1.0f, 0, 0, 1.0f);
					}
					else
						contact_judge1=false;
					}//障害壁に接した判定
				**/
		
				//バンカー判定
				if(view.getSandTrapStartX()<=view.ball_Point.x && view.ball_Point.x <=view.getSandTrapFinishX() && view.getSandTrapStartY() <=view.ball_Point.y && view.ball_Point.y <= view.getSandTrapFinishY()){
					if(sand_sound)
						soundPool.play(mSound[3], 1.0f, 1.0f, 0, 0, 1.0f);
					second*=0.9;
					sand_sound=false;
					}
				else
					sand_sound=true;
			//バンカー判定終了
			}
				}//ボール転がし終了
			
			else{//ボール転がし２回目以降のための処理
				for_second=true;
				view.initial_ball_Point=view.ball_Point;
				finger_up=false;
				sand_sound=true;
				numOfTouch++;
				if(game_clear_flag)
					game_clear=true;
				else
					view.setDrawText(true);//PleaseDrag
				/**テキスト表示**/
			}
			second--;
			
		}
		else{
//			view.runCounter(true);//いらないかも
			view.setCounter(view.getCounter()-0.5f);//カウントダウン->0.2f
		}
		view.invalidate();
		
	if(game_clear || view.finishCount()){//TEST OK!!
		Intent test_int = new Intent(this,GameOverActivity.class);//FOR TEST ACTIVITY
		test_int.putExtra("stage", ++currentStage);//次のステージ数を渡す
		test_int.putExtra("number of touch", numOfTouch);
		//ボーナスも作る
		test_int.putExtra("total score", totalScore);
		test_int.putExtra("left time",(int)(view.getTimeScore()));
		if(view.finishCount()){//ゲームオーバー
			test_int.putExtra("over", true);
		}
		else{
			test_int.putExtra("over", false);
			}//ゲームクリア
		startActivity(test_int);  									//FOR TEST ACTIVITY
		//Log.v("if(game_clear)","game_clear");
		game_clear=false;//2回目インテント回避
		finish();
		}	
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	//	Log.v("ActivityStart","onDestroy()");
				//使わなくなったサウンドをunload
		for(int i=0;i<mSound.length;i++)
				soundPool.unload(mSound[i]);
		
			//SoundPoolが使用しているリソースを全て解放
			soundPool.release();
	}

	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		Log.d("TouchEvent", "X:" + event.getX() + ",Y:" + event.getY());
		 
	    switch (event.getAction()) {
	    case MotionEvent.ACTION_DOWN://触れた時
	 //       Log.d("TouchEvent", "getAction()" + "ACTION_DOWN");
	        touch_start_x=event.getX();
	        touch_start_y=event.getY();
	        touch_start_time=event.getEventTime();
	   //     Log.v("onTouchEvent","touch_start_time: "+touch_start_time);
	        finger_up=false;
	        view.setDrawText(false);
	        /**テキスト非表示**/
	        break;
	    case MotionEvent.ACTION_UP://離れた時
	   //     Log.d("TouchEvent", "getAction()" + "ACTION_UP");
	        touch_finish_x=event.getX();
	        touch_finish_y=event.getY();
	        finger_up=true;
	        touch_finish_time=event.getEventTime();
	    //    Log.v("onTouchEvent","touch_finish_time"+touch_finish_time);
	        break;
	    case MotionEvent.ACTION_MOVE:
	  //      Log.d("TouchEvent", "getAction()" + "ACTION_MOVE");
	        break;
	    case MotionEvent.ACTION_CANCEL:
	    //    Log.d("TouchEvent", "getAction()" + "ACTION_CANCEL");
	        finger_up=false;
	        break;
	    }
		
	    //touch_size=event.getSize();触れている面積
	    
	    touch_time=(touch_finish_time - touch_start_time)/1000f;
	//    Log.d("TouchEvent","touch_time :"+touch_time);
		return true;};
	
	
	
	
	void overrideGetSize(Display display, Point outSize){//ディスプレイ取得　api高め用
		   try{
		      Class pointClass = Class.forName("android.graphics.Point");
		      Method newGetSize = Display.class.getMethod("getSize", new Class[]{ pointClass });
		      newGetSize.invoke(display, outSize);
		   }catch(Exception ex){
		      outSize.x = display.getWidth();
		      outSize.y = display.getHeight();
		   }
		}
	
	
	
	boolean wallFunction(Point startPt,Point finishPt,Point contactPt, double angle){//ボールが障害壁と衝突してる上でボールの反射先を決める

		
		/* 衝突前障害壁座標回転処理 */
		double px =finishPt.x-startPt.x;//start（回転中心）からのx距離　　start...回転中心座標
		double py =finishPt.y-startPt.y;//start（回転中心）からのy距離
		
		double new_x =RotAx(angle, px, py);//x軸 startを0,0としたfinish回転後のx座標
		double new_y =RotAy(angle, px, py);//y軸  startを0,0としたfinish回転後のy座標
		
		int not_par_ver=0;//平行でも垂直でもない...0     平行...1     垂直&右からボール...2　　 垂直&左からボール...3
		
		if(-1<new_y&& new_y<1){not_par_ver=1;}//平行
		else if(-1<new_x && new_x<1){
			if(wall_a>0)
				not_par_ver=2;
			else
				not_par_ver=3;
			}
		else{
			not_par_ver=0;}
		/* 衝突前障害壁座標回転処理終了 */
		
		/* 衝突前ボール中心回転処理 */
		double cx = (double)(view.ball_Point.x-startPt.x);//平行移動
		double cy = (double)((view.ball_Point.y)-startPt.y);//平行移動
		
		double new_cx=RotAx(angle,cx,cy);
		double new_cy=RotAy(angle,cx,cy);
		
		/* 衝突前ボール中心回転処理終了 */
		
		/* 衝突前加速度回転処理 */
		double ax =RotAx(angle,acceleration_x,acceleration_y);
		double ay =RotAy(angle,acceleration_x,acceleration_y);
		/* 衝突前加速度回転処理終了 */
		
		
		/* 衝突処理　1, 2, 3 */
		/***
		if(not_par_ver==1);//平行
		else if(not_par_ver==2);//垂直　右から
		else if(not_par_ver==3);//垂直　左から
		else;***/
		
		/*衝突時処理*/
		if(not_par_ver==2){
			if(new_cx-view.ball_radius/2<=new_x){/*ここからx方向処理*///左壁
				new_cx=new_cx+(new_x-(new_cx-view.ball_radius/2))+view.ball_radius/2;
				ax=-ax;	
			}
			else{return false;}
		}
		else if(not_par_ver==3){
			if(new_cx+view.ball_radius/2>=new_x){//右壁
				new_cx= new_cx+new_x-(new_cx+view.ball_radius/2);
				ax=-ax;	
			}
			else
				return false;
			}//方向処理もやる->ok?/*ここまでx方向処理*/
		else if(not_par_ver==1){
			if(new_cy-view.ball_radius/2<=new_y){/*ここからy方向処理*///天井風
				new_cy= new_cy+(new_y-(new_cy-view.ball_radius/2));
				ay=-ay;
			}
			else{
			return false;}
			}
		else{return false;}
		/*ここまでy方向処理*/
		/*衝突時処理終了*/
		
		/*衝突後回転処理*/
		
		//ボールの中心
		cx=RotAx(-angle,new_cx,new_cy);//回転をもとに戻す
		cy=RotAy(-angle,new_cx,new_cy);
		
		view.ball_Point.set((int)(cx+startPt.x),(int)((cy+startPt.y)));//平行移動してボールの中心とする
		//加速度
		acceleration_x=RotAx(-angle,ax,ay);
		acceleration_y=RotAy(-angle,ax,ay);
		
		/*衝突後回転処理終了*/
		return true;	
	}
	
	double RotAx(double angle, double ax, double ay){//回転後のx方向加速度関数
		double newAx;
		newAx=(double)(ax*Math.cos(angle)-ay*Math.sin(angle));
		return newAx;
	}
	
	double RotAy(double angle, double ax, double ay){//回転後のy方向加速度関数
		double newAy;
		newAy=(double)(ax*Math.sin(angle)+ay*Math.cos(angle));
		return newAy;
	}
	
	boolean regionHigh(Point p,Point c,Point XY){
		if((c.x-p.x)*(XY.y-c.y)+(c.y-p.y)*(c.x-XY.x)<0)
			return false;
		else
			return true;
	}

	
	
	/***インナークラスRefreshHandler 動的実現　run的なものだと***/
	class RefreshHandler extends Handler {
		@Override public void handleMessage(Message msg) {
		//	Log.v("RefreshHandler","handleMessage");
			update();
			if(mRedrawHandler!=null) mRedrawHandler.sleep(5);//10->5
		}
		public void sleep(long delayMills) {
			this.removeMessages(0);
			sendMessageDelayed(obtainMessage(0), delayMills);
		}
	}
}
