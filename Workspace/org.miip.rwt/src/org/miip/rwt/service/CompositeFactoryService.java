package org.miip.rwt.service;

import org.miip.waterway.ui.factory.ICompositeFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component( name="org.miip.rwt.service.composite", immediate=true)
public class CompositeFactoryService {

	private Dispatcher dispatcher = Dispatcher.getInstance();

	@Reference( cardinality = ReferenceCardinality.AT_LEAST_ONE,
			policy=ReferencePolicy.DYNAMIC)
	public void addEnvironment( ICompositeFactory factory){
		this.dispatcher.addCompositeFactory( factory );
	}

	public void removeEnvironment( ICompositeFactory factory ){
		this.dispatcher.removeFactory( factory );
	}
}
