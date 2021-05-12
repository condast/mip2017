package org.miip.rwt.entry;

import java.util.concurrent.TimeUnit;

import org.condast.commons.config.Config;
import org.condast.commons.data.colours.RGBA;
import org.condast.commons.data.plane.IField;
import org.condast.commons.ui.entry.AbstractRestEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Label;
import org.miip.rwt.core.FieldFactory;
import org.miip.waterway.ui.map.ColourEvent;
import org.miip.waterway.ui.map.RouterMapBrowser;
import org.miip.waterway.ui.swt.PixelXYGraph;
import org.miip.waterway.ui.swt.PixelXYMap;

public class RouterEntryPoint extends AbstractRestEntryPoint {
	private static final long serialVersionUID = 1L;

	public static final String S_ROUTER_ENTRY = "/router";
	
	private RouterMapBrowser browser;
	
	private CoolBar coolbar; 
	private Label labelStart, labelEnd;
	private PixelXYGraph graph;
	private PixelXYMap map;
	
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
		parent.setLayout(new GridLayout(2, false ));

		browser = new RouterMapBrowser( parent, SWT.NONE );	
		browser.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true));
		browser.addColourListener( e->onNotifyColourEvent(e));
		
		Composite comp = new Composite(parent, SWT.BORDER);
		comp.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true));
		comp.setLayout(new GridLayout( 3, false));
		
		coolbar = new CoolBar(comp, SWT.LEFT_TO_RIGHT);
		coolbar.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false, 3, 1));
		coolbar.setLayout(new GridLayout( 2, false));
		coolbar.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE));
		CoolItem item = new CoolItem(coolbar, SWT.NONE);
		Button button = new Button( coolbar, SWT.BORDER );
		button.setText("FILL");
		button.setLayoutData( new GridData( SWT.FILL, SWT.FILL, false, false));
		button.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					browser.fill();
					super.widgetSelected(e);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}		
		});
	    Point p = button.computeSize(SWT.DEFAULT, SWT.DEFAULT);
	    Point p2 = item.computeSize(p.x, p.y+10);
	    item.setSize(p2);
		item.setControl( button);
		coolbar.pack();
		
		GridData labelData = new GridData( SWT.FILL, SWT.FILL, false, false);
		labelData.widthHint = 20;
		labelStart = new Label( comp, SWT.BORDER );
		labelStart.setLayoutData( labelData);
		
		graph = new PixelXYGraph(comp, SWT.BORDER);
		graph.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false));

		labelEnd = new Label( comp, SWT.BORDER );
		labelEnd.setLayoutData( labelData);

		map = new PixelXYMap(comp, SWT.NONE);
		map.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 3, 1));

		Config config = new Config();
		browser.setInput(config.getServerContext());
		return browser;
	}

	
	@Override
	protected boolean postProcess(Composite parent) {
		try {
			IField field = FieldFactory.createField();
			browser.setField(field);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.postProcess(parent);
	}

	protected void onNotifyColourEvent( ColourEvent event ) {
		switch( event.getType()) {
		case POINT:
			RGBA colour = event.getColours()[0];
			if( event.getIndex() == 0)
				labelStart.setBackground( new Color( labelStart.getDisplay(), colour.getRed(), colour.getGreen(), colour.getBlue()));
			else
				labelEnd.setBackground( new Color( labelStart.getDisplay(), colour.getRed(), colour.getGreen(), colour.getBlue()));
			break;
		case AREA:
			map.setLayer(event.getMap());
			break;
		case LINE:
			graph.setInput(event.getColours());
			break;
		default:
			break;
		}
	}
}