package cn.edu.pku.EOSCN.DAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;


import cn.edu.pku.EOSCN.entity.Project;

public class ProjectDAO {

	
	public static Project getProjectByUuid(String uuid) throws SQLException, NullPointerException{
		Project project = DAOUtils.getResult(Project.class, "select * from project where uuid = ?", uuid).get(0);
		return project;
	}
	
	public static List<Project> getAllProject() throws SQLException{
		return DAOUtils.getResult(Project.class, "select * from project");
	}


	public static int updateProjectInfo(String uuid, Project project) throws SQLException {
		int result = DAOUtils.update("UPDATE project SET name = ?, hostUrl = ?, programmingLanguage = ?, description = ? WHERE uuid = ?", 
				project.getName(), project.getHostUrl(), project.getProgrammingLanguage(), project.getDescription(), uuid);
		return result;
	}
	
	public static int insertProject(Project project) throws SQLException {
		int result = DAOUtils.update("INSERT INTO project (uuid, name, programmingLanguage, description, hostUrl) VALUES (?,?,?,?,?)",
		                         project.getUuid(), project.getName(), project.getProgrammingLanguage(), project.getDescription(), project.getHostUrl());

		return result;
	}
	
	public static void main(String[] args) {
		try {
			JDBCPool.initPool();
			System.out.println(new ProjectDAO().getProjectByUuid("b7914db3-caa7-4d70-96cd-bd4b5b4ed029").getDescription());
			JDBCPool.shutDown();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



}
