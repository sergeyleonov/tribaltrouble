package com.oddlabs.matchservlet;

import com.oddlabs.util.Utils;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.sql.SQLException;

public final class ContextInitializer implements ServletContextListener {

	private final static int KEY_SIZE = 1024;
	private final static String KEY_ALGORITHM = "RSA";

	public void contextInitialized(ServletContextEvent servletContextEvent) {
		ServletContext servletContext = servletContextEvent.getServletContext();
		servletContext.setAttribute("db", getDataSource());
		KeyPair keyPair = generateKeyPair();
		servletContext.setAttribute("private_key", keyPair.getPrivate());
		servletContext.setAttribute("public_key", keyPair.getPublic());
		createDBSchema();
	}

	public void contextDestroyed(ServletContextEvent servletContextEvent) {
	}

	private void createDBSchema() {
		String sql = Utils.resourceToString("/db/createdb.sql");
		try {
			getDataSource().getConnection().prepareStatement(sql).execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static KeyPair generateKeyPair() {
		try {
			KeyPairGenerator keygen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
			keygen.initialize(KEY_SIZE);
			return keygen.generateKeyPair();
		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		}
	}

	private static DataSource getDataSource() {
		try {
			return InitialContext.doLookup("jdbc/matchDB");
		} catch (NamingException e) {
			throw new RuntimeException(e);
		}
	}
}
