package org.miip.waterway.sa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import org.condast.commons.autonomy.env.EnvironmentEvent;
import org.condast.commons.autonomy.env.IEnvironmentListener;
import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.sa.AbstractSituationalAwareness;
import org.condast.commons.autonomy.sa.SituationEvent;
import org.condast.commons.data.latlng.Field;
import org.condast.commons.data.latlng.LatLng;
import org.miip.waterway.model.IVessel;
import org.miip.waterway.model.Point;
import org.miip.waterway.model.Waterway;
import org.miip.waterway.model.def.IMIIPEnvironment;

public class SituationalAwareness extends AbstractSituationalAwareness<IMIIPEnvironment> {

	/*
	private AbstractOperator<Vector<Integer>, Vector<Integer>> operator = new AbstractOperator<Vector<Integer>, Vector<Integer>>(){

		@Override
		public Vector<Integer> calculate(Collection<Vector<Integer>> input) {
			float angle = 0;
			double distance = Integer.MAX_VALUE;
			for( Vector<Integer> vector: input ){
				angle += vector.getKey();
				if( vector.getValue() < distance )
					distance = vector.getValue();
			}
			return new Vector<Integer>( (int) (angle/input.size()), distance);
		}
	};
*/
	
	private Logger logger = Logger.getLogger( this.getClass().getName() );

	private IEnvironmentListener<IPhysical> listener = new IEnvironmentListener<IPhysical>() {

		@Override
		public void notifyEnvironmentChanged(EnvironmentEvent<IPhysical> event) {
			notifylisteners( new SituationEvent<IPhysical>( this, getReference()));
		}
	};
	
	public SituationalAwareness( IVessel vessel ) {
		super( vessel );
	}

	/**
	 * Get the critical distance for passage 
	 */
	@Override
	public double getCriticalDistance() {
		IVessel vessel = (IVessel) getReference(); 
		return vessel.getMinTurnDistance();
	}

	@Override
	public Collection<IPhysical> getRadar() {
		Collection<IPhysical> results = new ArrayList<IPhysical>();
		Waterway waterway = super.getInput().getWaterway();
		for( IPhysical phobj: waterway.getShips() )
			results.add(phobj);
		results.addAll(getBanks(waterway));
		return results;
	}

	@Override
	protected void onSetInput(IMIIPEnvironment environment) {
		if( super.getInput() != null ) {
			if( super.getInput().equals(environment ))
				return;
			super.getInput().removeListener(listener);
		}
		environment.addListener(listener);
	}

	public void controlShip( float min_distance, boolean max ){
		logger.info("IMPLEMENT");
		/*
		SequentialBinaryTreeSet<Vector<Double>> data = super.getBinaryView();
		if( data == null )
			return;
		List<Vector<Double>> vectors = data.getValues(5);
		if( vectors.isEmpty() )
			return;
		Collections.sort( vectors, new VectorComparator());
		for( Vector<Double> entry: vectors ){
			logger.fine("Angle: " + entry.getKey() + ", distance: " + entry.getValue() );
		}
		int angle = (int)vectors.get(0).getKey().doubleValue();
		double distance = vectors.get(0).getValue();
		if(max && distance > min_distance )
			return;
		CentreShip.Controls control = null;
		if(( angle < 325) || ( angle > 448 ))
			control = Controls.UP;
		else if( angle < 192)
			control = Controls.RIGHT;
		else if( angle < 320)
			control = Controls.DOWN;
		else if( angle < 448)
			control = Controls.LEFT;
		ship.setControl(control);
		
		logger.fine("Angle: " + angle + ", Distance " + distance + " Control: " + control.name() );
		*/
	}
	
	private Collection<Point> getBanks( Waterway waterway ){
		Field field = waterway.getField();
		LatLng location = null;
		Collection<Point> results = new ArrayList<Point>();
		double xstep = (double)field.getLength() /(field.getLength() + field.getWidth());
		long position = 0;
		while( position < field.getLength() ) {
			location = field.transform( position, 0);
			results.add( new Point( location ));
			location = field.transform( position, field.getWidth());
			results.add( new Point( location ));
			position += 3* xstep;
		}
		return results;	
	}
}
