package com.oddlabs.util;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.KeyedObjectPoolFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.StackKeyedObjectPoolFactory;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final strictfp class DBUtils {

	private static DataSource ds;

	public static void initConnection(String address, String user, String password) throws ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		GenericObjectPool connectionPool = new GenericObjectPool(null);
		ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(address, user, password);
		KeyedObjectPoolFactory keyed_factory = new StackKeyedObjectPoolFactory();
		new PoolableConnectionFactory(connectionFactory, connectionPool, keyed_factory, null, false, true);
		ds = new PoolingDataSource(connectionPool);
	}

	public static PreparedStatement createStatement(String sql) throws SQLException {
		return ds.getConnection().prepareStatement(sql);
	}

	public static void postHermesMessage(String message) throws SQLException {
		PreparedStatement stmt = DBUtils.createStatement("INSERT INTO messages (time, message) VALUES (NOW(), ?)");
		try {
			stmt.setString(1, message);
			int row_count = stmt.executeUpdate();
			assert row_count == 1;
		} finally {
			stmt.getConnection().close();
		}
	}
}
