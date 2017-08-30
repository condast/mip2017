package org.miip.waterway.ui.swt;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.condast.commons.data.binary.IBinaryTreeSet;
import org.condast.commons.data.binary.SequentialBinaryTreeSet;
import org.condast.commons.data.operations.AbstractOperator;
import org.condast.commons.data.operations.IOperator;
import org.condast.commons.latlng.Vector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;

public class AveragingRadar extends AbstractRadar{
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

	private Logger logger = Logger.getLogger( this.getClass().getName() );

	public AveragingRadar(Composite parent, int style) {
		super(parent, style);
	}
	
	@Override
	protected void onDrawStart(GC gc) {
		data = new SequentialBinaryTreeSet<Vector<Integer>>( average);
		Map<Integer, Double> radar = super.getSituationalAwareness().getRadar();
		Iterator<Map.Entry<Integer, Double>> iterator = radar.entrySet().iterator();
		while( iterator.hasNext() ){
			Map.Entry<Integer, Double> entry = iterator.next();
			data.add( new Vector<Integer>( entry.getKey(), entry.getValue()));
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
	protected void drawDegree( GC gc, int angle, double adist ){
		
		List<Vector<Integer>> results = this.data.getValues(0);
		Vector<Integer> vect = null;
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
		
		double xpos1 = centrex + length * Math.sin( toRadians( angle ));
		double ypos1 = centrey + length * Math.cos( toRadians( angle ));
		double xpos2 = centrex + length * Math.sin( toRadians( angle+1 ));
		double ypos2 = centrey + length * Math.cos( toRadians( angle+1 ));
		Color background = gc.getBackground();
		gc.setBackground( getColour( vect.getValue() ));
		gc.fillPolygon(new int[]{(int) centrex, (int)centrey, (int)xpos1, (int)ypos1, (int)xpos2, (int)ypos2});
		gc.setBackground(background);
	}
}
