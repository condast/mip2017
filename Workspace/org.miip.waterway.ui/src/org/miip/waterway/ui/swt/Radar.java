package org.miip.waterway.ui.swt;

import java.util.Map;
import java.util.logging.Logger;

import org.condast.commons.data.latlng.Field;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.miip.waterway.model.IVessel;
import org.miip.waterway.sa.ISituationalAwareness;

public class Radar<I extends Object> extends AbstractRadar<I,IVessel>{
	private static final long serialVersionUID = 1L;

	private Logger logger = Logger.getLogger( this.getClass().getName());
	
	public Radar(Composite parent, int style) {
		super(parent, style);
	}
	
	@Override
	protected void drawDegree( GC gc, IVessel ship ){
		ISituationalAwareness<I, IVessel> sa = super.getInput();
		IVessel reference = sa.getReference(); 
		if( ship.equals( reference ))
			return;
		
		Field field = sa.getField();
		Map.Entry<Double, Double> vector = field.getDifference(reference.getLocation(), ship.getLocation());
		double distance = vector.getValue();
		double angle = vector.getKey();
		if( distance > super.getRange() )
			return;
		logger.info("Comparing " + reference.getName() + " and " + ship.getName() + "}" );
		logger.info("Ships at {" + distance + ", " + angle + "}" );
		double centrex = getCentre().x;
		double centrey = getCentre().y;
		double length = Math.sqrt( centrex * centrex + centrey * centrey);
		
		double offset = distance * length/getRange();
		double xpos1 = centrex + offset * Math.sin( angle );
		double ypos1 = centrey - offset * Math.cos( angle );//correction for different positioning
		//logger.info( "Length:" + length + "Offset:" + offset + ":Coordinates {" + centrex + ", " + centrey + "}-{" + xpos1 + ", " + ypos1 + "}" );
		gc.setForeground( getColour( distance ));
		gc.drawLine((int)centrex, (int)centrey, (int)xpos1, (int)ypos1);
	}
}
