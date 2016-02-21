package jwisp.sensormouse.activity;

import android.app.Application;
import android.content.Context;

public class MainApplication extends Application {
	
	private static Context context;
	
	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();
		System.out.println("Application onCreate!");
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
	}
	
	public static Context getContext(){
		return context;
	}
}
