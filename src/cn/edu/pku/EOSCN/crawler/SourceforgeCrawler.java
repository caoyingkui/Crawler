package cn.edu.pku.EOSCN.crawler;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Path;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.ParagraphTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.tags.Div;
import org.htmlparser.tags.HeadingTag;

import cn.edu.pku.EOSCN.business.ThreadManager;
import cn.edu.pku.EOSCN.config.Config;
import cn.edu.pku.EOSCN.crawler.util.FileOperation.FileUtil;
import cn.edu.pku.EOSCN.crawler.util.UrlOperation.HtmlDownloader;
import cn.edu.pku.EOSCN.entity.Project;
import cn.edu.pku.EOSCN.HtmlTextParser.*;

public class SourceforgeCrawler extends Crawler {
	private String projectName; 
	private String projectBaseUrl;
	
	private String storageBasePath;
	private String storageBugPath;
	
	
	
	
	public List<String> urlList = new ArrayList<String>();
	
	protected static final Logger logger = Logger.getLogger(SourceforgeCrawler.class.getName());
	
	public SourceforgeCrawler(String projectName , String projectBaseUrl , String storageBasePath)
	{
		this.projectName = projectName;
		this.projectBaseUrl = projectBaseUrl;
		this.storageBasePath = storageBasePath;
		updateAllStorePath();
	}
	
	private void updateAllStorePath(){
		if(storageBasePath.charAt(storageBasePath.length() - 1) != '\\'){
			storageBasePath += "\\";
		}
		storageBugPath = storageBasePath + projectName + "\\bugs" ;
	}
	
	@Override
	public void init(){
		
		storageBasePath = String.format("%s%c%s%c%s" , 
				Config.getTempDir() , 
				Path.SEPARATOR , 
				this.getProject().getName() + "_" + projectName , 
				Path.SEPARATOR , 
				this.getClass().getName() );
	}

	@Override
	public void crawl_url() throws Exception {
		// TODO Auto-generated method stub
		Set<String> projectItemSet = new HashSet<String>();
		
		Pattern pattern  = Pattern.compile("/");
		Matcher matcher = pattern.matcher(projectBaseUrl);

		int length = projectBaseUrl.length() - 1;
		StringBuffer sb = new StringBuffer();
		while(matcher.find())
		{
			if(matcher.start() == length){
				matcher.appendReplacement(sb, "");
			}
		}
		matcher.appendTail(sb);
	
		Set<String> projectSet = new HashSet<String>();
		
		projectSet.add(projectBaseUrl + "something may");
		
		urlList.addAll(projectSet);
	}

	@Override
	public void crawl_middle(int id, Crawler crawler) {
		// TODO Auto-generated method stub
		SourceforgeCrawler sourceforgeCrawler = (SourceforgeCrawler) crawler;
		for(int i = 0 ; i < urlList.size() ; i++ ){
			if(i % this.subCrawlerNum == id){
				sourceforgeCrawler.urlList.add(this.urlList.get(i));
			}
		}
	}

	@Override
	public void crawl_data() {
		// TODO Auto-generated method stub
		for(String url : this.urlList){
			  String storagePath = this.storageBasePath + Path.SEPARATOR + url.replace("[<>\\/:*?]" , "");
			  System.out.println("Thread" + this.subid + "  " + url);
			  if(this.needLog){
				  if(FileUtil.logged(storagePath) && FileUtil.exist(storagePath)){
					  continue;
				  }else{
					  String text = HtmlDownloader.downloadOrin(url, null);
					  FileUtil.write(storagePath, text);
					  FileUtil.logging(storagePath);
				  }
			  }
			  else{
				  String text = HtmlDownloader.downloadOrin(url, null);
				  FileUtil.write(storagePath, text);
			  }
		}
	}

	public static void main(String[] args){
		
		System.out.println("start");
		SourceforgeCrawler crawler = new SourceforgeCrawler("sevenzip", "https://sourceforge.net" , "d:\\");
		
		crawler.getBugs();
		
		//crawler.getBug("https://sourceforge.net/p/sevenzip/bugs/1664/" , "d:\\");
		
		System.out.println("end");
	}
	
	
	/**
	 * in sourceforge , a project's base url for bugs is in format:https://sourceforge.net/p/corefonts/bugs/?source=projectName
	 * for example , for project navbar , the base url is https://sourceforge.net/p/corefonts/bugs/?source=navbar
	 * 
	 * in the bugs web , it will list all bugs with the following meta data info :(the pound sign # means the index number)
	 * # Summary  milestone  status owener  created   updated
	 * so ,we can construct the url for the 3rd bug as  https://sourceforge.net/p/corefonts/bugs/3/
	 *
	 */
	
