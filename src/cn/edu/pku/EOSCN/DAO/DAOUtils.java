package cn.edu.pku.EOSCN.DAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.edu.pku.EOSCN.TestUtil;

/**
 * 存放数据库读取公用方法的公共类
 * @author 张灵箫
 *
 */

public class DAOUtils {
	

	public static Connection getConnection() throws SQLException, ClassNotFoundException {
		return JDBCPool.getConnection(); 
	}
	
	/**
	 * @author 张灵箫
	 * select语句查询，得到的结果直接包装成javaBean形式，保证你定义的T类型中所有和数据库同名的域都有getter和setter方法
	 * 参见main方法
	 */
	public static <T> List<T> getResult(Class<T> t, String sqlString, Object... params) throws SQLException {
		Connection connection2 = JDBCPool.getConnection();
		ResultSetHandler<List<T>> h = new BeanListHandler<T>(t);
		QueryRunner runner = new QueryRunner();
		List<T> result = runner.query(connection2, sqlString, h, params);
		DbUtils.close(connection2);
		return result;
	}
	
	
	/**
	 * @author 张灵箫 
	 * 同步执行update语句
	 * 参见main方法
	 */
	public static int update(String sqlString, Object... params) throws SQLException {
		Connection connection2 = JDBCPool.getConnection();
		QueryRunner runner = new QueryRunner();
		int result = runner.update(connection2, sqlString, params);
		DbUtils.close(connection2);
		return result;
	}
//	/**
//	 * @author 张灵箫
//	 * 异步执行update语句
//	 * 有大量写操作时建议使用异步方法
//	 */
//	public static void AsyncUpdate(Connection connection, String sqlString, Object... params) {
//		ExecutorCompletionService<Integer> executor =
//			    new ExecutorCompletionService<Integer>(Executors.newCachedThreadPool());
//			AsyncQueryRunner asyncRun = new AsyncQueryRunner(executor);
//			
//
//	}
	
	public static int count(String sql, Object... params) throws SQLException {
		Connection connection = JDBCPool.getConnection();
		Long result = null;
		ScalarHandler<Long> handler = new ScalarHandler<Long>();
		QueryRunner queryRunner = new QueryRunner();
		result = queryRunner.query(connection, sql, handler, params);
		DbUtils.close(connection);
		return result.intValue();
	}

	public static void main(String[] args) {
		System.out.println(TestUtil.getLuceneProject().getDescription());
		System.out.println("done!");
	}
}
