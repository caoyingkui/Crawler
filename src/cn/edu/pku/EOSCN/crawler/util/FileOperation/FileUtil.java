package cn.edu.pku.EOSCN.crawler.util.FileOperation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.runtime.Path;

public class FileUtil {
	private static Map<String,Map<String,Boolean>> logCache = new ConcurrentHashMap <String,Map<String,Boolean>>();	
	private static Map<String,Map<String,Object>> writeBackList = new ConcurrentHashMap <String,Map<String,Object>>();
	static {
		FileUtil.init();
	}
	public static void init() {
		// TODO Auto-generated constructor stub
		Thread thread = new Thread(){
			public void run(){
				while (true){
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					FileUtil.clear();
				}
				
			}
		};
		//thread.setDaemon(false);
		thread.start();
	}
	
	
	public static void clear(){
		for (String str : writeBackList.keySet()){
			FileUtil.saveLog(str);
		}

	}
	
	public static File createPath(String path){
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String filePath = 
				String.format("%s%c%s", 
						path,
						Path.SEPARATOR,
						"log.txt");
		File file = new File(filePath);	
		try {
			if (!file.exists()){
				file.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dir;
	}
	
	public static File createFile(String path, String fileName) {
		createPath(path);
		String filePath = 
				String.format("%s%c%s", 
						path,
						Path.SEPARATOR,
						fileName);
		File file = new File(filePath);	
		try {
			if (!file.exists()){
				file.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}
	
	public static File createFile(String fullPath){
		String fileName = fullPath.substring(fullPath.lastIndexOf(Path.SEPARATOR)+1);
		String path = fullPath.substring(0,fullPath.lastIndexOf(Path.SEPARATOR));
		return createFile(path,fileName);
	}
	
	public static boolean exist(String path, String fileName){
		createPath(path);
		String filePath = 
				String.format("%s%c%s", 
						path,
						Path.SEPARATOR,
						fileName);
		File file = new File(filePath);	
		return file.exists();
	}
	
	public static boolean exist(String fullPath){
		String fileName = fullPath.substring(fullPath.lastIndexOf(Path.SEPARATOR)+1);
		String path = fullPath.substring(0,fullPath.lastIndexOf(Path.SEPARATOR));
		return exist(path,fileName);
	}
	
	public static void loadLog(String path, Map<String,Boolean> set){
		String loggerPath =
				String.format("%s%c%s",
						path,
						Path.SEPARATOR,
						"log.txt");
		createPath(path);
		File file = new File(loggerPath);
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufr = new BufferedReader(isr);
			String inputStr = null;
			while ((inputStr = bufr.readLine()) != null){
				set.put(inputStr, true);
			}
			bufr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void saveLog(String path){
		Map<String,Object> set = FileUtil.writeBackList.get(path);
		if (set == null) return;
		String loggerPath =
				String.format("%s%c%s",
						path,
						Path.SEPARATOR,
						"log.txt");
		File file = new File(loggerPath);
		FileWriter fw;
		try {
			fw = new FileWriter(file,true);
			if (fw != null) {
				for (String fileName : set.keySet()){
					//if (!set.get(fileName)){
						fw.append(fileName + "\n");
						set.put(fileName, true);
					//}
				}
				fw.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		set.clear();
	}
	
	public static boolean loggedByRegex(String path, String fileName, String regex){
		Map<String,Boolean> set;
		if (FileUtil.logCache.containsKey(path)){
			set = FileUtil.logCache.get(path);
		}else{
			set = new ConcurrentHashMap<String,Boolean>();
			loadLog(path,set);
			FileUtil.logCache.put(path, set);
		}
		for (String s : set.keySet()){
			if (s.matches(regex)) return true;
		}
		return false;
	}
	
	public static boolean logged(String path, String fileName){
		Map<String,Boolean> set;
		if (FileUtil.logCache.containsKey(path)){
			set = FileUtil.logCache.get(path);
		}else{
			set = new ConcurrentHashMap<String,Boolean>();
			loadLog(path,set);
			FileUtil.logCache.put(path, set);
		}
		return set.containsKey(fileName);
	}
	
	public static boolean loggedByRegex(String fullPath, String regex){
		String fileName = fullPath.substring(fullPath.lastIndexOf(Path.SEPARATOR)+1);
		String path = fullPath.substring(0,fullPath.lastIndexOf(Path.SEPARATOR));
		return loggedByRegex(path,fileName,regex);	
	}
	
	public static boolean logged(String fullPath){
		String fileName = fullPath.substring(fullPath.lastIndexOf(Path.SEPARATOR)+1);
		String path = fullPath.substring(0,fullPath.lastIndexOf(Path.SEPARATOR));
		return logged(path,fileName);
	}
	
	public static void logging(String path, String fileName){
		Map<String,Boolean> set;
		if (FileUtil.logCache.containsKey(path)){
			set = FileUtil.logCache.get(path);
		}else {
			set = new ConcurrentHashMap<String,Boolean>();
			loadLog(path,set);
			FileUtil.logCache.put(path, set);
		}
		
		if (!set.containsKey(fileName)){
			set.put(fileName, false);
			writeBack(path,fileName);
		}
	}
	
	public static void writeBack(String path, String filename){
		Map<String,Object> set;
		if (FileUtil.writeBackList.containsKey(path)){
			set = FileUtil.writeBackList.get(path);
		}else{
			set = new ConcurrentHashMap<String,Object>();
			FileUtil.writeBackList.put(path, set);
		}
		set.put(filename, new Object());
		if (set.size() > 20){
			saveLog(path);
		}
	}
	
	public static void logging(String fullPath){
		String fileName = fullPath.substring(fullPath.lastIndexOf(Path.SEPARATOR)+1);
		String path = fullPath.substring(0,fullPath.lastIndexOf(Path.SEPARATOR));
		logging(path,fileName);		
	}
	
	public static String read(String fullPath){
		String ret = "";
		File file = new File(fullPath);
		FileReader fr;
		try {
			fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			while ((line = br.readLine()) != null){
				ret += line + "\n";
			}
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return ret;
	}
	
	public static void write(String fullPath, String content){
		File file = FileUtil.createFile(fullPath);
		FileWriter fw;
		try {
			fw = new FileWriter(file);
			fw.write(content);
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
