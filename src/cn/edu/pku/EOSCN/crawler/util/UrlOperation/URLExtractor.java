package cn.edu.pku.EOSCN.crawler.util.UrlOperation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.pku.EOSCN.entity.CrawlerURL;


/**
* @ClassName: URLExtractor 

* @Description: 以一个html网页的源码和所在网页的URL为输入，返回一个List<String>，包含了源码中所有的URL（不重复）

* @author Huazb (huazb1989@126.com) Software Engineering Institute, Peking
 *          University, China

* @date 2013-5-26 下午03:38:30 

* 
 


 */
public class URLExtractor {
	/** 多次使用的话不需要重新编译正则表达式了，对于频繁调用能提高效率 */
	public static final String patternString1 = "[^\\s]*((<\\s*[aA]\\s+(href\\s*=[^>]+\\s*)>)(.*)</[aA]>).*";
	public static final String patternString2 = ".*(<\\s*[aA]\\s+(href\\s*=[^>]+\\s*)>(.*)</[aA]>).*";
	//public static final String patternString3 = ".*href\\s*=\\s*(\"|'|)http://.*";
	public static final String patternString3 = ".*href\\s*=\\s*(\"|'|).*";
	public static Pattern pattern1 = Pattern.compile(patternString1,
			Pattern.DOTALL);
	public static Pattern pattern2 = Pattern.compile(patternString2,
			Pattern.DOTALL);
	public static Pattern pattern3 = Pattern.compile(patternString3,
			Pattern.DOTALL);
	
	
	/**
	* @Title: getAllUrls 
	
	* @Description: 从一个网页源码中提取所有不重复的超链URL 
	
	* @param @param htmlSource    提取的网页源码	
	* @param @param fatherUrl	     网页的URL，以供在使用相对地址时进行拼接
	* @param @return    设定文件 
	
	* @return List<String>    返回类型 
	
	* @author: Huazb (huazb1989@126.com) Software Engineering Institute, Peking
	 *          University, China
	
	* @throws 
	
	 */
	public static  List<CrawlerURL> getAllUrls(String htmlSource,String fatherUrl,String homeURL)
	{
		
		
		Set<CrawlerURL> set = new HashSet<CrawlerURL>();
		/** 解析url并保存在set里 */
		parseUrl(set, htmlSource);
		/** 针对解析出来的url做处理 */
		return(replaceHtml(set, htmlSource,fatherUrl,homeURL));
	}
	

	/** 给每个url加上target属性 */
	public static List<CrawlerURL> replaceHtml(Set<CrawlerURL> set, String var,String fatherUrl,String homeURL) {
		List<CrawlerURL> result = new ArrayList<CrawlerURL>();
		/** 最好不要对参数修改 */
		
		Iterator<CrawlerURL> ite = set.iterator();
		String urlPrefix,sonUrl,fullUrlPrefix;
		if (fatherUrl.matches("http(s)?://[^/]*")){
			fatherUrl = fatherUrl + "/";
		}
		int lastSlash=fatherUrl.lastIndexOf("/");
		urlPrefix=fatherUrl.substring(0,lastSlash+1);
		fullUrlPrefix=fatherUrl;
		System.out.println("urlprefix="+urlPrefix);
		CrawlerURL crawlerURL;
		//System.out.println(urlPrefix);
		while (ite.hasNext()) {
			crawlerURL = ite.next();
		//	System.out.println("url="+url);
			if (crawlerURL != null) {
				//sonUrl=url.substring(5, url.length());
				
				sonUrl=crawlerURL.getUrl();
				
				sonUrl=sonUrl.split(" ")[0];
			//	System.out.println("b="+sonUrl);
			//	System.out.println("Raw url="+sonUrl);
				sonUrl=sonUrl.replaceAll("\"","");
			//	System.out.println("a="+sonUrl);
			//	System.out.println("sonURL="+sonUrl);
				
				
				//????????????????localhost转换问题  有些url拼接问题
				
				//System.out.println("sonurl:\t" + sonUrl);
				
				if (!sonUrl.startsWith("http"))
				{
					if (sonUrl.contains("#")){
						sonUrl="";
						//sonUrl=fullUrlPrefix+sonUrl;		//实际上是同一页 可以考虑不加入这个url 重要！！！
					//	System.out.println(1);
					}
				//	else if (!sonUrl.startsWith(".")){
					else{
						sonUrl=urlPrefix+sonUrl;	
					
					}
					//	System.out.println("2");
					//}
					
				}
				if (sonUrl.startsWith("http://svn")) sonUrl="";		//暂时不加入svn
				else if (sonUrl.startsWith("http://localhost"))
				{
					//System.out.println(3);
					sonUrl=sonUrl.substring(7, sonUrl.length()-1);
					//System.out.println("local status="+sonUrl);
					int temp=sonUrl.indexOf("/");
					sonUrl=sonUrl.substring(temp+1,sonUrl.length()-1);
					sonUrl=homeURL+sonUrl;
					//System.out.println("local status="+sonUrl);
					}
				crawlerURL.setUrl(sonUrl);
				//System.out.println("out URL="+sonUrl);
				//System.out.println("url name="+crawlerURL.getDocName());
				if (!sonUrl.equals("")) 	result.add(crawlerURL);

			}
		}
		return result;
	}

