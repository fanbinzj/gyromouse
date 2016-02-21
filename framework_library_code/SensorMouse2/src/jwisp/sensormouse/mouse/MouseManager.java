package jwisp.sensormouse.mouse;

import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.SensorEventListener;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import jwisp.sensormouse.activity.MainActivity;
import jwisp.sensormouse.activity.R;
import jwisp.sensormouse.button.ButtonManager;
import jwisp.sensormouse.button.ButtonSettingActivity;
import jwisp.sensormouse.sensor.ISensorCallback;
import jwisp.sensormouse.sensor.MSensorManager;

public class MouseManager implements ISensorCallback {

	private WindowManager windowManager;
	private WindowManager.LayoutParams params;
	private ImageView imageView;
	private AverageComputerForService computer;
	private boolean alreadyAdded = false;
	private boolean updateSwitch = true;
	private SensorEventListener listener;
	private int screenHeight, screenWidth;
	private MSensorManagerForService sensorManager;
	private int timeUnit;
	private int backtime;
	private int timewait;
	private static boolean isPortrait = true;
	private static int count = 0;
	private int id = count++;
	
	public MouseManager(int averageNum, float magnification, int offset, int direction,
			WindowManager w, Context context, int delay, int selectPosition, int unit, int back, int wait){
		
		System.out.println("id =======================================================================" +id);
		windowManager = w;
		params = new LayoutParams();
		InputStream is = context.getResources().openRawResource(R.drawable.cursor);
		Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(is), 48, 46, true);
		imageView = new ImageView(context);
		imageView.setImageBitmap(bitmap);
		
        params.type = LayoutParams.TYPE_SYSTEM_ALERT;
        params.format = PixelFormat.RGBA_8888;
        params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE 
        				| LayoutParams.FLAG_NOT_TOUCHABLE;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.alpha = 80;
        params.gravity = Gravity.LEFT|Gravity.TOP;
        screenWidth = windowManager.getDefaultDisplay().getWidth();
        screenHeight = windowManager.getDefaultDisplay().getHeight();
        System.out.println("1111 width = " +screenWidth+ ", height = " +screenHeight);
        params.x =  screenWidth/ 2;
        params.y = screenHeight / 2;
        computer = new AverageComputerForService(averageNum, magnification, offset, direction);
        sensorManager = new MSensorManagerForService(context, delay, selectPosition);
        timeUnit = unit;
        backtime = back;
        timewait = wait;
	}

	public void startMouse(){
		if (!alreadyAdded){
			alreadyAdded = true;
			windowManager.addView(imageView, params);
		}
		listener = sensorManager.register(this);
	}
	
	public void stopMouse(){
		if (alreadyAdded){
			alreadyAdded = false;
			windowManager.removeView(imageView);
		}
		sensorManager.unRegister(listener);
	}
	
	public static void setPortrait(boolean p) {
		System.out.println("will setportrait = " +p);
		isPortrait = p;
	}

	int i = 0;
	public void sensorCallback(float x, float y, float z) {
		if (updateSwitch && alreadyAdded){
			int[] position = computer.computeValue(x, y);
			if (position == null){
				
			} else {
				if (i++ % 100 == 0)
					System.out.println("id = " +id+ ", params.x = " +params.x+ ", params.y = " +params.y+ ", x = " +position[0]+ ", y = " +position[1]+ ", isPortrait = " +isPortrait);
				if (isPortrait)
					updateMousePosition(params.x + position[1], params.y + position[0]);
				else
					updateMousePosition(params.x + position[0], params.y + position[1]);
			}
		}
	}
	
	public void lockMouseAWhile(int[] position, boolean wait){
		updateMousePosition(position[0], position[1]);
		if (wait){
			updateSwitch = false;
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					updateSwitch = true;
				}
			}, timewait);
		}
	}
	
	public void updateMousePosition(int x, int y){
		params.x = x;
		params.y = y;
		if (params.x < 0){
			params.x = 0;
		} else if (params.x > screenWidth){
			params.x = screenWidth;
		} else if (params.y < 0){
			params.y = 0;
		} else if (params.y > screenHeight){
			params.y = screenHeight;
		}
		windowManager.updateViewLayout(imageView, params);
	}

	public void getPositon(int[] array){
		array[0] = params.x;
		array[1] = params.y + 30;
	}
	
	public int getScreenHeight() {
		return screenHeight;
	}
	public int getScreenWidth() {
		return screenWidth;
	}
	public int getTimeUnit(){
		return timeUnit;
	}
	public int getBackTime(){
		return backtime;
	}
}
