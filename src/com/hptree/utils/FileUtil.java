package com.hptree.utils;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.Enumeration;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

public class FileUtil {
	public static void main(String[] args) {
		new FileUtil().move(new File("D:\\test\\test.txt"), new File("D:\\test.txt"));
	}
	
	/**
     * 递归删除目录下的所有文件及子目录下所有文件,也可以删除单个文件
     * @param dir 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     *                 If a deletion fails, the method stops attempting to
     *                 delete and returns "false".
     */
	public boolean clear(File dir) {
	    if (dir.isFile() && dir.exists()) {  
	    	dir.delete();  
	        return true;  
	    }
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i=0; i<children.length; i++) {
                boolean success = clear(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }
	
	@SuppressWarnings({ "resource", "null" })
	public long move(File f1,File f2){
		try {
	        long time=new Date().getTime();
	        int length=2097152;
	        FileInputStream in=new FileInputStream(f1);
	        FileOutputStream out=new FileOutputStream(f2);
	        FileChannel inC=in.getChannel();
	        FileChannel outC=out.getChannel();
	        ByteBuffer b=null;
	        while(true){
	            if(inC.position()==inC.size()){
	                inC.close();
	                outC.close();
	                return new Date().getTime()-time;
	            }
	            if((inC.size()-inC.position())<length){
	                length=(int)(inC.size()-inC.position());
	            }else
	                length=2097152;
	            b=ByteBuffer.allocateDirect(length);
	            inC.read(b);
	            b.flip();
	            outC.write(b);
	            outC.force(false);
	       }
		} catch (Exception e) {
			return (Long) null;
		}
    }
	
	/**
	 * 解压一个文件
	 * @param zipfilename 解压的文件
	 * @param destDir  解压的目录
	 */
	public void unZip(String zipfilename,String destDir){
		File file=new File(zipfilename);
		OutputStream os=null;
		InputStream is=null;
		if (!file.isFile() || !file.getName().endsWith(".zip")) {
			System.out.println("该程序无法解压非zip文件");
		}else{
			destDir = destDir.endsWith("\\") ? destDir : destDir + "\\";
			byte b[] = new byte[1024];  
	        int length;  
	        ZipFile zipFile;
	        try {
				zipFile=new ZipFile(file,"gbk");
				Enumeration<?> enumeration =zipFile.getEntries();
				ZipEntry zipEntry = null; 
				while (enumeration.hasMoreElements()) {
					zipEntry = (ZipEntry) enumeration.nextElement();
					File loadFile = new File(destDir + zipEntry.getName());
					//判断压缩文件中的某个条目是文件夹还是文件
					if (zipEntry.isDirectory()) {//如果是目录，那么判断该文件是否已存在并且不是一个文件夹,解决空文件夹解压后不存在的问题
						if (!loadFile.exists()) {
							loadFile.mkdirs();
						}
					}else{
						if (!loadFile.getParentFile().exists()){  
	                        loadFile.getParentFile().mkdirs();  
	                    }  
						os=new FileOutputStream(loadFile);
						is = zipFile.getInputStream(zipEntry);
						while ((length = is.read(b)) > 0){
	                        os.write(b, 0, length);  
	                        os.flush();
	                     }  
					}
				
				}
				System.out.println("解压文件成功");
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				try {
					if (is!=null) {
						is.close();
					}
					if (os!=null) {
						os.close();
					}
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
	}
	
	
	/**
	 * 将一个文件写入到压缩流中，即完成压缩
	 * @param sourceFile 要压缩的文件或文件夹
	 * @param basePath  相对路径
	 * @param zos  压缩流
	 * 
	 * 注意，压缩文件与压缩文件夹的方法
	 * 压缩文件 zos.putNextEntry(new ZipEntry("a/b.txt"));
	 * 压缩文件夹  zos.putNextEntry(new ZipEntry("a/b/"));
	 */
	public static void zip(File sourceFile,String basePath,ZipOutputStream zos){
		InputStream is=null;
		try {

			//首先判断压缩的是一个文件夹，还是文件
			if (sourceFile.isDirectory()) {//如果是一个文件夹，那么先把该文件夹压缩进去，然后递归
				basePath=basePath+sourceFile.getName()+"/";
				zos.putNextEntry(new ZipEntry(basePath));
				File[] files=sourceFile.listFiles();
				for (int i = 0; i < files.length; i++) {
					File file = files[i];
					//回调
					zip(file, basePath, zos);
				}
			}else{//如果是一个文件就直接压缩进去，将内容写进去
				zos.putNextEntry(new ZipEntry(basePath+sourceFile.getName()));
				is=new FileInputStream(sourceFile);
				BufferedInputStream bis=new BufferedInputStream(is);
				byte[] buf=new byte[1024];
				int length=-1;
				while ((length=bis.read(buf))!=-1) {
					zos.write(buf, 0, length);
					zos.flush();
				}
				bis.close();
			}
		} catch (Exception e) {
            e.printStackTrace();
		}finally{
			if (is!=null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
