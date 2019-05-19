package org.miip.pond.services;

import org.condast.commons.autonomy.env.IEnvironment;
import org.condast.commons.preferences.AbstractPreferenceStore;
import org.condast.commons.preferences.IPreferenceStore;
import org.miip.pond.Activator;
import org.miip.pond.core.PondEnvironment;
import org.miip.waterway.model.IVessel;
import org.miip.waterway.model.def.IDesignFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.prefs.Preferences;

@Component( name="org.miip.pond.design.factory")
public class DesignFactory implements IDesignFactory<IVessel>{

	private static final String ID = "org.miip.pond.model.PondEnvironment";
	
	private static IEnvironment<IVessel> environment = new PondEnvironment();
	
	private IPreferenceStore<String, String> store;
	
	public DesignFactory() {
		super();
	}

	@Override
	public IEnvironment<IVessel> createEnvironment() {
		return environment;
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public IPreferenceStore<String, String> createPreferenceStore(IEnvironment<IVessel> environment) {
		if(( environment == null ) || !environment.equals( environment ))
			return null;
		if( store == null )
			store = new PreferenceStore();
		return store;
	}

	private class PreferenceStore extends AbstractPreferenceStore<PreferenceStore> {
		
		private PreferenceStore() {
			super( Activator.BUNDLE_ID);
		}
		
		protected PreferenceStore(Preferences preferences) {
			super(preferences);
		}

		@Override
		protected PreferenceStore onDecorate(Preferences preferences) {
			return new PreferenceStore( preferences );
		}
	}
}
