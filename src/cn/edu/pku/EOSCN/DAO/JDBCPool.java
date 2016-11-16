package cn.edu.pku.EOSCN.DAO;

import java.sql.Connection;
import java.sql.SQLException;

import cn.edu.pku.EOSCN.config.Config;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
/**
 * 用单件模式建立数据库连接池的类
 * @author 张灵箫
 *
 */
public class JDBCPool {
	private static BoneCP connectionPool = null;
	
	private JDBCPool() throws SQLException{

	}
	
	public static Connection getConnection() throws SQLException{
//		if (connectionPool == null) {
//			System.out.println("DB pool is disconnected!\n用main class测试数据库时请先调用JDBCPool.init方法。");
//			return null;
//			//initPool();
//		}
		return connectionPool.getConnection();
	}
	
	public static void shutDown() {
		if (connectionPool != null) {
			connectionPool.shutdown();
		}
	}
	
	public static synchronized void initPool() throws SQLException, ClassNotFoundException{
		System.out.println("initiating db pool...");
		if (connectionPool != null) {
			return;
		}
		Class.forName("com.mysql.jdbc.Driver");
		// setup the connection pool
		BoneCPConfig config = new BoneCPConfig();
		config.setJdbcUrl(Config.getDbhost()); // jdbc url specific to your
													// database, eg
													// jdbc:mysql://127.0.0.1/yourdb
		config.setUsername(Config.getDbuser());
		config.setPassword(Config.getDbpw());
		config.setMinConnectionsPerPartition(5);
		config.setMaxConnectionsPerPartition(10);
		config.setPartitionCount(1);
		connectionPool = new BoneCP(config); // setup the connection pool
		System.out.println("db pool initiated!");
	}

	public static void main(String[] args) {
		try {
			JDBCPool.initPool();
			Connection connection = JDBCPool.getConnection();
			System.out.println(connection);
		} catch (SQLException e) {
			System.out.println("connection pool initiation fail!");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("connection pool initiation fail!");
			e.printStackTrace();
		}
	}
}
