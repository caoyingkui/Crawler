/**
 * 
 */
package cn.edu.pku.EOSCN.crawler;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Path;

import cn.edu.pku.EOSCN.config.Config;
import cn.edu.pku.EOSCN.crawler.util.UrlOperation.URLReader;
import cn.edu.pku.EOSCN.entity.Project;

/**
 * @author nijinan
 *
 */
public abstract class GitCrawler extends Crawler {
	protected static final String gitApiBaseUrl = 
			"https://api.github.com/repos";
	private String storageBasePath;	
	private String apiBaseUrl;
	protected static final String gitToken = 
			"client_id=fa5191bf55e754d6d25b&client_secret=226fc9193e753ca8f69fd8d9279577e4a9c5448c";
	public GitCrawler() {
		super();
	}


	/* (non-Javadoc)
	 * @see cn.edu.pku.EhuyaOSCN.crawler.Crawler#init()
	 */
	@Override
	public void init() {
		// TODO Auto-generated method stub
		storageBasePath = String.format("%s%c%s%c%s", 
				Config.getTempDir(),
				Path.SEPARATOR,
				this.getProject().getName(),
				Path.SEPARATOR,
				this.getClass().getName());
	
		apiBaseUrl = String.format("%s/%s/%s", 
				GitCrawler.gitApiBaseUrl,
				this.getProject().getOrgName(),
				this.getProject().getProjectName());
		
	}


	@Override
	public void crawl_url()throws Exception {
		
	}

	public String getApiBaseUrl() {
		return apiBaseUrl;
	}


	public String getStorageBasePath() {
		return storageBasePath;
	}


}
