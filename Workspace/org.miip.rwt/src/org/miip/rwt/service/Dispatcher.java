package org.miip.rwt.service;

import java.util.HashMap;
import java.util.Map;

import org.condast.commons.Utils;
import org.miip.waterway.model.def.IDesignFactory;
import org.miip.waterway.model.def.IEnvironment;

public class Dispatcher {

	private static Dispatcher dispatcher = new Dispatcher();

	private Map<String, IEnvironment> environments;
	
	
	private Dispatcher() {
		environments = new HashMap<String, IEnvironment>();
	}

	public static Dispatcher getInstance(){
		return dispatcher;
	}

	public IEnvironment getEnvironment( String id ) {
		if( Utils.assertNull(this.environments))
			return null;
		return this.environments.get(id);
	}

	public void addEnvironment( IDesignFactory factory ){
		this.environments.put( factory.getId(), factory.createEnvironment() );
	}

	public void addEnvironment( String id, IEnvironment cenv ){
		this.environments.put( id, cenv );
	}

	public void removeEnvironment( String id ){
		this.environments.remove( id );
	}
}