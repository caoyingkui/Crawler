package cn.edu.pku.EOSCN.crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Path;
import org.json.JSONArray;
import org.json.JSONObject;

import cn.edu.pku.EOSCN.business.ThreadManager;
import cn.edu.pku.EOSCN.config.Config;
import cn.edu.pku.EOSCN.crawler.util.FileOperation.FileUtil;
import cn.edu.pku.EOSCN.crawler.util.UrlOperation.HtmlDownloader;
import cn.edu.pku.EOSCN.entity.Project;

public class JiraIssueCrawler extends Crawler {
	private String storageBasePath;
	private String projectJiraBaseUrl;
	private String hostStr;
	private String projectStr;
	public List<String> urls = new ArrayList<String>();
	private static final int DEFAULT_MAX_RESULTS = 100;//JIRA's maximum items per request
	
	private static final String TOTAL_ISSUE_NUM_URL_TEMPLATE = 
			"%s/rest/api/2/search?jql=project=%s&maxResults=0";
	private static final String ISSUE_METADATA_URL_TEMPLATE = 
			"%s/rest/api/2/search?jql=project=%s&startAt=%d&maxResults=%d&fields=id,key";//(projectName,startAt,maxResults)
	private static final String ISSUE_URL_TEMPLATE = 
			"%s/rest/api/2/issue/%s?expand=changelog";//(issue id or issue name)
	private static final String PATCH_URL_TEMPLATE = "%s/secure/attachment/%s/%s";//(patch id, patch name)	
	@Override
	public void init() throws Exception {
		// TODO Auto-generated method stub
		storageBasePath = String.format("%s%c%s%c%s", 
				Config.getTempDir(),
				Path.SEPARATOR,
				this.getProject().getName(),
				Path.SEPARATOR,
				this.getClass().getName());		 
	  	 String urlString = projectJiraBaseUrl;
 		 hostStr = urlString.split("/browse/")[0];
 		 projectStr = urlString.split("/browse/")[1];
	}

	@Override
	public void crawl_url() throws Exception {
		// TODO Auto-generated method stub
		int stepNum = 100;//设置一个文件里包含的bug数
		int totalNum = Integer.parseInt(getTotal());//获得项目的总bug数
		int startAt = 0;		
		for(; startAt < totalNum-stepNum; startAt += stepNum){			
			String crawUrl = String.format(ISSUE_METADATA_URL_TEMPLATE, hostStr,projectStr,startAt,DEFAULT_MAX_RESULTS);
			System.out.println("crawUrl is : " + crawUrl.toString());
			crawlWeb(crawUrl, storageBasePath + Path.SEPARATOR + "index" + Path.SEPARATOR + startAt + "-" + (startAt+stepNum) + ".json");//写入文件
		}
		if(startAt < totalNum){
			//GetJsonUrl getJsonUrl = new GetJsonUrl();
			String crawUrl = String.format(ISSUE_METADATA_URL_TEMPLATE, hostStr,projectStr,startAt,DEFAULT_MAX_RESULTS);
			//System.out.println(crawlUrl);
			crawlWeb(crawUrl, storageBasePath + Path.SEPARATOR + "index" + Path.SEPARATOR + startAt + "-" + totalNum + ".json");//写入文件
		}	
		File dirIndex = new File(storageBasePath + Path.SEPARATOR + "index");
		for (File file : dirIndex.listFiles()){
			if (file.getName().startsWith("log")) continue;
			String content = FileUtil.read(file.getAbsolutePath());
			JSONObject json = new JSONObject(content);
			JSONArray array = (JSONArray) json.get("issues");
			int issueNum = array.length();
			for(int i=0;i<issueNum;i++){
				JSONObject issue = array.getJSONObject(i);
				String issueId = issue.getString("id");
				this.urls.add(issueId);
			}
		}		
	}
	@Override
	public void crawl_middle(int id, Crawler crawler){
		JiraIssueCrawler mboxCrawler = (JiraIssueCrawler) crawler;
		mboxCrawler.projectJiraBaseUrl = this.projectJiraBaseUrl;
		for (int i = 0; i < urls.size(); i++){
			if (i % this.subCrawlerNum == id){
				mboxCrawler.urls.add(this.urls.get(i));
			}
		}
	} 
	@Override
	public void crawl_data() {
		// TODO Auto-generated method stub
		for (String url : urls){
			String storage = this.storageBasePath + Path.SEPARATOR + url + ".txt";
			crawlWeb(String.format(this.ISSUE_URL_TEMPLATE, hostStr,url),storage);
			String text = FileUtil.read(storage);
			JSONObject root = new JSONObject(text);
			JSONObject changelogObj = root.getJSONObject("changelog");
			String issueId = root.getString("id");
			JSONArray historiesArr = changelogObj.getJSONArray("histories");
			int hisNum = historiesArr.length();
			for(int i=0;i<hisNum;i++){
				JSONObject hisObj = historiesArr.getJSONObject(i);
				JSONArray hisItems = hisObj.getJSONArray("items");
				int hisItemNum = hisItems.length();
				
				//For a patch created info, "to" stands for patchId and "toString" stands for patchName.  
				for(int j=0;j<hisItemNum;j++){
					JSONObject hisItem = hisItems.getJSONObject(j);
					//get patch id if it is a patch created info
					if(hisItem.isNull("to")){//special check for Nullable field before getting its value.
						continue;
					}
					String patchId = hisItem.getString("to");
					//Not a POSITIVE Long indicates that it is not a patch created info
					if(!patchId.matches("^\\d{1,19}$")){
						continue;
					}
					//get patch name if it is a patch created info
					if(hisItem.isNull("toString")){//special check for Nullable field before getting its value.
						continue;
					}
					String patchName = hisItem.getString("toString");
					//Not a string which ends with ".patch" represents that it is not a patch created info 
					if(!patchName.endsWith(".patch")){
						continue;
					}
					crawlWeb(String.format(this.PATCH_URL_TEMPLATE, this.hostStr,patchId,patchName),
							this.storageBasePath + Path.SEPARATOR + "Patchs" + Path.SEPARATOR + patchId+"_"+patchName);

				}
			}			
		}
	}
	
