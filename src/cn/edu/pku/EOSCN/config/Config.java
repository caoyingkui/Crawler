package cn.edu.pku.EOSCN.config;

import java.util.ResourceBundle;
/**
 * 
 * @author 灵箫
 * 读取配置文件的类
 * 读值请调用getValue(String key)
 */
public class Config {
		// 属性文件标识符
		private static String CONFIG_FILE_NAME = "cn.edu.pku.EOSCN.config.eos";

		// 所使用的ResourceBundle
		private static ResourceBundle bundle;

		// 静态私有方法，用于从属性文件中取得属性值
		static {
			try {
				//System.out.println("init config file!");
				bundle = ResourceBundle.getBundle(CONFIG_FILE_NAME);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		
		private static String getValue(String key) {
			return bundle.getString(key);
		}
		
//		public static String getControlNodeServiceName() {
//			return getValue("controlNodeServiceName");
//		}
		
//		public static String getControlNodeIP() {
//			return getValue("controlNodeIP");
//		}
		
		public static String getDbhost() {
			return getValue("dbhost");
		}
		
		public static String getDbuser() {
			return getValue("dbuser");
		}
		
		public static String getDbpw() {
			return getValue("dbpw");
		}
		
		public static String getTempDir() {
			return getValue("tmpdir");
		}
		
		public static String getEOSDir() {
			return getValue("eosdir");
		}
		
		public static String getMaxTaskNum() {
			return getValue("maxtasknum");
		}
		
		public static void main(String[] args) {
			System.out.println(getDbhost());
			System.out.println(getDbuser());
			System.out.println(getDbpw());
		}
		
}
