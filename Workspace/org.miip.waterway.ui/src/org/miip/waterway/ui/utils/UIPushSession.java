package org.miip.waterway.ui.utils;

import org.condast.commons.ui.session.PushSession;

public class UIPushSession {

	private static UIPushSession container = new UIPushSession();
	
	private PushSession<Object> session;
	
	private UIPushSession() {
		session = new PushSession<>();
	}
	
	public static UIPushSession getInstance(){
		return container;
	}

	public PushSession<Object> getSession() {
		return session;
	}
}
