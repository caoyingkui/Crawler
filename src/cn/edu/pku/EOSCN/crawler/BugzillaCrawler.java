package cn.edu.pku.EOSCN.crawler;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.eclipse.core.runtime.Path;

import cn.edu.pku.EOSCN.DAO.JDBCPool;
import cn.edu.pku.EOSCN.business.CrawlerTaskManager;
import cn.edu.pku.EOSCN.business.ThreadManager;
import cn.edu.pku.EOSCN.config.Config;
import cn.edu.pku.EOSCN.crawler.util.FileOperation.FileUtil;
import cn.edu.pku.EOSCN.crawler.util.UrlOperation.HtmlDownloader;
import cn.edu.pku.EOSCN.entity.Project;

/** 
  * @author Jinan Ni E-mail: nijinan@pku.edu.cn
  * @date 2016年8月22日 下午4:01:33 
  * @version 1.0   */
public class BugzillaCrawler extends Crawler {
	private String storageBasePath;
	private String projectBugzillaBaseUrl;
	private static final String[] BUG_STATUS = {"UNCONFIRMED","CONFIRMED",
				"ACCEPTED","REOPENED","RESOLVED","VERIFIED","CLOSED", "ASSIGNED", "NEW"};
	private static final String BUG_STATUS_TEMPLATE = 
			"%s/buglist.cgi?chfieldfrom=%s&ctype=csv";
	private static final String CHANGE_DATE_TEMPLATE = 
			"%s/buglist.cgi?chfieldfrom=%s&ctype=csv&order=bug_id";
	private static final String SINGLE_BUG_TEMPLATE = 
			"%s/show_bug.cgi?id=%s&ctype=xml";	
	private List<String> bugList = new ArrayList<String>();
	private Set<String> bugSet = new HashSet<String>();
	public boolean increment = false;
	@Override
	public void init() throws Exception {
		// TODO Auto-generated method stub
		projectBugzillaBaseUrl = this.getEntrys();
		storageBasePath = String.format("%s%c%s%c%s", 
				Config.getTempDir(),
				Path.SEPARATOR,
				this.getProject().getName(),
				Path.SEPARATOR,
				this.getClass().getName());		
	}

	public String getCSV(String date){
		// TODO Auto-generated method stub
		String url = String.format(CHANGE_DATE_TEMPLATE, projectBugzillaBaseUrl,date);
		String text;
		String storagePath = storageBasePath + Path.SEPARATOR + "BugList"+date+".csv";
		if (this.needLog){
			if (FileUtil.logged(storagePath)){
				text = FileUtil.read(storagePath);
			}else {
				text = HtmlDownloader.downloadOrin(url,null);
				if (!text.startsWith("bug")) text = "";
				FileUtil.write(storagePath,text);
				FileUtil.logging(storagePath);
			}
		}else{
			text = HtmlDownloader.downloadOrin(url,null);
			if (!text.startsWith("bug")) text = "";
			FileUtil.write(storagePath,text);
		}
		return text;
	}
	
	public String getBugList(String csv){
		if (csv.length() < 10){return null;}
		Object [] HEADER = {"bug_id","changeddate"};
		CSVFormat csvFileFormat = CSVFormat.DEFAULT.DEFAULT;
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			CSVParser csvFileParser = CSVParser.parse(csv, csvFileFormat);
			List<CSVRecord> csvRecords = csvFileParser.getRecords(); 
            for (int i = 1; i < csvRecords.size(); i++) {
                CSVRecord record = csvRecords.get(i);
            	String id = record.get(0);
            	String dateStr = record.get(7);
        		Date dateTrans = sdf.parse(dateStr);
        		if (date == null) date = dateTrans; 
        		else{
        			if (dateTrans.after(date)){
        				date = dateTrans;
        			}
        		}
            	bugList.add(id);
            }
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return format.format(date);
	}
	
	public void saveBugList(String date){
		StringBuffer content = new StringBuffer();
		content.append(date);
		for (String s : bugList){
			bugSet.add(s);
		}
		bugList.clear();
		for (String s : bugSet){
			content.append("\n"+s);
			bugList.add(s);
		}
		FileUtil.write(storageBasePath + Path.SEPARATOR + "BUGLIST.txt", content.toString());
	}
	
	public String loadBugList(){
		String content = FileUtil.read(storageBasePath + Path.SEPARATOR + "BUGLIST.txt");
		String[] strs = content.split("\n");
		for (int i = 1; i < strs.length; i++){
			if (strs[i].length() > 0){
				bugList.add(strs[i]);
			}
		}
		return strs[0];
	}
	
	@Override
	public void crawl_url() throws Exception {
		if (!FileUtil.exist(storageBasePath, "BugList.txt")){
			String csv = this.getCSV("1000-01-01");
			String dateStr = this.getBugList(csv);
			if (dateStr == null) return;
			this.saveBugList(dateStr);
			this.increment = true;
		}else{
			String dateStr = this.loadBugList();
			String csv = this.getCSV(dateStr);
			if (increment) bugList.clear();
			dateStr = this.getBugList(csv);
			if (dateStr == null) return;
			this.saveBugList(dateStr);
		}
	}

	@Override
	public void crawl_middle(int id, Crawler crawler) {
		// TODO Auto-generated method stub
//		https://bugzilla.mozilla.org/buglist.cgi?chfieldfrom=2016-08-01&chfieldto=2016-08-31&query_format=advanced&type=csv
		((BugzillaCrawler)crawler).projectBugzillaBaseUrl = this.projectBugzillaBaseUrl;
		int cnt = 0;
		for (String str : this.bugList){
			if (cnt % this.subCrawlerNum == id){
				((BugzillaCrawler)crawler).bugList.add(str);
			}
			cnt++;
		}
	}

	@Override
	public void crawl_data() {
		// TODO Auto-generated method stub
		for (String id : bugList){
			String url = String.format(SINGLE_BUG_TEMPLATE, projectBugzillaBaseUrl,id);
			String text;
			String storagePath = storageBasePath + Path.SEPARATOR + id + ".xml";
			if (this.needLog){
				if (FileUtil.logged(storagePath) && !this.increment){
					text = FileUtil.read(storagePath);
				}else {
					text = HtmlDownloader.downloadOrin(url,null);
					FileUtil.write(storagePath,text);
					FileUtil.logging(storagePath);
				}
			}else{
				text = HtmlDownloader.downloadOrin(url,null);
				FileUtil.write(storagePath,text);
			}
		}
	}
	
	public void setProjectBugzillaBaseUrl(String projectBugzillaBaseUrl) {
		this.projectBugzillaBaseUrl = projectBugzillaBaseUrl;
	}
	
	public static void main(String[] args) throws InterruptedException, ParseException, ClassNotFoundException, SQLException{
		BugzillaCrawler crawl = new BugzillaCrawler();
		Project project = new Project();
		ThreadManager.initCrawlerTaskManager();
		JDBCPool.initPool();
		project.setOrgName("apache");
		project.setProjectName("lucene");
		project.setName("lucene");
		CrawlerTaskManager.createCrawlerTask(project, "Bugzilla");
		crawl.setProject(project);
		crawl.needLog = true;
		crawl.crawlerType = Crawler.MAIN;
		
		
		crawl.setProjectBugzillaBaseUrl("https://bugs.mageia.org");
		ThreadManager.addCrawlerTask(crawl);
		//ThreadManager.addCrawlerTask(crawl1);
		//sleep(10000);
		crawl.join();
		ThreadManager.finishCrawlerTaskManager();
		JDBCPool.shutDown();
		System.out.println("ok1");
	}


}
