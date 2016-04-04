package com.silence.web.spring_min.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;


public class DataSourcePool {
	private static Logger logger = Logger.getLogger(DataSourcePool.class);

	private static List<Connection> connections;
	
	private static Properties dbProperties;
	
	static{
		dbProperties = PropertiesUti.getProperties("/db.properties");
		connections=new ArrayList<>();
	}
	
	public static Connection getConnection(){
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
}
