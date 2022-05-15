package main;

import java.awt.FileDialog;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class DataWriter {
	BufferedWriter bw;
	public String fname;
	
	public DataWriter(){
		String path = "data."+MHelp.round(Filament_3DApp.time,1)+".dat";	
		constructor(path);
	}
	public DataWriter(String this_fname){
		constructor(this_fname);
	}
	public void constructor(String this_fname) {
		fname = this_fname;
		try {
			bw = new BufferedWriter(new FileWriter(fname, false));
		} catch (Exception e) {
			e.printStackTrace();
			bw = null;
		}
	}
	public void write_data(String value) {
		String line = value;
		try {
			bw.write(line);
			bw.newLine();
			bw.flush();
		} catch (Exception e) {
			e.printStackTrace();

		}
	}
	
	public void close(){
		try{
			bw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
