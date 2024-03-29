package org.miip.waterway.ui.swt;

import java.util.Map;
import java.util.logging.Logger;

import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.sa.ISituationalAwareness;
import org.condast.commons.autonomy.sa.radar.VesselRadarData;
import org.condast.commons.autonomy.ui.radar.AbstractSWTRadar;
import org.condast.commons.data.latlng.LatLngUtils;
import org.condast.commons.data.plane.IField;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.miip.waterway.model.IVessel;

public class DirectRadar extends AbstractSWTRadar<IVessel>{
	private static final long serialVersionUID = 1L;

	private Logger logger = Logger.getLogger( this.getClass().getName());

	public DirectRadar(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected void drawObject( GC gc, VesselRadarData ship ){
		ISituationalAwareness<VesselRadarData> sa = null;//super.getInput();
		IVessel reference = null;//sa.getReference();
		if( ship.getPhysical().equals( reference ))
			return;

		IField field = null;//sa.getView();
		Map.Entry<Double, Double> vector = field.getDifference(reference.getLocation(), ship.getLocation());
		double distance = vector.getValue();
		double angle = vector.getKey();
		if( distance > super.getRange() )
			return;
		logger.fine("Ships at {" + distance + ", " + angle + "}" );
		logger.fine("Distance: " + LatLngUtils.getDistance( reference.getLocation(), ship.getLocation()) + " compare with " + distance );

		double centrex = getCentre().x;
		double centrey = getCentre().y;
		double length = Math.sqrt( centrex * centrex + centrey * centrey);

		//ScalingUtils su = new ScalingUtils( this, sa.getField());
		double offset = distance * length/getRange();
		double xpos1 = centrex + offset * Math.sin( angle );
		double ypos1 = centrey - offset * Math.cos( angle );//correction for different positioning
		//logger.info( "Length:" + length + "Offset:" + offset + ":Coordinates {" + centrex + ", " + centrey + "}-{" + xpos1 + ", " + ypos1 + "}" );
		gc.setForeground( getColour( distance ));
		gc.drawLine((int)centrex, (int)centrey, (int)xpos1, (int)ypos1);
	}
}
