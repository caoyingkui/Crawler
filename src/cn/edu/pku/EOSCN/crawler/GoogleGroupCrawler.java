package cn.edu.pku.EOSCN.crawler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.Path;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import cn.edu.pku.EOSCN.business.ThreadManager;
import cn.edu.pku.EOSCN.config.Config;
import cn.edu.pku.EOSCN.crawler.util.FileOperation.FileUtil;
import cn.edu.pku.EOSCN.crawler.util.UrlOperation.HtmlDownloader;
import cn.edu.pku.EOSCN.crawler.util.UrlOperation.URLExtractor;
import cn.edu.pku.EOSCN.entity.CrawlerURL;
import cn.edu.pku.EOSCN.entity.Project;

public class GoogleGroupCrawler extends Crawler {

	public GoogleGroupCrawler() {
		// TODO Auto-generated constructor stub
	}
	private String storageBasePath;
	private String projectMailBaseUrl;
	private String email_pattern = "https://groups.google.com/forum/message/raw?msg=%GROUP_NAME%/%FORUM_NAME%/%EMAIL_NAME%";
	private String group_pattern = "https://groups.google.com/forum/?_escaped_fragment_=forum/%GROUP_NAME%%5B%START%-%END%%5D";
	private String forum_pattern = "https://groups.google.com/forum/?_escaped_fragment_=topic/%GROUP_NAME%/%FORUM_NAME%%5B%START%-%END%%5D";
	private List<String> urls = new ArrayList<String>();
	@Override
	public void init() throws Exception {
		// TODO Auto-generated method stub
		setStorageBasePath(String.format("%s%c%s%c%s", 
				Config.getTempDir(),
				Path.SEPARATOR,
				this.getProject().getName(),
				Path.SEPARATOR,
				this.getClass().getName()));	
		projectMailBaseUrl = group_pattern.replaceAll("%GROUP_NAME%", this.project.getName());
	}

	@Override
	public void crawl_url() throws Exception {
		// TODO Auto-generated method stub
		int start = 1;
		boolean flag = true;
		while (flag){
			flag = false;
			String tmpurl = projectMailBaseUrl.replaceAll("%START%", ""+start).
					replace("%END%", ""+(start + 99));
			String html = HtmlDownloader.downloadOrin(tmpurl,null);
			List<CrawlerURL> URLs = URLExtractor.getAllUrls(html, projectMailBaseUrl , "");
			
			for (CrawlerURL crawlerURL: URLs){
				String url = crawlerURL.getUrl();
				String num = url.substring(url.lastIndexOf("/")+1);
				if (url.matches("https://groups\\.google\\.com/d/topic/(.)*")){
					flag = true;
					urls.add(num);
				}
			}		
			start += 100;
		}
	}

	@Override
	public void crawl_middle(int id, Crawler crawler) {
		// TODO Auto-generated method stub
		GoogleGroupCrawler c = (GoogleGroupCrawler)crawler;
		c.projectMailBaseUrl = this.projectMailBaseUrl;
		int cnt = 0;
		for (String str : urls){
			if (cnt % this.subCrawlerNum == id){
				c.urls.add(str);
			}
			cnt++;
		}
	}

	@Override
	public void crawl_data() {
		// TODO Auto-generated method stub
		this.projectMailBaseUrl = forum_pattern.replaceAll("%GROUP_NAME%", this.project.getName());
		
		for (String name : urls){
			int start = 1;
			int flag = 100;
			while (flag >= 100){
				flag = 0;
				String tmpurl = projectMailBaseUrl.replaceAll("%START%", ""+start).
						replace("%END%", ""+(start + 99)).replaceAll("%FORUM_NAME%", name);
				String html = HtmlDownloader.downloadOrin(tmpurl,null);
				List<CrawlerURL> URLs = URLExtractor.getAllUrls(html, projectMailBaseUrl , "");
				
				for (CrawlerURL crawlerURL: URLs){
					String url = crawlerURL.getUrl();
					String num = url.substring(url.lastIndexOf("/")+1);
					if (url.matches("https://groups\\.google\\.com/d/msg/(.)*")){
						flag ++;
						
						String email_url = this.email_pattern.replaceAll("%GROUP_NAME%", this.project.getName()).
								replaceAll("%FORUM_NAME%", name).replaceAll("%EMAIL_NAME%", num);
						String text = HtmlDownloader.downloadOrin(email_url,null);
						FileUtil.write(this.storageBasePath + Path.SEPARATOR + num, text);
					}
				}
				start += 100;
			}
			
		}
	}

	public String getProjectMailBaseUrl() {
		return projectMailBaseUrl;
	}

	public void setProjectMailBaseUrl(String projectMailBaseUrl) {
		this.projectMailBaseUrl = projectMailBaseUrl;
	}

	public String getStorageBasePath() {
		return storageBasePath;
	}

	public void setStorageBasePath(String storageBasePath) {
		this.storageBasePath = storageBasePath;
	}
	
	public static void main(String[] args) throws Exception{
		GoogleGroupCrawler crawl = new GoogleGroupCrawler();
		Project project = new Project();
		project.setOrgName("mozilla");
		project.setProjectName("Firefox");
		project.setName("mozilla");
		crawl.setProject(project);
		//ThreadManager.initCrawlerTaskManager();
		crawl.needLog = true;
		crawl.crawlerType = Crawler.MAIN;
		crawl.init();
		crawl.crawl_url();
		crawl.crawl_data();
		//ThreadManager.addCrawlerTask(crawl);
		//crawl.join();
		//ThreadManager.finishCrawlerTaskManager();
	}
}
