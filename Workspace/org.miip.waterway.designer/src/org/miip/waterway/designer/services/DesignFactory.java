package org.miip.waterway.designer.services;

import org.miip.waterway.model.def.IDesignFactory;
import org.miip.waterway.model.def.IMIIPEnvironment;
import org.miip.waterway.model.def.IPhysical;
import org.miip.waterway.model.eco.MIIPEnvironment;
import org.osgi.service.component.annotations.Component;

@Component( name="org.miip.wterway.design.factory")
public class DesignFactory implements IDesignFactory<IPhysical>{

	private static final String ID = "org.miip.waterway.model.eco.MIIPEnvironment";
	
	private static IMIIPEnvironment environment = new MIIPEnvironment();
	
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

}
