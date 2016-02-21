package jwisp.sensormouse.activity;

import jwisp.sensormouse.button.ButtonManager;
import jwisp.sensormouse.mouse.MouseManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

public class ControllerService extends Service {
	
	private WindowManager windowManager;
	private MouseManager mouseManager;
	private ButtonManager buttonManager;
	private static Service instance;
	
	private static int count = 0;
	private int id = count++;
	private static int count2 = 0;
	private MyClass my;
	class MyClass {
		private int mId = count2++;
		public MyClass(){
			System.out.println("MYClass id = " +mId);
		}
	}
	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("ControllerService onCreate() id = " +id);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("ControllerService onStartCommand() id = " +id);
		int averageNum = intent.getIntExtra("averageNum", 10);
		float magnification = intent.getFloatExtra("magnification", 1.0f);
		int offset = intent.getIntExtra("offset", 1);
		int direction = intent.getIntExtra("direction", 1);
		int sensorDelay = intent.getIntExtra("sensorDelay", 100);
		int selectPosition = intent.getIntExtra("selectPosition", 0);
		int timeunit = intent.getIntExtra("timeunit", 1);
		int backtime = intent.getIntExtra("backtime", 1000);
		int timewait = intent.getIntExtra("timewait", 1000);
		windowManager = (WindowManager) getApplicationContext().getSystemService("window");
		if (my == null)
			my = new MyClass();
		if (mouseManager == null)
			mouseManager = new MouseManager(averageNum, magnification, offset, direction, windowManager, this, 
				sensorDelay, selectPosition, timeunit, backtime, timewait);
		mouseManager.startMouse();
		buttonManager = new ButtonManager(windowManager, this);
		buttonManager.startButton(mouseManager);
		instance = this;
		return START_STICKY;
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
			MouseManager.setPortrait(false);
		} else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
			MouseManager.setPortrait(true);
		}
	}
	
	@Override
	public void onDestroy() {
		Log.e("ControllerService", "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@Mouse service be stopped!!!!!!!!!!!!!!!!!!!!!!");
		Toast.makeText(this, "Mouse service be stopped!", Toast.LENGTH_SHORT).show();
		
		mouseManager.stopMouse();
		buttonManager.stopButton();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	public static Service getInstance() {
		return instance;
	}
	
}
