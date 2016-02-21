package jwisp.sensormouse.sensor;

import jwisp.sensormouse.activity.R;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class SensorDetailActivity extends Activity implements ISensorCallback {
	
	private MSensorManager sensorManager;
	private SensorEventListener listener;
	private Sensor sensor;
	private TextView valueText;
	public final static int[] LAYOUTID = {
		R.id.item_name_text,
		R.id.item_content_text,
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sensor_detail_layout);
		
		valueText = (TextView) findViewById(R.id.detail_value_text);
		SensorHelper sensorHelper = SensorHelper.getInstance();
		sensor = sensorHelper.getSensor();
		
		ListView lv = (ListView) findViewById(R.id.sensor_detail_listview);
		SimpleAdapter adapter = new SimpleAdapter(this, sensorHelper.getDetailListData(sensor), 
									R.layout.detail_item_layout, SensorHelper.MAPKEY, LAYOUTID);
		lv.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (sensorManager == null)
			sensorManager = MSensorManager.getInstance();
		listener = sensorManager.register(this, sensor);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		sensorManager.unRegister(listener);
	}

	public void sensorCallback(float x, float y, float z) {
		String value = x+ "\n" +y+ "\n" +z;
		valueText.setText(value);
	}
}
