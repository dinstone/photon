package com.dinstone.photon.session;

public class SessionManager {

	private static final SessionManager instance = new SessionManager();

	public static SessionManager getInstance() {
		return instance;
	}

	private SessionManager() {
	}
	
	
}
