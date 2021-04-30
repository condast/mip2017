package org.miip.rwt.entry;

import java.util.concurrent.TimeUnit;

import org.condast.commons.config.Config;
import org.condast.commons.ui.entry.AbstractRestEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.miip.rwt.service.Dispatcher;
import org.miip.waterway.ui.map.RouterMapBrowser;

public class RouterEntryPoint extends AbstractRestEntryPoint {
	private static final long serialVersionUID = 1L;

	public static final String S_ROUTER_ENTRY = "/router";
	
	private RouterMapBrowser browser;
	
	private Dispatcher dispatcher = Dispatcher.getInstance();
		
	@Override
	protected boolean prepare(Composite parent) {
		return true;
	}

	@Override
	protected void handleTimer() {
		browser.refresh();
		super.handleTimer();
	}

	@Override
	protected void createTimer(boolean create, int nrOfThreads, TimeUnit unit, int startTime, int rate) {
		super.createTimer(true, nrOfThreads, unit, startTime, rate);
	}

	@Override
	protected Composite createComposite(Composite parent) {
		parent.setLayout(new FillLayout());
		browser = new RouterMapBrowser( parent, SWT.NONE );
		
		Config config = new Config();
		browser.setInput(config.getServerContext());
		return browser;
	}
}