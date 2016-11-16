package cn.edu.pku.EOSCN.crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Path;
import org.json.JSONArray;
import org.json.JSONObject;

import cn.edu.pku.EOSCN.crawler.util.FileOperation.FileUtil;
import cn.edu.pku.EOSCN.crawler.util.UrlOperation.GitApiDownloader;
import cn.edu.pku.EOSCN.crawler.util.UrlOperation.URLReader;
import cn.edu.pku.EOSCN.entity.Project;

public class GitTagsCrawler extends GitCrawler {

	private List<String> tagsJsonPaths;
	
	public GitTagsCrawler() {
		// TODO Auto-generated constructor stub
		super();
	}

	public void crawl_url() throws Exception{
		String tagsUrl = 
				String.format("%s/%s",this.getApiBaseUrl(),"tags");
		int page = 0;
		tagsJsonPaths = new LinkedList<String>();
		while (true){
			page ++;
			String storagePath = 
					String.format("%s%c%s%d%s", 
							this.getStorageBasePath(),Path.SEPARATOR,
							"tags",page,".json");
			if (this.needLog){
				if (FileUtil.logged(storagePath))
					this.tagsJsonPaths.add(storagePath);
					continue;
			}
			String url = String.format("%s?page=%d&%s", tagsUrl,page,GitCrawler.gitToken);
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
			this.tagsJsonPaths.add(storagePath);
			FileUtil.write(storagePath, content);
		}
	}
	
	@Override
	public void crawl_middle(int id, Crawler crawler) {
		// TODO Auto-generated method stub
		GitTagsCrawler mboxCrawler = (GitTagsCrawler) crawler;
		for (int i = 0; i < tagsJsonPaths.size(); i++){
			if (i % this.subCrawlerNum == id){
				mboxCrawler.tagsJsonPaths.add(this.tagsJsonPaths.get(i));
			}
		}
	}
	
	@Override
	public void crawl_data(){
		// TODO Auto-generated method stub
		for (String tagsJsonPath : this.tagsJsonPaths){
			String s = FileUtil.read(tagsJsonPath);
			JSONArray ja = new JSONArray(s);
			for (int i = 0; i < ja.length(); i++){
				JSONObject jo = (JSONObject)ja.get(i);
				String name = (String) jo.get("name");
				String downloadUrl = (String) jo.get("zipball_url") + "?" + GitCrawler.gitToken; 
				String storagePath = 
						String.format("%s%c%s%c%s.zip", 
								this.getStorageBasePath(),Path.SEPARATOR,
								"tagsRelease",Path.SEPARATOR,
								name);
				FileUtil.createFile(storagePath);
				URLReader.downloadFromUrl(downloadUrl, storagePath);
			}
		}
	}
	public static void main(String args[]){
		Crawler crawl = new GitTagsCrawler();
		Project project = new Project();
		project.setOrgName("google");
		project.setProjectName("gson");
		project.setName(String.format("%s-%s",project.getOrgName(), project.getProjectName()));
		crawl.setProject(project);
		try {
			crawl.Crawl();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
