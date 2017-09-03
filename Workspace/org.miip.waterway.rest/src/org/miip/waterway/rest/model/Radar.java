package org.miip.waterway.rest.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.condast.commons.Utils;
import org.condast.commons.latlng.Vector;
import org.miip.waterway.sa.SituationalAwareness;

public class Radar{

	public enum Colour{
		RED,
		GREEN,
		BLUE;
	}
	
	private SituationalAwareness sa;
	private List<Vector<Integer>> vectors;
	
	private Map<Integer,Integer[]> colours;
	
	public Radar() {
		colours = new HashMap<Integer,Integer[]>();
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
		colours.put(angle, getIntColour( getColour( angle )));
	}
	
	public Map<Integer, Integer[]> getColours() {
		return colours;
	}

	public void setInput( SituationalAwareness sa ){
		this.sa = sa;
		vectors = sa.getBinaryView().getValues(5);
		this.drawField();
	}

	protected Map<Colour, Integer> getColour( double distance ){
		Map<Colour, Integer> colour = new HashMap<Colour, Integer>();
		colour.put( Colour.RED, 0);
		colour.put( Colour.GREEN, 0);
		colour.put( Colour.BLUE, 0);
		if( sa == null){
			return colour;
		}
		
		if( distance <= sa.getSensitivity() ){
			colour.replace( Colour.RED, 255);
			return colour;
		}
		if( distance > sa.getRange() )
			return colour;
		return getLinearColour( colour, (int) distance, sa.getRange(), sa.getSensitivity() );
	}
	
	protected Integer[] getIntColour( Map<Colour, Integer> colour ){
		Integer[] values = new Integer[3];
		values[0] = colour.get(Colour.RED);
		values[1] = colour.get(Colour.GREEN);
		values[2] = colour.get(Colour.BLUE);
		return values;
	}

	public static Map<Colour, Integer>  getLinearColour( Map<Colour, Integer> colour, int distance, int range, int sensitivity ){
		boolean far = ( distance > ( range - sensitivity ));
		int red = far? 50: (int)( 255 * ( 1 - distance/range ));
		colour.put(Colour.RED, red );
		int green = far? 255: (int)( 255 * distance/range );
		colour.put(Colour.GREEN, green );
		int blue = 50;
		colour.put(Colour.BLUE, blue);
		return colour;
	}

}
