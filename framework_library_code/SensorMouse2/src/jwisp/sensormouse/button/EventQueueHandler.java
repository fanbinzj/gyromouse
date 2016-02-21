package jwisp.sensormouse.button;

import java.util.LinkedList;
import java.util.Queue;

import jwisp.sensormouse.mouse.MouseManager;
import jwisp.sensormouse.mouse.TimeBackCompute;

import android.app.Instrumentation;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.MotionEvent.PointerProperties;

public class EventQueueHandler extends Thread {

	private ButtonManager buttonManager;
	private Queue<MotionEvent> queue;
	private Instrumentation inst;
	private volatile boolean goon;
	
	public static final int SENDHOMEKEY = -KeyEvent.KEYCODE_HOME;
	public static final int SENDBACKKEY = -KeyEvent.KEYCODE_BACK;
	public static final int SENDMENUKEY = -KeyEvent.KEYCODE_MENU;
	public static final int SENDSEARCHKEY = -KeyEvent.KEYCODE_SEARCH;
	public static final int SENDZOOM = -101;
	public static final int SENDPINCH = -102;
	public static final int SENDB2LEFT = -103;
	public static final int SENDB2RIGHT = -104;
	
	public EventQueueHandler(ButtonManager b){
		queue = new LinkedList<MotionEvent>();
		inst = new Instrumentation();
		goon = true;
		buttonManager = b;
	}
	
	public void pushMotionEvent(int action, int x, int y){
		long now = SystemClock.uptimeMillis();
		MotionEvent event = MotionEvent.obtain(now, now, action, x, y, 0);
		System.out.println("push a Event: (" +x+ ", " +y+ ") action = " +action);
		offer(event);
	}
	
	private void offer(MotionEvent event){
		queue.offer(event);
		synchronized (this) {
			notify();
		}
	}
	
	public void stopThread(){
		goon = false;
	}
	
	@Override
	public void run() {
		while (goon) {
			if (queue.size() == 0){
				synchronized (this) {
					try {
						wait();
					} catch (InterruptedException e) {
					}
				}
			}
			if (queue.size() != 0){
				MotionEvent event = queue.poll();
				int action = event.getAction();
				if (action >= 0){
					inst.sendPointerSync(event);
					System.out.println("poll Event: action = " +event.getAction()+ 
							", (" +event.getX()+ ", " +event.getY()+ "), down = "
							+event.getDownTime()+ ", eventTime = " +event.getEventTime());
				}
				else {
					switch (action) {
						case SENDZOOM:
							generateZoomGesture(1);
							break;
						case SENDPINCH:
							generateZoomGesture(-1);
							break;
						case SENDB2LEFT:
							buttonManager.handler.sendEmptyMessage(SENDB2LEFT);
							break;
						case SENDB2RIGHT:
							buttonManager.handler.sendEmptyMessage(SENDB2RIGHT);
							break;
	
						default:
							inst.sendKeyDownUpSync(-action);
							break;
					}
				}
			}
		}
	}
	
	
	
	
	
	
	public void generateZoomGesture(int direction) {

	    long downTime = SystemClock.uptimeMillis();
	    long eventTime = downTime;
	    MotionEvent event;
	    int sameX, downY1, downY2;

	    sameX = 150;
	    downY1 = 150 + direction*50;
	    downY2 = 300 - direction*50;

	    // specify the property for the two touch points
	    PointerProperties[] properties = new PointerProperties[2];
	    PointerProperties pp1 = new PointerProperties();
	    pp1.id = 0;
	    pp1.toolType = MotionEvent.TOOL_TYPE_FINGER;
	    PointerProperties pp2 = new PointerProperties();
	    pp2.id = 1;
	    pp2.toolType = MotionEvent.TOOL_TYPE_FINGER;

	    properties[0] = pp1;
	    properties[1] = pp2;

	    //specify the coordinations of the two touch points
	    //NOTE: you MUST set the pressure and size value, or it doesn't work
	    PointerCoords[] pointerCoords = new PointerCoords[2];
	    PointerCoords pc1 = new PointerCoords();
	    pc1.x = sameX;
	    pc1.y = downY1;
	    pc1.pressure = 1;
	    pc1.size = 1;
	    PointerCoords pc2 = new PointerCoords();
	    pc2.x = sameX;
	    pc2.y = downY2;
	    pc2.pressure = 1;
	    pc2.size = 1;
	    pointerCoords[0] = pc1;
	    pointerCoords[1] = pc2;

	    //////////////////////////////////////////////////////////////
	    // events sequence of zoom gesture
	    // 1. send ACTION_DOWN event of one start point
	    // 2. send ACTION_POINTER_2_DOWN of two start points
	    // 3. send ACTION_MOVE of two middle points
	    // 4. repeat step 3 with updated middle points (x,y),
	    //      until reach the end points
	    // 5. send ACTION_POINTER_2_UP of two end points
	    // 6. send ACTION_UP of one end point
	    //////////////////////////////////////////////////////////////

	    // step 1
	    event = MotionEvent.obtain(downTime, downTime, 
	                MotionEvent.ACTION_DOWN, 1, properties, 
	                pointerCoords, 0,  0, 1, 1, 0, 0, 0, 0 );

	    inst.sendPointerSync(event);

	    //step 2
	    event = MotionEvent.obtain(downTime, downTime, 
	                MotionEvent.ACTION_POINTER_2_DOWN, 2, 
	                properties, pointerCoords, 0, 0, 1, 1, 0, 0, 0, 0);

	    inst.sendPointerSync(event);

	    //step 3, 4
	    if (true) {
	        int stepY = 10;

	        for (int i = 0; i < 10; i++) {
	            // update the move events
	            eventTime += 10;
	            downY1 -= stepY*direction;
	            downY2 += stepY*direction;
	            pc1.x = sameX;
	            pc1.y = downY1;
	            pc2.x = sameX;
	            pc2.y = downY2;

	            pointerCoords[0] = pc1;
	            pointerCoords[1] = pc2;

	            event = MotionEvent.obtain(downTime, eventTime,
	                        MotionEvent.ACTION_MOVE, 2, properties, 
	                        pointerCoords, 0, 0, 1, 1, 0, 0, 0, 0);

	            inst.sendPointerSync(event);
	        }
	    }

	    //step 5
	    pc1.x = sameX;
	    pc1.y = downY1 + 10*direction;
	    pc2.x = sameX;
	    pc2.y = downY2 - 10*direction;
	    pointerCoords[0] = pc1;
	    pointerCoords[1] = pc2;

	    eventTime += 10;
	    event = MotionEvent.obtain(downTime, eventTime,
	                MotionEvent.ACTION_POINTER_2_UP, 2, properties, 
	                pointerCoords, 0, 0, 1, 1, 0, 0, 0, 0);
	    inst.sendPointerSync(event);

	    // step 6
	    event = MotionEvent.obtain(downTime, eventTime, 
	                MotionEvent.ACTION_UP, 1, properties, 
	                pointerCoords, 0, 0, 1, 1, 0, 0, 0, 0 );
	    inst.sendPointerSync(event);
	}
	
	
	
	
	
	
	
	
	
	
	
}
