package jwisp.sensormouse.button;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jwisp.sensormouse.activity.ControllerService;
import jwisp.sensormouse.activity.MainActivity;

import android.content.Context;

public class DataLogHelper {
	
	public static String FLAG_MOUSE_TRACE = "";
	public static String FLAG_CLICK_POSITION = "200";
	public static String FLAG_BACK_POSITION = "400";
    private static String LOG_FILENAME = "mousetrace.txt";

    private FileOutputStream outStream;
    private SimpleDateFormat timeFormat;
    private boolean open = false;

    private static DataLogHelper instance;
    
    private DataLogHelper() {}
    public static DataLogHelper getInstance() {
    	if (instance == null)
    		instance = new DataLogHelper();
		return instance;
	}
    
    private void open(){
    	if (!open){
    		open = true;
        	timeFormat = new SimpleDateFormat("HH:mm:ss:SSSS");
        	try {
				outStream = ControllerService.getInstance().openFileOutput(LOG_FILENAME, Context.MODE_PRIVATE);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
    	}
    }
    
    public void close() {
    	if (open){
    		open = false;
    		try {
				outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
	}
    
    public void writeALine(int[] p, String flag){
    	if (!open)
    		open();
        Date now = new Date();
        String line = timeFormat.format(now)+ "\t" +p[0]+ "\t" +p[1]+ "\t" +flag+ "\n";
        try {
			outStream.write(line.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void writeALine(int[] p, String flag, int[] p2, String flag2){
    	if (!open)
    		open();
        Date now = new Date();
        String line = timeFormat.format(now)+ "\t" +p[0]+ "\t" +p[1]+ "\t" +flag+
        		"\t" +flag2+ "\t" +p2[0]+ "\t" +p2[1]+ "\n";
        try {
			outStream.write(line.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void delLogFile() {
//        if (file.exists()) {
//            file.delete();
//        }
    }
}
