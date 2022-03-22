package org.miip.waterway.rest.service;


import org.miip.waterway.model.IVessel;
import org.miip.waterway.model.def.IDesignFactory;
import org.miip.waterway.rest.core.Dispatcher;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component( name="org.miip.waterway.rest.service", immediate=true)
public class EnvironmentService {

	private Dispatcher dispatcher = Dispatcher.getInstance();

	@Reference( cardinality = ReferenceCardinality.AT_LEAST_ONE,
			policy=ReferencePolicy.DYNAMIC)
	public void bindEnvironment( IDesignFactory<IVessel> factory){
		this.dispatcher.addfactory(factory);
	}

	public void unbindEnvironment( IDesignFactory<IVessel> factory ){
		this.dispatcher.removefactory(factory);
	}

}
