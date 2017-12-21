package org.miip.waterway.ui.factory;

import org.eclipse.swt.widgets.Composite;

public interface ICompositeFactory {

	/**
	 * Get the UI name for this composite
	 * @param id
	 * @return
	 */
	public String getName();
	
	/**
	 * Create a composite with the given id, or null if none was found
	 * @param id
	 * @return
	 */
	public Composite createComposite( Composite parent, int style );
}
