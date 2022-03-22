package org.miip.waterway.rest.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.condast.commons.Utils;
import org.condast.commons.autonomy.env.IEnvironment;
import org.condast.commons.preferences.IPreferenceStore;
import org.miip.waterway.model.IVessel;
import org.miip.waterway.model.def.IDesignFactory;
import org.miip.waterway.radar.RadarOptions;

public class Dispatcher {

	public static final String S_POND = "org.miip.pond.model.PondEnvironment";
	public static final String S_MIIP = "org.miip.waterway.model.eco.MIIPEnvironment";

	public static final String S_VESSEL_NAME = "Rest";

	private static Dispatcher dispatcher = new Dispatcher();

	private Map<String, IEnvironment<IVessel>> environments;

	private Collection<IDesignFactory<IVessel>> factories;

	private Dispatcher() {
		factories = new ArrayList<>();
		environments = new HashMap<>();
	}

	public static Dispatcher getInstance(){
		return dispatcher;
	}

	public RadarOptions getOptions() {
		IEnvironment<IVessel> env = getActiveEnvironment();
		for( IDesignFactory<IVessel> factory: factories ) {
			IPreferenceStore<String, String> store = factory.createPreferenceStore(env);
			if( store != null)
				return new RadarOptions(store, env.getName());
		}
		return null;
	}

	public IEnvironment<IVessel> getActiveEnvironment() {
		if(( this.environments == null ) ||( this.environments.isEmpty() ))
			return null;
		for( IEnvironment<IVessel> env: this.environments.values() ){
			if( env.isEnabled() )
				return env;
		}
		return getEnvironment( S_MIIP);
	}

	public IEnvironment<IVessel> getEnvironment( String id ) {
		if( Utils.assertNull(this.environments))
			return null;
		return this.environments.get(id);
	}

	public void addfactory( IDesignFactory<IVessel> factory ){
		this.factories.add(factory);
		this.environments.put( factory.getId(), factory.createEnvironment() );
	}

	public void removefactory( IDesignFactory<IVessel> factory ){
		this.factories.remove(factory);
		this.environments.remove( factory.getId());
	}

	public void addEnvironment( String id, IEnvironment<IVessel> cenv ){
		this.environments.put( id, cenv );
	}

	public void removeEnvironment( String id ){
		this.environments.remove( id );
	}


}