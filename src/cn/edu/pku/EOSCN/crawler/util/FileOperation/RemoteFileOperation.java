package cn.edu.pku.EOSCN.crawler.util.FileOperation;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.chainsaw.Main;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;

public class RemoteFileOperation {
	/**
	* @Title: smbGet 
	
	* @Description: 从一个共享地址下载一个文件  
	
	* @param @param remoteUrl	共享文件地址
	* @param @param localFilePath    本地地址 
	
	* @return void    返回类型 
	
	* @author: Huazb (huazb1989@126.com) Software Engineering Institute, Peking
	 *          University, China
	
	* @throws 
	  @case RemoteFileOperation.smbGet("smb://192.168.4.9/Upload/Temp/test_2.txt","d:/");
	 */
	public static void smbGet(String remoteUrl,String localDir) {   
		 InputStream in = null;   
		 OutputStream out = null;   
		 try {   
		  SmbFile remoteFile = new SmbFile(remoteUrl);   
		  if(remoteFile==null){   
		   System.out.println("共享文件不存在");   
		   return;   
		  }   
		  String fileName = remoteFile.getName();   
		  File localFile = new File(localDir+File.separator+fileName);   
		  in = new BufferedInputStream(new SmbFileInputStream(remoteFile));   
		  out = new BufferedOutputStream(new FileOutputStream(localFile));   
		  byte[] buffer = new byte[1024];
		  int len;
		  while(true){
			  len=in.read(buffer);
			//  System.out.println("len="+len);
			  if (len==-1) break;
			  out.write(buffer, 0, len);
		
		   //out.write(buffer);   
		   buffer = new byte[1024];   
		  }   
		 } catch (Exception e) {   
		  e.printStackTrace();   
		 } finally {   
		  try {   
		   out.close();   
		   in.close();   
		  } catch (IOException e) {   
		   e.printStackTrace();   
		  }   
		 }   
		}    
	
	
	/**
	* @Title: smbPut 
	
	* @Description: 向一个共享地址上传一个文件 
	
	* @param @param remoteUrl	共享地址
	* @param @param localFilePath    本地文件地址 
	
	* @return void    返回类型 
	
	* @author: Huazb (huazb1989@126.com) Software Engineering Institute, Peking
	 *          University, China
	
	* @throws 
	  @case RemoteFileOperation.smbPut("smb://192.168.4.9/Upload/Temp", "d:/test_1.txt");
	 */
	public static void smbPut(String remoteUrl,String localFilePath) {       
	    InputStream in = null;       
	    OutputStream out = null;       
	    try {       
	        File localFile = new File(localFilePath);       
	              
	        String fileName = localFile.getName();       
	        //System.out.println("remoteurl="+remoteUrl+"/"+fileName);
	        SmbFile remoteFile = new SmbFile(remoteUrl+"/"+fileName);       
	        in = new BufferedInputStream(new FileInputStream(localFile));          
	        out = new BufferedOutputStream(new SmbFileOutputStream(remoteFile));       
	        byte[] buffer = new byte[1024];     
	        int len;
	        while(true){ 
	        	 len=in.read(buffer);
			//	  System.out.println("len="+len);
				  if (len==-1) break;
				  out.write(buffer, 0, len);
	           //out.write(buffer);       
	           buffer = new byte[1024];       
	        }       
	    } catch (Exception e) {       
	        e.printStackTrace();       
	    } finally {       
	        try {       
	           out.close();       
	           in.close();       
	        } catch (IOException e) {       
	           e.printStackTrace();       
	        }       
	    }       
	 }   
	
	public static void smbPutFullRemoteSite(String remoteUrl,String localFilePath) {       
	    InputStream in = null;       
	    OutputStream out = null;       
	    try {       
	        File localFile = new File(localFilePath);       
	              
	        String fileName = localFile.getName();       
	        System.out.println("remoteurl="+remoteUrl);
	        SmbFile remoteFile = new SmbFile(remoteUrl);       
	        in = new BufferedInputStream(new FileInputStream(localFile));          
	        out = new BufferedOutputStream(new SmbFileOutputStream(remoteFile));       
	        byte[] buffer = new byte[1024];     
	        int len;
	        while(true){ 
	        	 len=in.read(buffer);
			//	  System.out.println("len="+len);
				  if (len==-1) break;
				  out.write(buffer, 0, len);
	           //out.write(buffer);       
	           buffer = new byte[1024];       
	        }       
	    } catch (Exception e) {       
	        e.printStackTrace();       
	    } finally {       
	        try {       
	           out.close();       
	           in.close();       
	        } catch (IOException e) {       
	           e.printStackTrace();       
	        }       
	    }       
	 }  
	public static void main(String args[]) {
	/*
	 *  远程url     smb://192.168.0.77/test如果需要用户名密码就这样：
	smb://username:password@192.168.0.77/test
		
	 */
		RemoteFileOperation.smbPut("smb://192.168.4.9/Upload/Temp", "d:/test_1.txt");
	//	RemoteFileOperation.smbGet("smb://192.168.4.9/Upload/Temp/test_2.txt","d:/");
	}


	public static int countFiles(SmbFile file) {
		try {
			if (file.isFile()) {
				return 1;
			}
			if (file.isDirectory()) {
				int count = 0;
//				System.out.println(file.getServer());
				SmbFile[] files = file.listFiles();
//				System.out.println("haha!");
				for (SmbFile smbFile : files) {
					count += countFiles(smbFile);
				}
				return count;
			}
	 	} catch (SmbException e) {
	 		System.out.println("read smbfile error!");
	 		e.printStackTrace();
			return 0;
		}
		return 0;
	}
	
}
