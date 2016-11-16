package cn.edu.pku.EOSCN.crawler.util.UrlOperation;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpEntity;  
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;  
import org.apache.http.client.entity.UrlEncodedFormEntity;  
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;  
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;  
import org.apache.http.message.BasicNameValuePair;  
import org.apache.http.util.EntityUtils;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;  
public class ProxyUtil {

	public ProxyUtil() {
		// TODO Auto-generated constructor stub
	}
	public static String getDocumentAt(String urlString)                //从url获取网页内容
	{ 
		 StringBuffer document = new StringBuffer(); 
		 	try { 
		 		if (urlString.endsWith("iso")){
		 			System.out.println("asd");
		 		}
		 		URL url = new URL(urlString); 
		 		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8087));  
		 		URLConnection conn = url.openConnection(); 
		 		
		 		conn.setConnectTimeout(10000);
		 		conn.setReadTimeout(5000);
				String headUrl[] ={"IBM WebExplorer /v0.94', 'Galaxy/1.0 [en] (Mac OS X 10.5.6; U; en)",
						"Opera/9.27 (Windows NT 5.2; U; zh-cn)","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20130406 Firefox/23.0", 
						"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:18.0) Gecko/20100101 Firefox/18.0",  
						"Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)", 
						"Opera/9.80 (Windows NT 6.0) Presto/2.12.388 Version/12.14",  
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko)"  ,  
						"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.0; Trident/5.0; TheWorld)"}; 
				
				Random rd1 = new Random();
				int randomIndex = rd1.nextInt(headUrl.length-1);
				//System.out.println(headUrl[randomIndex]);
				conn.setRequestProperty("User-Agent", headUrl[randomIndex]);
		 		
		 		
		 		BufferedReader reader = new BufferedReader(new InputStreamReader(conn. getInputStream(),"utf-8")); 
		 		String line = null; 
		 		byte[] c = new byte[2];
		 		c[0]=0x0d;
		 		c[1]=0x0a; 
		 		String c_string = new String(c);   
		 		while ( (line = reader.readLine()) != null) { 
		 			document.append( line+c_string );
		 			if (document.length() > 10000000){
		 				reader.close();
		 				return "";
		 			}
		 		} 
		 		reader.close(); 
		 		} 
		 	catch (MalformedURLException e) { 
		 		System.out.println("Unable to connect to URL: " + urlString); 
		 	} 
		 	catch (IOException e) { 
		 		System.out.println("IOException when connecting to URL: " + urlString); 
		 	}
		 	return document.toString(); 
	 }
	
	public static String DocFromUrl(String url) throws Exception{
        final WebClient webClient = new WebClient(BrowserVersion.CHROME);
        
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);

        webClient.getCookieManager().setCookiesEnabled(true);
        webClient.getCookieManager().addCookie(new Cookie("https://www.google.com.hk/search?hl=en&num=10&q=get+all+the+indexed+keywords+lucene&start=10","NID"
        		,"83=KiPNOEYfe0te1-ZyOebAtj6rTLii0WFRneTQR95rXLU7pYukGY0zs0OtIfpcg_3zXFR-NQQFx6hNJZB9pf-1ohCf4tQncTW2kdXCDGOX16HX7gn20Yr-tOJs9QjclF8M"));
        webClient.getCookieManager().addCookie(new Cookie("https://www.google.com.hk/search?hl=en&num=10&q=get+all+the+indexed+keywords+lucene&start=10","DV"
        		,"ggdrloICOG8Swo0fqxrKHfcNrU9WrAI"));
        webClient.getCookieManager().addCookie(new Cookie("https://www.google.com.hk/search?hl=en&num=10&q=get+all+the+indexed+keywords+lucene&start=10","OGPC"
        		,"5061821-1:"));                
        //webClient.waitForBackgroundJavaScript(1);
        Page page = webClient.getPage(url);
        String ss = "";
        if (page instanceof HtmlPage){
        	//ss = ((HtmlPage)page).asText();
        	ss = page.getWebResponse().getContentAsString();
        }else if (page instanceof UnexpectedPage){
        	InputStream is = ((UnexpectedPage)page).getInputStream();
            StringBuffer out = new StringBuffer(); 
            byte[] b = new byte[4096]; 
            for (int n; (n = is.read(b)) != -1;)   { 
            	out.append(new String(b, 0, n)); 
            } 
            ss = out.toString(); 
        }else{
        	ss = page.getWebResponse().getContentAsString();
        }
         
        //webClient.waitForBackgroundJavaScript(1);
        //String ss = page.asText();
        
        webClient.close();
		return ss;
	}
	
	public static void getProject(String name){
	}
	
    public static void main(String args[]) throws Exception {
//    	System.setProperty("http.proxyHost", "true"); 
//    	System.setProperty("http.proxyHost", "127.0.0.1"); 
//    	System.setProperty("http.proxyPort", "8087");    	
//    	System.setProperty("https.proxyHost", "true"); 
//    	System.setProperty("https.proxyHost", "127.0.0.1"); 
//    	System.setProperty("https.proxyPort", "8087"); 
    	//String url = "https://www.google.com.hk/search?&q=poi&bav=on.2,or.&cad=b&fp=1&biw=1101&bih=995&dpr=1&tch=1&ech=1";
    	//String url = "http://www.baidu.com";
    	String url = "https://www.google.com.hk/search?hl=en&num=10&q=get+all+the+indexed+keywords+lucene&start=10";
    	File file = new File("D:\\a.html");
    	FileWriter fw = new FileWriter(file);
    	//String ss = getDocumentAt(url);
    	String ss = DocFromUrl(url);
    	System.out.println(ss);
    	fw.write(ss);
    	fw.close();
    }  
}
