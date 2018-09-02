package org.miip.waterway.ui.swt;

import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.sa.ISituationalAwareness;
import org.condast.commons.data.latlng.LatLngUtils;
import org.condast.commons.range.DoubleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.miip.waterway.model.IVessel;

public class LedRing<I> extends AbstractSWTRadar<IVessel,IPhysical> {
	private static final long serialVersionUID = 1L;

	public static final int NR_OF_LEDS = 24;
	public static final int RADIUS = 10;
	public static final int DEGREES = 360;
	
	private int leds;
	
	private Map<Integer, Double> radar;
	
	private Logger logger = Logger.getLogger(this.getClass().getName());

	public LedRing(Composite parent, int style ) {
		super(parent, style);
		this.leds = NR_OF_LEDS;
		radar = new TreeMap<>( );
	}
	
	@Override
	protected void onDrawStart(GC gc) {
		this.radar.clear();
		super.onDrawStart(gc);
	}

	public int getKey( double angle ) {
		int key = ( int )((double) this.leds * angle /( 2*Math.PI ));
		int range = isInRange(key, angle);
		return (this.leds + key + range ) % this.leds;
	}
	
	protected void setKey( int key, double distance ) {
		Double dist = this.radar.get(key);
		if(( dist != null ) && ( dist < distance ))
			distance = dist;
		this.radar.put(key, distance);		
	}
	
	@Override
	protected void drawObject( GC gc, IPhysical ship ){
		IVessel reference = (IVessel) getInput().getReference(); 
		logger.fine(" Reference: " + reference.getLocation().toLocation() + " -\t" + ship.getLocation().toLocation());
		logger.fine(": Diff ( " + (ship.getLocation().getLatitude() - reference.getLocation().getLatitude()) + " (N), " + (ship.getLocation().getLongitude() - reference.getLocation().getLongitude()) + " (W)");
		double angle = LatLngUtils.getBearing(reference.getLocation(), ship.getLocation());
		double distance = LatLngUtils.getDistance(reference.getLocation(), ship.getLocation());
		int key = getKey( angle );
		setKey(key, distance);
		logger.info("Key:" + key + "; Angle of vessel: " + angle + ", distance = " + distance);
		if( distance < reference.getSituationalAwareness().getCriticalDistance() *3) {
			int lowkey = (this.leds + key - 1)%this.leds;
			setKey(lowkey, distance);
			int highkey = (this.leds + key + 1)%this.leds;
			setKey(highkey, distance);
			if( distance < reference.getSituationalAwareness().getCriticalDistance() *2) {
				lowkey = (this.leds + key - 2)%this.leds;
				setKey(lowkey, distance);
				highkey = (this.leds + key + 2)%this.leds;
				setKey(highkey, distance);
			}
		}
	}

	protected int isInRange( int key, double angle ) {
		double step = 2*Math.PI/this.leds;
		double ref = key*2*Math.PI/this.leds;
		DoubleRange range = new DoubleRange(ref-step, ref+step);
		return range.compareTo(angle);
	}
	
	protected Color getColour( int key, double distance) {
		RadarColours colour = RadarColours.GREEN;
		if( super.getInput() == null)
			return super.getColour(distance);
		ISituationalAwareness<IVessel, IPhysical> sa = super.getInput();
		int sensitivity = (int)(distance/sa.getCriticalDistance());
		logger.fine("Comparing distance: " + distance + " with sensitivity: " + sensitivity);
		switch( sensitivity) {
		case 6:
			colour = RadarColours.getColour( RadarColours.DARK_ORANGE.getIndex() );
			break;
		case 5:
		case 4:
		case 3:
			colour = RadarColours.getColour( RadarColours.ORANGE.getIndex() );
			break;
		case 2:
		case 1:
		case 0:
			colour = RadarColours.getColour( RadarColours.RED.getIndex());
			break;
		default:
			break;
		}
		return RadarColours.getColour(getDisplay(), colour);
	}

	@Override
	protected void onDrawEnd(GC gc) {
		double centrex = super.getCentre().x;
		double centrey = super.getCentre().y;
		double length = (centrex < centrey )? centrex: centrey;
		length = (( length - RADIUS ) < 0)? 0: length - RADIUS;
		Color background = gc.getBackground();
		double distance = super.getRange() + 10;
		for( int i=0; i< this.leds; i++ ) {
			Double value = radar.get( i );
			distance = ( value == null )?Double.MAX_VALUE: value;
			double phi = i * 2 * Math.PI/this.leds;
			double x = length * Math.sin( phi );
			double y = length * Math.cos( phi );
			gc.setBackground( getColour( i, distance ));
			gc.fillOval((int)(centrex + x), (int)(centrey-y), RADIUS, RADIUS );			
		}
		gc.setBackground(background);
		super.onDrawEnd(gc);
	}
}