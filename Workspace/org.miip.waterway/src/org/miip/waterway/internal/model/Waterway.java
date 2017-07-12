package org.miip.waterway.internal.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.condast.commons.lnglat.LngLat;
import org.condast.commons.lnglat.LngLatUtils;
import org.condast.commons.strings.StringStyler;
import org.miip.waterway.internal.model.AbstractModel;
import org.miip.waterway.model.def.IModel;

public class Waterway extends AbstractModel{

	private static final int DEFAULT_NR_OF_SHIPS = 1;
	
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

	public Waterway( LngLat lnglat, int length, int width) {
		this( lnglat, length, width, DEFAULT_NR_OF_SHIPS );
	}
	
	public Waterway( LngLat lnglat, int length, int width, int nrOfShips) {
		super( IModel.ModelTypes.WATERWAY, lnglat);
		this.length = length;
		this.width = width;
		this.nrOfShips = nrOfShips;
		ships = new ArrayList<Ship>();
		this.initialise();
	}
	
	protected void initialise(){
		createShips( super.getLnglat(), this.nrOfShips );
		//createShips( LngLatUtils.extrapolateEast( super.getLnglat(), length ), this.nrOfShips );
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
	
	public void update( LngLat lngLat, Date time, float distance ){
		super.setLnglat(lngLat);
		for( Ship ship: getShips() ){
			ship.sail( time );
		}
		createShips( super.getLnglat(), this.nrOfShips );
		//createShips( LngLatUtils.extrapolateEast( super.getLnglat(), length ), this.nrOfShips );
	}

	protected void createShips( LngLat place, int amount ){
		for( int i=0; i< amount; i++){
			double position = 1.8* ( Math.random() - 0.5f ) * width;
			LngLat lnglat = LngLatUtils.extrapolateNorth( place, position );
			Ship ship = Ship.createShip( lnglat, "newShip" );
			ships.add( ship );
		}
	}

}
