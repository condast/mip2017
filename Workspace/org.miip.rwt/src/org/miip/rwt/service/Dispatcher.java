package org.miip.rwt.service;

import org.miip.waterway.model.def.IMIIPEnvironment;
import org.miip.waterway.sa.ISituationalAwareness;
import org.miip.waterway.ui.FrontEndComposite;

public class Dispatcher {

	private static Dispatcher dispatcher = new Dispatcher();

	private IMIIPEnvironment cenv;
	private FrontEndComposite frontend;
	
	private Dispatcher() {}

	public static Dispatcher getInstance(){
		return dispatcher;
	}

	public void startApplication( FrontEndComposite frontend ){
		this.frontend = frontend;
		if( this.cenv != null )
			this.frontend.setInput( cenv );
	}

	public void startApplication( IMIIPEnvironment cenv ){
		this.cenv = cenv;
		if( this.frontend != null )
			this.frontend.setInput(cenv);
	}
	
	public ISituationalAwareness getSituationalAwareness(){
		return this.cenv.getSituationalAwareness();
	}
	public void dispose(){
	}

}
