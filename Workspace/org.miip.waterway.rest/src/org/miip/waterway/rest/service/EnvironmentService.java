package org.miip.waterway.rest.service;


import org.miip.waterway.model.def.IDesignFactory;
import org.miip.waterway.model.def.IPhysical;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component( name="org.miip.waterway.rest.service", immediate=true)
public class EnvironmentService {

	private Dispatcher dispatcher = Dispatcher.getInstance();
	
	@Reference( cardinality = ReferenceCardinality.AT_LEAST_ONE,
			policy=ReferencePolicy.DYNAMIC)
	public void bindEnvironment( IDesignFactory<IPhysical> factory){
		this.dispatcher.addEnvironment(factory);
	}

	public void unbindEnvironment( IDesignFactory<IPhysical> ce ){
	}

}
