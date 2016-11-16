package cn.edu.pku.EOSCN.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.edu.pku.EOSCN.TestUtil;
import cn.edu.pku.EOSCN.DAO.DAOUtils;
import cn.edu.pku.EOSCN.business.CrawlerBusiness;
import cn.edu.pku.EOSCN.entity.Project;

/**
 * Servlet implementation class Test
 * 用于测试的servlet
 * @author 张灵箫
 */
public class Test extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Test() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		CrawlerBusiness pb = new CrawlerBusiness();
		//pb.updateProjectInfoFromXML("b7914db3-caa7-4d70-96cd-bd4b5b4ed029");
		Project luceneProject = pb.getProjectByUuid("b7914db3-caa7-4d70-96cd-bd4b5b4ed029");
		TestUtil.printProjectInfo(luceneProject);
		//StorageUtil.getDocumentationsFilePath(luceneProject);
//		pb.crawlResource("test", luceneProject);
//		pb.crawlResource("test", luceneProject);
//		pb.crawlResource("test", luceneProject);
//		pb.crawlResource("test", luceneProject);
//		pb.crawlResource("test", luceneProject);
//		pb.crawlResource("test", luceneProject);
//		pb.crawlResource("test", luceneProject);
//		pb.crawlResource("test", luceneProject);
//		pb.crawlResource("test", luceneProject);
//		pb.crawlResource("test", luceneProject);
//		pb.crawlResource("test", luceneProject);
//		pb.crawlResource("test", luceneProject);
//		pb.crawlResource(ResourceMetaData.MAIL_TYPE, luceneProject);
//		String XmlFilePath = "D:/EOSdir/temp/tmp.xml";
//		for (int i = 0; i < 1000; i++) {
//			pb.createNewProjectFromXml(XmlFilePath);
//			System.out.println(i);
//		}
		System.out.println("Test Success!");
	}

}
