package org.miip.rwt.service;

import org.miip.waterway.ui.factory.ICompositeFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

@Component( name="org.miip.rwt.service.composite")
public class CompositeService {

	private Dispatcher dispatcher = Dispatcher.getInstance();
	
	@Reference( cardinality = ReferenceCardinality.AT_LEAST_ONE)
	public void bind( ICompositeFactory factory){
		this.dispatcher.addCompositeFactory( factory );
	}

	public void unbind( ICompositeFactory factory ){
		this.dispatcher.removeCompositeFactory( factory );
	}

}
