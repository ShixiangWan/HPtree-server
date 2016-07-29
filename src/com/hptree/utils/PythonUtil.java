package com.hptree.utils;


import java.io.BufferedReader;
import java.io.InputStreamReader;

// Note: Python scripts path of Linux/Windows system !!!
public class PythonUtil {
	public static void main(String[] args) {
		new PythonUtil().run_hadoops();
	}
	
	@SuppressWarnings("static-access")
	public boolean run_hadoops() {
		// stopped --> running
		String status = "";
		long startMili = System.currentTimeMillis();
		while (true) {
			status = new PythonUtil().SDK("start_hadoops");
			if (status.equals("running") || (System.currentTimeMillis()-startMili >= 60000)) break;
			else {
				try{
					Thread.currentThread().sleep(5*1000);
				}
				catch(InterruptedException e) {
					return false;
				}
			}
		}
		return true;
	}
	
	@SuppressWarnings("static-access")
	public boolean stop_hadoops() {
		// stopped --> running
		String status = "";
		long startMili = System.currentTimeMillis();
		while (true) {
			status = new PythonUtil().SDK("stop_hadoops");
			if (status.equals("stopped") || (System.currentTimeMillis()-startMili >= 60000)) break;
			else {
				try{
					Thread.currentThread().sleep(5*1000);
				}
				catch(InterruptedException e) {
					return false;
				}
			}
		}
		return true;
	}
	
	public String SDK(String api) {
		String status = "";
		try {
			Process process = Runtime.getRuntime().exec("python /home/ubuntu/tomcat/apache-tomcat-7.0.68/webapps/HPtree/python/" + api + ".py");
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = "";
			while ((line = br.readLine()) != null) {
                System.out.println("Log: " + line);
                if (line.contains("running") || line.contains("be started")) {
                	status = "running";
                	break;
                }
                if (line.contains("stopped") || line.contains("is not active")) {
                	status = "stopped";
                	break;
                }
            }
            br.close();
            process.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}
	
}