	public void crawlWeb(String wUrl, String storagePath) {
		try {
			if (this.needLog){
				if (FileUtil.logged(storagePath)){
					return;
				}else{
					String text = HtmlDownloader.downloadOrin(wUrl,null);
					FileUtil.write(storagePath, text);
					FileUtil.logging(storagePath);
				}
			}else{
				String text = HtmlDownloader.downloadOrin(wUrl,null);
				FileUtil.write(storagePath, text);				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	private String getTotal(){			
        String totalNum = null;
		String temURL = String.format(TOTAL_ISSUE_NUM_URL_TEMPLATE,this.hostStr, this.projectStr);//转换成可以取得totalNum的api网址             		
		String text = HtmlDownloader.downloadOrin(temURL,null);        	
        //打开URL
        String patt = "\"total\":([0-9]+)";//用正则表达式匹配total的值
        Pattern pattern = Pattern.compile(patt);
        String strTemp;
        Matcher matcher = pattern.matcher(text);
        if(matcher.find()) {//如果找到了total
        	totalNum = matcher.group();//赋值给largestId
        }
        totalNum = totalNum.replace("\"total\":", "");
		return totalNum;//返回total
	}

	public String getProjectJiraBaseUrl() {
		return projectJiraBaseUrl;
	}

	public void setProjectJiraBaseUrl(String projectJiraBaseUrl) {
		this.projectJiraBaseUrl = projectJiraBaseUrl;
	}
	public static void main(String[] args) throws Exception{
		JiraIssueCrawler crawl = new JiraIssueCrawler();
		Project project = new Project();
		project.setOrgName("apache");
		project.setProjectName("lucene");
		project.setName("lucene");
		crawl.setProject(project);
		crawl.setProjectJiraBaseUrl("https://issues.apache.org/jira/browse/LUCENE");
		ThreadManager.initCrawlerTaskManager();
		crawl.needLog = true;
		crawl.crawlerType = Crawler.MAIN;
		ThreadManager.addCrawlerTask(crawl);
		crawl.join();
		ThreadManager.finishCrawlerTaskManager();
	}
}
