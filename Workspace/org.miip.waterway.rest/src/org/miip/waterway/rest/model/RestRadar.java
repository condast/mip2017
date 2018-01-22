package org.miip.waterway.rest.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.condast.commons.Utils;
import org.condast.commons.data.binary.SequentialBinaryTreeSet;
import org.condast.commons.data.latlng.Vector;
import org.miip.waterway.model.def.IPhysical;
import org.miip.waterway.radar.Radar;
import org.miip.waterway.sa.ISituationalAwareness;

public class RestRadar extends Radar<IPhysical>{

	public enum Colour{
		RED,
		GREEN,
		BLUE;
	}
	
	private List<Vector<Double>> vectors;
	
	private Collection<RadarData> colours;
	
	public RestRadar() {
		colours = new ArrayList<RadarData>();
	}

	protected void drawField(){
		ISituationalAwareness<IPhysical, ?> sa = super.getInput();
		if( sa == null )
			return;
		colours.clear();
		for( int i=0; i< vectors.size(); i++ ){
			Vector<Double> vector = vectors.get(i);
			double angle = ( vector != null )? vector.getKey(): 0;
			double value = ( vector != null )? vector.getValue(): 0;
			drawDegree(angle, value);
		}
	}

	protected void drawDegree( double angle, double distance ){
		if(( Utils.assertNull( vectors )) || ( distance > getRange() )) 
			return;
		colours.add( new RadarData( (int) angle, getColour( angle )));
	}
	
	public Collection<RadarData> getColours() {
		return colours;
	}

	@Override
	public SequentialBinaryTreeSet<Vector<Double>> getBinaryView() {
		return super.getBinaryView();
	}

	@Override
	public void setInput( ISituationalAwareness<IPhysical, ?> sa ){
		super.setInput(sa);
		vectors = getBinaryView().getValues(5);
		this.drawField();
	}

	protected byte[] getColour( double distance ){
		byte[] colour = new byte[3];
		colour[0] = 0;
		colour[1] = 0;
		colour[2] = 0;
		if( getInput() == null){
			return colour;
		}
		
		if( distance <= getSensitivity() ){
			colour[0] = (byte) 255;
			return colour;
		}
		if( distance > getRange() )
			return colour;
		return getLinearColour( colour, (int) distance, getRange(), getSensitivity() );
	}
	
	protected byte[] getIntColour( Map<Colour, Byte> colour ){
		byte[] values = new byte[3];
		values[0] = colour.get(Colour.RED);
		values[1] = colour.get(Colour.GREEN);
		values[2] = colour.get(Colour.BLUE);
		return values;
	}

	public static byte[]  getLinearColour( byte[] colour, int distance, long range, int sensitivity ){
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
