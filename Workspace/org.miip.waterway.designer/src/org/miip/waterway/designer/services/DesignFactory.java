package org.miip.waterway.designer.services;

import org.miip.waterway.model.def.IDesignFactory;
import org.miip.waterway.model.def.IMIIPEnvironment;
import org.miip.waterway.model.eco.MIIPEnvironment;
import org.osgi.service.component.annotations.Component;

@Component
public class DesignFactory implements IDesignFactory{

	private static final String ID = "org.miip.waterway.model.eco.MIIPEnvironment";
	
	private static IMIIPEnvironment environment = new MIIPEnvironment();
	
	public DesignFactory() {
		super();
	}

	
	@Override
	public IMIIPEnvironment createEnvironment() {
		return environment;
	}

	@Override
	public String getId() {
		return ID;
	}

}
