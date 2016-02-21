package jwisp.sensormouse.button;

import jwisp.sensormouse.activity.ControllerService;
import jwisp.sensormouse.activity.MainActivity;
import jwisp.sensormouse.activity.R;
import jwisp.sensormouse.mouse.MouseManager;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;

public class ButtonSettingActivity extends Activity implements OnSharedPreferenceChangeListener {
	
	private static final String BACKTIMEKEY = "back_time_edit";
	private static final String TIMEUNITKEY = "time_unit_edit";
	private static final String TIMEWAITKEY = "time_wait_edit";
	private EditTextPreference backTimePrefs;
	private EditTextPreference timeUnitPrefs;
	private EditTextPreference timeWaitPrefs;
	private static SharedPreferences sharedPrefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		sharedPrefs = MainActivity.getSharedPrefs();
		sharedPrefs.registerOnSharedPreferenceChangeListener(this);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(android.R.id.content, mPreferenceFragment);
		ft.commit();
		
		stopService(new Intent(this, ControllerService.class));
	}
	
	private PreferenceFragment mPreferenceFragment = new PreferenceFragment() {
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.button_setting_preference);
			
			backTimePrefs = (EditTextPreference) findPreference(BACKTIMEKEY);
			timeUnitPrefs = (EditTextPreference) findPreference(TIMEUNITKEY);
			timeWaitPrefs = (EditTextPreference) findPreference(TIMEWAITKEY);
			backTimePrefs.setSummary(getBackTime()+ "ms");
			timeUnitPrefs.setSummary(getTimeUnit()+ "/unit");
			timeWaitPrefs.setSummary(getTimeUnit()+ "/unit");
		}
	};
	
	public static int getBackTime(){
		sharedPrefs = MainActivity.getSharedPrefs();
		return Integer.parseInt(sharedPrefs.getString(BACKTIMEKEY, null));
	}
	public static int getTimeUnit(){
		sharedPrefs = MainActivity.getSharedPrefs();
//		return Integer.parseInt(sharedPrefs.getString(TIMEUNITKEY, null));
		return Integer.parseInt(sharedPrefs.getString(TIMEUNITKEY, "10"));
	}
	public static int getTimeWait(){
		sharedPrefs = MainActivity.getSharedPrefs();
		return Integer.parseInt(sharedPrefs.getString(TIMEWAITKEY, null));
	}
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals(BACKTIMEKEY)){
			backTimePrefs.setSummary(getBackTime()+ "");
		} else if (key.equals(TIMEUNITKEY)){
			backTimePrefs.setSummary(getTimeUnit()+ "");
		}
	}
}
