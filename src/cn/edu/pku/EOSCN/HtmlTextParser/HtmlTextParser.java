package cn.edu.pku.EOSCN.HtmlTextParser;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.tags.ParagraphTag;
import org.htmlparser.util.NodeList;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.log4j.Logger;

public class HtmlTextParser {
	private String htmlText;
	private String charSet ;
	
	private static final Logger logger = Logger.getLogger(HtmlTextParser.class.getName());
	
	public void setHtmlText(String htmlText){
		this.htmlText = htmlText;
		charSet = "UTF-8";
	}
	
	public String getHtmlText(){
		return htmlText;
	}
	
	public void setEncode(String charSet){
		this.charSet = charSet;
	}
	
	public String getEncode(){
		return charSet;
	}
	
	public HtmlTextParser(String htmlText){
		this.htmlText = htmlText;
		charSet = null;
	}
	

	public NodeList parseHtml(OrFilter filter){
		NodeList list = null;
		
		try{
			Parser parser = Parser.createParser(htmlText, "UTF-8");
			list = parser.extractAllNodesThatMatch(filter);
		}
		catch(IllegalArgumentException e){
			logger.error("error from class \"HtmlTextParser\" ,  the html text is null \n");
			logger.error(e.getMessage());
		}
		catch(Exception e){
			logger.error("error from class \"HtmlTextParser\" \n");
			logger.error(e.getMessage());
		}
		return list;
	}
	
	public List<String> parseHtml(NodeClassFilter filter , Pattern pattern){
		List<String> stringSet = new LinkedList<String>();
		try{
			Parser parser = Parser.createParser(htmlText, charSet);
			NodeList list = null ;
			list = parser.extractAllNodesThatMatch(filter);
			for(int i = 0 ; i < list.size() ; i ++){
				Node node = list.elementAt(i);
				ParagraphTag tag = (ParagraphTag)node;
				
				String nodeText = tag.getStringText();
				Matcher macher = pattern.matcher(nodeText);
				if(macher.find() )
					stringSet.add(nodeText);
			}
			
		}catch(IllegalArgumentException e){
			logger.error("error from class \"HtmlTextParser\" ,  the html text is null \n");
			logger.error(e.getMessage());
		}
		catch(Exception e){
			logger.error("error from class \"HtmlTextParser\" \n");
			logger.error(e.getMessage());
		}
		
		return stringSet;
	}
	
	/**
	 * this function is used to replace the space character : \f \n \r \t \v  in a string while a space ' ' 
	 * for example , the oriString = 
	 * "<strong>Showing
	 *  7
	 * results of 7 </strong>"
	 * it will be convert to "Showing 7 results of 7"
	 * 
	 * @param oriString is the original string
	 * @return the string has replaced the space character  with space ' '
	 */
	public String corvertText(String oriString){
		StringBuffer sb = new StringBuffer(); 
		
		try{
			Pattern pattern = Pattern.compile("[\\s]+");
			Matcher matcher = pattern.matcher(oriString);
			while(matcher.find()){
				matcher.appendReplacement(sb, " ");
			}
			matcher.appendTail(sb);
		}catch(Exception e){
			logger.error("error from class \"HtmlTextParser\" ");
			logger.error(e.getMessage());
		}
		return sb.toString();
	}
	
}









