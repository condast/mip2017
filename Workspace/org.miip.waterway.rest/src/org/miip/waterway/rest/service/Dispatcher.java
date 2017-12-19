package org.miip.waterway.rest.service;

import java.util.ArrayList;
import java.util.Collection;

import org.miip.waterway.sa.ISituationalAwareness;

public class Dispatcher {

	private static Dispatcher dispatcher = new Dispatcher();

	private Collection<ISituationalAwareness> sas;
	
	private Dispatcher() {
		sas = new ArrayList<ISituationalAwareness>();
	}

	public static Dispatcher getInstance(){
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
