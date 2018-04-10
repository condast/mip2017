package org.miip.waterway.rest.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.sa.ISituationalAwareness;
import org.condast.commons.data.latlng.Motion;
import org.miip.waterway.model.def.IRadar;
import org.miip.waterway.radar.LedRadar;
import org.miip.waterway.radar.Radar;
import org.miip.waterway.rest.store.RadarOptions;

public class RestRadar{

	public enum Colour{
		RED,
		GREEN,
		BLUE;
	}
	
	private IRadar<IPhysical> radar;
	private RadarOptions options;
	private List<RadarData> colours;
	private int leds;
	private IRadar.RadarSelect radarType;

	public RestRadar( RadarOptions options, int leds, ISituationalAwareness<IPhysical, ?> sa ) {
		colours = new ArrayList<RadarData>();
		this.options = options;
		this.radarType = options.getRadarType();
		this.leds = leds;
		this.radar = new Radar<IPhysical>();
		this.radar.setInput(sa);
	}

	public List<RadarData> drawField(){
		colours.clear();
		ISituationalAwareness<IPhysical, ?> sa = radar.getInput();
		if( sa == null )
			return colours;
		radar.setRange( options.getRange());
		radar.setSensitivity( options.getSensitivity());
		radar.setSteps( this.leds );
		LedRadar<IPhysical> lr = new LedRadar<IPhysical>( radar );
		lr.refresh();
		for( int i=0; i< radar.getSteps(); i++ ) {
			byte[] colour = getBackgroundColour();
			Motion motion = lr.getRadarData(i);
			if( motion != null )
				colour = getColour( motion.getDistance());
			colours.add( new RadarData( i, colour , options.getTransparency()));
		}
		return colours;
	}
	
	public Collection<RadarData> getColours() {
		return colours;
	}

	protected byte[] getBackgroundColour(){
		byte[] colour = new byte[3];
		colour[0] = 0;
		colour[1] = 1;
		colour[2] = 0;
		return colour;
	}

	protected byte[] getColour( double distance ){
		byte[] colour = new byte[3];
		colour[0] = 0;
		colour[1] = 0;
		colour[2] = 0;
		if( radar.getInput() == null){
			return colour;
		}
		
		if( distance <= radar.getSensitivity() ){
			colour[0] = (byte) 255;
			return colour;
		}
		if( distance > radar.getRange() )
			return colour;
		return getLinearColour( colour, (int) distance, radar.getRange(), radar. getSensitivity() );
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
		private byte r;
		private byte g;
		private byte b;
		private byte t;
		protected RadarData(int angle, byte[] rgb, int transparency) {
			super();
			this.a = angle;
			this.r = rgb[0];
			this.g = rgb[1];
			this.b = rgb[2];
			this.t = (byte)transparency;
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
