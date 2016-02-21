package jwisp.sensormouse.mouse;

import jwisp.sensormouse.button.ButtonSettingActivity;
import jwisp.sensormouse.button.DataLogHelper;

public class TimeBackCompute extends Thread {
	
	private int length;
	private int[][] positionArray;
	private int sleepTime;
	private boolean goon;
	private int pointer;
	private DataLogHelper logHelper;
	private MouseManager mouseManager;
	
	public TimeBackCompute(MouseManager m){
		mouseManager = m;
		length = m.getTimeUnit() * 2;
		positionArray = new int[length][2];
		goon = true;
		pointer = 0;
		logHelper = DataLogHelper.getInstance();
		onTimeUnitChanged();
	}
	
	@Override
	public void run() {
		while (goon){
			for (int i=0; i<length; i++) {
				mouseManager.getPositon(positionArray[pointer]);
				logHelper.writeALine(positionArray[pointer], DataLogHelper.FLAG_MOUSE_TRACE);
				pointer++;
				if (pointer == length)
					pointer = 0;
				try {
//					System.out.println("sleep time : " +sleepTime);
					sleep(sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
		logHelper.close();
	}
	
	public void getTimebackPosition(int[] backPosition){
		int[] currentPosition = new int[2];
		int index = pointer + (length/2);
		if (index >= length)
			index = index - length;
		currentPosition[0] = positionArray[pointer][0];
		currentPosition[1] = positionArray[pointer][1];
		backPosition[0] = positionArray[index][0];
		backPosition[1] = positionArray[index][1];
		logHelper.writeALine(currentPosition, DataLogHelper.FLAG_CLICK_POSITION, 
				backPosition, DataLogHelper.FLAG_BACK_POSITION);
	}
	
	public void onTimeUnitChanged() {
		pointer = 0;
		length = mouseManager.getTimeUnit() * 2;
		sleepTime = (int) (mouseManager.getBackTime() / (length/2));
		positionArray = new int[length][2];
	}
	public void stopTimeBackCompute() {
		goon = false;
	}
}
