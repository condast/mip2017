package org.miip.waterway.rest.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.condast.commons.Utils;
import org.condast.commons.data.latlng.Vector;
import org.miip.waterway.sa.SituationalAwareness;

public class Radar{

	public enum Colour{
		RED,
		GREEN,
		BLUE;
	}
	
	private SituationalAwareness sa;
	private List<Vector<Integer>> vectors;
	
	private Collection<RadarData> colours;
	
	public Radar() {
		colours = new ArrayList<RadarData>();
	}

	protected void drawField(){
		if( sa == null )
			return;
		colours.clear();
		for( int i=0; i< vectors.size(); i++ ){
			Vector<Integer> vector = vectors.get(i);
			int angle = ( vector != null )? vector.getKey(): 0;
			double value = ( vector != null )? vector.getValue(): 0;
			drawDegree(angle, value);
		}
	}

	protected void drawDegree( int angle, double distance ){
		if(( Utils.assertNull( vectors )) || ( distance > sa.getRange() )) 
			return;
		colours.add( new RadarData( angle, getColour( angle )));
	}
	
	public Collection<RadarData> getColours() {
		return colours;
	}

	public void setInput( SituationalAwareness sa ){
		this.sa = sa;
		vectors = getBinaryView().getValues(5);
		this.drawField();
	}

	protected byte[] getColour( double distance ){
		byte[] colour = new byte[3];
		colour[0] = 0;
		colour[1] = 0;
		colour[2] = 0;
		if( sa == null){
			return colour;
		}
		
		if( distance <= sa.getSensitivity() ){
			colour[0] = (byte) 255;
			return colour;
		}
		if( distance > sa.getRange() )
			return colour;
		return getLinearColour( colour, (int) distance, sa.getRange(), sa.getSensitivity() );
	}
	
	protected byte[] getIntColour( Map<Colour, Byte> colour ){
		byte[] values = new byte[3];
		values[0] = colour.get(Colour.RED);
		values[1] = colour.get(Colour.GREEN);
		values[2] = colour.get(Colour.BLUE);
		return values;
	}

	public static byte[]  getLinearColour( byte[] colour, int distance, int range, int sensitivity ){
		boolean far = ( distance > ( range - sensitivity ));
		byte red = (far? (byte) 50: (byte)( 255 * ( 1 - distance/range )));
		colour[0] = red;
		byte green = (far? (byte) 255: (byte)( 255 * distance/range ));
		colour[1] = green;
		byte blue = 50;
		colour[2] = blue;
		return colour;
	}

	@SuppressWarnings("unused")
	private class RadarData{
		private int a;
		private byte r;
		private byte g;
		private byte b;
		protected RadarData(int angle, byte[] rgb) {
			super();
			this.a = angle;
			this.r = rgb[0];
			this.g = rgb[1];
			this.b = rgb[2];
		}
	}
}
