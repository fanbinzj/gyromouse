package jwisp.sensormouse.activity;

import java.util.List;

import jwisp.sensormouse.button.ButtonSettingActivity;
import jwisp.sensormouse.mouse.MouseManager;
import jwisp.sensormouse.mouse.MouseStartActivity;
import jwisp.sensormouse.sensor.SensorListActivity;
import jwisp.sensormouse.setting.PrefsEnum;
import jwisp.sensormouse.setting.SettingActivity;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends ListActivity {
	
	private static SensorManager sensorManager;
	private static List<Sensor> sensorList;
	private static SharedPreferences sharedPrefs;
	
	private final Class[] INTENTARRAY = {
			MouseStartActivity.class,
			SensorListActivity.class,
			SensorListActivity.class,
			SettingActivity.class,
			ButtonSettingActivity.class
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        String[] mainMenuArray = getResources().getStringArray(R.array.main_menu_array);
        ListView lv = getListView();
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mainMenuArray));
        lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View child, int position, long id) {
				if (position != 1)
					startActivity(new Intent(MainActivity.this, INTENTARRAY[position]));
				else
					stopService(new Intent(MainActivity.this, ControllerService.class));
			}
		});
        
        initAllData();
    }
    
    public void initAllData(){
    	sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    	sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
    	sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    	PreferenceManager.setDefaultValues(this, R.xml.setting_preference, false);
    	PreferenceManager.setDefaultValues(this, R.xml.button_setting_preference, true);
    	if (PrefsEnum.SELECTED_SENSOR.getSummary() == null){
    		int type = 0;
    		for (int i=0; i<sensorList.size(); i++) {
				if (sensorList.get(i).getType() == Sensor.TYPE_GYROSCOPE){
					type = i;
				}
			}
    		PrefsEnum.SELECTED_SENSOR.setSelectedSensor(type);
    	}
    }

	public static SensorManager getSensorManager() {
		return sensorManager;
	}
	public static List<Sensor> getSensorList() {
		return sensorList;
	}
	public static SharedPreferences getSharedPrefs() {
		return sharedPrefs;
	}
}