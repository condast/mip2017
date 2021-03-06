package org.miip.rwt.service;

import org.condast.commons.autonomy.model.IPhysical;
import org.miip.waterway.model.def.IDesignFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component( name="org.miip.rwt.service.environment", immediate=true)
public class EnvironmentService {

	private Dispatcher dispatcher = Dispatcher.getInstance();

	@Reference( cardinality = ReferenceCardinality.AT_LEAST_ONE,
			policy=ReferencePolicy.DYNAMIC)
	public void addEnvironment( IDesignFactory<IPhysical> factory){
		this.dispatcher.addEnvironment( factory );
	}

	public void removeEnvironment( IDesignFactory<IPhysical> ce ){
		//this.dispatcher.removeEnvironment( ce. );
	}
}
