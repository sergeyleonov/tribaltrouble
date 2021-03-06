package com.oddlabs.matchmaking;

import com.oddlabs.net.ARMIEvent;
import com.oddlabs.net.HostSequenceID;

import java.net.InetAddress;

public strictfp interface MatchmakingClientInterface {

	int PROFILE_ERROR = 1;
	int PROFILE_ERROR_GUEST = 2;

	int USER_ERROR_INVALID_EMAIL = 10;
	int USER_ERROR_NO_SUCH_USER = 11;
	int USER_ERROR_VERSION_TOO_OLD = 12;

	int USERNAME_ERROR_TOO_MANY = 20;
	int USERNAME_ERROR_ALREADY_EXISTS = 21;
	int USERNAME_ERROR_INVALID_CHARACTERS = 22;
	int USERNAME_ERROR_TOO_LONG = 23;
	int USERNAME_ERROR_TOO_SHORT = 24;

	int CHAT_ERROR_TOO_MANY_USERS = 30;
	int CHAT_ERROR_INVALID_NAME = 31;
	int CHAT_ERROR_NO_SUCH_NICK = 32;

	void updateProfileList(Profile[] profiles, String last_profile_nick);

	void updateProfile(Profile profiles);

	void createProfileError(int error_code);

	void createProfileSuccess();

	void joiningChatRoom(String room_name);

	void error(int error_code);

	void receiveChatRoomUsers(ChatRoomUser[] users);

	void receiveChatRoomMessage(String nick, String msg);

	void receivePrivateMessage(String nick, String msg);

	void receiveInfo(Profile profile);

	void updateStart(int type);

	void updateList(int type, Object[] names);

	void updateComplete(int next_update_key);

	void gameWonAck();

	void tunnelOpened(HostSequenceID from, InetAddress inet_address, InetAddress local_inet_address, Profile name);

	void tunnelClosed(HostSequenceID from);

	void tunnelAccepted(HostSequenceID from);

	void receiveRoutedEvent(HostSequenceID from, ARMIEvent event);

	void loginOK(String username, TunnelAddress address);

	void loginError(int error_code);
}
