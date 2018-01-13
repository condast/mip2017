package org.miip.pond.services;

import org.miip.pond.core.PondEnvironment;
import org.miip.waterway.model.def.IDesignFactory;
import org.miip.waterway.model.def.IEnvironment;
import org.osgi.service.component.annotations.Component;

@Component
public class DesignFactory implements IDesignFactory{

	private static final String ID = "org.miip.pond.model.PondEnvironment";
	
	private static IEnvironment environment = new PondEnvironment();
	
	public DesignFactory() {
		super();
	}

	@Override
	public IEnvironment createEnvironment() {
		return environment;
	}

	@Override
	public String getId() {
		return ID;
	}

}
