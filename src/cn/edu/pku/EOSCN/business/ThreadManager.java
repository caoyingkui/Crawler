package cn.edu.pku.EOSCN.business;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;

import cn.edu.pku.EOSCN.DAO.CrawlerTaskDao;
import cn.edu.pku.EOSCN.config.Config;
import cn.edu.pku.EOSCN.crawler.Crawler;
import cn.edu.pku.EOSCN.crawler.util.FileOperation.FileUtil;
import cn.edu.pku.EOSCN.entity.CrawlerTask;

/** 
  * @author Jinan Ni E-mail: nijinan@pku.edu.cn
  * @date 2016年8月18日 上午11:51:25 
  * @version 1.0   */
public class ThreadManager {
	protected static final Logger logger = Logger.getLogger(ThisReference.class.getName());
	//private static ExecutorService threadPool = null;
	private static final int MAX_ONGOING_TASK_NUM = Integer.parseInt(Config.getMaxTaskNum());
	private static List<Crawler> ongoingTasklist = new ArrayList<Crawler>();
	private static List<Crawler> waitingTasklist = new ArrayList<Crawler>();
	public static void initCrawlerTaskManager() {
	}
	public static void finishCrawlerTaskManager(){
		while (!ongoingTasklist.isEmpty() || !waitingTasklist.isEmpty())
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public static synchronized void addCrawlerTask(Crawler crawler) {
		if (ongoingTasklist.size() < MAX_ONGOING_TASK_NUM) {
			startCrawlerTask(crawler);
		} else {
			waitingTasklist.add(crawler);
	        logger.info(crawler.getClass().getName() + " for project \"" + crawler.getProject().getName() + "\" is in the waiting list");
		}

	}

	private static void startCrawlerTask(Crawler crawler) {
		ongoingTasklist.add(crawler);
		crawler.start();
		logger.info(crawler.getClass().getName() + " for project \"" + crawler.getProject().getName() + "\" is crawling");
	}
	
	public static void printTaskStatus() {
        for (Crawler c : ongoingTasklist) {
			System.out.println(c.getCrawleruuid() + " is " + c.getStatus());
		}
	}

	public static synchronized void removeFinishedTask(Crawler crawler) {
    	ongoingTasklist.remove(crawler);
		NotifyWaitingList();
	}

	private static void NotifyWaitingList() {
		if (waitingTasklist.size() > 0) {
			Crawler crawler =  waitingTasklist.remove(0);
			ongoingTasklist.add(crawler);
			crawler.start();
		    logger.info(crawler.getClass().getName() + " for project \"" + crawler.getProject().getName() + "\" is crawling");
		}
	}

	public static int getTaskStatus(String uuid) {
		for (Crawler ct : ongoingTasklist) {
			if (ct.getCrawleruuid().equals(uuid)) {
				return CrawlerTask.IN_PROGRESS;
			}
		}
		for (Crawler ct : waitingTasklist) {
			if (ct.getCrawleruuid().equals(uuid)) {
				return CrawlerTask.WAITING;
			}
		}
		return CrawlerTask.ERROR;
	}
}