	public void getBugs(){
		try{
			int bugCount = 0 ;
			
			//get the oriUrl
			String oriUrl = "https://sourceforge.net/p/" + projectName + "/bugs/?source=navbar" ;
			String htmlText = HtmlDownloader.downloadOrin(oriUrl , null);
			//System.out.println(htmlText);
			
			NodeClassFilter filter = new NodeClassFilter(ParagraphTag.class);
			
			/**
			 * code read : warnning, the pattern has not been constructed correctly
			 */
			
			
			//get the bug count 
			Pattern pattern = Pattern.compile("results[\\s]+of[\\s]+([0-9]+)");
			HtmlTextParser htmlTextParser = new HtmlTextParser(htmlText);
			List<String> set = htmlTextParser.parseHtml(filter , pattern);
			int sizess = set.size();
			if(set.size() == 1){
				for(String string : set){
					Matcher matcher = pattern.matcher(string);
					matcher.find();			
					bugCount = Integer.parseInt( matcher.group(1) );			
					System.out.println(bugCount);
				}
			}
			
			//get the biggest number of the bug ,being used to constructed the bug url
			pattern = Pattern.compile("/p/" + projectName + "/bugs/([0-9]+)");
			Matcher matcher = pattern.matcher(htmlText);
			
			int biggestBugNum = 0;
			while(matcher.find()){
				int temp = Integer.parseInt(matcher.group(1));
				if(temp > biggestBugNum ){
					biggestBugNum = temp;
				}
			}
			
			for(int bugIndex = 0 ; bugIndex <= biggestBugNum ; bugIndex ++){
				String newBugUrl = "https://sourceforge.net/p/" + projectName + "/bugs/" + bugIndex + "/";
				if(getBug(newBugUrl , storageBugPath)){
					bugCount -- ;
				}
				
			}
			
			if(bugCount != 0){
				System.out.println("there has been some url being lost when crawl the bug for the project " + projectName);
			}
			
		}catch(Exception e){
			logger.error("error from class \"SourceforgeCrawler\" when crawl data for " + projectName);
			logger.error(e.getMessage());
		}
		
	}
	
	public boolean getBug(String bugUrl , String storePath){
		
		try{
			String bugText = HtmlDownloader.downloadOrin(bugUrl , null);
			if(bugText.length() == 0)
				return false ;
			
			
			String bugNumber = null;
			String[] temps = bugUrl.split("/");
			Pattern matchBugNumberPattern = Pattern.compile("[0-9]+");
			for(int i = temps.length - 1 ; i > -1 ; i --){
				Matcher matcher = matchBugNumberPattern.matcher(temps[i]);
				if(matcher.find()){
					bugNumber = temps[i];
					break;
				}
			}
			
			/**
			 * I used a loose condition to match the title for the bug .
			 * so , there will be a chance that more than 1 string match the condition
			 * so , when there are only 1 string matching the condition , i will use the string as the bug file's name
			 * else , i will use it # for the bug as the bug file's name
			 */
			
			NodeClassFilter filter = new NodeClassFilter(HeadingTag.class);
			Parser parser = Parser.createParser(bugText, "UTF-8");
			NodeList list = parser.extractAllNodesThatMatch(filter);
			int NodeCount = list.size();
			String contentStr;
			int matchedNumber = 0 ;
			String bugTitle = null;
			for(int i = 0 ; i < NodeCount ; i++){
				contentStr =((HeadingTag)list.elementAt(i)).getStringText();
				//find what we want  , likd #56 , meaning the 56th bug
				if(contentStr.contains("#" + bugNumber)){
					matchedNumber ++;
					temps = contentStr.split("<!-- actions -->");
					for(String temp : temps){
						if(temp.contains("#" + bugNumber)){
							//remove the space character in the temp 
							HtmlTextParser p = new HtmlTextParser("");
							bugTitle = p.corvertText(temp);
						}
					}
				}
			}
			if(matchedNumber > 1){
				bugTitle = "#" + bugNumber;
			}
			
			//set the bug file's name
			
			String fileAbsolutePath;
			if(storePath.charAt( storePath.length() - 1) == '\\'){
				fileAbsolutePath = storePath + bugTitle;
			}else{
				fileAbsolutePath = storePath + "\\" + bugTitle;
			}
			
			System.out.println(fileAbsolutePath);
			byte[] textContent = bugText.getBytes();
			int length = bugText.length();
			fileAbsolutePath = getValidBugFileName(storageBugPath , bugTitle);
			System.out.println(fileAbsolutePath);
			
			File bug = new File(fileAbsolutePath);
			if(!bug.exists()){
				bug.createNewFile();
			}
			FileOutputStream writer = new FileOutputStream(bug);
			writer.write(textContent, 0, bugText.length());
				
		}catch(Exception e){
			logger.error("error from class \"SourceforgeCrawler\" when get bug: " + bugUrl + "\n");
			logger.error(e.getMessage());
		}
		
		return true;
		
	}
	
	public String getValidBugFileName(String storePath , String oriFileName){
		String result = null;
		
		if(storePath.charAt(storePath.length() - 1) != '\\'){
			storePath += "\\";
		}
		result = storePath ;
		Pattern pattern = Pattern.compile("[/\\\\\\?%\\*:\\|\"<>\\. ]+");
		Matcher matcher = pattern.matcher(oriFileName);
		
		StringBuffer sb = new StringBuffer();
		while(matcher.find()){
			matcher.appendReplacement(sb , " ");
		}
		matcher.appendTail(sb);
		
		result += sb.toString();
		HtmlTextParser htParser = new HtmlTextParser("");
		result = htParser.corvertText(result);
		
		result = result.trim();
		
		if(result.length() > 255)
			result = result.substring(result.length() - 255, result.length() - 1);
		
		return result;
	}

}	
	
	
	
	
	
	
	
	
	
	
	
	