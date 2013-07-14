package com.example.fingergolf;

//import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ViewDisplay extends View {
	
	static int hole_width;
	static int hole_height;
	static int ball_radius;
	
	static float hole_x_s;
	static float hole_x_f;
	static float hole_y_s;
	static float hole_y_f;
	
	private float counter_stack_width;
	
	private float counter_rxs;
	private float counter_rxf;
	private float counter_rys;
	private float counter_ryf;

	private float ccounter_rxs;
	private float ccounter_rxf;
	private float ccounter_rys;
	private float ccounter_ryf;
	
	private float wall_xs;
	private float wall_ys;
	private float wall_xf;
	private float wall_yf;
//	private double rotate_angle;
	private float sand_xs;
	private float sand_ys;
	private float sand_xf;
	private float sand_yf;
	private float sand_width;
	private float sand_height;
	
	private float state_xs;
	private float state_ys;
	private float state_xf;
	private float state_yf;

	
	private Paint paint_back_ground;
	private Paint paint_ball;
	private Paint paint_hole;
	private Paint paint_text;
//	private Paint paint_counter_stack;
	private Paint paint_counter;
	private Paint paint_ccounter;
	private Paint paint_wall;
	private Paint paint_wall_end;
	private Paint paint_sand_trap;
	private Paint paint_sand_grains;
	private Paint paint_state;
	
	Point windowPoint;
	Point ball_Point;
	Point initial_ball_Point;
	private RectF rect;
//	private RectF counter_stack_rect;
	private RectF counter_rect;
	private RectF ccounter_rect;
	private boolean draw_text=true;
	private RectF sand_rect;
	
	Resources res = this.getContext().getResources();
    Bitmap grass = BitmapFactory.decodeResource(res, R.drawable.sibafu);
	
    private RectF state_rect;
	
public ViewDisplay(Context context,Point wp){
	super(context);
	windowPoint=wp;
//	Log.v("ViewDisplay","wp.x: "+wp.x+" wp.y: "+wp.y);
	hole_width=wp.x/4;
	hole_height=hole_width/2;
	ball_radius=hole_height/2;
	ball_Point = new Point(windowPoint.x/8,windowPoint.y-ball_radius*4);
	initial_ball_Point=ball_Point;
	hole_x_s=windowPoint.x*11/16;
	hole_y_s=windowPoint.y/16;
	hole_x_f=hole_x_s+hole_width;
	hole_y_f=hole_height+hole_y_s;
	
	counter_rxs=windowPoint.x*8/14;
	counter_rxf=windowPoint.x*12/14;
	counter_rys=windowPoint.y*55/60;
	counter_ryf=windowPoint.y*56/60;
	counter_stack_width=counter_rxf-counter_rxs;
	
	ccounter_rxs=windowPoint.x*8/14;
	ccounter_rxf=windowPoint.x*12/14;
	ccounter_rys=windowPoint.y*55/60;
	ccounter_ryf=windowPoint.y*56/60;
	
	
	///**
	wall_xs=(float)((windowPoint.x-hole_width-ball_radius)*Math.random());
	wall_ys=(float)((windowPoint.y-hole_height-ball_radius)*Math.random());
	
	wall_xf=(float)((windowPoint.x-hole_width-ball_radius)*Math.random());
	wall_yf=(float)((windowPoint.y-hole_height-ball_radius)*Math.random());	

	if((int)wall_xf==(int)wall_xs)
		wall_xf+=1;
	
	sand_width=windowPoint.x/2;
	sand_height=(float)(windowPoint.y*Math.random());
	sand_xs=(float)Math.random()*windowPoint.x/2;
	sand_ys=(float)Math.random()*(windowPoint.y-sand_height);
	sand_xf=sand_xs+sand_width;
	sand_yf=sand_ys+sand_height;

	
	state_xs=0f;
	state_ys=windowPoint.y*27/30;;
	state_xf=windowPoint.x;
	state_yf=windowPoint.y;
	
	
	}

