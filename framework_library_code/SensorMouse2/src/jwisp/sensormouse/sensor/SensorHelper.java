package jwisp.sensormouse.sensor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jwisp.sensormouse.activity.MainActivity;

import android.hardware.Sensor;

public class SensorHelper {
	
	public final static String[] SENSORTYPE = {
		"ACCELEROMETER",
		"MAGNETIC_FIELD",
		"ORIENTATION",
		"GYROSCOPE",
		"LIGHT",
		"PRESSURE",
		"TEMPERATURE",
		"PROXIMITY",
		"GRAVITY",
		"LINEAR_ACCELERATION",
		"ROTATION_VECTOR",
		"RELATIVE_HUMIDITY",
		"AMBIENT_TEMPERATURE"
	};
	public final static String[] ITEMNAME = {
		"Name",
		"Type",
		"Maximum Range",
		"Min Delay",
		"Vendor",
		"Power",
		"Resolution",
		"Version",
		"Class"
	};
	public final static String[] MAPKEY = {
		"name",
		"value"
	};
	
	private SensorHelper(){}
	private static SensorHelper instance;
	public static SensorHelper getInstance() {
		if (instance == null)
			instance = new SensorHelper();
		return instance;
	}
	
	private Sensor sensor;
	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}
	public Sensor getSensor() {
		return sensor;
	}
	
	public List<Map<String, String>> getDetailListData(Sensor s){

		String[] valueArray = new String[ITEMNAME.length];
		valueArray[0] = s.getName();
		valueArray[1] = SENSORTYPE[s.getType()-1];
		valueArray[2] = s.getMaximumRange() +"";
		valueArray[3] = s.getMinDelay() +"";
		valueArray[4] = s.getVendor();
		valueArray[5] = s.getPower() +"";
		valueArray[6] = s.getResolution() +"";
		valueArray[7] = s.getVersion() +"";
		valueArray[8] = s.getClass().toString();
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		for (int i=0; i<ITEMNAME.length; i++) {
			Map<String, String> map = new HashMap<String, String>();
			map.put(MAPKEY[0], ITEMNAME[i]);
			map.put(MAPKEY[1], valueArray[i]);
			list.add(map);
		}
		return list;
	}
	
	public static final String[] SENSORDELAYNAMES = {
		"SENSOR_DELAY_FASTEST",
		"SENSOR_DELAY_GAME",
		"SENSOR_DELAY_UI",
		"SENSOR_DELAY_NORMAL",
	};
	
	public String[] getAllSensorName(){
		List<Sensor> list = MainActivity.getSensorList();
		int len = list.size();
		String[] names = new String[len];
		for (int i=0; i<len; i++) {
			names[i] = list.get(i).getName();
		}
		return names;
	}
}
