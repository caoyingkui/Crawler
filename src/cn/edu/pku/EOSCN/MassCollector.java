package cn.edu.pku.EOSCN;

import java.sql.SQLException;
import java.util.List;

import org.openqa.jetty.http.ResourceMetaData;

import cn.edu.pku.EOSCN.DAO.CrawlerTaskDao;
import cn.edu.pku.EOSCN.entity.CrawlerTask;
import cn.edu.pku.EOSCN.DAO.JDBCPool;
import cn.edu.pku.EOSCN.business.CrawlerBusiness;
import cn.edu.pku.EOSCN.business.CrawlerTaskManager;
import cn.edu.pku.EOSCN.crawler.Crawler;
import cn.edu.pku.EOSCN.entity.Project;

public class MassCollector {

	public static void StartNewTask(ResourceMetaData data, Project project) throws SQLException {
		if (data == null) {
			return;
		}
		System.out.println(project.getName() + ": CRAWLING " + data.getType() + " in " + data.getUrlListString() + " USING " + data.getCrawler());
		CrawlerTask crawlerTask = new CrawlerTask(project, data.getType());
		crawlerTask.setCrawlerNode("192.168.4.204:8080");
		CrawlerTaskDao.insertCrawlerTask(crawlerTask);
		crawlerTask = CrawlerTaskDao.getTask(crawlerTask);
		
		Crawler crawler = null;
		try {
			crawler = (Crawler) Class.forName(Crawler.class.getPackage().getName() + "." + data.getCrawler()).newInstance();
		} catch (Exception e) {
			System.out.println("Can't instantiate crawler class: " + data.getCrawler());
			return;
		}
		crawler.setProject(project);
		crawler.setUrlList(data.getBaseUrls());
		crawler.setCrawleruuid(crawlerTask.getUuid());
		crawler.isServerTask = false;

		try {
			crawler.init();
			crawler.Crawl();
		} catch (Exception e) {
			e.printStackTrace();
			CrawlerTaskManager.reportErrorTask(crawlerTask.getUuid(), e.getMessage());
		}
	}
	
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		JDBCPool.initPool();
		List<Project> projects = CrawlerBusiness.getAllProject();
		for (Project project : projects) {
			if (CrawlerTaskDao.isTaskCrawled(project, "RelativeWeb")) {
				continue;
			}
			if (project.getName().contains("Lucene")) {
//				StartNewTask(project.getResourceByType("RelativeWeb"), project);
			}
			StartNewTask(project.getResourceByType("RelativeWeb"), project);
		}
	}
}
