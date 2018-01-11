package org.miip.rwt.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.condast.commons.Utils;
import org.miip.waterway.model.def.ISimpleEnvironment;
import org.miip.waterway.ui.factory.ICompositeFactory;
import org.miip.waterway.ui.swt.MiipComposite;

public class Dispatcher {

	private static Dispatcher dispatcher = new Dispatcher();

	private MiipComposite frontend;
	
	private Collection<ICompositeFactory> factories;
	
	private Map<String, ISimpleEnvironment> environments;
	
	//private IXMLFactoryListener listener = new IX
	
	private Dispatcher() {
		factories = new ArrayList<ICompositeFactory>();
		environments = new HashMap<String, ISimpleEnvironment>();
	}

	public static Dispatcher getInstance(){
		return dispatcher;
	}

	public void startApplication( MiipComposite frontend ){
		this.frontend = frontend;
		if( !Utils.assertNull( this.environments )){
			this.frontend.setInput(factories);
		}
	}

	public void startApplication( ISimpleEnvironment cenv ){
		this.environments.put( cenv.getName(), cenv );
		//if( this.frontend != null ){
		//	this.frontend.setInput(cenv);
		//}
	}
		
	public void addCompositeFactory( ICompositeFactory factory ) {
		this.factories.add( factory );
	}

	public void removeCompositeFactory( ICompositeFactory factory ) {
		this.factories.remove( factory );
	}

	public void removeCompositeFactory( ISimpleEnvironment environment ) {
		this.environments.remove( environment.getName() );
	}
}