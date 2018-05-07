package com.oddlabs.regservlet;

import com.oddlabs.registration.RegServiceInterface;
import com.oddlabs.registration.RegistrationInfo;
import com.oddlabs.registration.RegistrationKey;
import com.oddlabs.registration.RegistrationRequest;
import com.oddlabs.util.KeyManager;
import com.oddlabs.util.PasswordKey;

import javax.crypto.Cipher;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.*;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignedObject;
import java.sql.SQLException;

public final strictfp class RegistrationServlet extends HttpServlet {

	private final static String GG_IGNITION_URL = "http://www.garagegames.com/ignition/validate.php?ProductId=0xFA00E87&Key=";
	private final static String LOCAL_KEYGEN_URL = "http://oddlabs.com/keygen_gg.php?";

	private PrivateKey privateRegKey;

	private static int parseIntOrZero(String val) {
		try {
			return Integer.parseInt(val);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	private static String readReplyFromURL(URL url) throws IOException {
		InputStream in = url.openStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = reader.readLine();
		reader.close();
		return line;
	}

	private static String generateGGKey(String requestKeyStr, String ggUsername) throws IOException {
		return readReplyFromURL(new URL(LOCAL_KEYGEN_URL + "gg_key=" + requestKeyStr + "&username=" + ggUsername));
	}

	private static String normalizeKey(String key) throws IOException {
		String result = key.toUpperCase();
		result = result.replaceAll("-", "");
		if (result.length() != 16) {
			throw new IOException("Invalid key: " + key);
		}
		result = result.substring(0, 4) + '-' + result.substring(4, 8) + '-' + result.substring(8, 12) + '-' + result.substring(12, 16);
		return result;
	}

	private static DataSource getDataSource() throws ServletException {
		try {
			return InitialContext.doLookup("java:comp/env/jdbc/mainDB");
		} catch (NamingException e) {
			throw new ServletException(e);
		}
	}

	public final void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			String password = config.getInitParameter("reg_key_pass");
			Cipher decryptCipher = KeyManager.createPasswordCipherFromPassword(password.toCharArray(), Cipher.DECRYPT_MODE);
			privateRegKey = PasswordKey.readPrivateKey(decryptCipher, RegServiceInterface.PRIVATE_KEY_FILE, RegServiceInterface.KEY_ALGORITHM);
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	public final void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String requestKey = normalizeKey(request.getParameter("key"));
		String affiliateId = request.getParameter("affiliate_id");
		String currentAffiliateId = request.getParameter("current_affiliate_id");
		int version = parseIntOrZero(request.getParameter("version"));
		boolean timelimit = Boolean.valueOf(request.getParameter("timelimit"));
		int maxtime = parseIntOrZero(request.getParameter("maxtime"));
		boolean forcequit = Boolean.valueOf(request.getParameter("forcequit"));
		int maxgames = parseIntOrZero(request.getParameter("maxgames"));
		log("Oddlabs: got key request: remote host = " + request.getRemoteHost() + " | key_str = " + requestKey + " | current_affiliate_id = " + currentAffiliateId + " | affiliate_id = " + affiliateId + " | version = " + version + " | timelimit = " + timelimit + " | maxtime = " + maxtime + " | forcequit = " + forcequit + " | maxgames = " + maxgames);
		String keyString = createKey(requestKey, currentAffiliateId);
		long key = RegistrationKey.decode(keyString);
		try {
			SignedObject signedRegistration = register(new RegistrationRequest(key, affiliateId, version, timelimit, maxtime, forcequit, maxgames));
			response.setContentType("application/octet-stream");
			ObjectOutputStream oos = new ObjectOutputStream(response.getOutputStream());
			oos.writeObject(signedRegistration);
			oos.close();
			log("Oddlabs: Registered key: " + keyString);
		} catch (Exception e) {
			log("got exception while registering: " + e);
			response.sendError(500, e.toString());
		}
	}

	private String validateGGKey(String ggKey) throws IOException {
		String reply = readReplyFromURL(new URL(GG_IGNITION_URL + ggKey));
		String errorPrefix = "Error: ";
		String successPrefix = "Valid: ";
		if (reply.startsWith(errorPrefix)) {
			String errorMsg = reply.substring(errorPrefix.length());
			throw new IOException("GarageGames server validation failed: " + errorMsg);
		} else if (reply.startsWith(successPrefix)) {
			String username = reply.substring(successPrefix.length());
			log("GarageGames username = " + username);
			return username;
		} else {
			throw new IOException("Unknown reply from GarageGames: " + reply);
		}
	}

	private String createKey(String requestKeyStr, String affiliateId) throws IOException {
		if (affiliateId != null && affiliateId.equals("garagegames")) {
			String ggUsername = validateGGKey(requestKeyStr);
			String localKey = generateGGKey(requestKeyStr, ggUsername);
			log("GarageGames local key = " + localKey);
			return localKey;
		} else
			return requestKeyStr;
	}

	private SignedObject sign(Serializable obj) throws ServletException {
		try {
			Signature signature = Signature.getInstance(RegServiceInterface.SIGN_ALGORITHM);
			return new SignedObject(obj, privateRegKey, signature);
		} catch (GeneralSecurityException e) {
			throw new ServletException(e);
		} catch (IOException e) {
			throw new ServletException(e);
		}
	}

	private SignedObject register(RegistrationRequest registrationRequest) throws SQLException, ServletException {
		RegistrationInfo registrationInfo = DBInterface.registerKey(getDataSource(), registrationRequest);
		return sign(registrationInfo);
	}
}
