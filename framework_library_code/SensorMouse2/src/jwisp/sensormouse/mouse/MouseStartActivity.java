package jwisp.sensormouse.mouse;

import jwisp.sensormouse.activity.ControllerService;
import jwisp.sensormouse.activity.R;
import jwisp.sensormouse.button.ButtonSettingActivity;
import jwisp.sensormouse.setting.PrefsEnum;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class MouseStartActivity extends Activity {

	private static final int GRIDWIDTH = 100;
	private Button buttonTest;
	private static WindowManager wManager;
	private static int screenWidth;
	private static int screenHeight;
	public static boolean needToast = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mouse_activity_layout);
		
		Intent intent = new Intent(this, ControllerService.class);
		intent.putExtra("averageNum", PrefsEnum.getAverageNum());
		intent.putExtra("magnification", PrefsEnum.getMagnification());
		intent.putExtra("offset", PrefsEnum.getOffset());
		intent.putExtra("direction", PrefsEnum.getDirection());
		intent.putExtra("sensorDelay", PrefsEnum.getSensorDelay());
		intent.putExtra("selectPosition", PrefsEnum.getSelectPosition());
		intent.putExtra("timeunit", ButtonSettingActivity.getTimeUnit());
		intent.putExtra("backtime", ButtonSettingActivity.getBackTime());
		intent.putExtra("timewait", ButtonSettingActivity.getTimeWait());
		stopService(intent);
		startService(intent);

		wManager = (WindowManager) getApplicationContext().getSystemService("window");
		screenWidth = wManager.getDefaultDisplay().getWidth();
		screenHeight = wManager.getDefaultDisplay().getHeight();
		
		ViewGroup vGroup = (ViewGroup) findViewById(R.id.mouse_activity_layout);
		CanvasView canvasView = new CanvasView(this);
		vGroup.addView(canvasView);
		
		buttonTest = (Button) findViewById(R.id.test_button);
		vGroup.removeView(buttonTest);
		
		buttonTest.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						System.out.println("ACTION_DOWN (" +event.getRawX()+ ", " +event.getRawY()+ ")");
						Toast.makeText(MouseStartActivity.this, "ACTION_DOWN (" +event.getRawX()+ ", " +event.getRawY()+ ")", 
								Toast.LENGTH_SHORT).show();
						break;
					case MotionEvent.ACTION_MOVE:
						System.out.println("ACTION_MOVE (" +event.getRawX()+ ", " +event.getRawY()+ ")");
						Toast.makeText(MouseStartActivity.this, "ACTION_MOVE (" +event.getRawX()+ ", " +event.getRawY()+ ")", 
								Toast.LENGTH_SHORT).show();
						break;
					case MotionEvent.ACTION_UP:
						System.out.println("ACTION_UP (" +event.getRawX()+ ", " +event.getRawY()+ ")");
						Toast.makeText(MouseStartActivity.this, "ACTION_UP (" +event.getRawX()+ ", " +event.getRawY()+ ")", 
								Toast.LENGTH_SHORT).show();
						break;
					default:
						break;
				}
				return false;
			}
		});
		buttonTest.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Toast.makeText(MouseStartActivity.this, "Central Button Clicked!", 
						Toast.LENGTH_SHORT).show();
			}
		});
		buttonTest.setOnLongClickListener(new OnLongClickListener() {
			public boolean onLongClick(View v) {
				Toast.makeText(MouseStartActivity.this, "CenterCentralButton Long clicked!", 
						Toast.LENGTH_SHORT).show();
				return false;
			}
		});
		
		
		vGroup.addView(buttonTest);
		System.out.println("test_button(" +buttonTest.getX()+ ", " +buttonTest.getY()+ ")");
		
		 gestureDetector = new GestureDetector(this, mGestureListener);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (gestureDetector.onTouchEvent(event))
			return true;
		else
			return false; 
	}
	
	class CanvasView extends View {

		public CanvasView(Context context) {
			super(context);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			
			Paint paint = new Paint();
			paint.setColor(Color.RED);
			canvas.drawColor(Color.WHITE);
			
			
			for (int i=1; i<=screenHeight/GRIDWIDTH; i++){
				canvas.drawLine(0, GRIDWIDTH*i, screenWidth, GRIDWIDTH*i, paint);
			}
			for (int i=1; i<=screenWidth/GRIDWIDTH; i++){
				canvas.drawLine(GRIDWIDTH*i, 0, GRIDWIDTH*i, screenHeight, paint);
			}
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		needToast = false;
	}
	@Override
	protected void onPause() {
		super.onPause();
		needToast = true;
	}
	@Override
	protected void onStop() {
		super.onStop();
		System.out.println("MouseStartActivity : onStop()");
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.out.println("MouseStartActivity : onDestroy()");
	}
	
	public static int getScreenWidth() {
		return screenWidth;
	}
	public static int getScreenHeight() {
		return screenHeight;
	}
	public static WindowManager getWManager() {
		return wManager;
	}
	
	//for test
	private SimpleOnGestureListener mGestureListener = new SimpleOnGestureListener(){
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			System.out.println("!!!!!!!**********onFling*********!!!!!!: velocityX = " +velocityX+ ", velocityY" +velocityY);
			return true;
		};
	};
	private GestureDetector gestureDetector;
}
