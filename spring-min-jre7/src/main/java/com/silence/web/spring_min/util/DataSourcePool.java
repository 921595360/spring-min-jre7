package com.silence.web.spring_min.util;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.log4j.Logger;


public class DataSourcePool implements DataSource{
	
	private static DataSourcePool dataSourcePool;
	
	private static Logger logger = Logger.getLogger(DataSourcePool.class);

	private static List<Connection> connections;
	
	private static Properties dbProperties;
	
	static{
		dbProperties = PropertiesUti.getProperties("/db.properties");
		connections=new ArrayList<>();
	}
	
	@Override
	public  Connection getConnection(){
		if(connections.size()==0){
			Connection connection=getNewConnection();
			connections.add(connection);
		}
		
		Connection connection = connections.remove(0);
		return connection;
	}
	
	public static void returnToPool(Connection conn){
		connections.add(conn);
	}
	
	private DataSourcePool(){}
	
	
	
	private static Connection getNewConnection(){
		logger.info("getNewConnection");
		WebLogUtil.addMsg(DataSourcePool.class.toString()+":==>getNewConnection");
		try {
			Class.forName(dbProperties.getProperty("jdbc.driver"));
			Connection connection = DriverManager.getConnection(
					dbProperties.getProperty("jdbc.url"), 
					dbProperties.getProperty("jdbc.username"), 
					dbProperties.getProperty("jdbc.password"));
			return connection;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static DataSourcePool getDataSource(){
		if(null==dataSourcePool){
			dataSourcePool=new DataSourcePool();
		} 
		return dataSourcePool;
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return null;
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
}
