package jwisp.sensormouse.sensor;

import java.util.List;

import jwisp.sensormouse.activity.MainActivity;

import android.app.ListActivity;
import android.content.Intent;
import android.hardware.Sensor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SensorListActivity extends ListActivity {
	
	private List<Sensor> sensorList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        sensorList = MainActivity.getSensorList();
        int len = sensorList.size();
        String[] sensorNames = new String[len];
        for (int i=0; i<len; i++) {
			sensorNames[i] = sensorList.get(i).getName();
		}
        
        setListAdapter(new ArrayAdapter<String>(SensorListActivity.this, 
        							android.R.layout.simple_list_item_1, sensorNames));
        
        ListView lv = getListView();
        lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				SensorHelper.getInstance().setSensor(sensorList.get(position));
				startActivity(new Intent(SensorListActivity.this, SensorDetailActivity.class));
			}
		});
    }
    
}
