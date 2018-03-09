package org.miip.waterway.rest.service;

import java.util.HashMap;
import java.util.Map;

import org.condast.commons.Utils;
import org.condast.commons.autonomy.env.IEnvironment;
import org.condast.commons.autonomy.model.IPhysical;
import org.miip.waterway.model.def.IDesignFactory;
import org.miip.waterway.rest.store.RadarOptions;

public class Dispatcher {

	public static final String S_VESSEL_NAME = "Rest";
	
	private static Dispatcher dispatcher = new Dispatcher();

	private RadarOptions options;
	
	private Map<String, IEnvironment<IPhysical>> environments;
	
	private Dispatcher() {
		environments = new HashMap<String, IEnvironment<IPhysical>>();
		this.options = new RadarOptions( S_VESSEL_NAME );
	}

	public static Dispatcher getInstance(){
		return dispatcher;
	}

	public RadarOptions getOptions() {
		return options;
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