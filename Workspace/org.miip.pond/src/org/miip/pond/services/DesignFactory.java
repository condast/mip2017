package org.miip.pond.services;

import org.miip.pond.core.MIIPEnvironment;
import org.miip.waterway.model.def.IDesignFactory;
import org.miip.waterway.model.def.IMIIPEnvironment;
import org.osgi.service.component.annotations.Component;

@Component
public class DesignFactory implements IDesignFactory{

	private static IMIIPEnvironment environment = new MIIPEnvironment();
	
	public DesignFactory() {
		super();
	}

	@Override
	public IMIIPEnvironment createEnvironment() {
		return environment;
	}

}
