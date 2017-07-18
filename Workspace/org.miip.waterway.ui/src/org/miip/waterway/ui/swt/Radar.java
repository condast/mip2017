package org.miip.waterway.ui.swt;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;

public class Radar extends AbstractRadar{
	private static final long serialVersionUID = 1L;

	
	public Radar(Composite parent, int style) {
		super(parent, style);
	}
	
	protected void drawDegree( GC gc, int angle, double distance ){
		double centrex = getCentre().x;
		double centrey = getCentre().y;
		double length = Math.sqrt( centrex * centrex + centrey * centrey);
		double offset = distance * length/getSituationalAwareness().getRange();
		
		double xpos1 = centrex + offset * Math.sin( toRadians( angle ));
		double ypos1 = centrey + offset * Math.cos( toRadians( angle ));
		gc.setForeground( getColour( distance ));
		//gc.fillPolygon(new int[]{(int) centrex, (int)centrey, (int)xpos1, (int)ypos1, (int)xpos2, (int)ypos2});
		gc.drawLine((int)centrex, (int)centrey, (int)xpos1, (int)ypos1);
		
	}
}
