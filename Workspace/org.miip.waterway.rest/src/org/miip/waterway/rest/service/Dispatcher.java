package org.miip.waterway.rest.service;

import java.util.HashMap;
import java.util.Map;

import org.condast.commons.Utils;
import org.miip.waterway.environment.IEnvironment;
import org.miip.waterway.model.def.IDesignFactory;
import org.miip.waterway.model.def.IPhysical;

public class Dispatcher {

	private static Dispatcher dispatcher = new Dispatcher();

	private Map<String, IEnvironment<IPhysical>> environments;
	
	
	private Dispatcher() {
		environments = new HashMap<String, IEnvironment<IPhysical>>();
	}

	public static Dispatcher getInstance(){
		return dispatcher;
	}

	public IEnvironment<IPhysical> getEnvironment( String id ) {
		if( Utils.assertNull(this.environments))
			return null;
		return this.environments.get(id);
	}

	public void addEnvironment( IDesignFactory<IPhysical> factory ){
		this.environments.put( factory.getId(), factory.createEnvironment() );
	}

	public void addEnvironment( String id, IEnvironment<IPhysical> cenv ){
		this.environments.put( id, cenv );
	}

	public void removeEnvironment( String id ){
		this.environments.remove( id );
	}
}