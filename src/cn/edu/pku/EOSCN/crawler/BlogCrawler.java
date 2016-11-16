package cn.edu.pku.EOSCN.crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.List;
import java.util.LinkedList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpException;
import org.eclipse.core.runtime.Path;

import cn.edu.pku.EOSCN.config.Config;
import cn.edu.pku.EOSCN.crawler.util.FileOperation.FileUtil;
import cn.edu.pku.EOSCN.crawler.util.UrlOperation.ProxyUtil;
import cn.edu.pku.EOSCN.crawler.util.UrlOperation.StringEncoders;
import cn.edu.pku.EOSCN.crawler.util.UrlOperation.URLReader;
import cn.edu.pku.EOSCN.entity.Project;
import jcifs.smb.SmbException;

public class BlogCrawler extends Crawler {
	private static final String googleApiBase = 
			"https://www.google.com.hk/search?hl=en&num=%NUM%&q=%QUERY%";
//	private static final String googleApiBase = 
//			"https://www.google.com/search?q=%QUERY%";	
	private List<String> googleBlogPaths;
	private String storageBasePath;
	public BlogCrawler() {
		// TODO Auto-generated constructor stub
	}


	@Override
	public void init() throws Exception {
		// TODO Auto-generated method stub
		storageBasePath = String.format("%s%c%s%c%s", 
				Config.getTempDir(),
				Path.SEPARATOR,
				this.getProject().getProjectName(),
				Path.SEPARATOR,
				//this.getClass().getName());
				this.getProject().getName());
		googleBlogPaths = new LinkedList<String>();
	}

