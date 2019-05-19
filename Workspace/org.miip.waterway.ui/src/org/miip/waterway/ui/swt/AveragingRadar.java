package org.miip.waterway.ui.swt;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.sa.ISituationalAwareness;
import org.condast.commons.autonomy.ui.radar.AbstractSWTRadar;
import org.condast.commons.data.binary.IBinaryTreeSet;
import org.condast.commons.data.binary.SequentialBinaryTreeSet;
import org.condast.commons.data.latlng.LatLngUtils;
import org.condast.commons.data.latlng.LatLngVector;
import org.condast.commons.data.operations.AbstractOperator;
import org.condast.commons.data.operations.IOperator;
import org.condast.commons.data.plane.IField;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.miip.waterway.model.IVessel;

public class AveragingRadar<I extends Object>  extends AbstractSWTRadar<IVessel,IPhysical>{
	private static final long serialVersionUID = 1L;

	private IBinaryTreeSet<LatLngVector<Integer>> data;

	IOperator<LatLngVector<Integer>, LatLngVector<Integer>> average = new AbstractOperator<LatLngVector<Integer>, LatLngVector<Integer>>(){

		
		@Override
		public LatLngVector<Integer> calculate(LatLngVector<Integer>[] parents) {
			Double avgdist = 0d;
			int degree = 0;
			for( int i=0; i< parents.length; i++ ){
				LatLngVector<Integer> value = parents[i];
				degree += value.getKey();
				avgdist+=value.getValue();
			}
			degree = (int)((float)degree/parents.length);
			return new LatLngVector<Integer>( degree, new Double((double)avgdist/parents.length));
		}

		@Override
		public boolean check(LatLngVector<Integer> input) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		protected LatLngVector<Integer> onNext() {
			// TODO Auto-generated method stub
			return null;
		}
		
	};

	public AveragingRadar(Composite parent, int style) {
		super(parent, style);
	}
	
	@Override
	protected void onDrawStart(GC gc) {
		ISituationalAwareness<IVessel,IPhysical> sa = super.getInput();
		IVessel reference = sa.getReference(); 
		
		data = new SequentialBinaryTreeSet<LatLngVector<Integer>>( average);
		Collection<? extends IPhysical> radar = sa.getScan();
		IField field = sa.getField();
		for( IPhysical vessel: radar ){
			if( vessel.equals( reference ))
				continue;
			Map.Entry<Double, Double> vector = field.getDifference(reference.getLocation(), vessel.getLocation());
			double distance = vector.getValue();
			double angle = vector.getKey();
			data.add( new LatLngVector<Integer>((int)angle, (int)distance ));
		}
		super.onDrawStart(gc);
	}

	/**
	 * distance is average distance in this case
	 * @param gc
	 * @param length
	 * @param depth
	 * @param angle
	 * @param adist
	 */
	@Override
	protected void drawObject( GC gc, IPhysical ship ){
		
		List<LatLngVector<Integer>> results = this.data.getValues(0);
		LatLngVector<Integer> vect = null;
		IVessel reference = (IVessel) getInput().getReference(); 
		//double distance = LatLngUtils.getDistance(reference.getLocation(), ship.getLocation());
		double angle = LatLngUtils.getBearing(reference.getLocation(), ship.getLocation());
		for( LatLngVector<Integer> vector: results ){
			if( vector.getKey() != angle )
				continue;
			vect = vector;
			break;
		}
		if( vect == null )
			return;

		double centrex = super.getCentre().x;
		double centrey = super.getCentre().y;
		double length = (centrex < centrey )? centrex: centrey;
		length = length * ( vect.getValue() / super.getRange());
		
		double xpos1 = centrex + length * Math.sin( toRadians( (int) angle ));
		double ypos1 = centrey + length * Math.cos( toRadians( (int) angle ));
		double xpos2 = centrex + length * Math.sin( toRadians( (int) (angle+1) ));
		double ypos2 = centrey + length * Math.cos( toRadians( (int) (angle+1) ));
		Color background = gc.getBackground();
		gc.setBackground( getColour( vect.getValue() ));
		gc.fillPolygon(new int[]{(int) centrex, (int)centrey, (int)xpos1, (int)ypos1, (int)xpos2, (int)ypos2});
		gc.setBackground(background);
	}
}
