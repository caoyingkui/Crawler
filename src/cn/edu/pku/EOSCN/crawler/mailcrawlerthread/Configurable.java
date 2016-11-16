package cn.edu.pku.EOSCN.crawler.mailcrawlerthread;

/**   
* @Title: Configurable.java
* @Package cn.edu.pku.EOS.crawler.thread
* @Description: 含有成员变量config类
* @author jinyong     jinyonghorse@hotmail.com  
* @date 2013-6-3 上午10:16:33
*/

public class Configurable {
	protected CrawlerConfig config;
	
	public CrawlerConfig getConfig() {
		return config;
	}

	public void setConfig(CrawlerConfig config) {
		this.config = config;
	}

	public Configurable(CrawlerConfig config) {
		this.config = config;
	}
}
