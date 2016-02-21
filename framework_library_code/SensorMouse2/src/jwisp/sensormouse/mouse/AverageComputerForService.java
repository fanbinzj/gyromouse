package jwisp.sensormouse.mouse;

import android.widget.TextView;

public class AverageComputerForService {

	private int averageNum;
	private float magnification;
	private int offset;
	private int direction;
	private float[][] averageArray;
	private int[] values;
	
	
	
	public AverageComputerForService(int a, float m, int o, int d) {
		averageNum = a;
		magnification = m;
		offset = o;
		direction = d;
		averageArray = new float[averageNum][3];
		values = new int[3];
	}

	private int count1 = 0;
	public int[] computeValue(float x, float y){
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

}
