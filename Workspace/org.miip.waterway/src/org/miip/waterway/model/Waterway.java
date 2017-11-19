package org.miip.waterway.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Logger;

import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;
import org.condast.commons.strings.StringStyler;
import org.miip.waterway.internal.model.AbstractModel;
import org.miip.waterway.model.Ship.Bearing;
import org.miip.waterway.model.def.IModel;

public class Waterway extends AbstractModel{

	private static final int DEFAULT_NR_OF_SHIPS = 1;
	private static final int MARGIN_X = 20;//The margin with which ships can disappear behind the visible waterway
	
	public enum Banks{
		UPPER,
		LOWER;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
		
	}
	private Collection<Ship> ships;
	private int nrOfShips;
	
	private int length, width;

	private Logger logger = Logger.getLogger( this.getClass().getName() );
	
	private LatLng centre;

	public Waterway( LatLng lnglat, int length, int width) {
		this( lnglat, length, width, DEFAULT_NR_OF_SHIPS );
	}
	
	public Waterway( LatLng lnglat, int length, int width, int nrOfShips) {
		super( IModel.ModelTypes.WATERWAY, lnglat);
		this.length = length;
		this.width = width;
		this.nrOfShips = nrOfShips;
		ships = new ArrayList<Ship>();
		this.initialise();
	}
	
	protected void initialise(){
		centre = LatLngUtils.extrapolate( super.getLatLng(), Bearing.EAST.getAngle(), length/2);
		createShips( super.getLatLng(), (int)(this.nrOfShips/2) );
		createShips( LatLngUtils.extrapolateEast( super.getLatLng(), length-350 ), (int)( this.nrOfShips/2) );
	}

	public void clear(){
		this.ships.clear();
	}
	
	public int getLength() {
		return length;
	}

	public int getWidth() {
		return width;
	}

	public int getNrOfShips() {
		return nrOfShips;
	}

	public void setNrOfShips(int nrOfShips) {
		this.nrOfShips = nrOfShips;
	}

	public Ship[] getShips(){
		return ships.toArray( new Ship[ ships.size() ]);
	}
	
	public void update( LatLng position, Date time, double distance ){
		super.setLnglat(position);
		logger.fine( "Update Position " + position );
		for( Ship ship: getShips() ){
			LatLng ll = ship.sail( time );
			double dist = LatLngUtils.distance(super.getLatLng(), ll); 
			if(( dist > -MARGIN_X ) || ( dist < -( this.length + MARGIN_X )))
				ships.remove( ship );
			//logger.info( "New Position for spped:" + ship.getSpeed() + ",\n\t" + ship.getLnglat() );
			//logger.info( "Diff " + (position.getLongitude() - ship.getLnglat().getLongitude() ));
			//logger.info( "Diff " + LatLngUtils.distance(position, ship.getLnglat() ));
		}
		createShips( super.getLatLng(), (int)(this.nrOfShips/2) );
		LatLng east = LatLngUtils.extrapolateEast( super.getLatLng(), length );
		createShips( east, (int)(this.nrOfShips/2) );
	}

	protected void createShips( LatLng place, int amount ){
		if( this.ships.size() >= amount )
			return;
		for( int i=this.ships.size(); i< amount; i++){
			double position = ( Math.random() - 0.5f ) * width;
			LatLng lnglat = LatLngUtils.extrapolateNorth( place, position );
			Ship ship = Ship.createShip( lnglat, "newShip" );
			logger.fine("Adding ship: " + ship.getLatLng() + " bearing " + ship.getBearing());
			logger.fine( "Distance to centre: " + LatLngUtils.distance(centre, ship.getLatLng()));
			ships.add( ship );
		}
	}
}
