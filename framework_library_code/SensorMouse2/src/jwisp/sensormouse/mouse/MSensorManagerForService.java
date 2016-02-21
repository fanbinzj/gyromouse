package jwisp.sensormouse.mouse;

import jwisp.sensormouse.sensor.ISensorCallback;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MSensorManagerForService {
	
	private SensorManager sensorManager;
	private int sensorDelay;
	private Sensor selectSensor;
	
	public MSensorManagerForService(Context context, int delay, int selectPosition){
    	sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    	sensorDelay = delay;
    	selectSensor = sensorManager.getSensorList(Sensor.TYPE_ALL).get(selectPosition);
    	
	}
	
	public SensorEventListener register(ISensorCallback c){
		SensorEventListener listener = new MSensorEventListener(c);
		sensorManager.registerListener(listener, selectSensor, sensorDelay);
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