@Override
protected void onDraw(Canvas canvas){
			
	canvas.drawColor(Color.TRANSPARENT);
	paint_back_ground=new Paint();
	canvas.drawBitmap(grass, 0, 0, paint_back_ground);
	//canvas.drawColor(Color.argb(100, 100, 100, 100));
	
	/*** sand trap ***/
	paint_sand_grains = new Paint();
	paint_sand_grains.setColor(Color.argb(200, 200, 200, 200));
	paint_sand_grains.setStrokeWidth(1);
	paint_sand_grains.setAntiAlias(true);
	paint_sand_grains.setStyle(Paint.Style.FILL);
	for(int j=0;j<=3;j++)
		for(int i=0;i<=4;i++){
			if(!((j==0||j==3)&&(i==0 || i==4)))
				canvas.drawCircle(sand_xs+sand_width*i/4,sand_ys+sand_height*j/3,2,paint_sand_grains);
			}
	paint_sand_trap = new Paint();
	paint_sand_trap.setColor(Color.argb(180, 250, 250, 100));
	paint_sand_trap.setStrokeWidth(2);
	paint_sand_trap.setAntiAlias(true);
	paint_sand_trap.setStyle(Paint.Style.FILL);
	sand_rect = new RectF(sand_xs,sand_ys,sand_xf,sand_yf);//x始点、y始点、x終点,y終点
	canvas.drawRoundRect(sand_rect,sand_height/4,sand_height/4, paint_sand_trap);
	
	
			/*** hole ***/
	paint_hole = new Paint();
	paint_hole.setColor(Color.BLACK);
	paint_hole.setStrokeWidth(5);
	paint_hole.setAntiAlias(true);
	paint_hole.setStyle(Paint.Style.FILL);
	rect = new RectF(hole_x_s,hole_y_s,hole_x_f,hole_y_f); //x始点、y始点、x終点,y終点
	canvas.drawOval(rect, paint_hole);
	//canvas.drawCircle(100, 200, 30, paint_hole);
	//Log.v("ViewDisplay","onDraw Last");

	
		/*** ball ***/
	paint_ball = new Paint();
	paint_ball.setColor(Color.RED);
//	paint_ball.setStrokeWidth(5);
	paint_ball.setAntiAlias(true);
	paint_ball.setStyle(Paint.Style.FILL_AND_STROKE);
	canvas.drawCircle(ball_Point.x,ball_Point.y , ball_radius, paint_ball);//left_x,left_y,width,Paint
	
	
	/*** Wall ***/
	paint_wall=new Paint();
	paint_wall.setColor(Color.DKGRAY);
	paint_wall.setStrokeWidth(hole_width/20);
	canvas.drawLine(wall_xs, wall_ys, wall_xf, wall_yf, paint_wall);
	paint_wall_end = new Paint();
	paint_wall_end.setColor(Color.DKGRAY);
	paint_wall_end.setStrokeWidth(hole_width/20);
	paint_wall_end.setStyle(Paint.Style.FILL_AND_STROKE);
	canvas.drawCircle(wall_xs, wall_ys, hole_width/20, paint_wall_end);
	canvas.drawCircle(wall_xf, wall_yf, hole_width/20, paint_wall_end);
	
	
	/*** text ***/
	if(draw_text){
		paint_text=new Paint();
		paint_text.setColor(Color.BLACK);
		paint_text.setTextSize(hole_width/4);
		canvas.drawText("Please Drag", windowPoint.x/3.0f, windowPoint.y/3.0f,paint_text);
		}
	
	/*** state bar ***/
	paint_state=new Paint();
	paint_state.setColor(Color.argb(230,30,30,30));
	paint_state.setStyle(Paint.Style.FILL_AND_STROKE);
	paint_state.setAntiAlias(true);
	state_rect=new RectF(state_xs,state_ys,state_xf,state_yf);
	canvas.drawRect(state_rect, paint_state);
	
	/*** Counter Stack ***/
	
	paint_ccounter=new Paint();
	paint_ccounter.setColor(Color.argb(150, 200, 200,200));//半透明
	paint_ccounter.setStyle(Paint.Style.FILL_AND_STROKE);
	paint_ccounter.setAntiAlias(true);
	ccounter_rect = new RectF(ccounter_rxs,ccounter_rys,ccounter_rxf,ccounter_ryf); //x始点、y始点、x終点,y終点
//	Log.v("ViewDisplay","counter_rxf:"+counter_rxf);
	canvas.drawRoundRect(ccounter_rect, (ccounter_ryf-ccounter_rys)/2, (ccounter_ryf-ccounter_rys)/2, paint_ccounter);
	
	paint_counter=new Paint();
	paint_counter.setColor(Color.argb(150, 0, 170,255));//半透明ブルー
	paint_counter.setStyle(Paint.Style.FILL_AND_STROKE);
	paint_counter.setAntiAlias(true);
	counter_rect = new RectF(counter_rxs,counter_rys,counter_rxf,counter_ryf); //x始点、y始点、x終点,y終点
//	Log.v("ViewDisplay","counter_rxf:"+counter_rxf);
	canvas.drawRoundRect(counter_rect, (counter_ryf-counter_rys)/2, (counter_ryf-counter_rys)/2, paint_counter);
	
	

	
	
	}

	public void setDrawText(boolean b){draw_text=b;}//ドラッグしてくださいを表示するかしないか判定
	public float getCounter(){return counter_rxf;}
	public void setCounter(float fl){counter_rxf=fl;counter_stack_width=counter_rxf-counter_rxs;}
	public boolean finishCount(){
		if(counter_stack_width==0)//<=0だと２回インデントされた
			return true;
		else
			return false;
	}
	public float getTimeScore(){return 100*counter_stack_width/(windowPoint.x*4/14);}//windowPoint.x*4/14はデフォの長さ
	public float getWallPointStartX(){return wall_xs;}
	public float getWallPointFinishX(){return wall_xf;}
	public float getWallPointStartY(){return wall_ys;}
	public float getWallPointFinishY(){return wall_yf;}
	public float getSandTrapStartX(){	return sand_xs;}
	public float getSandTrapStartY(){return sand_ys;}
	public float getSandTrapFinishX(){return sand_xf;}
	public float getSandTrapFinishY(){return sand_yf;}
	public float getWindowSizeUnder(){return state_ys;}
	
//	public double getRotateAngle(){return rotate_angle;}
}

