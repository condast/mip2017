package org.miip.rwt.service;

import java.util.ArrayList;
import java.util.Collection;

import org.miip.rwt.frontend.Frontend;
import org.miip.waterway.model.def.IMIIPEnvironment;
import org.miip.waterway.sa.ISituationalAwareness;
import org.miip.waterway.ui.factory.ICompositeFactory;

public class Dispatcher {

	private static Dispatcher dispatcher = new Dispatcher();

	private IMIIPEnvironment cenv;
	private Frontend frontend;
	
	private Collection<ICompositeFactory> factories;
	
	private Dispatcher() {
		factories = new ArrayList<ICompositeFactory>();
	}

	public static Dispatcher getInstance(){
		return dispatcher;
	}

	public void startApplication( Frontend frontend ){
		this.frontend = frontend;
		if( this.cenv != null ){
			this.frontend.setInput( cenv );
			this.frontend.setInput(factories);
		}
	}

	public void startApplication( IMIIPEnvironment cenv ){
		this.cenv = cenv;
		if( this.frontend != null ){
			this.frontend.setInput(cenv);
		}
	}
	
	public ISituationalAwareness getSituationalAwareness(){
		return this.cenv.getSituationalAwareness();
	}
	
	public void addCompositeFactory( ICompositeFactory factory ) {
		this.factories.add( factory );
	}

	public void removeCompositeFactory( ICompositeFactory factory ) {
		this.factories.remove( factory );
	}
}