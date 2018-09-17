package org.miip.waterway.designer.services;

import org.condast.commons.autonomy.env.IEnvironment;
import org.condast.commons.preferences.AbstractPreferenceStore;
import org.condast.commons.preferences.IPreferenceStore;
import org.miip.waterway.designer.Activator;
import org.miip.waterway.model.IVessel;
import org.miip.waterway.model.def.IDesignFactory;
import org.miip.waterway.model.def.IMIIPEnvironment;
import org.miip.waterway.model.eco.MIIPEnvironment;
import org.miip.waterway.radar.RadarOptions;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.prefs.Preferences;

@Component( name="org.miip.wterway.design.factory")
public class DesignFactory implements IDesignFactory<IVessel>{

	private static final String ID = "org.miip.waterway.model.eco.MIIPEnvironment";
	
	private static IMIIPEnvironment environment = new MIIPEnvironment();

	private IPreferenceStore<String, String> store;
	
	public DesignFactory() {
	}
	
	@Override
	public IMIIPEnvironment createEnvironment() {
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

	private class PreferenceStore extends AbstractPreferenceStore {
		
		private PreferenceStore() {
			super( Activator.BUNDLE_ID);
			RadarOptions options = new RadarOptions( this, environment.getInhabitant().getId() );
			options.setEnable(true);
		}
		
		protected PreferenceStore(Preferences preferences) {
			super(preferences);
		}

		@Override
		protected IPreferenceStore<String, String> onAddChild(Preferences preferences) {
			return new PreferenceStore( preferences );
		}
	}
}