package org.miip.rwt.service;


import org.miip.waterway.model.def.IDesignFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component
public class EnvironmentService {

	private Dispatcher dispatcher = Dispatcher.getInstance();
	
	@Reference
	public void bindEnvironment( IDesignFactory factory){
		this.dispatcher.startApplication( factory.createEnvironment());
	}

	public void unbindEnvironment( IDesignFactory ce ){
	}

}
