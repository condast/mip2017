package org.miip.waterway.model.eco;

import java.util.ArrayList;
import java.util.Collection;

import org.condast.commons.strings.StringStyler;
import org.miip.waterway.model.Location;

public class Bank{

	private static final int DEFAULT_NR_OF_TREES = 20;
	
	public enum Banks{
		UPPER,
		LOWER;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
		
	}
	
	private Collection<Location> shore;
	private int nrOfShoreObjects;
	
	private Rectangle rectangle;

	public Bank( Rectangle rectangle ) {
		this( rectangle, DEFAULT_NR_OF_TREES );
	}

	public Bank( long length, long width) {
		this( length, width, DEFAULT_NR_OF_TREES );
	}
	
	public Bank( long length, long width, int nrOfShoreObjects) {
		this( new Rectangle( 0,0, length, width ), nrOfShoreObjects );
	}
	
	public Bank( Rectangle rectangle, int nrOfShoreObjects) {
		this.rectangle = rectangle;
		this.nrOfShoreObjects = nrOfShoreObjects;
		shore = new ArrayList<Location>();
		this.initialise();
	}
	
	protected void initialise(){
		double offset = rectangle.getLength()/nrOfShoreObjects;
		double halfWidth = rectangle.getWidth()/2;
		for( int i=0; i< this.nrOfShoreObjects; i++){
			double x = rectangle.getXPos() + offset * i * Math.random();
			double y = rectangle.getYPos() + ( 0.5f + Math.random()) * halfWidth;
			shore.add( new Location((int)x, (int) y));
		}
	}

	public Location[] getShoreObjects(){
		return shore.toArray( new Location[ shore.size() ]);
	}
	
	public void update( double distance ){
		double offset = rectangle.getLength()/this.nrOfShoreObjects;
		for( Location location: getShoreObjects() ){
			shore.remove( location );
			double x = (double)location.getX() - distance;
			double y = location.getY();
			if( x < 0 ){
				x = rectangle.getXPos() + rectangle.getLength() + ( Math.random() - 1 ) *offset; 
				y = rectangle.getYPos() + ( 0.5f + Math.random()) * rectangle.getWidth()/2;
			}
			shore.add( new Location( x, y ));
		}
	}
}
