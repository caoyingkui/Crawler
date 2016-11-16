package cn.edu.pku.EOSCN;

import java.sql.SQLException;
import java.util.List;

import cn.edu.pku.EOSCN.DAO.JDBCPool;
import cn.edu.pku.EOSCN.business.CrawlerBusiness;
import cn.edu.pku.EOSCN.entity.Project;

/**
 * 测试类
 * @author 张灵箫
 *
 */
public class TestUtil {
	
	/**
	 * 本地用main方法测试时，可以用该方法得到一个lucene的project对象
	 * @author 张灵箫
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 */
	public static Project getLuceneProject(){
		//插入lucene项目
		//Project luceneProject = new Project("Apache Lucene");
		//DAOUtils.update(connection, "INSERT INTO project (uuid, name, programmingLanguage, description, hostUrl) VALUES (?,?,?,?,?)",
        //                 luceneProject.getUuid(), luceneProject.getName(),Project.JAVA , "Apache LuceneTM is a high-performance, full-featured text search engine library written entirely in Java. It is a technology suitable for nearly any application that requires full-text search, especially cross-platform.", "http://lucene.apache.org/core/" );
		try {
			JDBCPool.initPool();
			CrawlerBusiness pBusiness = new CrawlerBusiness();
//			return pBusiness.getProjectByUuid("b7914db3-caa7-4d70-96cd-bd4b5b4ed029");
			return pBusiness.getProjectByUuid("f5a7e31b-aa3d-413a-9ad6-1eb13b4125a6");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JDBCPool.shutDown();
			return null;
		}
	}

	/**
	 * @author 张灵箫
	 * 打印项目全部信息
	 * @param project
	 */
	public static void printProjectInfo(Project project) {
		System.out.println("==================this is project info=================");
		System.out.println(project.getName());
		System.out.println(project.getHostUrl());
		System.out.println(project.getProgrammingLanguage());
		System.out.println(project.getDescription());
	
		System.out.println("==================this is project info=================");
	}
	
	public static void main(String[] args) {
		//String aString  = "a\\?b";
	}
}
