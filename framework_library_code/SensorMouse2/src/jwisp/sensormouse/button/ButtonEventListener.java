package jwisp.sensormouse.button;

import jwisp.sensormouse.activity.MainActivity;
import jwisp.sensormouse.activity.R;
import jwisp.sensormouse.mouse.MouseManager;
import jwisp.sensormouse.mouse.TimeBackCompute;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class ButtonEventListener implements OnTouchListener {
	
	private static final int STATE_INIT = -1;
	private static final int STATE_DOWN = 0;
	private static final int STATE_UP = 1;
	private static final int STATE_DOWNANDUP = 2;
	private static final int STATE_DOWNED = 3;
	private static final int STATE_CANCEL = 4;
	private static final int STATE_MODE = 10;
	private static final int STATE_MODE_KEY = 11;
	private static final int STATE_MODE_PHOTO = 12;
	private static final int STATE_MODE_B2_LEFT = 13;
	private static final int STATE_MODE_B2_RIGHT = 14;
	private static final int BUTTON_MOVE_OFFSET = 8;
	private static final int MODE_STATE_START = 0;
	private static final int MODE_STATE_MOVE = 1;
	private static final int MODE_SCAN_DELAY = 50;
	private static final int MODE_COUNTER_THRESHOLD = 40;
	private static final int MODE_MOUSEOFFSET_THRESHOLD = 60;

	private Context context;
	private ImageButton button;
	private EventQueueHandler eventHandler;
	private TimeBackCompute timebackCompute;
	private final String TOUCH_ACTION_UP = "jwisp.sensormouse.action.TOUCH_ACTION_UP";
	private MouseManager mouseManager;
	private Handler handler;
	private int eventSendState = STATE_INIT;
	private int modeFlag = STATE_INIT;
	private String[] toastStrArray;
	private boolean isRight;

	public ButtonEventListener(Context c, MouseManager m, EventQueueHandler eventHandler, ImageButton button2) {
		this.eventHandler = eventHandler;
		button = button2;
		
		IntentFilter filter = new IntentFilter(TOUCH_ACTION_UP);
		context = c;
		context.registerReceiver(receiver, filter);
		mouseManager = m;
		isRight = true;
		handler = new Handler();
		toastStrArray = context.getResources().getStringArray(R.array.state_mode_array);

		timebackCompute = new TimeBackCompute(m);
		timebackCompute.start();
	}
	
	public void setRight(boolean right) {
		isRight = right;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		timebackCompute.stopTimeBackCompute();
	}



	private float downX, downY, lengthX, lengthY;
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				downX = event.getX();
				downY = event.getY();
				lengthX = -1;
				lengthY = -1;
				button.setPressed(true);
				eventSendState = STATE_DOWN;
				modeFlag = STATE_INIT;
				handler.postDelayed(sendEventRunnable, 100);
				break;
			case MotionEvent.ACTION_MOVE:
				lengthX = event.getX() - downX;
				lengthY = event.getY() - downY;
				System.out.println("Button.onTouch() MOVE: downX = " +downX+ ", downY = " +downY+ ", lengthX = " +lengthX+ ", lengthY = " +lengthY);
				if (eventSendState==STATE_DOWN && (Math.abs(lengthX) > BUTTON_MOVE_OFFSET || Math.abs(lengthY) > BUTTON_MOVE_OFFSET)){
					System.out.println("Cancel sendEventRunnable !!!!!!! ");
					handler.removeCallbacks(sendEventRunnable);
					eventSendState = STATE_CANCEL;
				}
				if (eventSendState == STATE_CANCEL){
//					System.out.println(1 + "isRight = " +isRight+ ", lengthX-lengthY" +(lengthX-lengthY));
//					if (lengthX>40 && lengthY<-30){
					if ((isRight && lengthX-lengthY>30) || (!isRight && -lengthX-lengthY>30)){
						System.out.println(2);
						modeFlag = STATE_MODE_KEY;
						modeHandler.sendEmptyMessage(MODE_STATE_START);
					} else if (lengthY>50 && lengthY>(Math.abs(lengthX)*2)){
						modeFlag = STATE_MODE_PHOTO;
						modeHandler.sendEmptyMessage(MODE_STATE_START);
					} else if (Math.abs(lengthX)>(Math.abs(lengthY)*2)){
						if (lengthX > 30){
							modeFlag = STATE_MODE_B2_RIGHT;
							modeHandler.sendEmptyMessage(MODE_STATE_START);
						} else if (lengthX < -30 && Math.abs(lengthY)<Math.abs(lengthX)){
							modeFlag = STATE_MODE_B2_LEFT;
							modeHandler.sendEmptyMessage(MODE_STATE_START);
						}
					}
				}
				break;
			default:
				break;
		}
		return true;
	}
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(TOUCH_ACTION_UP) && eventSendState<STATE_MODE){
				if (eventSendState == STATE_DOWNED)
					eventSendState = STATE_UP;
				else if (eventSendState == STATE_DOWN)
					eventSendState = STATE_DOWNANDUP;
				button.setPressed(false);
			}
		}
	};
	
	private static final int SEND_DOWN_DELAY = 100;
	private static final int SEND_MOVE_DELAY = 20;
	int[] positionArray = new int[2];
	private Runnable sendEventRunnable = new Runnable() {
		final int offset = 10;
		private int downX, downY;
		@Override
		public void run() {
			System.out.println("SendEventRunnable -> run() : sendState = " +eventSendState);
			switch (eventSendState) {
				case STATE_DOWN:
					eventSendState = STATE_DOWNED;
					timebackCompute.getTimebackPosition(positionArray);
					downX = positionArray[0];
					downY = positionArray[1];
					eventHandler.pushMotionEvent(MotionEvent.ACTION_DOWN, downX, downY);
					mouseManager.lockMouseAWhile(new int[]{downX, downY-30}, true);
					handler.postDelayed(this, SEND_DOWN_DELAY);
					break;
				case STATE_UP:
					eventSendState = STATE_INIT;
					mouseManager.getPositon(positionArray);
					eventHandler.pushMotionEvent(MotionEvent.ACTION_UP, positionArray[0], positionArray[1]);
					break;
				case STATE_DOWNANDUP:
					eventSendState = STATE_INIT;
					mouseManager.getPositon(positionArray);
					eventHandler.pushMotionEvent(MotionEvent.ACTION_DOWN, positionArray[0], positionArray[1]);
					eventHandler.pushMotionEvent(MotionEvent.ACTION_UP, positionArray[0], positionArray[1]);
					break;
				case STATE_DOWNED:
					mouseManager.getPositon(positionArray);
					if (Math.abs(downX-positionArray[0])>offset || Math.abs(downY-positionArray[1])>offset)
						eventHandler.pushMotionEvent(MotionEvent.ACTION_MOVE, positionArray[0], positionArray[1]);
					handler.postDelayed(this, SEND_MOVE_DELAY);
					break;
				default:
					break;
			}
		}
	};
	
	private Handler modeHandler = new Handler(){
		private int modeStateCount = 0;
		private int startX, startY;
		private int eventCode = -1;
		public void handleMessage(Message msg) {
			int msgWhat = msg.what;
			eventSendState = STATE_MODE;
			System.out.println("****** MODE START ***** : get() : modeFlag = " +modeFlag+ ", sendState = " +eventSendState+ ", msgWhat = " +msgWhat);
			mouseManager.getPositon(positionArray);
			switch (modeFlag) {
				case STATE_MODE_KEY:
					if (msgWhat == MODE_STATE_START){
						modeStateCount = 0;
						startX = positionArray[0];
						startY = positionArray[1];
						Toast toast = Toast.makeText(context, toastStrArray[0], Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
						toast.show();
						modeHandler.sendEmptyMessageDelayed(MODE_STATE_MOVE, MODE_SCAN_DELAY);
					} else if (msgWhat == MODE_STATE_MOVE){
						if (++modeStateCount < MODE_COUNTER_THRESHOLD){
							int lengthX = positionArray[0] - startX;
							int lengthY = positionArray[1] - startY;
							if (lengthX > MODE_MOUSEOFFSET_THRESHOLD){
								eventHandler.pushMotionEvent(-KeyEvent.KEYCODE_HOME, 0, 0);
							} else if (lengthX < -MODE_MOUSEOFFSET_THRESHOLD){
								eventHandler.pushMotionEvent(-KeyEvent.KEYCODE_BACK, 0, 0);
							} else if (lengthY > MODE_MOUSEOFFSET_THRESHOLD){
								eventHandler.pushMotionEvent(-KeyEvent.KEYCODE_SEARCH, 0, 0);
							} else if (lengthY < -MODE_MOUSEOFFSET_THRESHOLD){
								eventHandler.pushMotionEvent(-KeyEvent.KEYCODE_MENU, 0, 0);
							} else{
								modeHandler.sendEmptyMessageDelayed(MODE_STATE_MOVE, MODE_SCAN_DELAY);
							}
						}
					}
					break;
				case STATE_MODE_PHOTO:
					if (msgWhat == MODE_STATE_START){
						modeStateCount = 0;
						startY = positionArray[1];
						Toast toast = Toast.makeText(context, toastStrArray[1], Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
						toast.show();
						modeHandler.sendEmptyMessageDelayed(MODE_STATE_MOVE, MODE_SCAN_DELAY);
					} else if (msgWhat == MODE_STATE_MOVE){
						if (++modeStateCount < MODE_COUNTER_THRESHOLD){
							int lengthY = positionArray[1] - startY;
							if (lengthY > MODE_MOUSEOFFSET_THRESHOLD){
								eventHandler.pushMotionEvent(EventQueueHandler.SENDPINCH, 0, 0);
							} else if (lengthY < -MODE_MOUSEOFFSET_THRESHOLD){
								eventHandler.pushMotionEvent(EventQueueHandler.SENDZOOM, 0, 0);
							} else{
								modeHandler.sendEmptyMessageDelayed(MODE_STATE_MOVE, MODE_SCAN_DELAY);
							}
						}
					}
					break;
				case STATE_MODE_B2_LEFT:
					eventHandler.pushMotionEvent(EventQueueHandler.SENDB2LEFT, 0, 0);
					break;
				case STATE_MODE_B2_RIGHT:
					eventHandler.pushMotionEvent(EventQueueHandler.SENDB2RIGHT, 0, 0);
					break;
				default:
					break;
			}
		}
	};
}


