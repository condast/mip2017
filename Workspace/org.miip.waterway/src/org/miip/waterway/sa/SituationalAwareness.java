package org.miip.waterway.sa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import org.condast.commons.autonomy.env.EnvironmentEvent;
import org.condast.commons.autonomy.env.IEnvironmentListener;
import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.model.IReferenceEnvironment;
import org.condast.commons.autonomy.sa.AbstractSituationalAwareness;
import org.condast.commons.autonomy.sa.SituationEvent;
import org.condast.commons.autonomy.sa.radar.RadarData;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;
import org.condast.commons.data.plane.IField;
import org.miip.waterway.model.IVessel;
import org.miip.waterway.model.Waterway;
import org.miip.waterway.model.def.IMIIPEnvironment;

public class SituationalAwareness extends AbstractSituationalAwareness<IPhysical, IVessel> {

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
	
	private IField field;

	private IEnvironmentListener<IVessel> listener = new IEnvironmentListener<IVessel>() {

		@Override
		public void notifyEnvironmentChanged(EnvironmentEvent<IVessel> event) {
			notifylisteners( new SituationEvent<IPhysical>( this, getReference(), null));
		}
	};
	
	public SituationalAwareness( IVessel vessel, IField field ) {
		super( vessel, (int)( field.getDiameter()/2 ));
		this.field = field;
	}

	@Override
	public IField getView() {
		return field;
	}
	
	@Override
	public Collection<RadarData<IPhysical>> getScan() {
		Collection<RadarData<IPhysical>> results = new ArrayList<>();
		if( super.getInput() == null )
			return results;
		IMIIPEnvironment env = (IMIIPEnvironment) super.getInput();
		Waterway waterway = env.getWaterway();
		for( IVessel phobj: waterway.getShips() ) {
			double distance = LatLngUtils.distance(super.getReference().getLocation(), phobj.getLocation());
			results.add(new RadarData<IPhysical>(phobj, phobj.getLocation(), 0, phobj.getHeading(), phobj.getSpeed(), distance));
		}
		results.addAll(getBanks(waterway));
		return results;
	}

	
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public RadarData<IPhysical> getRadarData(IPhysical other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(long time) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onSetInput(IReferenceEnvironment<IVessel, IPhysical> input) {
		IMIIPEnvironment env = (IMIIPEnvironment) super.getInput();
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
	
	private Collection<RadarData<IPhysical>> getBanks( Waterway waterway ){
		IField field = waterway.getField();
		LatLng location = null;
		Collection<RadarData<IPhysical>> results = new ArrayList<>();
		double xstep = (double)field.getLength() /(field.getLength() + field.getWidth());
		long position = 0;
		while( position < field.getLength() ) {
			location = field.transform( position, 0);
			results.add( new RadarData<IPhysical>( null, location,  0, 0 ));
			location = field.transform( position, field.getWidth());
			results.add( new RadarData<IPhysical>( null, location,  0, 0 ));
			position += 3* xstep;
		}
		return results;	
	}
}
