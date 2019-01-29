package org.miip.waterway.radar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.sa.ISituationalAwareness;
import org.condast.commons.data.latlng.LatLngUtils;
import org.condast.commons.range.DoubleRange;
import org.condast.commons.ui.radar.IRadarColours;
import org.miip.waterway.model.IVessel;
import org.miip.waterway.model.def.IRadar;

public class RestRadar{

	public enum Colour{
		RED,
		GREEN,
		BLUE;
	}
	
	private IRadar<IVessel, IPhysical> radar;

	private Map<Integer, Double> scan;

	private RadarOptions options;
	private List<RadarData> colours;
	private int leds;
	private IRadar.RadarSelect radarType;

	private Logger logger = Logger.getLogger(this.getClass().getName());

	public RestRadar( RadarOptions options, int leds, ISituationalAwareness<IVessel, IPhysical> sa ) {
		colours = new ArrayList<RadarData>();
		this.options = options;
		this.radarType = options.getRadarType();
		this.leds = leds;
		this.scan = new HashMap<>();
		this.radar = new Radar<IVessel, IPhysical>();
		this.radar.setInput(sa);
	}

	public IRadar.RadarSelect getRadarType() {
		return radarType;
	}
	
	public List<RadarData> drawField(){
		colours.clear();
		ISituationalAwareness<IVessel, IPhysical> sa = radar.getInput();
		if( sa == null )
			return colours;
		radar.setRange( options.getRange());
		double sensitivity = ( options.getSensitivity() <= 0)?sa.getRange(): options.getSensitivity();
		radar.setSensitivity( (int) sensitivity);
		radar.setSteps( this.leds );
		for( IPhysical obj: sa.getScan() ){
			drawObject( sa.getReference(), obj );
		}
		this.onDrawEnd();	
		return colours;
	}

	public int getKey( double angle ) {
		int key = ( int )((double) this.leds * angle /( 2*Math.PI ));
		int range = isInRange(key, angle);
		return (this.leds + key + range ) % this.leds;
	}
	
	protected void setKey( int key, double distance ) {
		Double dist = this.scan.get(key);
		if(( dist != null ) && ( dist < distance ))
			distance = dist;
		this.scan.put(key, distance);		
	}

	protected int isInRange( int key, double angle ) {
		double step = 2*Math.PI/this.leds;
		double ref = key*2*Math.PI/this.leds;
		DoubleRange range = new DoubleRange(ref-step, ref+step);
		return range.compareTo(angle);
	}
	

	public void drawObject( IVessel reference, IPhysical ship ){
		logger.fine(" Reference: " + reference.getLocation().toLocation() + " -\t" + ship.getLocation().toLocation());
		logger.fine(": Diff ( " + (ship.getLocation().getLatitude() - reference.getLocation().getLatitude()) + " (N), " + (ship.getLocation().getLongitude() - reference.getLocation().getLongitude()) + " (W)");
		double angle = LatLngUtils.getBearing(reference.getLocation(), ship.getLocation());
		double distance = LatLngUtils.getDistance(reference.getLocation(), ship.getLocation());
		int key = getKey( angle );
		setKey(key, distance);
		logger.fine("Key:" + key + "; Angle of vessel: " + angle + ", distance = " + distance);
		if( distance < reference.getCriticalDistance() *3) {
			int lowkey = (this.leds + key - 1)%this.leds;
			setKey(lowkey, 1.2*distance);
			int highkey = (this.leds + key + 1)%this.leds;
			setKey(highkey, 1.2*distance);
			if( distance < reference.getCriticalDistance() *2) {
				lowkey = (this.leds + key - 2)%this.leds;
				setKey(lowkey, 1.5*distance);
				highkey = (this.leds + key + 2)%this.leds;
				setKey(highkey, 1.5*distance);
			}
		}
	}

	protected void onDrawEnd() {
		int[] colour = getBackgroundColour();
		double distance = radar.getRange() + 10;
		ISituationalAwareness<IVessel, IPhysical> sa = radar.getInput();
		for( int i=0; i< this.leds; i++ ) {
			Double value = scan.get( i );
			distance = ( value == null )?Double.MAX_VALUE: value;
			logger.info("Distance: " + distance);
			colour = getColour( radar.getSensitivity(), (int) sa.getReference().getCriticalDistance(), distance);
			colours.add( new RadarData( i, colour , options.getTransparency()));
		}
	}

	public Collection<RadarData> getColours() {
		return colours;
	}

	protected int[] getBackgroundColour(){
		int[] colour = new int[3];
		colour[0] = 0;
		colour[1] = 1;
		colour[2] = 0;
		return colour;
	}

	protected int[] getColour( int sensitivity, int critical, double distance ){
		int[] colour = new int[3];
		colour[0] = 0;
		colour[1] = 0;
		colour[2] = 0;
		if( radar.getInput() == null){
			return colour;
		}
		
		if( distance <= radar.getSensitivity() ){
			colour[0] = 255;
			return colour;
		}
		if( distance > radar.getRange() )
			colour = IRadarColours.RadarColours.getColour(sensitivity, critical, distance);
		return colour;
		//return getLinearColour( colour, (int) distance, radar.getRange(), radar. getSensitivity() );
	}
	
	protected byte[] getIntColour( Map<Colour, Byte> colour ){
		byte[] values = new byte[3];
		values[0] = colour.get(Colour.RED);
		values[1] = colour.get(Colour.GREEN);
		values[2] = colour.get(Colour.BLUE);
		return values;
	}

	public byte[]  getLinearColour( byte[] colour, int distance, long range, int sensitivity ){
		byte red = 0;
		byte green = 0;
		byte blue = 0;
		boolean far = ( distance > ( range - sensitivity ));
		if( radar.getInput() != null) {
			red = (far? (byte) 50: (byte)( 255 * ( 1 - distance/range )));
			green = (far? (byte) 255: (byte)( 255 * distance/range ));
			blue = 50;
		}
		colour[0] = red;
		colour[1] = green;
		colour[2] = blue;
		return colour;
	}

	public static class RadarData{
		private int a;
		private int r;
		private int g;
		private int b;
		private int t;

		protected RadarData(int angle, int[] rgb, int transparency) {
			super();
			this.a = angle;
			this.r = rgb[0];
			this.g = rgb[1];
			this.b = rgb[2];
			this.t = transparency;
		}
		
		public int getAngle() {
			return this.a;
		}
		
		public int[] getColor() {
			int[] rgb = new int[3];
			rgb[0] = this.r;
			rgb[1] = this.g;
			rgb[2] = this.b;
			return rgb;
		}
		
		@Override
		public String toString() {
			StringBuffer buffer = new StringBuffer();
			buffer.append("RGB[" + a + ":]= {");
			buffer.append(String.valueOf(r) + ", ");
			buffer.append(String.valueOf(g) + ", ");
			buffer.append(String.valueOf(b) + ", ");
			buffer.append(String.valueOf(t) + "}");
			return buffer.toString();
		}
	}
}
