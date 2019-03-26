package org.miip.waterway.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import org.condast.commons.autonomy.model.AbstractModel;
import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.data.latlng.Field;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;
import org.condast.commons.data.latlng.LatLngUtilsDegrees;

public class Waterway extends AbstractModel{

	private static final int DEFAULT_NR_OF_SHIPS = 1;
	private static final int MARGIN_X = 20;//The margin with which ships can disappear behind the visible waterway
	
	private Collection<IVessel> ships;
	private int nrOfShips;
	
	private Field field;
	
	//The distance travelled since the start button was pressed
	private long travelled;
	
	private Logger logger = Logger.getLogger( this.getClass().getName() );
	
	public Waterway( LatLng latlng, Field field) {
		this( latlng, field, DEFAULT_NR_OF_SHIPS  );
	}
	
	public Waterway( LatLng latlng, Field field, int nrOfShips) {
		super( IPhysical.ModelTypes.WATERWAY, latlng );
		this.field = field;
		this.nrOfShips = nrOfShips;
		ships = new ArrayList<IVessel>();
		this.initialise();
	}

	protected void initialise(){
		//createShips( 20, (int)(this.nrOfShips/2) );
		//createShips( field.getLength()-200, (int)( this.nrOfShips/2) );
	}

	public void clear(){
		this.ships.clear();
		this.travelled = 0;
	}
	
	public Field getField() {
		return field;
	}

	public long getTravelled() {
		return travelled;
	}

	public int getNrOfShips() {
		return nrOfShips;
	}

	public void setNrOfShips(int nrOfShips) {
		this.nrOfShips = nrOfShips;
	}

	public IVessel[] getShips(){
		return ships.toArray( new Ship[ ships.size() ]);
	}

	protected void createShips( long xposition, int amount ){
		if( this.ships.size() >= nrOfShips )
			return;
		Field newfield = new Field( super.getLocation(), field.getLength(), field.getWidth() );
		for( int i=this.ships.size(); i< amount; i++){
			long yposition = (long) ( Math.random() * newfield.getWidth());
			LatLng lnglat = newfield.transform( xposition, yposition );
			Ship ship = Ship.createShip( lnglat, "newShip" );
			logger.fine("Adding ship: " + ship.getLocation() + " bearing " + ship.getBearing());
			logger.fine( "Distance to centre: " + LatLngUtils.distance( newfield.getCentre(), ship.getLocation()));
			ships.add( ship );
		}
	}

	public void update( long interval, double distance ){
		this.travelled += distance;
		Field newField = new Field( super.getLocation(), field.getLength(), field.getWidth() );
		for( IVessel vessel: getShips() ){
			Ship ship = (Ship) vessel;
			LatLng ll = ship.move( interval );
			if( !newField.isInField(ll, MARGIN_X ))
				ships.remove( ship );
			//logger.info( "New Position for spped:" + ship.getSpeed() + ",\n\t" + ship.getLnglat() );
			//logger.info( "Diff " + (position.getLongitude() - ship.getLnglat().getLongitude() ));
			//logger.info( "Diff " + LatLngUtils.distance(position, ship.getLnglat() ));
		}
		//createShips( 0, 20);//(int)(this.nrOfShips/2) );
		//createShips( this.getField().getLength() -100, 1 );//(int)(this.nrOfShips/2) );
		super.setLnglat( LatLngUtilsDegrees.extrapolate( super.getLocation(), LatLng.Compass.EAST.getAngle(), distance));
		logger.fine( "Update Position " + super.getLocation() );
	}
}
