package jwisp.sensormouse.sensor;

import jwisp.sensormouse.activity.MainActivity;
import jwisp.sensormouse.setting.PrefsEnum;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MSensorManager {
	
	private SensorManager sensorManager;
	private static MSensorManager instance;
	
	private MSensorManager(){
		sensorManager = MainActivity.getSensorManager();
	}
	
	public static MSensorManager getInstance(){
		if (instance == null)
			instance = new MSensorManager();
		return instance;
	}
	
	public SensorEventListener register(ISensorCallback c){
		SensorEventListener listener = new MSensorEventListener(c);
		sensorManager.registerListener(listener, PrefsEnum.getSelectedSensor(), PrefsEnum.getSensorDelay());
		return listener;
	}
	
	public SensorEventListener register(ISensorCallback c, Sensor sensor){
		SensorEventListener listener = new MSensorEventListener(c);
		sensorManager.registerListener(listener, sensor, PrefsEnum.getSensorDelay());
		return listener;
	}
	
	public void unRegister(SensorEventListener listener){
		sensorManager.unregisterListener(listener);
	}
	
	class MSensorEventListener implements SensorEventListener {
		private ISensorCallback callback;
		public MSensorEventListener (ISensorCallback c){
			callback = c;
		}
		public void onSensorChanged(SensorEvent event) {
			callback.sensorCallback(event.values[0], event.values[1], event.values[2]);
		}
		public void onAccuracyChanged(Sensor sensor, int accuracy) {}
	}
}