	@Override
	public void crawl_url() throws Exception {
		// TODO Auto-generated method stub
		String storagePath = 
				String.format("%s%c%s.txt", 
						storageBasePath,Path.SEPARATOR,"0URLList");
		File file = FileUtil.createFile(storagePath);
		if (FileUtil.exist(storagePath) && FileUtil.logged(storagePath)){
			FileReader fr = new FileReader(file);
			BufferedReader bf = new BufferedReader(fr);
			String s;
			while((s = bf.readLine())!= null){ 
				googleBlogPaths.add(s);
			}
			return;
		}
		Random rd = new Random();
		int num=0;   //总的链接数
		String projectName = null;
		try {
			projectName = URLEncoder.encode(project.getName(), "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//检索为blog类别，每次100个结果，语言为英文
		String GoogleSearchUrl = googleApiBase.replace("%NUM%", "10").replace("%QUERY%", projectName);
		int index = 0;
		int failnum = 0;
		while (index < 5){
			String url = GoogleSearchUrl + "&start=" + num;
			Pattern p = Pattern.compile("<h3 class=\"r\"><a href=\"(http[^\"]*)\"",Pattern.DOTALL);      //地址解析
			Pattern ti = Pattern.compile("<h3 class=\"r\"><a [^>]*>(.*?)</a></h3>",Pattern.DOTALL);      //标题解析
			//String html = getDocumentAt(url);   //页面html
			String html =  ProxyUtil.DocFromUrl(url);
			html = html.replace("&amp;", "\"");
			html = html.replace("<a href=\"/url?q=", "<a href=\"");
			html = html.replace("<a href=\"/url?url=", "<a href=\"");
			html = html.replace("<a href=\"http://www.google.com.hk/url?url=", "<a href=\"");
			Matcher m = p.matcher(html);
			Matcher mtitle = ti.matcher(html);
			
			List<String> tmpList = new LinkedList<String>();
			int cnt = 0;
			while(m.find()){	
				mtitle.find();
				String webUrl = m.group(1);                 //依次找到所有的地址     
//					System.out.println(num+": "+webUrl);
				webUrl = StringEncoders.decode(webUrl,StringEncoders.hexUrlEncoder);
				String title = mtitle.group(1);
				System.out.println(num+": \t"+webUrl);
				tmpList.add(webUrl);
				num++;
				cnt++;
			}	

			try {
				Thread.sleep(5000+rd.nextInt(7000));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (cnt != 10){
				failnum++;
				if (failnum < 3){
					num -= cnt;
					continue;
				}
			}
			googleBlogPaths.addAll(tmpList);
			index++;
			failnum = 0;
		}
		System.out.println(num+" urls crawled!");
		FileWriter fw = new FileWriter(file);
		for (String s : googleBlogPaths){
			fw.write(s + "\n");
		}
		fw.close();		
		FileUtil.logging(storagePath);
	}		

	@Override
	public void crawl_data() {
		// TODO Auto-generated method stub
		int index = 0;
		for (String url : googleBlogPaths){
			index ++;
			String name = url;
			name = name.replaceAll("[<>\\/:*?]", "");
			String storagePath = 
					String.format("%s%c_%d%s.html", 
							storageBasePath,Path.SEPARATOR,index,name);
			if (FileUtil.exist(storagePath)){// && FileUtil.logged(storagePath)){
				continue;
			}
			try {
				File file = FileUtil.createFile(storagePath);
				//URLReader.downloadFromUrl(url, storagePath);
				//String urlContent = URLReader.getHtmlContentWithTimeLimit(url, 180000);
				String urlContent = ProxyUtil.DocFromUrl(url);
				FileWriter fw = new FileWriter(file);
				fw.write(urlContent);
				fw.close();
				FileUtil.logging(storagePath);
			}catch (Exception e){
				System.out.println(url);
				e.printStackTrace();
			}
		}
	}
	
	public String getDocumentAt(String urlString){                //从url获取网页内容
		
		StringBuffer document = new StringBuffer(); 
		try { 
			URL url = new URL(urlString); 
			URLConnection conn = url.openConnection(); 
			
			conn.setConnectTimeout(180000);
			conn.setReadTimeout(600000);
			String headUrl[] ={"IBM WebExplorer /v0.94', 'Galaxy/1.0 [en] (Mac OS X 10.5.6; U; en)","Opera/9.27 (Windows NT 5.2; U; zh-cn)","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20130406 Firefox/23.0", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:18.0) Gecko/20100101 Firefox/18.0",  "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)", "Opera/9.80 (Windows NT 6.0) Presto/2.12.388 Version/12.14",  "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko)"  ,  "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.0; Trident/5.0; TheWorld)"}; 

			Random rd1 = new Random();
			int randomIndex = rd1.nextInt(headUrl.length-1);
			//System.out.println(headUrl[randomIndex]);
			//conn.setRequestProperty("User-Agent", headUrl[randomIndex]);
			conn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36");
			conn.setRequestProperty("Upgrade-Insecure-Requests","1");
			System.out.println(headUrl[randomIndex]);
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn. getInputStream(),"utf-8")); 
			String line = null; 
			byte[] c = new byte[2];
			c[0]=0x0d;
			c[1]=0x0a;
			String c_string = new String(c);   
			while ( (line = reader.readLine()) != null) { 
				document.append( line+c_string ); 
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
	
	public static void main(String args[]) throws HttpException, IOException{
		//System.setProperty("java.net.preferIPv4Stack", "true");
        //System.setProperty("java.net.preferIPv6Addresses", "true");
		Crawler crawl = new BlogCrawler();
		Project project = new Project();
		project.setOrgName("apache");
		project.setProjectName("lucene");
		File list = new File("ListLucene.txt");
		BufferedReader br = new BufferedReader(new FileReader(list));
		String line = null;
		while ((line = br.readLine())!=null){
			if (line.length() < 2) break;
			project.setName(line + " " + project.getProjectName());
			//project.setName("tokenize a string");
			crawl.setProject(project);
			try {
				crawl.Crawl();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public void crawl_middle(int id, Crawler crawler) {
		// TODO Auto-generated method stub
		
	}
}
