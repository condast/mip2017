package org.miip.waterway.model.eco;

import java.util.ArrayList;
import java.util.Collection;

import org.condast.commons.latlng.LatLng;
import org.condast.commons.latlng.LatLngUtils;
import org.condast.commons.strings.StringStyler;
//import org.eclipse.swt.graphics.Rectangle;
import org.miip.waterway.internal.model.AbstractModel;
import org.miip.waterway.model.Location;
import org.miip.waterway.model.def.IModel;

public class Bank extends AbstractModel{

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

	public Bank( Banks bank, LatLng lnglat, Rectangle rectangle) {
		this( bank, lnglat, rectangle, DEFAULT_NR_OF_TREES );
	}
	
	public Bank( Banks bank, LatLng lnglat, Rectangle rectangle, int nrOfShoreObjects) {
		super( bank.toString(), IModel.ModelTypes.BANK, lnglat);
		this.rectangle = rectangle;
		this.nrOfShoreObjects = nrOfShoreObjects;
		shore = new ArrayList<Location>();
		this.initialise();
	}
	
	protected void initialise(){
		double offset = rectangle.width/nrOfShoreObjects;
		double halfWidth = rectangle.height/2;
		for( int i=0; i< this.nrOfShoreObjects; i++){
			double x = offset * i * ( 1 + Math.random() );
			double y = rectangle.y + ( 0.5f + Math.random()) * halfWidth;
			shore.add( new Location((int)x, (int) y));
		}
	}

	public Location[] getShoreObjects(){
		return shore.toArray( new Location[ shore.size() ]);
	}
	
	public void update( double distance ){
		double offset = rectangle.width/this.nrOfShoreObjects;
		for( Location location: getShoreObjects() ){
			shore.remove( location );
			double x = (double)location.getX() - distance;
			double y = location.getY();
			if( x < 0 ){
				x = rectangle.x + rectangle.width + ( Math.random() - 1 )*offset; 
				y = rectangle.y + ( 0.5f + Math.random()) * rectangle.height/2;
			}
			shore.add( new Location( x, y ));
		}
		super.setLnglat( LatLngUtils.extrapolateEast( super.getLatLng(), distance));	
	}
}
