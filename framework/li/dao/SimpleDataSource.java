package li.dao;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * DataSource的简单实现,不建议在项目中使用
 * 
 * @author li (limw@w.cn)
 * @version 0.1.2 (2012-05-08)
 */
public class SimpleDataSource implements DataSource {
	private String url;
	private String username;
	private String password;

	public Connection getConnection() throws SQLException {
		return getConnection(this.username, this.password);
	}

	public Connection getConnection(String username, String password) throws SQLException {
		return DriverManager.getConnection(this.url, username, password);
	}

	public int getLoginTimeout() throws SQLException {
		return 0;
	}

	public PrintWriter getLogWriter() throws SQLException {
		return new PrintWriter(System.out);
	}

	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	public void setLoginTimeout(int seconds) throws SQLException {
	}

	public void setLogWriter(PrintWriter out) throws SQLException {
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}
}