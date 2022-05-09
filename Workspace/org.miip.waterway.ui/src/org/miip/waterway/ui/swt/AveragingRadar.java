package org.miip.waterway.ui.swt;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.sa.ISituationalAwareness;
import org.condast.commons.autonomy.sa.radar.RadarData;
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

public class AveragingRadar<I extends Object>  extends AbstractSWTRadar<IPhysical>{
	private static final long serialVersionUID = 1L;

	private IBinaryTreeSet<LatLngVector<Integer>> data;

	IOperator<LatLngVector<Integer>, LatLngVector<Integer>> average = new AbstractOperator<LatLngVector<Integer>, LatLngVector<Integer>>(){


		@Override
		public LatLngVector<Integer> calculate(LatLngVector<Integer>[] parents) {
			double avgdist = 0d;
			int degree = 0;
			for (LatLngVector<Integer> value : parents) {
				degree += value.getKey();
				avgdist+=value.getValue();
			}
			degree = (int)((float)degree/parents.length);
			return new LatLngVector<>( degree, Double.valueOf(avgdist/parents.length));
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
		ISituationalAwareness<IPhysical, IVessel> sa = null;//super.getInput();
		IVessel reference = sa.getReference();

		data = new SequentialBinaryTreeSet<>( average);
		Collection<RadarData> radar = sa.getScan();
		IField field = sa.getView();
		for( RadarData vessel: radar ){
			if( vessel.getPhysical().equals( reference ))
				continue;
			Map.Entry<Double, Double> vector = field.getDifference(reference.getLocation(), vessel.getLocation());
			double distance = vector.getValue();
			double angle = vector.getKey();
			data.add( new LatLngVector<>((int)angle, (int)distance ));
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
	protected void drawObject( GC gc, RadarData ship ){

		List<LatLngVector<Integer>> results = this.data.getValues(0);
		LatLngVector<Integer> vect = null;
		IVessel reference = null;//getInput().getReference();
		//double distance = LatLngUtils.getDistance(reference.getLocation(), ship.getLocation());
		double angle = LatLngUtils.getHeading(reference.getLocation(), ship.getLocation());
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
