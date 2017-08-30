package org.miip.waterway.ui.swt;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;

public class Radar extends AbstractRadar{
	private static final long serialVersionUID = 1L;

	private static final int CORRECTION = 100;//To fill the screen
	
	public Radar(Composite parent, int style) {
		super(parent, style);
	}
	
	protected void drawDegree( GC gc, int angle, double distance ){
		if( distance > super.getRange() )
			return;
		double centrex = getCentre().x;
		double centrey = getCentre().y;
		double length = CORRECTION + Math.sqrt( centrex * centrex + centrey * centrey);
		double offset = distance * length/getSituationalAwareness().getRange();
		
		double xpos1 = centrex + offset * Math.sin( toRadians( angle ));
		double ypos1 = centrey + offset * Math.cos( toRadians( angle ));
		gc.setForeground( getColour( distance ));
		gc.drawLine((int)centrex, (int)centrey, (int)xpos1, (int)ypos1);
		
	}
}
