package cn.edu.pku.EOSCN.DAO;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import cn.edu.pku.EOSCN.DAO.DAOUtils;
import cn.edu.pku.EOSCN.entity.CrawlerTask;
import cn.edu.pku.EOSCN.entity.Project;


public class CrawlerTaskDao {

	public static int updateTaskStatus(String uuid, int status, Date finishTime, String errorLog) throws SQLException {
		int result = DAOUtils.update("UPDATE crawlerTask SET status = ?, finishTime = ?, errorLog = ? WHERE uuid = ?", 
				status, finishTime, errorLog, uuid);
		return result;
	}
	
	public static int insertCrawlerTask(CrawlerTask crawlerTask) throws SQLException {
		int result = DAOUtils.update("INSERT INTO crawlerTask (uuid, projectUuid, crawlerNode, resourceType, startTime, urls) VALUES (?,?,?,?,?,?)",
		                         crawlerTask.getUuid(), crawlerTask.getProjectUuid(), crawlerTask.getCrawlerNode(), crawlerTask.getResourceType(), crawlerTask.getStartTime(), crawlerTask.getEntrys());
		return result;
	}
	
	public static CrawlerTask getTaskByUuid(String uuid) throws SQLException{
		List<CrawlerTask> crawlerTasks = DAOUtils.getResult(CrawlerTask.class, "select * from crawlerTask where uuid = ? ", uuid);
		return crawlerTasks.get(0);
	}
	
	public static CrawlerTask getTask(CrawlerTask crawlerTask) throws SQLException {
		List<CrawlerTask> crawlerTasks = DAOUtils.getResult(CrawlerTask.class, "select * from crawlerTask where projectUuid = ? AND resourceType = ? AND startTime = ?",
				crawlerTask.getProjectUuid(), crawlerTask.getResourceType(), crawlerTask.getStartTime());
		return crawlerTasks.get(0);
	}
	
	public static boolean isTaskCrawled(Project project, String resourceType) throws SQLException {
		List<CrawlerTask> crawlerTasks = DAOUtils.getResult(CrawlerTask.class, "select * from crawlerTask where projectUuid = ? AND resourceType = ?",
				project.getUuid(), resourceType);
		if (crawlerTasks.size() > 0) {
			return true;
		}
		return false;
	}
}
