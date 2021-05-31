package org.miip.rwt.service;

import java.util.HashMap;
import java.util.Map;

import org.condast.commons.Utils;
import org.condast.commons.autonomy.model.IPhysical;
import org.miip.waterway.model.def.IDesignFactory;
import org.miip.waterway.model.def.IMIIPEnvironment;
import org.miip.waterway.ui.factory.ICompositeFactory;
import org.miip.waterway.ui.swt.MiipComposite;

public class Dispatcher {

	public static final String S_POND = "org.miip.pond.model.PondEnvironment";
	public static final String S_MIIP = "org.miip.waterway.model.eco.MIIPEnvironment";

	private static Dispatcher dispatcher = new Dispatcher();

	private Map<String, IMIIPEnvironment> environments;
	private Map<String, ICompositeFactory> factories;
	
	private MiipComposite miipComposite;
	
	private Dispatcher() {
		environments = new HashMap<>();
		factories = new HashMap<String, ICompositeFactory>();
	}

	public static Dispatcher getInstance(){
		return dispatcher;
	}

	public MiipComposite getMiipComposite() {
		return miipComposite;
	}

	public void setMiipComposite(MiipComposite miipComposite) {
		this.miipComposite = miipComposite;
	}

	public IMIIPEnvironment getActiveEnvironment() {
		if(( this.environments == null ) ||( this.environments.isEmpty() ))
			return null;
		for( IMIIPEnvironment env: this.environments.values() ){
			if( env.isActive() )
				return env;
		}
		return getEnvironment( S_MIIP);
	}

	public IMIIPEnvironment getEnvironment( String id ) {
		if( Utils.assertNull(this.environments))
			return null;
		return this.environments.get(id);
	}

	public void addEnvironment( IDesignFactory<IPhysical> factory ){
		this.environments.put( factory.getId(), factory.createEnvironment() );
	}

	public void addEnvironment( String id, IMIIPEnvironment cenv ){
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