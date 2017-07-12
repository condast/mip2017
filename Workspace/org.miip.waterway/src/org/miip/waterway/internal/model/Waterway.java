package org.miip.waterway.internal.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.condast.commons.lnglat.LatLng;
import org.condast.commons.lnglat.LngLatUtils;
import org.condast.commons.strings.StringStyler;
import org.miip.waterway.internal.model.AbstractModel;
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
		createShips( super.getLnglat(), this.nrOfShips );
		createShips( LngLatUtils.extrapolateEast( super.getLnglat(), length-350 ), this.nrOfShips );
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
	
	public void update( LatLng lngLat, Date time, double distance ){
		super.setLnglat(lngLat);
		for( Ship ship: getShips() ){
			LatLng ll = ship.sail( time );
			double dist = LngLatUtils.distance(super.getLnglat(), ll); 
			if(( dist < -MARGIN_X ) || ( dist > ( this.length + MARGIN_X )))
				ships.remove( ship );
		}
		createShips( super.getLnglat(), this.nrOfShips );
		createShips( LngLatUtils.extrapolateEast( super.getLnglat(), length ), this.nrOfShips );
	}

	protected void createShips( LatLng place, int amount ){
		for( int i=0; i< amount; i++){
			double position = 1.8* ( Math.random() - 0.5f ) * width;
			LatLng lnglat = LngLatUtils.extrapolateNorth( place, position );
			Ship ship = Ship.createShip( lnglat, "newShip" );
			ships.add( ship );
		}
	}
}
