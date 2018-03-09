package org.miip.waterway.ui.swt;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.sa.ISituationalAwareness;
import org.condast.commons.data.binary.IBinaryTreeSet;
import org.condast.commons.data.binary.SequentialBinaryTreeSet;
import org.condast.commons.data.latlng.Field;
import org.condast.commons.data.latlng.LatLngUtils;
import org.condast.commons.data.latlng.Vector;
import org.condast.commons.data.operations.AbstractOperator;
import org.condast.commons.data.operations.IOperator;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.miip.waterway.model.IVessel;

public class AveragingRadar<I extends Object>  extends AbstractSWTRadar<IPhysical>{
	private static final long serialVersionUID = 1L;

	private IBinaryTreeSet<Vector<Integer>> data;

	IOperator<Vector<Integer>, Vector<Integer>> average = new AbstractOperator<Vector<Integer>, Vector<Integer>>(){

		@Override
		public Vector<Integer> calculate(Collection<Vector<Integer>> parents) {
			Double avgdist = 0d;
			Iterator<Vector<Integer>> iterator = parents.iterator();
			int degree = 0;
			while( iterator.hasNext() ){
				Vector<Integer> value = iterator.next();
				degree += value.getKey();
				avgdist+=value.getValue();
			}
			degree = (int)((float)degree/parents.size());
			return new Vector<Integer>( degree, new Double((double)avgdist/parents.size()));
		}
		
	};


	public AveragingRadar(Composite parent, int style) {
		super(parent, style);
	}
	
	@Override
	protected void onDrawStart(GC gc) {
		ISituationalAwareness<IPhysical,?> sa = super.getInput();
		IVessel reference = (IVessel) sa.getReference(); 
		
		data = new SequentialBinaryTreeSet<Vector<Integer>>( average);
		Collection<IPhysical> radar = sa.getRadar();
		Field field = sa.getField();
		for( IPhysical vessel: radar ){
			if( vessel.equals( reference ))
				continue;
			Map.Entry<Double, Double> vector = field.getDifference(reference.getLocation(), vessel.getLocation());
			double distance = vector.getValue();
			double angle = vector.getKey();
			data.add( new Vector<Integer>((int)angle, (int)distance ));
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
		
		List<Vector<Integer>> results = this.data.getValues(0);
		Vector<Integer> vect = null;
		IVessel reference = (IVessel) getInput().getReference(); 
		//double distance = LatLngUtils.getDistance(reference.getLocation(), ship.getLocation());
		double angle = LatLngUtils.getBearing(reference.getLocation(), ship.getLocation());
		for( Vector<Integer> vector: results ){
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
