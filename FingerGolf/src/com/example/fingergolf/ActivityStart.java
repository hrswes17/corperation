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

	Display display;//��ʃT�C�Y�擾�@API���x���������ꍇ��overrideGetDisplay���Ăяo��
	Point size = new Point();//��ʃT�C�Y���i�[
//	private Intent to_over;
	private ViewDisplay view;//View�N���X�@���ۂ̕`��
	
	private RefreshHandler mRedrawHandler;//�X���b�h����@�n���h��

	private float touch_start_x;//�G�ꂽ�ꏊx
	private float touch_start_y;//�ȉ����ly
	private float touch_finish_x;//���ꂽ�ꏊx
	private float touch_finish_y;//�ȉ����ly
	private float distance_x;//�G�ꂽ����x
	private float distance_y;//�ȉ����ly
	
	private float touch_time;//�w���G��Ă�������
	private long touch_start_time;//�w���G�ꂽ����
	private long touch_finish_time;//�w�����ꂽ����
	private int move_time;//�{�[�����~�܂�܂ł̎��Ԃ��i�[
	private double acceleration_x;//x���������x
	private double acceleration_y;//y���������x
	private int second=0;//�����x���狗���𓾂邽�߂ɗp�ӂ������ԕϐ��@�@move_time�������l�Ƃ��A0�ɂȂ�܂Ō�������@
	private boolean for_second = true;//�`�ʏI����Asecond�����������邽�߂ɗp�ӂ����ϐ�
	private int X;//clear_flag�������Ă���A�{�[���̕�����ς��邽�߂Ɍ��ƃ{�[���̒��S���W��x����(+-�����t)���i�[
	private int Y;//��L���l
	private float distance_center_x;//���ƃ{�[���̒��S���W��x�����@��Βl�ł���Ă��邩��+
	private float distance_center_y;//��L���ly����
	private float dcx;//distance_center_x�̋����i�[
	private float dcy;
	
	private boolean finger_up=false;//�w������Ă��鎞�ŁA�`�ʂ��Ă���Ƃ�true�ɂȂ�
	private boolean game_clear_flag=false;//�O���~��`�����߂̂��́A���ɐڋ߂����Ƃ���true�@��������30*100msec�`��
	private boolean game_clear=false;//Intent��finish�p
	private Point hole_center;//���̒��S���W
	private Point ball_center=new Point();//�{�[���̒��S���W
	private boolean first=true;//game_clear_flag���ɓ����Ă���s���ŏ��̏����̂��߂̕ϐ�1�x�����g��Ȃ�
	//MediaPlayer se = MediaPlayer.create(getContext(), R.raw.tap);
	//private SoundPool tap_sound;
	//private SoundPool wall_sound;
	private final static int MAX_STREAMS = 5;//���y�Ȑ�
	private SoundPool soundPool= new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC,0);
	private int[] mSound = new int[MAX_STREAMS];
	private final int TESTC=10;
	
	private int currentStage;//���݂̃X�e�[�W��
	private int numOfTouch;//�^�b�`��
	private int totalScore;//�S�X�e�[�W�X�R�A
	
	private Point wall_start;//��Q�ǎn�܂�̓_
	private Point wall_finish;//��Q�ǏI���̓_
	private double wall_angle;//x���ɑ΂��ď�Q�ǂ̊p�x
	private Point wall_ball_contact1=new Point(0,0);//��Q�ǂƃ{�[���̐ړ_���̂P...�{�[������Q�ǂɂԂ���Ƃ�����̓_
	private Point wall_ball_contact2=new Point(0,0);//��Q�ǂƃ{�[���̐ړ_����2...����
	private boolean contact_judge1;//��Q�ǂɂԂ����������肻�̂P
