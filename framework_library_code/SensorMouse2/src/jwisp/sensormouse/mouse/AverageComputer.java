package jwisp.sensormouse.mouse;

import jwisp.sensormouse.activity.MainActivity;
import jwisp.sensormouse.setting.PrefsEnum;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.widget.TextView;

public class AverageComputer implements OnSharedPreferenceChangeListener {

	private int averageNum;
	private float magnification;
	private int offset;
	private int direction;
	private float threshold;
	private float[][] averageArray;
	private int[] values;
	
	public AverageComputer(){
		averageNum = PrefsEnum.getAverageNum();
		magnification = PrefsEnum.getMagnification();
		offset = PrefsEnum.getOffset();
		direction = PrefsEnum.getDirection();
		threshold = PrefsEnum.getThreshold();
		averageArray = new float[averageNum][3];
		values = new int[3];
		MainActivity.getSharedPrefs().registerOnSharedPreferenceChangeListener(this);
	}
	
	private int count1 = 0;
	public int[] computeValue(float x, float y){
//		if (x>=1 || x<=-1 || y>=1 || y<=-1){
//			System.out.println("x = " +x+ ", y = " +y);
//		}
		averageArray[count1][0] = x * magnification;
		averageArray[count1][1] = y * magnification;
		if (++count1 == averageNum){
			count1 = 0;
			for (int i=0; i<2; i++) {
				values[i] = 0;
				for (int j=1; j<averageNum; j++) {
					averageArray[0][i] = averageArray[0][i] + averageArray[j][i];
				}
				values[i] = (int) (averageArray[0][i] * offset * direction);
			}
//			System.out.println("move offset(" +values[0]+ ", " +values[1]+ ")");
			return values;
		} else {
			return null;
		}
	}
	private int count2 = 0;
	public void computeValue(float x, float y, float z, TextView textView){
		averageArray[count2][0] = x * magnification;
		averageArray[count2][1] = y * magnification;
		averageArray[count2][2] = z * magnification;
		if (++count2 == averageNum){
			count2 = 0;
			for (int i=0; i<3; i++) {
				for (int j=1; j<averageNum; j++) {
					averageArray[0][i] = averageArray[0][i] + averageArray[j][i];
				}
				averageArray[0][i] = averageArray[0][i] * offset * direction;
			}
			textView.setText(averageArray[0][0] +"\n"+ averageArray[0][1] +"\n"+ averageArray[0][2]);
		}
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals(PrefsEnum.AVERAGENUM.getKey())){
			count1 = 0;
			count2 = 0;
			averageNum = PrefsEnum.getAverageNum();
			averageArray = new float[averageNum][3];
		} else if (key.equals(PrefsEnum.MAGNIFICATION.getKey())){
			magnification = PrefsEnum.getMagnification();
		} else if (key.equals(PrefsEnum.OFFSET.getKey())){
			offset = PrefsEnum.getOffset();
		} else if (key.equals(PrefsEnum.DIRECTION.getKey())){
			direction = PrefsEnum.getDirection();
		} else if (key.equals(PrefsEnum.THRESHOLD.getKey())){
			threshold = PrefsEnum.getThreshold();
		}
	}
}
