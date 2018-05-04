package com.oddlabs.matchserver;

import com.oddlabs.event.Deterministic;
import com.oddlabs.event.NotDeterministic;
import com.oddlabs.matchmaking.MatchmakingServerInterface;
import com.oddlabs.net.*;
import com.oddlabs.registration.RegistrationKey;
import com.oddlabs.util.DBUtils;
import com.oddlabs.util.KeyManager;

import java.io.IOException;
import java.net.InetAddress;
import java.security.PublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.*;
import java.util.logging.Handler;

public final class MatchmakingServer implements ConnectionListenerInterface {

	private static final Logger logger = Logger.getLogger("com.oddlabs.matchserver");
	private static final Map<String, Client> online_users = new HashMap<String, Client>();
	private static int current_id = 1;

	static {
		try {
			Handler fh = new FileHandler("logs/matchserver.%g.log", 10 * 1024 * 1024, 50);
			fh.setFormatter(new SimpleFormatter());
			logger.addHandler(fh);
			logger.setLevel(Level.ALL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private final Logger chat_logger = Logger.getLogger("chatlog");
	private final AlgorithmParameterSpec param_spec;
	private final PublicKey public_reg_key;
	private final NetworkSelector network;
	private final Map<Integer, Client> client_map = new HashMap<Integer, Client>();

	private MatchmakingServer() throws Exception {
		Deterministic deterministic = new NotDeterministic();
		this.network = new NetworkSelector(deterministic);
		Handler fh = new FileHandler("logs/chatlog.%g.log", 10 * 1024 * 1024, 50);
		fh.setFormatter(new SimpleFormatter());
		chat_logger.addHandler(fh);
		chat_logger.setLevel(Level.ALL);
		this.public_reg_key = RegistrationKey.loadPublicKey();
		DBUtils.initConnection("jdbc:h2:mem:db;MODE=MYSQL", "dev", "dev");
		logger.info("Generating encryption keys.");
		this.param_spec = KeyManager.generateParameterSpec();
		new ConnectionListener(network, null, MatchmakingServerInterface.MATCHMAKING_SERVER_PORT, this);
		DBInterface.initDropGames();
		DBInterface.clearOnlineProfiles();
		logger.info("Matchmaking server started.");
		while (true) {
			network.tickBlocking();
		}
	}

	public static void main(String[] args) {
		try {
			new MatchmakingServer();
		} catch (Throwable t) {
			logger.throwing("MatchmakingServer", "main", t);
			postPanic();
			System.exit(1);
		}
	}

	public static Logger getLogger() {
		return logger;
	}

	private static void postPanic() {
		try {
			DBUtils.postHermesMessage("elias, xar, jacob, thufir: Matchmaking service crashed!");
		} catch (Throwable t) {
			logger.throwing("MatchmakingServer", "postPanic", t);
		}
	}

	@Override
	public final void error(AbstractConnectionListener conn_id, IOException e) {
		logger.severe("Server socket failed!");
		throw new RuntimeException(e);
	}

	@Override
	public final void incomingConnection(AbstractConnectionListener connection_listener, Object remote_address) {
		int id = current_id++;
		AbstractConnection conn = connection_listener.acceptConnection(null);
		SecureConnection secure_conn = new SecureConnection(network.getDeterministic(), conn, param_spec);
		Authenticator client = new Authenticator(this, secure_conn, (InetAddress) remote_address, id);
	}

	public final Logger getChatLogger() {
		return chat_logger;
	}

	public final PublicKey getPublicRegKey() {
		return public_reg_key;
	}

//	public final boolean isKeyOnline(String key_encoded) {
//		return online_keys.contains(key_encoded);
//	}

	public final AlgorithmParameterSpec getSpec() {
		return param_spec;
	}

	public final void loginClient(InetAddress remote_address, InetAddress local_remote_address, String username, AbstractConnection conn, String key_code_encoded, int revision, int host_id) {
//		online_keys.add(key_code_encoded);
		Client old_logged_in = online_users.remove(username.toLowerCase());
		if (old_logged_in != null) {
			old_logged_in.close();
			logger.info(username + " overtaked old login");
		}
		Client client = new Client(this, conn, remote_address, local_remote_address, username, key_code_encoded == null, revision, host_id);
		online_users.put(username.toLowerCase(), client);
		client_map.put(client.getHostID(), client);
		logger.info(username + " logged in, with key " + key_code_encoded);
	}

	public final Client getClientFromID(int host_id) {
		return client_map.get(host_id);
	}

	public final void logoutClient(Client client) {
		online_users.remove(client.getUsername().toLowerCase());
		removeInstance(client.getHostID());
	}

	private void removeInstance(int instance_id) {
		client_map.remove(instance_id);
	}
}
