package cn.edu.pku.EOSCN.entity;

import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

import cn.edu.pku.EOSCN.DAO.ProjectDAO;
import cn.edu.pku.EOSCN.crawler.Crawler;

public class CrawlerTask {

	public static final int WAITING = 0;
	public static final int SUCCESS = 1;
	public static final int ERROR = -1;
	public static final int IN_PROGRESS = 2;
	
	private String uuid;
	private String projectUuid;
	private String crawlerNode = "127.0.0.1";
	private String resourceType;
	private Date startTime = new Date();
	private Date finishTime = null;
	private String entrys = "";
	private int status = WAITING;
	
	public CrawlerTask(){}
	
	public CrawlerTask(Project project, String resoucetype) {
		this.projectUuid = project.getUuid();
		this.resourceType = resoucetype;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getProjectUuid() {
		return projectUuid;
	}
	public void setProjectUuid(String projectUuid) {
		this.projectUuid = projectUuid;
	}
	public String getCrawlerNode() {
		return crawlerNode;
	}
	public void setCrawlerNode(String crawlerNode) {
		this.crawlerNode = crawlerNode;
	}
	public String getResourceType() {
		return resourceType;
	}
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getFinishTime() {
		return finishTime;
	}
	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Crawler toCrawler(){
		if (resourceType == null) return null;
		Crawler crawler = null;
		try {
			crawler = (Crawler) Class.forName(Crawler.class.getPackage().getName() + "." + resourceType +"Crawler").newInstance();
			Project project = ProjectDAO.getProjectByUuid(projectUuid);
			if (project == null) return null;
			crawler.setProject(project);
			crawler.setCrawleruuid(uuid);
			crawler.setStatus(status);
			crawler.setEntrys(entrys);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return crawler;
	}

	public String getEntrys() {
		return entrys;
	}

	public void setEntrys(String entrys) {
		this.entrys = entrys;
	}
}
