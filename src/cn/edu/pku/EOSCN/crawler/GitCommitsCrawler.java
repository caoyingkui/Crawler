package cn.edu.pku.EOSCN.crawler;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Path;

import cn.edu.pku.EOSCN.crawler.util.FileOperation.FileUtil;
import cn.edu.pku.EOSCN.crawler.util.UrlOperation.GitApiDownloader;
import cn.edu.pku.EOSCN.crawler.util.UrlOperation.URLReader;
import cn.edu.pku.EOSCN.entity.Project;

public class GitCommitsCrawler extends GitCrawler {
	private List<String> commitsJsonPaths;
	
	public GitCommitsCrawler() {
		// TODO Auto-generated constructor stub
	}
	public void crawl_url() throws Exception{
		String commitsUrl = 
				String.format("%s/%s",this.getApiBaseUrl(),"commits");
		int page = 1;
		commitsJsonPaths = new LinkedList<String>();
		while (true){
			String storagePath = 
					String.format("%s%c%s%d%s", 
							this.getStorageBasePath(),Path.SEPARATOR,
							"commits",page,".json");
			if (this.needLog){
				if (FileUtil.logged(storagePath))
					this.commitsJsonPaths.add(storagePath);
					continue;
			}
			String url = String.format("%s?page=%d&%s", commitsUrl,page,GitCrawler.gitToken);
			//String url = String.format("%s?page=%d", commitsUrl,page);
			Map<String, List<String>> map = new HashMap<String,List<String>>();
			String content = GitApiDownloader.downloadOrin(url,map);
			List<String> list = map.get("X-RateLimit-Remaining");
			String remain = null;
			if (list != null) remain = list.get(0);
			System.out.println(remain);
			if ((remain != null)&&(remain.equals("0"))){
				sleep(60*1000);
			}
			if (content.length() == 0) continue;
			if (content.length() < 10) break; 
			this.commitsJsonPaths.add(storagePath);
			FileUtil.write(storagePath, content);
			page ++;
		}
	}
	
	@Override
	public void crawl_middle(int id, Crawler crawler) {
		// TODO Auto-generated method stub
		GitCommitsCrawler mboxCrawler = (GitCommitsCrawler) crawler;
		for (int i = 0; i < commitsJsonPaths.size(); i++){
			if (i % this.subCrawlerNum == id){
				mboxCrawler.commitsJsonPaths.add(this.commitsJsonPaths.get(i));
			}
		}
	}
	
	@Override
	public void crawl_data(){
		// TODO Auto-generated method stub
	}
	
	public static void main(String args[]){
		Crawler crawl = new GitCommitsCrawler();
		Project project = new Project();
		project.setOrgName("google");
		project.setProjectName("gson");
		project.setName(String.format("%s-%s",project.getOrgName(), project.getProjectName()));
		crawl.setProject(project);
		try {
			crawl.run();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
