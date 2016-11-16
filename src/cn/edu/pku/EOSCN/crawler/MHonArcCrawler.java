package cn.edu.pku.EOSCN.crawler;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Path;

import cn.edu.pku.EOSCN.business.ThreadManager;
import cn.edu.pku.EOSCN.config.Config;
import cn.edu.pku.EOSCN.crawler.util.FileOperation.FileUtil;
import cn.edu.pku.EOSCN.crawler.util.UrlOperation.HtmlDownloader;
import cn.edu.pku.EOSCN.crawler.util.UrlOperation.URLExtractor;
import cn.edu.pku.EOSCN.entity.CrawlerURL;
import cn.edu.pku.EOSCN.entity.Project;

/** 
  * @author Jinan Ni E-mail: nijinan@pku.edu.cn
  * @date 2016年8月25日 上午11:18:01 
  * @version 1.0   */
public class MHonArcCrawler extends Crawler {
	private String storageBasePath;
	private String projectMailBaseUrl;
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
	}

	@Override
	public void crawl_url() throws Exception {
		// TODO Auto-generated method stub
		String html = HtmlDownloader.downloadOrin(projectMailBaseUrl,null);
		List<CrawlerURL> URLs = URLExtractor.getAllUrls(html, projectMailBaseUrl , "");
		for (CrawlerURL crawlerURL: URLs){
			String url = crawlerURL.getUrl();
			String num = url.substring(url.lastIndexOf("/")+1);
			if (num.matches("msg[0-9]*\\.html")){
				urls.add(url);
			}
		}
	}

	@Override
	public void crawl_middle(int id, Crawler crawler) {
		// TODO Auto-generated method stub
		MHonArcCrawler c = (MHonArcCrawler)crawler;
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
		for (String url : urls){
			String html = HtmlDownloader.downloadOrin(url,null);
			String storagePath = 
					String.format("%s%c%s", 
							this.getStorageBasePath(),Path.SEPARATOR,
							HtmlDownloader.url2path(url));
			FileUtil.write(storagePath, html);
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
		Crawler crawl = new MHonArcCrawler();
		Project project = new Project();
		ThreadManager.initCrawlerTaskManager();
		project.setOrgName("apache");
		project.setProjectName("lucene");
		project.setName("lucene");
		crawl.setProject(project);
		crawl.needLog = true;
		((MHonArcCrawler)crawl).setProjectMailBaseUrl("https://lists.debian.org/debian-announce/1994/threads.html");
		crawl.crawlerType = Crawler.MAIN;
		
		ThreadManager.addCrawlerTask(crawl);
		crawl.join();
		ThreadManager.finishCrawlerTaskManager();
		System.out.println("ok1");
	}
}
