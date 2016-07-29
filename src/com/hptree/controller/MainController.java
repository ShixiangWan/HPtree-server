package com.hptree.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.Timer;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.hptree.utils.FileUtil;
import com.hptree.utils.MailUtil;
import com.hptree.utils.TimerUtil;

@Controller
public class MainController implements ServletContextAware{
		private ServletContext servletContext;
		@Autowired
		public void setServletContext(ServletContext context) {
			this.servletContext  = context;
		}
		@RequestMapping(value="predict", method = RequestMethod.POST)
		public ModelAndView Main(@RequestParam("email")String email, @RequestParam("file")CommonsMultipartFile file, Model map){
			String fileName = "";
			String path = "";
			try {
				String root = this.servletContext.getRealPath("/");
				String time = Long.toString(new Date().getTime());
				path = root + "tree/";
				if (!file.isEmpty()){
				   fileName = file.getOriginalFilename();
				   String fileType = fileName.substring(fileName.lastIndexOf(".")); //format
				   fileName = "example" + fileType;
				   File file2 = new File(path, fileName); //create file
				   try {
					    file.getFileItem().write(file2); //write zip file
					    new FileUtil().unZip(path + fileName, path + "upload/"); //upzip file
					    new FileUtil().clear(new File(path + fileName)); //delete zip file
					    fileName = "example" + ".fasta"; //new file name
					    new File(path + "upload/" + new File(path + "upload/").list()[0])
					    	.renameTo(new File(path + "upload/" + fileName));
					    new FileUtil().move(new File(path + "upload/" + fileName), new File(path + fileName));
					    new FileUtil().clear(new File(path + "upload/" + fileName));
				   } catch (Exception e) {
					    e.printStackTrace();
				   }
				} else {
					map.addAttribute("error", "Please upload a fasta DNA/RNA file!!!");
					return new ModelAndView("index");
				}
				
				//open Timer
				update_timer();
				//run Hptree on hadoop
				Process process = Runtime.getRuntime().exec("/usr/local/hadoop/bin/hadoop jar "
						+ path + "HPTree1.0.jar " + "MSA_console " + path + "properties_file");
				process.waitFor();
				//generate svg image
				new FileUtil().clear(new File(path + "tree.tre"));
				new File(path + "HPTree_OutPut").renameTo(new File(path + "tree.tre"));
				process = Runtime.getRuntime().exec("java -jar " 
						+ path + "TreeGraph.jar -convert " + path + "tree.tre -xtg " + path + "tree.xtg");
				process.waitFor();
				process = Runtime.getRuntime().exec("java -jar " + path + "TreeGraph.jar -image " 
						+ path + "tree.xtg " + path + "tree.svg");
				process.waitFor();
				//send email 
				if (!email.equals(null) && !email.equals("")) {
					new MailUtil().send(email, path + "tree.svg");
				}
				
				map.addAttribute("time", time);
			    return new ModelAndView("index");
			} catch (Exception e) {
				map.addAttribute("e",e);
				map.addAttribute("error", "Please Start Hadoop First.");
			}
			return new ModelAndView("index");
		}
		
		public void update_timer() {
			try {
				// Timer, gap is 10 min
				Timer timer = new Timer();
				timer.schedule(new TimerUtil(), 1*60*1000, 10*60*1000);
				// Update Timer File
				BufferedWriter bWriter = new BufferedWriter(new FileWriter("timer.txt"));
				bWriter.write("running");
				bWriter.write("\n");
				bWriter.write(Long.toString(System.currentTimeMillis()));
				bWriter.close();
			} catch (Exception e) {
				
			}
		}
		
}
