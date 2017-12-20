package org.miip.waterway.rest.service;

import org.miip.waterway.model.def.IMIIPEnvironment;

public class Dispatcher {

	private static Dispatcher dispatcher = new Dispatcher();

	private IMIIPEnvironment cenv;
	
	private Dispatcher() {}

	public static Dispatcher getInstance(){
		return dispatcher;
	}

	public void startApplication( IMIIPEnvironment cenv ){
		this.cenv = cenv;
	}
	
	public IMIIPEnvironment getEnvironment() {
		return cenv;
	}

	public void dispose(){
	}

}
