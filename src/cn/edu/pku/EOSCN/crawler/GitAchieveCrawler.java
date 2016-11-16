package cn.edu.pku.EOSCN.crawler;

import org.eclipse.core.runtime.Path;

import cn.edu.pku.EOSCN.crawler.util.UrlOperation.GitApiDownloader;
import cn.edu.pku.EOSCN.crawler.util.UrlOperation.URLReader;

public class GitAchieveCrawler extends GitCrawler {

	public GitAchieveCrawler() {
		// TODO Auto-generated constructor stub
		super();
	}

	@Override
	public void crawl_url() {
		String downloadUrl = 
				String.format("%s/%s", 
						this.getApiBaseUrl(),
						"zipball/master");
		String storagePath = 
				String.format("%s%c%s", 
						this.getStorageBasePath(),
						Path.SEPARATOR,
						"lastArchive.zip");
		GitApiDownloader.downloadFromUrl(downloadUrl, storagePath);
	}

	@Override
	public void crawl_middle(int id, Crawler crawler) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void crawl_data() {
		// TODO Auto-generated method stub
		
	}

}