//	private boolean contact_judge2;//��Q�ǂɂԂ����������肻�̂Q
	double rotAftAx;//��Q�ǂɂԂ��������x���������x
	double rotAftAy;//��Q�ǂɂԂ��������y���������x
	
	double wall_a;//��Q�ǌX��
	double wall_c;//��Q�ǒ萔
	
	final int JUDGE_RANGE=0;//��Q�ǔ���͈�
	private boolean sand_sound=true;
	
	private boolean current_region_high1;//     /���@  �܂���    ��\�̂Ƃ� true
	private boolean previous_region_high1;// ����
	private boolean current_region_high2;//     /���@  �܂���    ��\�̂Ƃ� true
	private boolean previous_region_high2;// ����
	
	private double acceleration_angle;//�����x����
	
	private Point pre_contact1 = new Point();
	private Point pre_contact2 = new Point();
	boolean up_down_sf=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		display = getWindowManager().getDefaultDisplay();
		
		Intent get_intent= getIntent();
		currentStage=get_intent.getIntExtra("stage", 0);//�X�e�[�W�擾
		totalScore=get_intent.getIntExtra("total score", 0);

		overrideGetSize(display, size);//Window�T�C�Y�̎擾Point size�Ɋi�[ ->�ł���->method�ύX�Ē���->�ł����I
		 
		numOfTouch=0;
		
		 view = new ViewDisplay(getApplication(),size);
		 view.setDrawText(true);//�e�L�X�g�\��
		 setContentView(view);
		 
		 size.y=(int)view.getWindowSizeUnder();
		 
		 mSound[0]=soundPool.load(getApplicationContext(),R.raw.cup_in,1);//0->CUP IN
		 mSound[1]=	soundPool.load(getApplicationContext(),R.raw.wall,1);//1->WALL
		 mSound[2]=soundPool.load(getApplicationContext(),R.raw.tap,1);//2->TAP
		 mSound[3]=soundPool.load(getApplicationContext(),R.raw.sand,1);//3->SAND
		 mSound[4]=soundPool.load(getApplicationContext(),R.raw.syougai_wall,1);//4->syougai_wall

		 hole_center=new Point((int)(view.hole_x_s+(view.hole_x_f-view.hole_x_s)/2.0), (int)(view.hole_y_s+(view.hole_y_f-view.hole_y_s)/2));
		 
		 
		 if(view.getWallPointStartY()<view.getWallPointFinishY()){//X���ɋ߂��_���n�_�Ƃ���//�킩���
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
		//TODO update����
		if(finger_up){
			
			distance_x=touch_finish_x-touch_start_x;//��������x
			distance_y=touch_finish_y-touch_start_y;//��������y
			distance_x/=touch_time;//�{�[�i�X����x (m/s)
			distance_y/=touch_time;//�{�[�i�X����y (m/s)
			
			if(Math.abs(distance_x)>Math.abs(distance_y)){
				move_time=(int)distance_x/TESTC;//�����I�Ȃ��̂�TESTC�Ŋ����ă{�[���������b����move_time�Ƃ���
			}
			else{
				move_time=(int)distance_y/TESTC;
			}
			if(move_time<0)
				move_time=-move_time;
				
			if(for_second){
				second=move_time;for_second=false;//�`�ʂɎg���鎞�Ԃ̋L�^
				acceleration_x=(distance_x/move_time)/((double)move_time/2.0);//�����x x
				acceleration_y=(distance_y/move_time)/((double)move_time/2.0);//�����x y
				soundPool.play(mSound[2], 1.0f, 1.0f, 0, 0, 1.0f);//TAP sound	
			}
			
		
			if(second>0){//�{�[���]������
				view.ball_Point.x=view.initial_ball_Point.x+(int)((acceleration_x*second*second)/100);//�{�[���̈ʒux
				view.ball_Point.y=view.initial_ball_Point.y+(int)((acceleration_y*second*second)/100);//�{�[���̈ʒuy
				
				/*�z�[���C������*/
				if(view.hole_y_s<view.ball_Point.y+view.ball_radius && view.ball_Point.y+view.ball_radius<view.hole_y_f){
					if(view.hole_x_s<view.ball_Point.x+view.ball_radius/2 && view.ball_Point.x+view.ball_radius/2 <view.hole_x_f){
						game_clear_flag=true;//�N���A�t���O
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
				
				/*�z�[���C�������I��*/
				
				
				else if(!game_clear_flag){/*�܂��Q�[���͏I���Ȃ���*/
				/*�ǏՓˎ�����*/
				if((int)(view.ball_Point.x-(view.ball_radius/2))<=0){/*��������x��������*/
					view.ball_Point.x=-(int)(view.ball_Point.x-(view.ball_radius));//�ς����ǂ��ꂶ��Ȃ��ƃ{�[�����U������
					acceleration_x=-acceleration_x;	
//					Log.v("wall test","this "+view.ball_Point.x+"�@�@"+view.ball_radius);
					soundPool.play(mSound[1], 1.0f, 1.0f, 0, 0, 1.0f);//WALL sound
				}//�������������->ok?
				if(view.ball_Point.x+view.ball_radius/2>=size.x){
					view.ball_Point.x=size.x-(view.ball_Point.x+view.ball_radius-size.x);
					acceleration_x=-acceleration_x;	
					soundPool.play(mSound[1], 1.0f, 1.0f, 0, 0, 1.0f);//WALL sound
				}//�������������->ok?/*�����܂�x��������*/
				
				if(view.ball_Point.y-view.ball_radius/2<=0){/*��������y��������*/
					view.ball_Point.y=-view.ball_Point.y+view.ball_radius;
					acceleration_y=-acceleration_y;
					soundPool.play(mSound[1], 1.0f, 1.0f, 0, 0, 1.0f);//WALL sound
				}
				if(view.ball_Point.y+view.ball_radius/2>=size.y){
					view.ball_Point.y=size.y-(view.ball_Point.y+view.ball_radius-size.y);
					acceleration_y=-acceleration_y;
					soundPool.play(mSound[1], 1.0f, 1.0f, 0, 0, 1.0f);//WALL sound
				}/*�����܂�y��������*/
				/*�ǏՓˎ������I��*/
				
				/*��Q�Ǐ���*/
				//�ǂɂԂ���Ƃ�����̓_
				
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
				if((wall_angle<acceleration_angle && acceleration_angle < wall_angle+Math.PI)){//    \�� con1
					if(regionHigh(pre_contact1,wall_ball_contact1,wall_start)!=regionHigh(pre_contact1,wall_ball_contact1,wall_finish))
						up_down_sf=true;
					else
						up_down_sf=false;
				}
				else{ //   ��\
					if(regionHigh(pre_contact2,wall_ball_contact2,wall_start)!=regionHigh(pre_contact2,wall_ball_contact2,wall_finish))
						up_down_sf=true;
					else
						up_down_sf=false;
				}
			}
			else{
				if((wall_angle+Math.PI<acceleration_angle && acceleration_angle < 2*Math.PI+wall_angle)){//  /�� con2
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
									&& ((wall_angle<acceleration_angle && acceleration_angle < wall_angle+Math.PI))){  //    \��
						contact_judge1=wallFunction(wall_start,wall_finish,wall_ball_contact1,Math.PI/2-wall_angle);
						if(contact_judge1)
							soundPool.play(mSound[4], 1.0f, 1.0f, 0, 0, 1.0f);
						}//��Q�ǂɐڂ�������->�{�[���̌��������Ĕ��˂�����������߂�
					else if(((current_region_high1!=current_region_high2)||(current_region_high2 != previous_region_high2))&&up_down_sf
									&&( (0<=acceleration_angle && acceleration_angle<=wall_angle)||(wall_angle+Math.PI<=acceleration_angle && acceleration_angle <= 2*Math.PI))){//   ��\
						contact_judge1=wallFunction(wall_start,wall_finish,wall_ball_contact2, -wall_angle);
						if(contact_judge1)
							soundPool.play(mSound[4], 1.0f, 1.0f, 0, 0, 1.0f);
						}
					else
						contact_judge1=false;		
					}
				else{
					if(((current_region_high1!=current_region_high2)||(current_region_high2 != previous_region_high2))&&up_down_sf
									&& (wall_angle+Math.PI<acceleration_angle && acceleration_angle < 2*Math.PI+wall_angle)){//  /�� con2
						Log.v("/�� con2","/�� con2");
						contact_judge1=wallFunction(wall_start,wall_finish,wall_ball_contact2,-wall_angle);
						if(contact_judge1)
							soundPool.play(mSound[4], 1.0f, 1.0f, 0, 0, 1.0f);	
					}//��Q�ǂɐڂ�������->�{�[���̌��������Ĕ��˂�����������߂�
					else if(((current_region_high1!=current_region_high2)||(current_region_high1 != previous_region_high1))&&up_down_sf
							&& ((0<=acceleration_angle&&acceleration_angle<=Math.PI+wall_angle)||(2*Math.PI+wall_angle <= acceleration_angle && acceleration_angle <=2*Math.PI))){// ��/ con1
						contact_judge1=wallFunction(wall_start,wall_finish,wall_ball_contact1,-(Math.PI/2+wall_angle));
						Log.v("��/ con2","��/ con2");
						if(contact_judge1)
							soundPool.play(mSound[4], 1.0f, 1.0f, 0, 0, 1.0f);
					}
					else
						contact_judge1=false;
					}
				
				/**
				if(wall_angle>0){//-PI/2~PI/2
					if((wall_ball_contact1.y>=wall_a*wall_ball_contact1.x+wall_c+JUDGE_RANGE)&&(wall_start.x <= wall_ball_contact1.x && wall_ball_contact1.x<=wall_finish.x && wall_start.y <= wall_ball_contact1.y && wall_ball_contact1.y<=wall_finish.y)&& (wall_ball_contact2.y<=wall_a*wall_ball_contact2.x+wall_c-JUDGE_RANGE) && !(acceleration_x>0&&acceleration_y<0)){  //    \��
						contact_judge1=wallFunction(wall_start,wall_finish,wall_ball_contact1,Math.PI/2-wall_angle);
						if(contact_judge1)
							soundPool.play(mSound[4], 1.0f, 1.0f, 0, 0, 1.0f);
						}//��Q�ǂɐڂ�������->�{�[���̌��������Ĕ��˂�����������߂�
					else if((wall_ball_contact1.y>=wall_a*wall_ball_contact1.x+wall_c+JUDGE_RANGE)&&(wall_start.x <= wall_ball_contact2.x && wall_ball_contact2.x<=wall_finish.x && wall_start.y <= wall_ball_contact2.y && wall_ball_contact2.y<=wall_finish.y)&& (wall_ball_contact2.y<=wall_a*wall_ball_contact2.x+wall_c-JUDGE_RANGE) && !(acceleration_x<0 && acceleration_y>0)){//   ��\
						contact_judge1=wallFunction(wall_start,wall_finish,wall_ball_contact2, -wall_angle);
						if(contact_judge1)
							soundPool.play(mSound[4], 1.0f, 1.0f, 0, 0, 1.0f);
						}
					else
						contact_judge1=false;		
					}
				else{
					if((wall_ball_contact1.y>=wall_a*wall_ball_contact1.x+wall_c+JUDGE_RANGE)&&(wall_finish.x<=wall_ball_contact1.x&&wall_ball_contact1.x<=wall_start.x && wall_start.y<=wall_ball_contact1.y&&wall_ball_contact1.y<=wall_finish.y)&& (wall_ball_contact2.y<=wall_a*wall_ball_contact2.x+wall_c-JUDGE_RANGE) && !(acceleration_x>0 && acceleration_y>0)){//  /�� con1
						contact_judge1=wallFunction(wall_start,wall_finish,wall_ball_contact1,-wall_angle);
						if(contact_judge1)
							soundPool.play(mSound[4], 1.0f, 1.0f, 0, 0, 1.0f);	
					}//��Q�ǂɐڂ�������->�{�[���̌��������Ĕ��˂�����������߂�
					else if((wall_ball_contact1.y>=wall_a*wall_ball_contact1.x+wall_c+JUDGE_RANGE)&&(wall_finish.x<=wall_ball_contact2.x&&wall_ball_contact2.x<=wall_start.x && wall_start.y<=wall_ball_contact2.y&&wall_ball_contact2.y<=wall_finish.y)&& (wall_ball_contact2.y<=wall_a*wall_ball_contact2.x+wall_c-JUDGE_RANGE)&& !(acceleration_y<0 && acceleration_x<0)){// ��/ con2
						contact_judge1=wallFunction(wall_start,wall_finish,wall_ball_contact2,-(Math.PI/2+wall_angle));
						if(contact_judge1)
							soundPool.play(mSound[4], 1.0f, 1.0f, 0, 0, 1.0f);
					}
					else
						contact_judge1=false;
					}//��Q�ǂɐڂ�������
				**/
		
				//�o���J�[����
				if(view.getSandTrapStartX()<=view.ball_Point.x && view.ball_Point.x <=view.getSandTrapFinishX() && view.getSandTrapStartY() <=view.ball_Point.y && view.ball_Point.y <= view.getSandTrapFinishY()){
					if(sand_sound)
						soundPool.play(mSound[3], 1.0f, 1.0f, 0, 0, 1.0f);
					second*=0.9;
					sand_sound=false;
					}
				else
					sand_sound=true;
			//�o���J�[����I��
			}
				}//�{�[���]�����I��
			
			else{//�{�[���]�����Q��ڈȍ~�̂��߂̏���
				for_second=true;
				view.initial_ball_Point=view.ball_Point;
				finger_up=false;
				sand_sound=true;
				numOfTouch++;
				if(game_clear_flag)
					game_clear=true;
				else
					view.setDrawText(true);//PleaseDrag
				/**�e�L�X�g�\��**/
			}
			second--;
			
		}
		else{
//			view.runCounter(true);//����Ȃ�����
			view.setCounter(view.getCounter()-0.5f);//�J�E���g�_�E��->0.2f
		}
		view.invalidate();
		
	if(game_clear || view.finishCount()){//TEST OK!!
		Intent test_int = new Intent(this,GameOverActivity.class);//FOR TEST ACTIVITY
		test_int.putExtra("stage", ++currentStage);//���̃X�e�[�W����n��
		test_int.putExtra("number of touch", numOfTouch);
		//�{�[�i�X�����
		test_int.putExtra("total score", totalScore);
		test_int.putExtra("left time",(int)(view.getTimeScore()));
		if(view.finishCount()){//�Q�[���I�[�o�[
			test_int.putExtra("over", true);
		}
		else{
			test_int.putExtra("over", false);
			}//�Q�[���N���A
		startActivity(test_int);  									//FOR TEST ACTIVITY
		//Log.v("if(game_clear)","game_clear");
		game_clear=false;//2��ڃC���e���g���
		finish();
		}	
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	//	Log.v("ActivityStart","onDestroy()");
				//�g��Ȃ��Ȃ����T�E���h��unload
		for(int i=0;i<mSound.length;i++)
				soundPool.unload(mSound[i]);
		
			//SoundPool���g�p���Ă��郊�\�[�X��S�ĉ��
			soundPool.release();
	}

	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		Log.d("TouchEvent", "X:" + event.getX() + ",Y:" + event.getY());
		 
	    switch (event.getAction()) {
	    case MotionEvent.ACTION_DOWN://�G�ꂽ��
	 //       Log.d("TouchEvent", "getAction()" + "ACTION_DOWN");
	        touch_start_x=event.getX();
	        touch_start_y=event.getY();
	        touch_start_time=event.getEventTime();
	   //     Log.v("onTouchEvent","touch_start_time: "+touch_start_time);
	        finger_up=false;
	        view.setDrawText(false);
	        /**�e�L�X�g��\��**/
	        break;
	    case MotionEvent.ACTION_UP://���ꂽ��
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
		
	    //touch_size=event.getSize();�G��Ă���ʐ�
	    
	    touch_time=(touch_finish_time - touch_start_time)/1000f;
	//    Log.d("TouchEvent","touch_time :"+touch_time);
		return true;};
	
	
	
	
	void overrideGetSize(Display display, Point outSize){//�f�B�X�v���C�擾�@api���ߗp
		   try{
		      Class pointClass = Class.forName("android.graphics.Point");
		      Method newGetSize = Display.class.getMethod("getSize", new Class[]{ pointClass });
		      newGetSize.invoke(display, outSize);
		   }catch(Exception ex){
		      outSize.x = display.getWidth();
		      outSize.y = display.getHeight();
		   }
		}
	
	
	
	boolean wallFunction(Point startPt,Point finishPt,Point contactPt, double angle){//�{�[������Q�ǂƏՓ˂��Ă��Ń{�[���̔��ː�����߂�

		
		/* �ՓˑO��Q�Ǎ��W��]���� */
		double px =finishPt.x-startPt.x;//start�i��]���S�j�����x�����@�@start...��]���S���W
		double py =finishPt.y-startPt.y;//start�i��]���S�j�����y����
		
		double new_x =RotAx(angle, px, py);//x�� start��0,0�Ƃ���finish��]���x���W
		double new_y =RotAy(angle, px, py);//y��  start��0,0�Ƃ���finish��]���y���W
		
		int not_par_ver=0;//���s�ł������ł��Ȃ�...0     ���s...1     ����&�E����{�[��...2�@�@ ����&������{�[��...3
		
		if(-1<new_y&& new_y<1){not_par_ver=1;}//���s
		else if(-1<new_x && new_x<1){
			if(wall_a>0)
				not_par_ver=2;
			else
				not_par_ver=3;
			}
		else{
			not_par_ver=0;}
		/* �ՓˑO��Q�Ǎ��W��]�����I�� */
		
		/* �ՓˑO�{�[�����S��]���� */
		double cx = (double)(view.ball_Point.x-startPt.x);//���s�ړ�
		double cy = (double)((view.ball_Point.y)-startPt.y);//���s�ړ�
		
		double new_cx=RotAx(angle,cx,cy);
		double new_cy=RotAy(angle,cx,cy);
		
		/* �ՓˑO�{�[�����S��]�����I�� */
		
		/* �ՓˑO�����x��]���� */
		double ax =RotAx(angle,acceleration_x,acceleration_y);
		double ay =RotAy(angle,acceleration_x,acceleration_y);
		/* �ՓˑO�����x��]�����I�� */
		
		
		/* �Փˏ����@1, 2, 3 */
		/***
		if(not_par_ver==1);//���s
		else if(not_par_ver==2);//�����@�E����
		else if(not_par_ver==3);//�����@������
		else;***/
		
		/*�Փˎ�����*/
		if(not_par_ver==2){
			if(new_cx-view.ball_radius/2<=new_x){/*��������x��������*///����
				new_cx=new_cx+(new_x-(new_cx-view.ball_radius/2))+view.ball_radius/2;
				ax=-ax;	
			}
			else{return false;}
		}
		else if(not_par_ver==3){
			if(new_cx+view.ball_radius/2>=new_x){//�E��
				new_cx= new_cx+new_x-(new_cx+view.ball_radius/2);
				ax=-ax;	
			}
			else
				return false;
			}//�������������->ok?/*�����܂�x��������*/
		else if(not_par_ver==1){
			if(new_cy-view.ball_radius/2<=new_y){/*��������y��������*///�V�䕗
				new_cy= new_cy+(new_y-(new_cy-view.ball_radius/2));
				ay=-ay;
			}
			else{
			return false;}
			}
		else{return false;}
		/*�����܂�y��������*/
		/*�Փˎ������I��*/
		
		/*�Փˌ��]����*/
		
		//�{�[���̒��S
		cx=RotAx(-angle,new_cx,new_cy);//��]�����Ƃɖ߂�
		cy=RotAy(-angle,new_cx,new_cy);
		
		view.ball_Point.set((int)(cx+startPt.x),(int)((cy+startPt.y)));//���s�ړ����ă{�[���̒��S�Ƃ���
		//�����x
		acceleration_x=RotAx(-angle,ax,ay);
		acceleration_y=RotAy(-angle,ax,ay);
		
		/*�Փˌ��]�����I��*/
		return true;	
	}
	
	double RotAx(double angle, double ax, double ay){//��]���x���������x�֐�
		double newAx;
		newAx=(double)(ax*Math.cos(angle)-ay*Math.sin(angle));
		return newAx;
	}
	
	double RotAy(double angle, double ax, double ay){//��]���y���������x�֐�
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

	
	
	/***�C���i�[�N���XRefreshHandler ���I�����@run�I�Ȃ��̂���***/
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
