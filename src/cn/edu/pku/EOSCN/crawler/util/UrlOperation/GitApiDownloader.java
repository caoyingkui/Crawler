package cn.edu.pku.EOSCN.crawler.util.UrlOperation;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import cn.edu.pku.EOSCN.crawler.util.FileOperation.FileUtil;

public class GitApiDownloader {
	protected static final Logger logger = Logger.getLogger(GitApiDownloader.class.getName());
	public static File downloadFromUrl(String url, String storagePath) {
		String tempPath = storagePath;
		//File tempFile = FileUtil.createFile(tempPath,url.substring(url.lastIndexOf('/') + 1));
		File tempFile = FileUtil.createFile(tempPath);
		logger.info("download url content to file path : " + tempPath);
		
		try {
			URL httpurl = new URL(url);
			FileUtils.copyURLToFile(httpurl,tempFile);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tempFile;
	}
	public static String downloadOrin(String url, Map<String, List<String>> headers){
		String content = HtmlDownloader.downloadOrin(url,headers);
		return content;
	}
}
