package cn.edu.pku.EOSCN.crawler;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.pku.EOSCN.business.CrawlerTaskManager;
import cn.edu.pku.EOSCN.business.ThreadManager;
import cn.edu.pku.EOSCN.entity.CrawlerTask;
import cn.edu.pku.EOSCN.entity.Project;
/**
 * 所有爬虫需要实现该抽象类中的crawl方法
 * 子类中必须实现无参数的构造方法，urllist和project变量由爬虫管理模块统一set
 * @author 张灵箫
 *
 */
public abstract class Crawler extends Thread{
	public static final int SUCCESS = 1;
	public static final int WAITING = 0;
	public static final int ERROR = 2;
	public static final int IN_PROGRESS = 3;
	
	public static final int MAIN = 0;
	public static final int SUB = 1;
	public static final int FULL = 2;
	
	protected static final Logger logger = Logger.getLogger(Crawler.class.getName());
	private String crawleruuid;
	private String resourceType;
	public boolean needLog = false;
	public int crawlerType = FULL;
	public int subCrawlerNum = 4;
	public int subCrawlerRun = 4;
	public int subid;
	public boolean isServerTask = true;
	public Crawler father;
	private int status = WAITING;
	private int percentage = 0;
	private String entrys;
	protected Project project = null;
	public Project getProject() {
		return project;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public void setProject(Project project) {
		this.project = project;
	}
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getPercentage() {
		return percentage;
	}

	public void setPercentage(int percentage) {
		this.percentage = percentage;
	}
	
	/**
	 * @author 张灵箫
	 * 默认构造方法
	 * 测试时请调用带参数的构造方法
	 */
	public Crawler(){}
	
    @Override
    public final void run() {
        status = IN_PROGRESS;
        try {
        	this.init();
            this.Crawl();
            this.finish();    
		} catch (Exception e) {
			e.printStackTrace();
		}
    } 
    

	public void addPercentageBy(int i) {
		percentage += i;
	}
    
	abstract public void init()throws Exception;
	abstract public void crawl_url()throws Exception;
	abstract public void crawl_middle(int id, Crawler crawler);
	abstract public void crawl_data();

	public final void Crawl() throws Exception{
		if (this.crawlerType == Crawler.FULL){
			this.crawl_url();
			this.crawl_data();
		}else if (this.crawlerType == Crawler.SUB){
			synchronized (father){
				father.subCrawlerRun --;
			}
			this.crawl_data();
		}else{
			this.crawl_url();
			List<Crawler> subs = new ArrayList<Crawler>(); 
			for (int i = 0; i < subCrawlerNum; i++){
				Crawler crawler = (Crawler)Class.forName(this.getClass().getName()).newInstance();
				crawler.crawlerType = Crawler.SUB;
				crawler.subid = i;				
				crawler.project = this.project;
				crawler.needLog = true;
				crawler.father = this;
				crawler.entrys = this.entrys;
				this.crawl_middle(i, crawler);
				subs.add(crawler);
				ThreadManager.addCrawlerTask(crawler);
			}
			while (this.subCrawlerRun > 0){
				sleep(10000);
			}
			for (Crawler crawler : subs){
				crawler.join();
			}
			
		}
		//this.finish();
	}

	public void setCrawleruuid(String crawleruuid) {
		this.crawleruuid = crawleruuid;
	}

	public String getCrawleruuid() {
		return crawleruuid;
	}
	
	public final void finish() {
		if (this.crawlerType == MAIN)
			CrawlerTaskManager.reportCompletedTask(crawleruuid);
	}
	
	public CrawlerTask toCrawlerTask(){
		CrawlerTask crawlerTask = new CrawlerTask();
		crawlerTask.setUuid(crawleruuid);
		crawlerTask.setStatus(status);
		crawlerTask.setEntrys(entrys);
		return crawlerTask;
	}

	public String getEntrys() {
		return entrys;
	}

	public void setEntrys(String entrys) {
		this.entrys = entrys;
	}
	
}
