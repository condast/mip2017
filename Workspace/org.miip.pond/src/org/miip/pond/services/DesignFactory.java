package org.miip.pond.services;

import org.miip.pond.core.PondEnvironment;
import org.miip.waterway.environment.IEnvironment;
import org.miip.waterway.model.def.IDesignFactory;
import org.miip.waterway.model.def.IPhysical;
import org.osgi.service.component.annotations.Component;

@Component( name="org.miip.pond.design.factory")
public class DesignFactory implements IDesignFactory<IPhysical>{

	private static final String ID = "org.miip.pond.model.PondEnvironment";
	
	private static IEnvironment<IPhysical> environment = new PondEnvironment();
	
	public DesignFactory() {
		super();
	}

	@Override
	public IEnvironment<IPhysical> createEnvironment() {
		return environment;
	}

	@Override
	public String getId() {
		return ID;
	}

}
