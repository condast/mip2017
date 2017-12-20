package org.miip.waterway.rest.service;

import java.util.ArrayList;
import java.util.Collection;

import org.miip.waterway.sa.ISituationalAwareness;

public class SADispatcher {

	private static SADispatcher dispatcher = new SADispatcher();

	private Collection<ISituationalAwareness> sas;
	
	private SADispatcher() {
		sas = new ArrayList<ISituationalAwareness>();
	}

	public static SADispatcher getInstance(){
		return dispatcher;
	}

	public void add( ISituationalAwareness manager ){
		this.sas.add( manager);
	}

	public void remove( ISituationalAwareness manager ){
		this.sas.remove( manager);
	}
	
	public ISituationalAwareness getSituationalAwareness( ) {
		return sas.iterator().next();
	}

	public void dispose(){
		this.sas.clear();
	}
}
