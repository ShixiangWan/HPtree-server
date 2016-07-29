package com.hptree.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.TimerTask;

// Note: Stop Hadoops and Instances after 30 mins if no operation.
public class TimerUtil extends TimerTask{
	@SuppressWarnings("resource")
	@Override
	public void run() {
		try {
			BufferedReader br = new BufferedReader(new FileReader("timer.txt"));
			String status = br.readLine();
			if (status.equals("stopped")) {
				return;
			}
			long timer = Long.parseLong(br.readLine());
			if (System.currentTimeMillis()-timer > 20*60*1000) {
				new PythonUtil().stop_hadoops();
				BufferedWriter bw = new BufferedWriter(new FileWriter("timer.txt"));
				bw.write("stopped");
				bw.close();
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
