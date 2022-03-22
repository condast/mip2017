package org.miip.waterway.model.eco;

import java.util.ArrayList;
import java.util.Collection;

import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.plane.Field;
import org.condast.commons.data.plane.IField;
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

	private IField field;
	private int xoffset, yoffset;

	public Bank( IField field ) {
		this( field, 0, 0, DEFAULT_NR_OF_TREES );
	}

	public Bank( IField field, int xoffset, int yoffset ) {
		this( field, xoffset, yoffset, DEFAULT_NR_OF_TREES );
	}

	public Bank( LatLng location, int xoffset, int yoffset, long length, long width) {
		this( location, xoffset, yoffset, length, width, DEFAULT_NR_OF_TREES );
	}

	public Bank( LatLng location, int xoffset, int yoffset, long length, long width, int nrOfShoreObjects) {
		this( new Field( location, length, width ), xoffset, yoffset, nrOfShoreObjects );
	}

	public Bank( IField field, int xoffset, int yoffset, int nrOfShoreObjects) {
		this.field = field;
		this.xoffset = xoffset;
		this.yoffset = yoffset;
		this.nrOfShoreObjects = nrOfShoreObjects;
		shore = new ArrayList<>();
		this.initialise();
	}

	protected void initialise(){
		double offset = field.getLength()/nrOfShoreObjects;
		double halfWidth = field.getWidth()/2;
		for( int i=0; i< this.nrOfShoreObjects; i++){
			double x =  offset * i * Math.random();
			double y = ( 0.5f + Math.random()) * halfWidth;
			shore.add( new Location((int)x, (int) y));
		}
	}

	public Location[] getShoreObjects(){
		return shore.toArray( new Location[ shore.size() ]);
	}

	public void update( double distance ){
		double offset = field.getLength()/this.nrOfShoreObjects;
		for( Location location: getShoreObjects() ){
			shore.remove( location );
			double x = location.getX() - distance;
			double y = location.getY();
			if( x < 0 ){
				x = xoffset + field.getLength() + ( Math.random() - 1 ) *offset;
				y = yoffset + ( 0.5f + Math.random()) * field.getWidth()/2;
			}
			shore.add( new Location( x, y ));
		}
	}
}
