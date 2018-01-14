package org.miip.rwt.service;

import org.miip.waterway.model.def.IDesignFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

@Component( name="org.miip.rwt.service.environment", immediate=true)
public class EnvironmentService {

	private Dispatcher dispatcher = Dispatcher.getInstance();

	@Reference( cardinality = ReferenceCardinality.AT_LEAST_ONE)
	public void addEnvironment( IDesignFactory factory){
		this.dispatcher.addEnvironment( factory );
	}

	public void removeEnvironment( IDesignFactory ce ){
	}
}
