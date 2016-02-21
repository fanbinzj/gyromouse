package jwisp.sensormouse.setting;

import jwisp.sensormouse.activity.MainApplication;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;

public enum PrefsEnum {
	AVERAGENUM,
	MAGNIFICATION,
	OFFSET,
	DIRECTION,
	THRESHOLD,
	SELECTED_SENSOR,
	SENSOR_DELAY;
	
	private static SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainApplication.getContext());
	
	public int getId() {
		return ordinal();
	}
	public String getKey(){
		return KEY[ordinal()];
	}
	public String getSummary(){
		return sharedPrefs.getString(KEY[ordinal()], null);
	}
	public static int getAverageNum(){
		System.out.println("1 sharedPrefs = " +sharedPrefs);
		String result = sharedPrefs.getString(KEY[0], null);
		System.out.println("sharedPrefs = " +sharedPrefs+ ", result = " +result);
		return Integer.parseInt(result);
	}
	public static float getMagnification(){
		return Float.parseFloat(sharedPrefs.getString(KEY[1], null));
	}
	public static int getOffset(){
		return Integer.parseInt(sharedPrefs.getString(KEY[2], null));
	}
	public static int getDirection(){
		return sharedPrefs.getBoolean(KEY[3], true)? 1 : -1;
	}
	public static float getThreshold(){
		return Float.parseFloat(sharedPrefs.getString(KEY[4], null));
	}
	public static int getSelectPosition(){
		return Integer.parseInt(sharedPrefs.getString(KEY[5], null));
	}
	public static Sensor getSelectedSensor(){
		int position = Integer.parseInt(sharedPrefs.getString(KEY[5], null));
		SensorManager sm = (SensorManager) MainApplication.getContext().getSystemService(Context.SENSOR_SERVICE);
    	return sm.getSensorList(Sensor.TYPE_ALL).get(position);
	}
	public static int getSensorDelay(){
		return Integer.parseInt(sharedPrefs.getString(KEY[6], null));
	}
	public void setSelectedSensor(int type){
		Editor editor = sharedPrefs.edit();
		editor.putString(KEY[5], type+"");
		editor.commit();
	}
	
	public static final String KEY[] = {
		"averagenum_edit",
		"magnification_edit",
		"offset_edit",
		"direction_switch",
		"threshold_edit",
		"sensor_type_list",
		"sensor_rate_list",
	};
}
