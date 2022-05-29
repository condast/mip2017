package org.miip.waterway.radar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.condast.commons.autonomy.sa.ISituationalAwareness;
import org.condast.commons.autonomy.sa.radar.IRadar;
import org.condast.commons.autonomy.sa.radar.IRadarData;
import org.condast.commons.autonomy.sa.radar.Radar;
import org.condast.commons.autonomy.sa.radar.VesselRadarData;
import org.condast.commons.data.latlng.LatLngUtils;
import org.condast.commons.range.DoubleRange;
import org.condast.commons.ui.radar.IRadarColours;
import org.miip.waterway.model.IVessel;
import org.miip.waterway.model.def.IMIIPRadar;

public class RestRadar{

	private IRadar radar;

	private Map<Integer, Double> scan;

	private RadarOptions options;
	private List<MIIPRadarData> colours;
	private int leds;
	private IMIIPRadar.RadarSelect radarType;

	private Logger logger = Logger.getLogger(this.getClass().getName());

	public RestRadar( RadarOptions options, int leds, ISituationalAwareness<VesselRadarData> sa ) {
		colours = new ArrayList<>();
		this.options = options;
		this.radarType = options.getRadarType();
		this.leds = leds;
		this.scan = new HashMap<>();
		this.radar = new Radar();
		//this.radar.setInput(sa);
		radar.setRange( options.getRange());
		double sensitivity = ( options.getSensitivity() <= 0)?sa.getRange(): options.getSensitivity();
		radar.setSensitivity( (int) sensitivity);
		radar.setSteps( this.leds );
	}

	public IMIIPRadar.RadarSelect getRadarType() {
		return radarType;
	}

	public List<MIIPRadarData> drawField(){
		colours.clear();
		ISituationalAwareness<VesselRadarData> sa = null;//radar.getInput();
		if( sa == null )
			return colours;
		for( IRadarData<VesselRadarData> radarData: sa.getRadarData() ){
			VesselRadarData data = radarData.getData( IRadarData.DefaultDimensions.VESSEL_RADAR_DATA.getIndex());
			drawObject( null/*sa.getReference()*/, data );
		}
		this.onDrawEnd();
		return colours;
	}

	public int getKey( double angle ) {
		int key = ( int )(this.leds * angle /( 2*Math.PI ));
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

	public void drawObject( IVessel reference, VesselRadarData ship ){
		logger.fine(" Reference: " + reference.getLocation().toLocation() + " -\t" + ship.getLocation().toLocation());
		logger.fine(": Diff ( " + (ship.getLocation().getLatitude() - reference.getLocation().getLatitude()) + " (N), " + (ship.getLocation().getLongitude() - reference.getLocation().getLongitude()) + " (W)");
		double angle = LatLngUtils.getHeading(reference.getLocation(), ship.getLocation());
		double distance = LatLngUtils.getDistance(reference.getLocation(), ship.getLocation());
		int key = getKey( angle );
		setKey(key, distance);
		logger.info("Key:" + key + "; Angle of vessel: " + angle + ", distance = " + distance);
		if( distance >= reference.getCriticalDistance() *3)
			return;

		int lowkey = (this.leds + key - 1)%this.leds;
		setKey(lowkey, 1.2*distance);
		int highkey = (this.leds + key + 1)%this.leds;
		setKey(highkey, 1.2*distance);
		if( distance >= reference.getCriticalDistance() *2)
			return;

		lowkey = (this.leds + key - 2)%this.leds;
		setKey(lowkey, 1.5*distance);
		highkey = (this.leds + key + 2)%this.leds;
		setKey(highkey, 1.5*distance);
	}

	protected void onDrawEnd() {
		int[] colour = getBackgroundColour();
		double distance = radar.getRange() + 10;
		ISituationalAwareness<VesselRadarData> sa = null;//radar.getInput();
		for( int i=0; i< this.leds; i++ ) {
			Double value = scan.get( i );
			distance = ( value == null )?Double.MAX_VALUE: value;
			colour = IRadarColours.RadarColours.getColour( radar.getRange(), radar.getSensitivity(), 0 /*(int) sa.getReference().getCriticalDistance()*/, distance);
			colours.add( new MIIPRadarData( i, colour , options.getTransparency()));
		}
	}

	public Collection<MIIPRadarData> getColours() {
		return colours;
	}

	protected int[] getBackgroundColour(){
		int[] colour = new int[3];
		colour[0] = 0;
		colour[1] = 1;
		colour[2] = 0;
		return colour;
	}

	public static class MIIPRadarData{
		private int a;
		private int r;
		private int g;
		private int b;
		private int t;

		protected MIIPRadarData(int angle, int[] rgb, int transparency) {
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