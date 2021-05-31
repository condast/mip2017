package org.miip.waterway.model.def;

import org.condast.commons.autonomy.env.IEnvironment;
import org.condast.commons.preferences.IPreferenceStore;

public interface IDesignFactory<D extends Object> {

	public String getId();
	
	/**
	 * Create a new environment
	 * @return
	 */
	public IMIIPEnvironment createEnvironment();
	
	/**
	 * Create the preference store for the given environment. This will always be unique within the application
	 * @param environment
	 * @return
	 */
	public IPreferenceStore<String,String> createPreferenceStore( IEnvironment<D> environment);
}
