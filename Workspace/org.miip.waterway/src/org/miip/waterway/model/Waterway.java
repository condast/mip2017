package org.miip.waterway.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Logger;

import org.condast.commons.data.latlng.Field;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;
import org.miip.waterway.internal.model.AbstractModel;
import org.miip.waterway.model.def.IModel;
import org.miip.waterway.model.eco.Rectangle;

public class Waterway extends AbstractModel{

	private static final int DEFAULT_NR_OF_SHIPS = 1;
	private static final int MARGIN_X = 20;//The margin with which ships can disappear behind the visible waterway
	
	private Collection<Ship> ships;
	private int nrOfShips;
	
	private Rectangle rectangle;
	
	//The distance travelled since the start button was pressed
	private long travelled;

	private Logger logger = Logger.getLogger( this.getClass().getName() );
	
	public Waterway( LatLng latlng, Rectangle rectangle) {
		this( latlng, rectangle, DEFAULT_NR_OF_SHIPS  );
	}
	
	public Waterway( LatLng latlng, Rectangle rectangle, int nrOfShips) {
		super( IModel.ModelTypes.WATERWAY, latlng );
		this.rectangle = rectangle;
		this.nrOfShips = nrOfShips;
		ships = new ArrayList<Ship>();
		this.initialise();
	}

	protected void initialise(){
		//createShips( 20, (int)(this.nrOfShips/2) );
		//createShips( rectangle.getLength()-200, (int)( this.nrOfShips/2) );
	}

	public void clear(){
		this.ships.clear();
		this.travelled = 0;
	}
	
	public Rectangle getRectangle() {
		return rectangle;
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

	public Ship[] getShips(){
		return ships.toArray( new Ship[ ships.size() ]);
	}

	protected void createShips( long xposition, int amount ){
		if( this.ships.size() >= nrOfShips )
			return;
		Field field = new Field( super.getLatLng(), rectangle.getLength(), rectangle.getWidth() );
		for( int i=this.ships.size(); i< amount; i++){
			long yposition = (long) ( Math.random() * rectangle.getWidth());
			LatLng lnglat = field.transform( xposition, yposition );
			Ship ship = Ship.createShip( lnglat, "newShip" );
			logger.fine("Adding ship: " + ship.getLatLng() + " bearing " + ship.getBearing());
			logger.fine( "Distance to centre: " + LatLngUtils.distance( field.getCentre(), ship.getLatLng()));
			ships.add( ship );
		}
	}

	public void update( Date time, double distance ){
		this.travelled += distance;
		Field field = new Field( super.getLatLng(), rectangle.getLength(), rectangle.getWidth() );
		for( IVessel ship: getShips() ){
			LatLng ll = ship.sail( time );
			if( !field.isInField(ll, MARGIN_X ))
				ships.remove( ship );
			//logger.info( "New Position for spped:" + ship.getSpeed() + ",\n\t" + ship.getLnglat() );
			//logger.info( "Diff " + (position.getLongitude() - ship.getLnglat().getLongitude() ));
			//logger.info( "Diff " + LatLngUtils.distance(position, ship.getLnglat() ));
		}
		createShips( 0, 20);//(int)(this.nrOfShips/2) );
		createShips( this.getRectangle().getLength() -100, 1 );//(int)(this.nrOfShips/2) );
		super.setLnglat( LatLngUtils.extrapolate( super.getLatLng(), LatLng.Compass.EAST.getAngle(), distance));
		logger.fine( "Update Position " + super.getLatLng() );
	}
}
