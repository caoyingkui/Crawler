package cn.edu.pku.EOSCN.crawler.util.UrlOperation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.TimeLimiter;
import com.google.common.util.concurrent.UncheckedTimeoutException;

import cn.edu.pku.EOSCN.config.Config;
import cn.edu.pku.EOSCN.crawler.util.FileOperation.FileUtil;

/**
* @ClassName: URLReader 

* @Description: 根据一个html的URL返回内容

* @author Huazb (huazb1989@126.com) Software Engineering Institute, Peking
 *          University, China

* @date 2013-5-26 下午02:06:11 

* 
 


 */
public class URLReader implements URLReaderInterface{
	protected static final Logger logger = Logger.getLogger(URLReader.class.getName());
	public static StringBuffer getHtmlContent_SB(String strurl)
	{
		StringBuffer html=new StringBuffer();
     try{
    	// strurl="http://www.baidu.com/index.html";
    	 URL url=new URL(strurl);
    	 BufferedReader br= new BufferedReader(new InputStreamReader(url.openStream()));
    	 //html=new StringBuffer();
    	 String s=null;
    	 while ( (s=br.readLine())!=null) 
    	 {
     
    			 html.append(s);
    		
         }
     }catch(IOException e){
    	 System.out.println(e);
    	 return null;
     }
     
     	return html;
	}
	/**
	* @Title: getHtmlContent 
	
	* @Description: 返回Url对应的html网页源码 
	
	* @param @param strurl
	* @param @return    设定文件 
	
	* @return String    返回类型 
	
	* @author: Huazb (huazb1989@126.com) Software Engineering Institute, Peking
	 *          University, China
	
	* @throws 
	
	 */
	public static String getHtmlContent(String strurl)
	{
		StringBuffer html=new StringBuffer();
     try{
    	// strurl="http://www.baidu.com/index.html";
    	 URL url=new URL(strurl);
    	 BufferedReader br= new BufferedReader(new InputStreamReader(url.openStream()));
    	 //html=new StringBuffer();
    	 String s=null;
    	 while ( (s=br.readLine())!=null) 
    	 {
     
    			 html.append(s);
    			 html.append("\n");
    		
         }
     }catch(IOException e){
    	 System.out.println(e);
    	 return null;
     }
     
     	return new String(html);
	}
	/**
	* @Title: getPlainHtmlContent 
	
	* @Description: 返回url对应的网页中的文本信息  现有bug对于javascript内容未过滤
	
	* @param @param strurl
	* @param @return    设定文件 
	
	* @return String    返回类型 
	
	* @author: Huazb (huazb1989@126.com) Software Engineering Institute, Peking
	 *          University, China
	
	* @throws 
	
	 */
	public static String getPlainHtmlContent(String strurl)
	{
		String htmlContent;
		htmlContent=getHtmlContent("http://lucene.apache.org/core/4_3_0/core/org/apache/lucene/codecs/lucene42/package-summary.html#package_description");
	//	System.out.println(htmlContent);
		Pattern p = Pattern.compile("<(\\S*?)[^>]*>.*?| <.*? />");  
		
	    //String html = "<td><a target=\"_blank\" href=\"http://www.baidu.com/baidu?cl=3&tn=baidutop10&fr=top1000&wd=%A1%B6%CC%C6%C9%BD%B4%F3%B5%D8%D5%F0%A1%B7%B9%AB%D3%B3\">《唐山大地震》公映</a></td>";  
		
		//htmlContent="ss<br>w    ww<b>rr";
		String html = htmlContent.replaceAll("<br>|<b>|<p>|<ul>|<li>", "\n");
		
	//	System.out.println(html);
	        Matcher m = p.matcher(html);  
	  
	        String rs = new String(html);
	        
	        // 找出所有html标记。  
	        while (m.find()) {  
	           // System.out.println(m.group());  
	            // 删除html标记。  
	            rs = rs.replace(m.group(), " ");  
	        }  
	      //  System.out.println("-----");  
	        rs=rs.replaceAll("&nbsp;", " ");
	        rs = rs.replaceAll(" {2,}"," "); 
	        rs= rs.replaceAll("\n{2,}","\n"); //去除多余空行
	      //  System.out.println(rs);
	  //      System.out.println(rs);
	        return rs;
		
	}
	
