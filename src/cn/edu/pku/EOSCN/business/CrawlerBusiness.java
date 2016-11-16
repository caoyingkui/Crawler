package cn.edu.pku.EOSCN.business;

import java.sql.SQLException;
import java.util.List;


import org.apache.log4j.Logger;

import cn.edu.pku.EOSCN.DAO.JDBCPool;
import cn.edu.pku.EOSCN.DAO.ProjectDAO;
import cn.edu.pku.EOSCN.DAO.ResourceMetaDataDAO;
import cn.edu.pku.EOSCN.crawler.Crawler;
import cn.edu.pku.EOSCN.entity.Project; 

/**
 * project相关业务逻辑
 * @author 张灵箫
 *
 */
public class CrawlerBusiness {
	private static final Logger logger = Logger.getLogger(CrawlerBusiness.class.getName());
	private static final String REMOTE_SUCCESS_RESPONCE = "success";
	
	
	/**
	 * 根据uuid得到project对象，包含所有的resourceMetadata
	 * @author 张灵箫
	 * @param uuid
	 * @return
	 */
	public Project getProjectByUuid(String uuid) {
		Project project = null;
		ProjectDAO projectDAO = new ProjectDAO();
		try {
			project = projectDAO.getProjectByUuid(uuid);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
		return project;
	}
	
	public static List<Project> getAllProject() {
		List<Project> projects = null;
		try {
			projects = new ProjectDAO().getAllProject();
			for (Project project : projects) {
			}
		} catch (SQLException e) {
			System.out.println("database error!");
		}
		return projects;
	}
	
	
	/**
	 * @author 张灵箫
	 * 根据xml文件得到新的project对象，存入数据库
	 * @param xmlFilePath xml文件路径
	 * @return
	 */
	public Project createNewProjectFromXml(String xmlFilePath) {
		Project project = null;
		project = new Project(xmlFilePath);
		ProjectDAO projectDAO = new ProjectDAO();
		ResourceMetaDataDAO dataDAO = new ResourceMetaDataDAO();
		try {
			projectDAO.insertProject(project);
		} catch (SQLException e) {
			System.out.println("Database error!");
			e.printStackTrace();
		}
		return project;
	}
	
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		JDBCPool.initPool(); 
		List<Project> projects = CrawlerBusiness.getAllProject();
//		projects = projects.subList(0, 1);
		for (Project project : projects) {

		}
	}


}
