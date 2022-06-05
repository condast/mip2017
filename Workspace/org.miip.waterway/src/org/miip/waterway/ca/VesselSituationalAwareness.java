package org.miip.waterway.ca;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.condast.commons.autonomy.ca.ICollisionAvoidance;
import org.condast.commons.autonomy.env.EnvironmentEvent;
import org.condast.commons.autonomy.env.IEnvironmentListener;
import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.model.IReferenceEnvironment;
import org.condast.commons.autonomy.sa.AbstractSituationalAwareness;
import org.condast.commons.autonomy.sa.SituationEvent;
import org.condast.commons.autonomy.sa.radar.IDataEntry;
import org.condast.commons.autonomy.sa.radar.VesselRadarData;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;
import org.condast.commons.data.plane.IField;
import org.miip.waterway.model.IVessel;
import org.miip.waterway.model.Waterway;
import org.miip.waterway.model.def.IMIIPEnvironment;

public class VesselSituationalAwareness extends AbstractSituationalAwareness<VesselRadarData> {

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

	private IVessel vessel;
	private IField field;
	private IMIIPEnvironment env;

	private IEnvironmentListener<IVessel> listener = new IEnvironmentListener<IVessel>() {

		@Override
		public void notifyEnvironmentChanged(EnvironmentEvent<IVessel> event) {
			notifylisteners( new SituationEvent<VesselRadarData>( this));
		}
	};

	public VesselSituationalAwareness( IVessel vessel, IField field ) {
		super( ICollisionAvoidance.DefaultSituationalAwareness.VESSEL_RADAR.toString(), IDataEntry.DefaultDimensions.VESSEL_RADAR_DATA.getIndex(), field );
		this.vessel = vessel;
		this.field = field;
	}

	@SuppressWarnings("unchecked")
	@Override
	public IPhysical[] getScan() {
		Collection<IPhysical> results = new ArrayList<>();
		if( env == null )
			return results.toArray( new IPhysical[ results.size()]);
		Waterway waterway = env.getWaterway();
		for( IVessel phobj: waterway.getShips() ) {
			double distance = LatLngUtils.distance(vessel.getLocation(), phobj.getLocation());
			//results.add(new VesselRadarData(phobj, phobj.getLocation(), 0, phobj.getHeading(), phobj.getSpeed(), distance));
		}
		//results.addAll(getBanks(waterway));
		return results.toArray( new IPhysical[ results.size()]);
	}


	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	protected void onSetInput(IReferenceEnvironment<IVessel, IPhysical> input) {
		if( env!= null ) {
			if( env.equals(input ))
				return;
			env.removeListener(listener);
		}
		input.addListener(listener);
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

	private Collection<VesselRadarData> getBanks( Waterway waterway ){
		IField field = waterway.getField();
		LatLng location = null;
		Collection<VesselRadarData> results = new ArrayList<>();
		double xstep = (double)field.getLength() /(field.getLength() + field.getWidth());
		long position = 0;
		while( position < field.getLength() ) {
			location = field.transform( position, 0);
			results.add( new VesselRadarData( null, location,  0, 0 ));
			location = field.transform( position, field.getWidth());
			results.add( new VesselRadarData( null, location,  0, 0 ));
			position += 3* xstep;
		}
		return results;
	}
	
	public static Set<VesselRadarData> getSortedRadarData( Collection<VesselRadarData> data ){
		return new TreeSet<VesselRadarData>( data);
	}
}