	public static String getHtmlStringFromUrl(String pageurl) throws IOException,
			HttpException {
		HttpClient client = new HttpClient();
		client.getParams().setParameter("http.connection.timeout", 60000);
		GetMethod get = new GetMethod(pageurl);
		get.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(0, false));
		get.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 180000);

		int statusCode = client.executeMethod(get);
		if (statusCode != HttpStatus.SC_OK) {
			System.err.println("Method failed: " + get.getStatusLine());
		}
		//byte[] responseBody = get.getResponseBody();
		BufferedReader br = new BufferedReader(new InputStreamReader(get.getResponseBodyAsStream()));
		StringBuffer htmlStringBuffer = new StringBuffer();
		String s = null;
		while ((s = br.readLine()) != null) {
			htmlStringBuffer.append(s);
			htmlStringBuffer.append("\n");
		}
		//String htmlPageString = new String(responseBody);

		// System.out.println(htmlPageString);
		get.releaseConnection();
		return htmlStringBuffer.toString();
	}
	

	public static String getHtmlContentWithTimeLimit(String webUrl, int i) throws HttpException, IOException {
		TimeLimiter limiter = new SimpleTimeLimiter();
		URLReaderInterface proxy = limiter.newProxy(new URLReader(), URLReaderInterface.class,
				i, TimeUnit.MILLISECONDS);
		try {
			return proxy.getHtmlContentForTimelimit(webUrl);
		} catch (UncheckedTimeoutException e) {
			System.out.println("TIME OUT! QIUT READING!");
			return null;
		}
	}
	@Override
	public String getHtmlContentForTimelimit(String strurl) {
		StringBuffer html = new StringBuffer();
		try {
			// strurl="http://www.baidu.com/index.html";
			URL url = new URL(strurl);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					url.openStream()));
			// html=new StringBuffer();
			String s = null;
			while ((s = br.readLine()) != null) {

				html.append(s);
				html.append("\n");

			}
		} catch (IOException e) {
			System.out.println(e);
			return null;
		}
		return new String(html);
	}
	public static String getUrlType(String tempURL) throws IOException {
		URL url = null;
		try {
			url = new URL(tempURL);
		} catch (MalformedURLException e) {
			return null;
		}
		URLConnection connection = url.openConnection();
		Map<String, List<String>> map = connection.getHeaderFields();
		Set<String> keySet = map.keySet();
		for (String string : keySet) {
			if (string != null && string.equals("Content-Type")) {
				return map.get(string).get(0);
			}
		}
		
		return null;
	}

	
	public static File downloadFromUrl(String url, String storagePath) {
		//TODO 该路径是否需要修改？
		String tempPath = storagePath;
		//File tempFile = FileUtil.createFile(tempPath,url.substring(url.lastIndexOf('/') + 1));
		File tempFile = FileUtil.createFile(tempPath);
		logger.info("download url content to file path : " + tempPath);
		
		try {
			URL httpurl = new URL(url);
			FileUtils.copyURLToFile(httpurl,tempFile);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tempFile;
	}
	public static void main(String[] args) throws IOException {
		//String type=getUrlType("http://www.apache.org/dist//maven/maven-3/3.0.5/binaries/apache-maven-3.0.5-bin.tar.gz.asc");
		//System.out.println(type);
		String tmp = "https://api.github.com/repos/google/gson/zipball/8b464231f735b3157f38c6e589171dc17f709ba1";
		URLReader.downloadFromUrl(tmp,"D:\\");
		
	}

}
