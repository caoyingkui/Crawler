package cn.edu.pku.EOSCN.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.edu.pku.EOSCN.business.CrawlerBusiness;
import cn.edu.pku.EOSCN.entity.Project;

/**
 * Servlet implementation class CrawlResource
 */
public class CrawlResource extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CrawlResource() {
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
		String result = null;
		String uuid = request.getParameter("projectuuid");
		String type = request.getParameter("type");
		String taskuuid = request.getParameter("taskuuid");
		CrawlerBusiness cb = new CrawlerBusiness();
		Project p = cb.getProjectByUuid(uuid);
		
		response.setContentType("text/plain");  
	    response.getWriter().print(result); 
	}

}
