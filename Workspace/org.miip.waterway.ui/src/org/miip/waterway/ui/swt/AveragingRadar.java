package org.miip.waterway.ui.swt;

import java.util.List;

import org.condast.commons.data.binary.IBinaryTreeSet;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;

public class AveragingRadar extends AbstractRadar{
	private static final long serialVersionUID = 1L;

	private int depth;
	
	private int expand;
	
	public AveragingRadar(Composite parent, int style) {
		super(parent, style);
		this.expand = 1;
	}
	
	public int getExpand() {
		return expand;
	}

	public void setExpand(int expand) {
		this.expand = expand;
	}

	@Override
	protected void drawField(GC gc) {
		IBinaryTreeSet<Double> tree = getSituationalAwareness().getTreeSet();
		for( int i=0; i<tree.getDepth(); i++ ){
			depth = i;
			List<Double> values = tree.getValues(i);
			for( int j=0; j<values.size(); j++ ){
				double value = (values.get(j) == null)?0: values.get(j); 
				drawDegree(gc, j, value );
			}
		}
	}

	/**
	 * distance is average distance in this case
	 * @param gc
	 * @param length
	 * @param depth
	 * @param angle
	 * @param adist
	 */
	protected void drawDegree( GC gc, int degrees, double adist ){
		double length = depth*expand;
		double centrex = getCentre().x - length/2;
		double centrey = getCentre().y - length/2;
		
		double phi = (depth < 2)? 2 * Math.PI: Math.toRadians( 360/depth);
		double angle = degrees * phi;
		gc.setForeground( getColour( adist ));
		int[] poly = new int[6];
		poly[0] = (int)centrex;
		poly[1] = (int)centrey;
		poly[2] = getX(degrees, angle);
		poly[3] = getY( degrees, angle );
		poly[4] = getX( degrees, angle + phi);
		poly[5] =  getY( degrees, angle + phi);
		gc.drawPolyline(poly);
	}
	
	protected int getX( int degrees, double angle ){
		double length = depth*expand;
		double centrex = getCentre().x - length/2;
		double step = (depth == 0 )? degrees: (double)degrees/depth;
		int xsign = (( step > 0.25) && ( step < 0.75 ))?-1: 1;
		int xpos = (int)( centrex + xsign*( length * Math.sin( angle * degrees )));
		return xpos;
	}

	protected int getY( int degrees, double angle ){
		double length = depth*expand;
		double centrey = getCentre().y - length/2;
		double step = (depth == 0 )? degrees:(double)degrees/depth;
		int ysign = (( step > 0) && ( step < 0.5 ))?-1: 1;
		return (int)( centrey + ysign*( length * Math.cos( angle )));
	}

}
