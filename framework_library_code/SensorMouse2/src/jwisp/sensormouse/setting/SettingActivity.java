package jwisp.sensormouse.setting;

import jwisp.sensormouse.activity.ControllerService;
import jwisp.sensormouse.activity.MainActivity;
import jwisp.sensormouse.activity.R;
import jwisp.sensormouse.mouse.AverageComputer;
import jwisp.sensormouse.mouse.MouseManager;
import jwisp.sensormouse.sensor.ISensorCallback;
import jwisp.sensormouse.sensor.MSensorManager;
import jwisp.sensormouse.sensor.SensorHelper;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.TextView;

public class SettingActivity extends Activity implements OnSharedPreferenceChangeListener, ISensorCallback {
	
	private SensorHelper sensorHelper;
	private TextView origiText;
	private TextView filterText;
	private Preference[] prefsArray;
	private PrefsEnum[] prefsEnum;
	private int len;
	private ListPreference sensorTypePrefs;
	private ListPreference sensorDepayPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_layout);
		
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(android.R.id.content, mPreferenceFragment);
		ft.commit();
		
		origiText = (TextView) findViewById(R.id.origi_data_text);
		filterText = (TextView) findViewById(R.id.filter_data_text);
		
		sensorHelper = SensorHelper.getInstance();
		MainActivity.getSharedPrefs().registerOnSharedPreferenceChangeListener(this);
		computer = new AverageComputer();
		stopService(new Intent(this, ControllerService.class));
	}
	
	private PreferenceFragment mPreferenceFragment = new PreferenceFragment() {
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.setting_preference);
			
			prefsEnum = PrefsEnum.values();
			len = prefsEnum.length;
			prefsArray = new Preference[len];
			for (int i=0; i<len; i++) {
				prefsArray[i] = findPreference(prefsEnum[i].getKey());
				if (prefsEnum[i] != PrefsEnum.DIRECTION){
					prefsArray[i].setSummary(prefsEnum[i].getSummary());
				}
			}
			sensorTypePrefs = (ListPreference) prefsArray[PrefsEnum.SELECTED_SENSOR.ordinal()];
			sensorTypePrefs.setEntries(sensorHelper.getAllSensorName());
			sensorTypePrefs.setEntryValues(getIndexString(sensorHelper.getAllSensorName().length));
			sensorTypePrefs.setSummary(PrefsEnum.SELECTED_SENSOR.getSelectedSensor().getName());
			sensorTypePrefs.setDefaultValue("3");
			sensorDepayPrefs = (ListPreference) prefsArray[PrefsEnum.SENSOR_DELAY.ordinal()];
			sensorDepayPrefs.setEntries(SensorHelper.SENSORDELAYNAMES);
			sensorDepayPrefs.setEntryValues(getIndexString(SensorHelper.SENSORDELAYNAMES.length));
			sensorDepayPrefs.setSummary(SensorHelper.SENSORDELAYNAMES[PrefsEnum.SENSOR_DELAY.getSensorDelay()]);
			sensorDepayPrefs.setDefaultValue("3");
		}
	};
	
	private String[] getIndexString(int len){
		String[] indexs = new String[len];
		for (int i=0; i<len; i++) {
			indexs[i] = "" +i;
		}
		return indexs;
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		
		if (key.equals(PrefsEnum.AVERAGENUM.getKey())) {
			prefsArray[0].setSummary(PrefsEnum.AVERAGENUM.getSummary());
		} else if (key.equals(PrefsEnum.MAGNIFICATION.getKey())){
			prefsArray[1].setSummary(PrefsEnum.MAGNIFICATION.getSummary());
		} else if (key.equals(PrefsEnum.OFFSET.getKey())){
			prefsArray[2].setSummary(PrefsEnum.OFFSET.getSummary());
		} else if (key.equals(PrefsEnum.DIRECTION.getKey())){
		} else if (key.equals(PrefsEnum.THRESHOLD.getKey())){
			prefsArray[4].setSummary(PrefsEnum.THRESHOLD.getSummary());
		} else if (key.equals(PrefsEnum.SELECTED_SENSOR.getKey())){
			sensorTypePrefs.setSummary(PrefsEnum.SELECTED_SENSOR.getSelectedSensor().getName());
		} else if (key.equals(PrefsEnum.SENSOR_DELAY.getKey())){
			sensorDepayPrefs.setSummary(SensorHelper.SENSORDELAYNAMES[PrefsEnum.SENSOR_DELAY.getSensorDelay()]);
		}
	}
	
	private SensorEventListener listener;
	private AverageComputer computer;
	@Override
	protected void onResume() {
		super.onResume();
		Sensor sensor = PrefsEnum.getSelectedSensor();
		listener = MSensorManager.getInstance().register(this, sensor);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		MSensorManager.getInstance().unRegister(listener);
	}

	public void sensorCallback(float x, float y, float z) {
		String content = x+ "\n" +y+ "\n" +z;
		origiText.setText(content);
		computer.computeValue(x, y, z, filterText);
	}
}