	public static void parseUrl(Set<CrawlerURL> set, String var) {
		Matcher matcher = null;
		String result = null;
		// 假设最短的a标签链接为 <a href=http://www.a.cn></a>则计算他的长度为28
		CrawlerURL crawlerURL;
		final String regex = "<a.*?/a>";
		int i=0;
		 final Pattern pt = Pattern.compile(regex);  
		  final Matcher mt = pt.matcher(var);  
		  while (mt.find()) {  
		//   System.out.println(mt.group());  
		   i++;  
		   crawlerURL=new CrawlerURL();
		   // 获取标题  
		   final Matcher title = Pattern.compile(">.*?</a>").matcher(mt.group());  
		   while (title.find()) {  
		    //System.out.println("标题:"   + title.group().replaceAll(">|</a>", ""));  
			   crawlerURL.setDocName(title.group().replaceAll(">|</a>", ""));
		   }  
		  
		   // 获取网址  
		   final Matcher myurl = Pattern.compile("href=.*?>").matcher(mt.group());  
		   while (myurl.find()) {  
		 //   System.out.println("网址:"    + myurl.group().replaceAll("href=|>", ""));
		    crawlerURL.setUrl(myurl.group().replaceAll("href=|>", ""));
		    set.add(crawlerURL);
		   }  
		  
		   //System.out.println();  
		}
		

	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		/** 测试的数据 */
		String ss = "这是测试<a href=\"http://www.google.cn\" class=\"ss\">www.google.cn</a><a href=http://www.baidu.cn>www.google.cn</a>"
			+"<a href=core/org/apache/lucene/codecs/lucene42/package-summary.html#package_description>www.google.cn</a><a href=http://www.goossgle.cn>www.google.cn</a>是测试";
		
		
		//ss=URLReader.getHtmlContent("http://lucene.apache.org/core/4_3_0/index.html");
		//ss="<a href=\"http://www.google.cn\" class=\"ss\">www.google.cn</a>";
		//String fatherUrl="http://lucene.apache.org/core/4_3_0/index.html";
		String fatherUrl="http://wiki.apache.org/solr/SolrCloud";
		ss=URLReader.getHtmlContent(fatherUrl);
	//	System.out.println(ss);
		/** 保存提取出来的url,用set从某种程度去重，只是字面上，至于语义那就要需要考虑很多了 */
		//Set<String> set = new HashSet<String>();
		/** 解析url并保存在set里 */
		//parseUrl(set, ss);
		/** 针对解析出来的url做处理 */
		//System.out.println(replaceHtml(set, ss));
	//	System.out.println(ss);
		List<CrawlerURL> tempList=getAllUrls(ss,fatherUrl,"http://wiki.apache.org/");
		for (int i=0;i<tempList.size();i++)
		{
			System.out.println(tempList.get(i).getUrl());
			System.out.println("name=="+tempList.get(i).getDocName());
		}

	}
}


