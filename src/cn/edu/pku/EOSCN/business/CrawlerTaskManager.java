package cn.edu.pku.EOSCN.business;

import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;

import cn.edu.pku.EOSCN.DAO.CrawlerTaskDao;
import cn.edu.pku.EOSCN.DAO.JDBCPool;
import cn.edu.pku.EOSCN.DAO.ProjectDAO;
import cn.edu.pku.EOSCN.crawler.Crawler;
import cn.edu.pku.EOSCN.entity.CrawlerTask;
import cn.edu.pku.EOSCN.entity.Project;

/**
 * 线程池类, 为每个爬虫单独维护一个线程
 * @author 张灵箫
 *
 */
public class CrawlerTaskManager {
	protected static final Logger logger = Logger.getLogger(ThisReference.class.getName());
	private static final String[] CRAWLER_NAMES = {"Bugzilla","Google","WebDoc"};//"GitAchive","GitCommits","GitTags"};
	
	public static void addCrawlerTask(Crawler crawler){
		try {
			CrawlerTaskDao.insertCrawlerTask(crawler.toCrawlerTask());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void reportCompletedTask(String taskuuid){
		try {
			CrawlerTaskDao.updateTaskStatus(taskuuid, CrawlerTask.SUCCESS, new Date(), null);
		} catch (SQLException e) {
			logger.info("database error: task " + taskuuid + "completed but not reported!");
			e.printStackTrace();
		}
	}
	
	public static void reportErrorTask(String taskuuid, String errorLog){
		try {
			CrawlerTaskDao.updateTaskStatus(taskuuid, CrawlerTask.ERROR, new Date(), errorLog);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void changeCrawlerStatus(Crawler crawler){
		
	}

	public static void resumeCrawlerTask(String uuid){
		try {
			CrawlerTask crawlerTask = CrawlerTaskDao.getTaskByUuid(uuid);
			Crawler crawler = crawlerTask.toCrawler();
			if (crawler != null) ThreadManager.addCrawlerTask(crawler);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void startCrawlerTasks(Project project){
		for (String crawlerName : CrawlerTaskManager.CRAWLER_NAMES){
			Crawler crawler = createCrawlerTask(project, crawlerName).toCrawler();
			if (crawler != null){
				addCrawlerTask(crawler);
			}
		}
	}
	
	public static CrawlerTask createCrawlerTask(Project project, String crawlerName){
		CrawlerTask crawlerTask = new CrawlerTask();
		crawlerTask.setProjectUuid(project.getUuid());
		crawlerTask.setResourceType(crawlerName);
		crawlerTask.setUuid(UUID.randomUUID().toString());
		return crawlerTask;
	}
	public static void main(String[] args) throws SQLException, ClassNotFoundException{
		JDBCPool.initPool();
//		Project project = new Project();
//		project.setName("lucene");
//		project.setUuid(UUID.randomUUID().toString());
//		ProjectDAO.insertProject(project);
//		project = ProjectDAO.getProjectByUuid(project.getUuid());
//		CrawlerTask crawlerTask = createCrawlerTask(project,"Bugzilla");
//		crawlerTask.setEntrys("https://bugs.mageia.org");
//		CrawlerTaskDao.insertCrawlerTask(crawlerTask);
		InitBusiness.initEOS();
		resumeCrawlerTask("51b75efd-3d44-41ff-98c3-44be58e2d1f6");
		ThreadManager.finishCrawlerTaskManager();
		//startCrawlerTasks(project);
		//set crawler entry
		//String uuid = null;
		//resumeCrawlerTask(uuid);
		JDBCPool.shutDown();
	}
}
