package jwisp.sensormouse.button;

import jwisp.sensormouse.activity.MainActivity;
import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import jwisp.sensormouse.activity.R;
import jwisp.sensormouse.mouse.MouseManager;

public class ButtonManager {
	
	private ImageButton button;
	private WindowManager windowManager;
	private LayoutParams params;
	private boolean alreadyAdded;
	private EventQueueHandler eventHandler;
	private Context context;
	private ButtonEventListener buttonEventListener;
	
	public ButtonManager(WindowManager w, Context c){

		context = c;
		windowManager = w;
		params = new LayoutParams();
		button = new ImageButton(context);
//		button.setHeight(30);
//		button.setText("LEFT");
		button.setBackgroundResource(R.drawable.button);
		button.setEnabled(true);
		
        params.type = LayoutParams.TYPE_PHONE;
//        params.type = LayoutParams.TYPE_STATUS_BAR;
        params.format = PixelFormat.RGBA_8888;
        params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE;
        params.height = 200;
        params.width = 200;
        params.gravity = Gravity.LEFT|Gravity.TOP;
        params.x = windowManager.getDefaultDisplay().getWidth() - 30;
        params.y = 0;
	}
	
	public void startButton(MouseManager mouse){
		if (!alreadyAdded){
			alreadyAdded = true;
			windowManager.addView(button, params);
			
	        eventHandler = new EventQueueHandler(this);
	        eventHandler.start();
		}
		buttonEventListener = new ButtonEventListener(context, mouse, eventHandler, button);
        button.setOnTouchListener(buttonEventListener);
	}
	
	public void stopButton(){
		if (alreadyAdded){
			alreadyAdded = false;
			windowManager.removeView(button);
			
			eventHandler.stopThread();
		}
	}
	
	public Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			if (msg.what == EventQueueHandler.SENDB2LEFT){
				buttonEventListener.setRight(false);
		        params.gravity = Gravity.RIGHT|Gravity.TOP;
		        windowManager.updateViewLayout(button, params);
			} else if (msg.what == EventQueueHandler.SENDB2RIGHT){
				buttonEventListener.setRight(true);
		        params.gravity = Gravity.LEFT|Gravity.TOP;
		        windowManager.updateViewLayout(button, params);
			}
		};
	};
}
