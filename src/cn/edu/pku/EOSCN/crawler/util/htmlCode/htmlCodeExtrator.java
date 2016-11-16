package cn.edu.pku.EOSCN.crawler.util.htmlCode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.Path;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import cn.edu.pku.EOSCN.crawler.util.FileOperation.FileUtil;
import cn.edu.pku.EOSCN.crawler.util.htmlCode.HtmlPage.Segment;

public class htmlCodeExtrator {
	public static int codebytes = 0;
	public static int blogbytes = 0;
	public static String parseHtml(String html){
		if (!html.contains("<html") && !html.contains("<HTML")) return html;
		Document root = Jsoup.parse(html);
		Element body = root.getElementsByTag("body").first();
		String ret = parseElement(body);
		return ret + "\n";
	}
	
	public static String parseElement(Node root){
		String ret = "";
		if (root.nodeName().equals("br")) return "\n";
		if (root.nodeName().equals("span")){
			//System.out.println("asd");
		}
		if (root.nodeName().equals("#text")){
			return root.toString().replaceAll("\t+", " ").replaceAll(" +", " ");
		}
		if (root.nodeName().equals("div")) ret += "\n\n";
		if (root.nodeName().equals("tr")) ret += "\n";
		if (root.nodeName().equals("p")) ret += "\n\n";
		if (root.nodeName().equals("pre")) ret += "\n\n";
		for(Node child : root.childNodes()){
			ret += parseElement(child);
		}
		if (root.nodeName().equals("div")) ret += "\n\n";
		if (root.nodeName().equals("tr")) ret += "\n";
		if (root.nodeName().equals("p")) ret += "\n\n";
		if (root.nodeName().equals("pre")) ret += "\n\n";
		return ret;
	}
	
	public static List<String> getCode(String text){
		List<String> ret = new LinkedList<String>();
		return ret;
	}
	public static String process(String htmlStr) throws IOException{
		String ret = "";
		String page = parseHtml(htmlStr);
		page = page.replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&").
				replace("&quot;", "\"")
		.replace("&nbsp;", " ");
		blogbytes += page.length();
		HtmlPage html = new HtmlPage(page);
		html.process();
		for (Segment seg : html.segments){
			if (seg.getContentType() == Segment.CODE_CONTENT){
				ret += seg.getContentText();
				codebytes += seg.getContentText().length();
				ret += "***********\n";
			}
		}
		return ret;
	}
	public static void main(String args[])throws IOException{
		//File dir = new File("D:\\tmp\\get+similarity+between+two+documents+Lucene\\get+similarity+between+two+documents+Lucene");
		File dir = new File("D:\\tmp\\RelativeWeb");
		FileUtil.createPath(dir.getAbsolutePath() + Path.SEPARATOR + "code");
		for (File file : dir.listFiles()){
		try{
			if (file.isDirectory()) continue;
			if (!file.getName().startsWith("000")){
				//continue;
			}
			if (file.getName().contains("ppt")){
				continue;
			}
			if (file.getName().contains("pdf")){
				continue;
			}			
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String data = null;
			String htmlStr = "";
			while((data = br.readLine()) != null){
				htmlStr = htmlStr + data + "\n";
			}
			String ret = process(htmlStr);
			
			File f = new File(dir.getAbsolutePath()+Path.SEPARATOR+"code"+Path.SEPARATOR+file.getName());
			FileWriter fw = new FileWriter(f);
			fw.write(ret);
			fw.close();
		}catch (Exception e){
			e.printStackTrace();
		}
		}
		System.out.println(codebytes);
		System.out.println(blogbytes);
	}
}
