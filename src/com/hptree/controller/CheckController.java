package com.hptree.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hptree.utils.PythonUtil;

@Controller
public class CheckController{
	@RequestMapping(value="start_hadoops", method = RequestMethod.GET)
	@ResponseBody
	public String start_hadoops(Model map){
		if (!new PythonUtil().run_hadoops()) {
			return "error"; // Hadoops starting fail, please contact administrator!
		}
		try {
		    // Update Timer File
			BufferedWriter bWriter = new BufferedWriter(new FileWriter("timer.txt"));
			bWriter.write("running");
			bWriter.write("\n");
			bWriter.write(Long.toString(System.currentTimeMillis()));
			bWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "success";
	}
	
	@RequestMapping(value="check_status", method = RequestMethod.GET)
	@ResponseBody
	public String check_status(Model map){
		try {
			BufferedReader br = new BufferedReader(new FileReader("timer.txt"));
			String status = br.readLine();
			br.close();
			if (status.equals("running")) {
				return "running";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			BufferedWriter bWriter = new BufferedWriter(new FileWriter("timer.txt"));
			bWriter.write("stopped");
			bWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "stopped";
	}


}
