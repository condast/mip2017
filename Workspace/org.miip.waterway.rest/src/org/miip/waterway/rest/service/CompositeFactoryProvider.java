package org.miip.waterway.rest.service;

import org.eclipse.swt.widgets.Composite;
import org.miip.waterway.rest.ui.RadarSettingsComposite;
import org.miip.waterway.ui.factory.ICompositeFactory;
import org.osgi.service.component.annotations.Component;

@Component(
		name = "org.miip.waterway.rest.service.composite.provider",
		immediate=true
)
public class CompositeFactoryProvider implements ICompositeFactory{

	public static final String S_RADAR_SETTINGS = "RadarSettings";
	public static final String S_RADAR = "Radar Settings";
	
	@Override
	public String getName() {
		return S_RADAR;
	}

	@Override
	public Composite createComposite(Composite parent, int style) {
		RadarSettingsComposite rsc = new RadarSettingsComposite( parent, style );	
		return rsc;
	}
}
