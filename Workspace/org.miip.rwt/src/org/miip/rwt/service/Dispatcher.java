package org.miip.rwt.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.condast.commons.Utils;
import org.condast.commons.autonomy.env.IEnvironment;
import org.condast.commons.autonomy.model.IPhysical;
import org.miip.waterway.model.def.IDesignFactory;
import org.miip.waterway.ui.factory.ICompositeFactory;

public class Dispatcher {

	public static final String S_POND = "org.miip.pond.model.PondEnvironment";
	public static final String S_MIIP = "org.miip.waterway.model.eco.MIIPEnvironment";

	private static Dispatcher dispatcher = new Dispatcher();

	private Map<String, IEnvironment<IPhysical>> environments;
	private Map<String, ICompositeFactory> factories;
	
	
	private Dispatcher() {
		environments = new HashMap<String, IEnvironment<IPhysical>>();
		factories = new HashMap<String, ICompositeFactory>();
	}

	public static Dispatcher getInstance(){
		return dispatcher;
	}

	public void activateEnvironment( String id ) {
		if(( this.environments == null ) ||( this.environments.isEmpty() ))
			return;
		Iterator<Map.Entry<String, IEnvironment<IPhysical>>> iterator = this.environments.entrySet().iterator();
		while( iterator.hasNext() ) {
			Map.Entry<String, IEnvironment<IPhysical>> entry = iterator.next();
			entry.getValue().setActive( entry.getKey().equals(id));
		}
	}

	public IEnvironment<IPhysical> getActiveEnvironment() {
		if(( this.environments == null ) ||( this.environments.isEmpty() ))
			return null;
		for( IEnvironment<IPhysical> env: this.environments.values() ){
			if( env.isActive() )
				return env;
		}
		return getEnvironment( S_MIIP);
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

	public void addCompositeFactory( ICompositeFactory factory ){
		this.factories.put( factory.getName(), factory );
	}
	
	public void removeFactory( ICompositeFactory factory ){
		this.factories.remove( factory.getName() );
	}

	public Map<String, ICompositeFactory> getFactories() {
		return factories;
	}
}