package cn.edu.pku.EOSCN.crawler.util.UrlOperation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.eclipse.core.runtime.Path;
import org.json.JSONArray;
import org.json.JSONObject;

import cn.edu.pku.EOSCN.crawler.util.FileOperation.FileUtil;

public class HtmlDownloader {
	public static String downloadHU(String url){
		try {
			return ProxyUtil.DocFromUrl(url);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			return null;
		}
	}
	public static String downloadOrin(String urlString, Map<String, List<String>> headers){
		StringBuffer document = new StringBuffer(); 
	 	try { 
	 		URL url = new URL(urlString); 
	 		//Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8087));  
	 		URLConnection conn = url.openConnection(); 
	 		conn.setConnectTimeout(10000);
	 		conn.setReadTimeout(5000);
			String headUrl[] ={"IBM WebExplorer /v0.94', 'Galaxy/1.0 [en] (Mac OS X 10.5.6; U; en)","Opera/9.27 (Windows NT 5.2; U; zh-cn)","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20130406 Firefox/23.0", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:18.0) Gecko/20100101 Firefox/18.0",  "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)", "Opera/9.80 (Windows NT 6.0) Presto/2.12.388 Version/12.14",  "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko)"  ,  "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.0; Trident/5.0; TheWorld)"}; 
			Random rd1 = new Random();
			int randomIndex = rd1.nextInt(headUrl.length-1);
			//System.out.println(headUrl[randomIndex]);
			conn.setRequestProperty("User-Agent", headUrl[randomIndex]);
			if (headers != null){
				Map header = conn.getHeaderFields();
				headers.putAll(header);
			}
	 		BufferedReader reader = new BufferedReader(new InputStreamReader(conn. getInputStream(),"utf-8")); 
	 		String line = null; 
	 		byte[] c = new byte[2];
	 		c[0]=0x0d;
	 		c[1]=0x0a; 
	 		String c_string = new String(c);   
	 		while ( (line = reader.readLine()) != null) { 
	 			document.append( line+c_string );
	 			if (document.length() > 100000000){
	 				reader.close();
	 				return "";
	 			}
	 		} 
	 		reader.close(); 
	 		}  
	 	catch (IOException e) { 
	 		System.out.println("IOException when connecting to URL: " + urlString);
	 		return "";
	 	}
	 	return document.toString(); 
	}
	
	public static String getHost(String url){
		if (url.startsWith("https://")) url = url.substring(8);
		if (url.startsWith("http://")) url = url.substring(7);
		return url.split("/")[0];
	}
	public static String url2path(String url){
		if (url.startsWith("https://")) url = url.substring(8);
		if (url.startsWith("http://")) url = url.substring(7);
		url = url.replaceAll("[<>:*?]", "");
		if (url.endsWith("/")){
			url = url.substring(0, url.length() - 1);
		}
		url = url.replaceAll("/+", ""+Path.SEPARATOR);
		url = url+"__FILE";
		return url;
	}
	
	public static void main(String[] args) throws InterruptedException, IOException{
		String urlString = "https://api.github.com/search/users?q=created:%DATE%";
		String gitToken = 
				"client_id=fa5191bf55e754d6d25b&client_secret=226fc9193e753ca8f69fd8d9279577e4a9c5448c";
		urlString += "&" + gitToken;
	    Calendar start = Calendar.getInstance();  
	    start.set(2008, 1 - 1, 1);
	    Long startTIme = start.getTimeInMillis();  
	    Calendar end = Calendar.getInstance();  
	    end.set(2016, 8 - 1, 31);  
	    Long endTime = end.getTimeInMillis();  
	    Long oneDay = 1000 * 60 * 60 * 24l;  
	    Long time = startTIme;  
		File index = new File("D:\\tmp\\a.txt");
		FileWriter fw = new FileWriter(index);
	    while (time <= endTime) {  
	        Date d = new Date(time);  
	        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");  
	        String url = urlString.replace("%DATE%", df.format(d));
			String ss = HtmlDownloader.downloadOrin(url, null);
			if (ss.length() < 2){
				Thread.sleep(1000);
				continue;
			}
			JSONObject ja = new JSONObject(ss);
			String ret = "" + df.format(d) + " " + ja.get("total_count")+"\n";
			fw.append(ret);
			fw.flush();
			Thread.sleep(1000);
	        time += oneDay;  
	    }  
	    fw.close();
	}
}
