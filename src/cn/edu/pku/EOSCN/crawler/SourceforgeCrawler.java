package cn.edu.pku.EOSCN.crawler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Path;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.RemarkNode;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.ParagraphTag;
import org.htmlparser.tags.Span;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.tags.BodyTag;
import org.htmlparser.tags.Bullet;
import org.htmlparser.tags.BulletList;
import org.htmlparser.tags.Div;
import org.htmlparser.tags.HeadingTag;
import org.htmlparser.tags.LinkTag;

import cn.edu.pku.EOSCN.business.ThreadManager;
import cn.edu.pku.EOSCN.config.Config;
import cn.edu.pku.EOSCN.crawler.util.FileOperation.FileUtil;
import cn.edu.pku.EOSCN.crawler.util.UrlOperation.HtmlDownloader;
import cn.edu.pku.EOSCN.entity.Project;
import cn.edu.pku.EOSCN.HtmlTextParser.*;

import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SourceforgeCrawler extends Crawler {
	private final String SF_URL_BASE = "https://sourceforge.net/";
	
	private final String SF_URL_PREFIX = "https://sourceforge.net/p/";
	
	private final String SF_URL_POSTFIX_QUERY = "?source=navbar";
	
	private final String SF_URL_POSTFIX_BUG = "bugs/";
	private final String SF_URL_POSTFIX_PATCH = "patchs/";
	private final String SF_URL_POSTFIX_FEATUREREQUEST = "feature-requests/";
	
	
	private final String SF_URL_PROJECT_HOME_PAGE = "https://sourceforge.net/projects/projectName/";
	private final String SF_URL_PROJECT_FILES = "https://sourceforge.net/projects/projectName/files";
	private final String SF_URL_PROJECT_BUGS = "";
	private final String SF_URL_PROJECT_PATCHS = "";
	private final String SF_URL_PROJECT_VIEWS = "https://sourceforge.net/projects/projectName/reviews";
	
	//this url is used to get user view about a project
	//the keywords projectName and offsetNumber when you wang to use it.
	private final String SF_URL_USER_VIEW = "https://sourceforge.net/projects/projectName/reviews?source=navbar&amp;offset=offsetNumber#reviews-n-ratings";
	
	//all the file has the same prefix for the file's url.
	//the key words mirrorWebSit and projectName should be replaced when you want to use is.
	private final String SF_URL_DOWNLOAD = "http://mirrorWebSite.dl.sourceforge.net/project/projectName";
	
	//this url has been linked the a xml file contianning the information about the files' stroage url
	//the key words projectName should be replaced when you want to use is.
	private final String SF_URL_RSS_INFORMATION = "https://sourceforge.net/projects/projectName/rss?path=";
	
	private final String DOWNLOAD_INFO_FILE = "\\download.txt";
	
	private final String SF_PATH_SEPARATE_CHAR = "/";
	
	
	private String projectName; 
	private String projectBaseUrl;
	
	private String storageSummaryPath;
	private String storageBasePath;
	private String storageFilePath;
	private String storageBugPath;
	private String storagePatchPath;
	private String storageViewPath;
	private String storageFeatureRequestPath;
	
	
	
	
	public List<String> urlList = new ArrayList<String>();
	
	protected static final Logger logger = Logger.getLogger(SourceforgeCrawler.class.getName());
	
	
	public static enum Tickets{
		BUG,
		PATCH,
		FEATURE_REQUEST
	}
	
	public SourceforgeCrawler(String projectName , String projectBaseUrl , String storageBasePath)
	{
		this.projectName = projectName;
		this.projectBaseUrl = projectBaseUrl;
		this.storageBasePath = storageBasePath;
		
		if(storageBasePath.charAt(storageBasePath.length() - 1) == '\\'){
			storageBasePath = storageBasePath.substring(0, storageBasePath.length() - 1);
		}
		storageBasePath = storageBasePath + "\\SourceForge";
		updateAllStorePath();
	}
	
	private void updateAllStorePath(){
		
		
		this.makeDir(storageBasePath);
		
		storageSummaryPath = storageBasePath + "\\summary";
		this.makeDir(storageSummaryPath);
		
		//make the directory for bugs
		storageBugPath = storageBasePath + "\\bugs";
		this.makeDir(storageBugPath);
		
		//make the directory for patchs
		storagePatchPath = storageBasePath + "\\patchs";
		this.makeDir(storagePatchPath);
		
		//make the directory for feature-requests
		storageFeatureRequestPath = storageBasePath + "\\features-request";
		this.makeDir(storageFeatureRequestPath);
		
		storageFilePath = storageBasePath + "\\files";
		
	}
	
	/**
	 * Make a directory for storage
	 * @param path : the directory path
	 * @return if success ,return tru e ; else ,return false
	 */
	public boolean makeDir(String path){
		boolean result = false;
		
		File file = new File(path);
		if(!file.exists()){
			result = file.mkdirs();
		}
		else if(file.isDirectory()){
			result = true;
			logger.warn("warnning : There has been a diretory with the same path :\"" + path +"\"");
		}
		else if(file.isFile()){
			result = false;
			logger.error("error : There has been a file with the same name :\"" + path +"\"");
		}
		
		return result;
	}
	
	@Override
	public void init(){
		
		updateAllStorePath();
		
		/*storageBasePath = String.format("%s%c%s%c%s" , 
				Config.getTempDir() , 
				Path.SEPARATOR , 
				this.getProject().getName() + "_" + projectName , 
				Path.SEPARATOR , 
				this.getClass().getName() );*/
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

	public static void main(String[] args) throws ParserException, IOException{
		
		System.out.println("start");
		SourceforgeCrawler crawler = new SourceforgeCrawler("npppluginmgr", "https://sourceforge.net" , "d:\\");
		
		///crawler.getOneOfTheTickets(Tickets.BUG);
		//crawler.getOneOfTheTickets(Tickets.PATCH);
		//crawler.getBugs();
		
		//crawler.getBug("https://sourceforge.net/p/sevenzip/bugs/1664/" , "d:\\");
		
		//crawler.findAllInfomationKindForProject("sevenzip");
		crawler.crawlReviews();
		
		/*String file = HtmlDownloader.downloadOrin("http://jaist.dl.sourceforge.net/project/npppluginmgr/xml/plugins.zip", null);
		
		File f = new File("d:\\plugins.zip");
		System.out.print(file.length());
		System.out.print(file.getBytes());
		OutputStream  out = new FileOutputStream(f);
		
		//out.write(file.getBytes("GBK"));
		
		URL url = new URL("http://jaist.dl.sourceforge.net/project/npppluginmgr/xml/plugins.zip");
		URLConnection con = url.openConnection();
		
		InputStream in = con.getInputStream();
		byte[] bytes = new byte[20000];
		int length = 0;
		while((length = in.read(bytes)) != -1){
			out.write(bytes , 0 , length);
		}
		System.out.println();
		*/
 	}
	
	public static void test(Node node , String header){
		
		
	}
	
	public Map<String , String> readDownloadInfo(String file){
		Map<String , String> result = new HashMap<String , String>();
		File downloadInfoFile = new File(file);
		
		try{
			if(downloadInfoFile.exists() && downloadInfoFile.isFile()){
				BufferedReader reader = new BufferedReader(new FileReader(downloadInfoFile));
				String line ;
				
				//filter the first line of the file : ITEM SERIAL NUMBER --> URL
				reader.readLine();
				while((line = reader.readLine()) != null){
					String[] splits = line.split(" --> ");
					result.put(splits[0], splits[1]);
				}
			}
			else{
				result = null;
			}
		}
		catch(Exception e){
			logger.error(e.getMessage());
			System.out.println(e.getMessage());
			result = null;
		}
		
		return result;
	}
	
	public void writeDownloadInfo(String file , Map<String , String>downloadInfo){
		
		Map<String , String> result = new HashMap<String , String>();
		try{
			
			RandomAccessFile downloadInfoFile = new RandomAccessFile(file , "rw");
			downloadInfoFile.seek(downloadInfoFile.length());
			if(downloadInfoFile.length() == 0){
				downloadInfoFile.write(("ITEM SERIAL NUMBER --> URL\n").getBytes());
			}
			for(String itemSerialNumber : downloadInfo.keySet()){
				downloadInfoFile.write((itemSerialNumber + " --> " + downloadInfo.get(itemSerialNumber).toString() + "\n").getBytes());
			}
		
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			logger.error(e.getMessage());
		}
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
	public void getOneOfTheTickets(Tickets ticket){
		
		//The original url for one ticket for a project
		String oriUrl = null;
		//The resource storage path for a tickets
		String storagePath = null ;
		//The download info file
		String downloadInfoFile = null;
		
		
		//The filter is used to filter one kind of html tag
		NodeClassFilter filter;
		
		int totalItem = 0; // the total number of item for a kind of ticket resource
		int itemUpMostSerialNumber = 0; // the largest serial number for constructing item url
		
		String itemHtmlText = null;
		
		Map<String , String> todoList = new HashMap<String , String>();
		Map<String , String> completeList = new HashMap<String , String>();
		
		switch(ticket) {
			case BUG:{
				oriUrl = SF_URL_PREFIX + projectName + "/" + SF_URL_POSTFIX_BUG + SF_URL_POSTFIX_QUERY;
				storagePath = storageBugPath;
				break;
			}
			case PATCH:{
				oriUrl = SF_URL_PREFIX + projectName + "/" + SF_URL_POSTFIX_PATCH + SF_URL_POSTFIX_QUERY;
				storagePath = storagePatchPath;
				break;
			}
			case FEATURE_REQUEST:{
				oriUrl = SF_URL_PREFIX + projectName + "/" + SF_URL_POSTFIX_FEATUREREQUEST + SF_URL_POSTFIX_QUERY;
				break;
			}
			default :{
				;
			}
			
		}
		downloadInfoFile = storagePath + "\\" + DOWNLOAD_INFO_FILE_NAME;
		
		
		try{
			
			
			
			/**
			 * int the html , there will be a paragraph tag <p>Showing  *** results of number </p>
			 * the number indicates how many bugs are there in the project .
			 */
			/**
			 * code read : warnning, the pattern has not been constructed correctly
			 */
			itemHtmlText = HtmlDownloader.downloadOrin(oriUrl, null);
			filter = new NodeClassFilter(ParagraphTag.class);
			Pattern pattern = Pattern.compile("results[\\s]+of[\\s]+([0-9]+)");
			HtmlTextParser htmlTextParser = new HtmlTextParser(itemHtmlText);
			List<String> set = htmlTextParser.parseHtml(filter , pattern);
			int sizess = set.size();
			//there should be only one paragraph tag containning "results of number " 
			//else , it means the matcher pattern has some wrong matching
			if(set.size() == 1){
				for(String string : set){
					Matcher matcher = pattern.matcher(string);
					matcher.find();			
					totalItem = Integer.parseInt( matcher.group(1) );			
					//System.out.println(bugCount);
				}
				
				//get the biggest number of the bug ,being used to constructed the bug url
				pattern = Pattern.compile("/p/" + projectName + "/bugs/([0-9]+)");
				Matcher matcher = pattern.matcher(itemHtmlText);
				
				while(matcher.find()){
					int temp = Integer.parseInt(matcher.group(1));
					if(temp > itemUpMostSerialNumber ){
						itemUpMostSerialNumber = temp;
					}
				}
				
				Map<String , String> downloadHistory = readDownloadInfo(downloadInfoFile);
				if(downloadHistory == null){
					downloadHistory = new HashMap<String , String>();
				}
				
				for(int bugIndex = 0 ; bugIndex <= itemUpMostSerialNumber ; bugIndex ++){
					String newBugUrl = SF_URL_PREFIX + projectName + "/" + SF_URL_POSTFIX_BUG + bugIndex;
					//String newBugUrl = "https://sourceforge.net/p/" + projectName + "/bugs/" + bugIndex + "/";
					if(!downloadHistory.containsKey(bugIndex + "")){
						todoList.put(bugIndex + "", newBugUrl);
					}
					
				}
				//todoList.put("0",SF_URL_PREFIX + projectName + "/" + SF_URL_POSTFIX_BUG + "0" );
				//todoList.put("1",SF_URL_PREFIX + projectName + "/" + SF_URL_POSTFIX_BUG + "1" );
				for(String itemSerialNumber : todoList.keySet()){
					try{

						if(crawlItem(todoList.get(itemSerialNumber) , storagePath)){
							totalItem -- ;
							completeList.put(itemSerialNumber + ""  , todoList.get(itemSerialNumber));
						}
					}
					catch(Exception e){
						logger.error(e.getMessage());
						System.out.println(e.getMessage());
						continue;
					}	
				}
			}
					
		}catch(Exception e){
			logger.error("error from class \"SourceforgeCrawler\" when crawl data for " + projectName);
			logger.error(e.getMessage());
		}finally{
			writeDownloadInfo(downloadInfoFile , completeList);
		}
		
	}
	
	/**
	 * this fuction is used to crawl the one html page 
	 * including bugs , patchs ...
	 * all these items' html page has the same page format
	 * 
	 * For example , one bug url may fit the format "https://sourceforge.net/p/sevenzip/bugs/309"
	 * sevenzip is the project name , 309 is the serial number of the bug
	 * 
	 * @param itemUrl , the url to crawl
	 * @param storePath , the store path
	 * @return
	 */
	public boolean crawlItem(String itemUrl , String storePath){
		
		try{
			String itemText = HtmlDownloader.downloadOrin(itemUrl , null);
			if(itemText == null || itemText.length() == 0)
				return false ;
			
			
			//get the serial number of the item ,which is used to construct the path for the item storing
			String bugNumber = null;
			String[] temps = itemUrl.split("/");
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
			Parser parser = Parser.createParser(itemText, "UTF-8");
			NodeList list = parser.extractAllNodesThatMatch(filter);
			int NodeCount = list.size();
			String contentStr;
			int matchedNumber = 0 ;
			String itemTitle = null;
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
							itemTitle = p.corvertText(temp);
						}
					}
				}
			}
			if(matchedNumber > 1){
				itemTitle = "#" + bugNumber;
			}
			
			//set the bug file's name
			
			String fileAbsolutePath;
			if(storePath.charAt( storePath.length() - 1) == '\\'){
				fileAbsolutePath = storePath + itemTitle;
			}else{
				fileAbsolutePath = storePath + "\\" + itemTitle;
			}
			
			System.out.println(fileAbsolutePath);
			byte[] textContent = itemText.getBytes();
			int length = itemText.length();
			fileAbsolutePath = getValidBugFileName(storageBugPath , itemTitle);
			System.out.println(fileAbsolutePath);
			
			File bug = new File(fileAbsolutePath);
			if(!bug.exists()){
				bug.createNewFile();
			}
			FileOutputStream writer = new FileOutputStream(bug);
			writer.write(textContent, 0, itemText.length());
				
		}catch(Exception e){
			logger.error("error from class \"SourceforgeCrawler\" when get bug: " + itemUrl + "\n");
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

	public Map<String , String> findAllInfomationKindForProject(String projectName){
		Map<String , String> result = new  HashMap<String , String>();
		String infoKind = null;
		String infoURL = null;
		String mainPageURL = SF_URL_PROJECT_HOME_PAGE.replace("projectName", projectName);
		String mainPageText = null;
		Parser mainPageParser = null;
		NodeClassFilter divFilter = new NodeClassFilter(Div.class);
		NodeClassFilter ulFilter = new NodeClassFilter(BulletList.class);
		NodeClassFilter liFilter = new NodeClassFilter(Bullet.class);
		NodeClassFilter linkTagFilter = new NodeClassFilter(LinkTag.class);
		
		try{
			mainPageText = HtmlDownloader.downloadOrin(mainPageURL, null);
			if(true || mainPageText.length() >0 ){
				//mainPageParser = new Parser("C:\\Users\\曹生\\Desktop\\nppp.html");//(mainPageText);
				mainPageParser = new Parser(mainPageText);
				NodeList list = mainPageParser.extractAllNodesThatMatch(divFilter);
				NodeIterator iterator = list.elements();
				while(iterator.hasMoreNodes()){
					Div node = (Div)iterator.nextNode();
					
					 
					if(node.getAttribute("id") != null && node.getAttribute("id").compareTo("top_nav_admin") == 0){
						Div node_top_nav_admin = node;
						NodeList childList_top_nav_admin = node_top_nav_admin.getChildren();
						int size_top_nav_admin = childList_top_nav_admin.size();
						for(int i = 0 ; i < size_top_nav_admin ; i++){
							
							//find a <ul> </ul> tag
							if(ulFilter.accept(childList_top_nav_admin.elementAt(i))){
								NodeList childList_infoKind = childList_top_nav_admin.elementAt(i).getChildren();
								
								int size_infoKind = childList_infoKind.size();
								for(int j = 0 ; j < size_infoKind ; j ++){
									if(liFilter.accept(childList_infoKind.elementAt(j))){
										infoURL = "";
										Bullet node_infoKind = (Bullet)childList_infoKind.elementAt(j);
										LinkTag link = (LinkTag)node_infoKind.getFirstChild().getNextSibling();
										infoKind = ((Span)link.getFirstChild().getNextSibling()).getStringText().trim();
										if(infoKind.contains("▾")){
											String infoKindHead = infoKind.replaceAll(" ▾", "_");
											BulletList sub = (BulletList)link.getNextSibling().getNextSibling();
											NodeList subKind = sub.getChildren();
											int subSize = subKind.size();
											for(int k = 0 ; k < subSize ; k++){
												if(subKind.elementAt(k) instanceof Bullet){
													Bullet subChild = (Bullet)subKind.elementAt(k);
													LinkTag subInfo =(LinkTag) subChild.getFirstChild().getNextSibling();
													infoURL = subInfo.getLink();
													infoURL = infoURL.substring(1);
													infoURL = SF_URL_BASE + infoURL;
													infoKind = infoKindHead + subInfo.getStringText().trim();
													
													result.put(infoKind, infoURL);
													System.out.println(infoKind + " --> " + infoURL);
												}
											}
										}
										else{
											infoURL = link.getLink();
											infoURL = infoURL.substring(1);
											infoURL = SF_URL_BASE + infoURL ;
											result.put(infoKind, infoURL);
											System.out.println(infoKind + "  --> " + infoURL);
										}
										
										
									}
									
									
								}
								break;
							}
						}
					
							
					}
				}
			}
			
		}
		catch(Exception e){
			logger.error(e.getMessage());
			result = null;
		}
		
		
		
		return result;
	}

	/**
	 * getFilesURL : parameter xmlFileURL is linked to a xml file contain all the files' storage url information
	 * 				 when get this  xml file , we can find every single file's download page
	 * 				 then , we can get the mirror web site in which the file has been stored,
	 * 				 so , we can use the mirror name , to construct the real url for the file
	 * 
	 * @param xmlFile : the file contains the files information , which is downloaded from sf.
	 * @return Map<String , String> , in the format <fileRelativeStorePath , fileURL>
	 */
	public Map<String , String> getFilesURL(String xmlFileURL){
	
		Map<String , String> result = new HashMap<String , String>();
		Document fileParser ;
		org.w3c.dom.Element root ;
		org.w3c.dom.NodeList nodeList;
		org.w3c.dom.Node filePathNode;
		org.w3c.dom.Node fileURLNode;
		
		String filePath;
		String fileURL;
		
		try{
			fileParser = (Document)DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFileURL);
			root = (Element)fileParser.getDocumentElement();
			
			//every single file's information is inclueded by a pair of <item> and </item>
			nodeList = fileParser.getElementsByTagName("item");
			int length = nodeList.getLength();
			for(int i = 0 ; i < length ; i++){
				org.w3c.dom.Node node = nodeList.item(i);
				org.w3c.dom.NodeList childList = node.getChildNodes();
				org.w3c.dom.Node tempNode = null;
				int childLength = childList.getLength();
				filePath = fileURL = null;
				for(int j = 0 ; j < childLength ; j ++ ){
					tempNode = childList.item(j);
					
					//single file's relative storage path is included by a pair of <title> and </title>
					if(tempNode != null && tempNode.getNodeName().compareTo("title") == 0){
						filePath = tempNode.getTextContent().trim();
					
					}
					//single file's download page url is included by a pair of <link> and </link> 
					else if(tempNode != null && tempNode.getNodeName().compareTo("link") == 0){
						//this fileURL links to the single file's download page 
						//in this download page , the file's storage url is generated by java script , which can not be easy to get
						//so , when we get the download page , we need to find the  mirror web site  in which the file has been stroage 
						//then ,we can use the mirror web site's name to construct the real download url.
						fileURL = tempNode.getTextContent().trim();
						
						String downloadPage = HtmlDownloader.downloadOrin(fileURL, null);
						NodeClassFilter linkFilter = new NodeClassFilter(LinkTag.class);
						Parser downloadPageParser = new Parser(downloadPage);
						NodeList linkList = downloadPageParser.extractAllNodesThatMatch(linkFilter);
						
						int linkLength = linkList.size();
						for(int loop = 0 ; loop < linkLength ; loop++){
							LinkTag link = (LinkTag)linkList.elementAt(loop);
							//the linktag's content contains "direct link"
							if(link.getStringText().replace("\n" , "").compareTo("direct link") == 0){
								fileURL = link.getLink();
								
								//get the mirror web site name
								Pattern pattern = Pattern.compile("use_mirror=([a-z]+)");
								Matcher matcher = pattern.matcher(fileURL);
								matcher.find();
								String mirror = matcher.group(1);
								
								//construct the url
								fileURL = SF_URL_DOWNLOAD.replace("mirrorWebSite", mirror);
								fileURL = fileURL.replace("projectName", projectName);
								fileURL += filePath;
							}
						}
						break;
					}
					
				}
				if(filePath != null && fileURL != null){
					result.put(fileURL , filePath);
					//System.out.println(fileURL + " " + filePath);
				}
			}
			
		}catch(Exception e){
			logger.error(e.getMessage());
		}
		return result;
	}

	public void crawlSummary(){
		String homePageURL = SF_URL_PROJECT_HOME_PAGE.replace("projectName", projectName);
		String homePageText = null;
		Parser homePageParser = null;
		NodeClassFilter pFilter = new NodeClassFilter(ParagraphTag.class);
		NodeClassFilter linkFilter = new NodeClassFilter(LinkTag.class);
		
		try{
			homePageText = HtmlDownloader.downloadOrin(homePageURL, null);
			
			//get the home page on sourceforge for the project 
			if(homePageText.length() > 0){
				//get the home page on sourceforge fro the project 
				File sf_homepageFile = new File(storageSummaryPath + "sfMainPage.html");
				BufferedWriter writer = new BufferedWriter(new FileWriter(sf_homepageFile));
				writer.write(homePageText);
				writer.close();
				
				//get the description on sourceforge for the project
				homePageParser = new Parser(homePageText);
				NodeList pNodeList = homePageParser.extractAllNodesThatMatch(pFilter);
				int length = pNodeList.size();
				for(int i = 0 ; i < length ; i++){
					ParagraphTag pNode = (ParagraphTag)(pNodeList.elementAt(i));
					if(pNode.getAttribute("id").compareTo("description") == 0){
						String description = pNode.getStringText();
						File sf_descriptionFile = new File(storageSummaryPath + "sfDescription.txt");
						writer = new BufferedWriter(new FileWriter(sf_descriptionFile));
						writer.write(description);
						writer.close();
					}
				}
				
				NodeList linkNodeList = homePageParser.extractAllNodesThatMatch(linkFilter);
				length = linkNodeList.size();
				for(int i = 0 ; i < length ; i ++){
					LinkTag linkNode = (LinkTag)linkNodeList.elementAt(i);
					if(linkNode.getAttribute("id").compareTo("homepage") == 0){
						String projectHomePageURL = linkNode.getLink();
						
						//the home page is same with the sf_homepage
						if(projectHomePageURL.contains("sourceforge.net")){
							break;
						}						
						String projectHomePageText = HtmlDownloader.downloadOrin(projectHomePageURL, null);
						
						File projectHomePageFile = new File(storageSummaryPath + "homePage.html");
						
						writer = new BufferedWriter(new FileWriter(projectHomePageFile));
						writer.write(projectHomePageText);
						writer.close();
					}
				}
			}
			
			
			
			
		}
		catch(Exception e){
			logger.error(e.getMessage());
		}
	}
	
	public void crawlFiles(){
		//storageFilePath = "d:\\test";
		String downloadInfoFile = storageFilePath + DOWNLOAD_INFO_FILE;
		//<fileURL , fileName>
		Map<String , String> hasDownloadedInfo = readDownloadInfo(downloadInfoFile);
		Map<String , String> downloadInfo ;
		try{
			String filesPageText = HtmlDownloader.downloadOrin("https://sourceforge.net/projects/npppluginmgr/rss?path=/", null);
			//String filesPageText = HtmlDownloader.downloadOrin(SF_URL_PROJECT_FILES, null);
			
			downloadInfo = getFilesURL(SF_URL_RSS_INFORMATION.replace("projectName" , projectName));
			
			if(hasDownloadedInfo != null){
				for(String key : hasDownloadedInfo.keySet()){
					if(downloadInfo.containsKey(key)){
						downloadInfo.remove(key);
					}
				}
			}
			String filePath = storageFilePath ;
			
			for(String key : downloadInfo.keySet()){
				String fileName = null;
				String fileRelativePath = downloadInfo.get(key);
				String[] pathSplit = fileRelativePath.split(SF_PATH_SEPARATE_CHAR);
				
				filePath = storageFilePath;
				if(pathSplit.length > 1){
					int i ;
					for(i = 1 ; i < pathSplit.length - 1 ; i++){
						filePath += ("\\" + pathSplit[i]);
					}
					fileName = pathSplit[i];
				}
				else{
					//there is no SF_PATH_SEPARATE_CHAR in fileRelativePath 
					//that means the fileRelativePath is the file name
					filePath = storageFilePath;
					fileName = fileRelativePath;
				}
				
				makeDir(filePath);
				File file = new File(filePath + "\\" + fileName);
				if(file.exists()){
					logger.error("error : there is the file with the same path" + filePath + "\\" + fileName);
				}
				else{
					
					URL file_url = new URL(key);
					URLConnection connection = file_url.openConnection();
					connection.connect();
					
					InputStream in = connection.getInputStream();
					OutputStream out = new FileOutputStream(file);
					
					byte[] bytes = new byte[10240] ; // 10k
					int length = 0;
					while((length = in.read(bytes)) != -1){
						out.write(bytes , 0 , length);
					}
					out.close();
					in.close();
				}
			} 			
		}catch(Exception e){
			logger.error(e.getMessage());
		}
		
		
		
	}

	public void crawlReviews(){
		String viewPageText ;
		int viewCount = 0;
		storageViewPath = "d:\\test\\view";
		try
		{
			viewPageText = HtmlDownloader.downloadOrin(SF_URL_PROJECT_VIEWS.replace("projectName", projectName) , null);
			Parser parser = new Parser(viewPageText);
			TagNameFilter sectionFilter = new TagNameFilter("section");
			NodeList sectionNodeList = parser.extractAllNodesThatMatch(sectionFilter);
			int nodeListLength = sectionNodeList.size();
			for(int i = 0 ; i < nodeListLength ; i++){
				if(((LinkTag)sectionNodeList.elementAt(i)).getAttribute("id").compareTo("ratings") == 0){
					
					NodeList nodeList = ((LinkTag)sectionNodeList.elementAt(i)).get;
					int nodeLength = nodeList.size();
					for(int loop = 0 ; loop < nodeLength ; loop ++){
						TagNode temp = (TagNode)nodeList.elementAt(i);
						if(temp.getAttribute("class") != null && temp.getAttribute("class").compareTo("bargraph") == 0){
							NodeList childList = temp.getChildren();
							int childLength = childList.size();
							int rating = 5 ; //from 5 stars rating
							for(int j = 0 ; j < childLength ; j ++){
								Div node = (Div)childList.elementAt(j);
								if(node.getAttribute("class") != null && node.getAttribute("class").compareTo("stars-" + rating) == 0){
									String numberString ; 
									numberString = node.getStringText().trim();
									viewCount += Integer.parseInt(numberString);
									rating --; // rating is between 1 and 5 , it is does no matter when rating fall below 1
								}
								
							}
						}
					}
					
					
				}
			}
			
			//there will be 25 view items on a page , so 
			makeDir(storageViewPath + "\\userViews");
			for(int i = 0 ; i <= viewCount ; i = i + 25)
			{
				String urlString = SF_URL_USER_VIEW.replace("projectName", projectName);
				urlString = urlString.replace("offsetNumber", i + "");
				
				URL url = new URL(urlString);
				URLConnection connection = url.openConnection();
				
				InputStream in = connection.getInputStream();
				byte[] bytes = new byte[10240];// 10k
				
				OutputStream out = new FileOutputStream(new File(storageViewPath + "\\userViews\\" + i + "to" + i + 24  + ".html"));
				
				int length;
				while(( length = in.read(bytes) ) != -1){
					out.write(bytes , 0 , length);
				}
				
				
				  
			}
			
			
		}catch(Exception e){
			logger.error(e.getMessage());
		}
		
		
		
	}
	
	
	
	
	public void crawlBugs(){
		getOneOfTheTickets(Tickets.BUG);
	}
	
	public void crawlPatch(){
		getOneOfTheTickets(Tickets.PATCH);
	}
	
}	
	
	
	
	
	
	
	
	
	
	
	
	