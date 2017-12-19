package org.miip.waterway.rest.service;

import org.miip.waterway.sa.ISituationalAwareness;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

@Component( name="org.miip.waterway.rest.service.sa")
public class SituationalAwarenessService {

	private Dispatcher dispatcher = Dispatcher.getInstance();
	
	@Reference( cardinality = ReferenceCardinality.AT_LEAST_ONE)
	public void bind( ISituationalAwareness sa){
		this.dispatcher.add( sa );
	}

	public void unbind( ISituationalAwareness sa ){
		this.dispatcher.remove( sa );
	}

}
