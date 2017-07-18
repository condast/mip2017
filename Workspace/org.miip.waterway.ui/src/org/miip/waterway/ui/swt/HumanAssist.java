package org.miip.waterway.ui.swt;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;

public class HumanAssist extends AbstractRadar {
	private static final long serialVersionUID = 1L;

	public static final int BAR_WIDTH = 20;
	
	private int bar;
	
	public HumanAssist(Composite parent, int style ) {
		super(parent, style);
		this.bar = BAR_WIDTH;
	}
	
	protected void drawDegree( GC gc, int angle, double distance ){
		double centrex = super.getCentre().x;
		double centrey = super.getCentre().y;
		double length = (centrex < centrey )? centrex: centrey;
		
		double xpos1 = centrex + length * Math.sin( toRadians( angle ));
		double ypos1 = centrey + length * Math.cos( toRadians( angle ));
		double xpos2 = centrex + length * Math.sin( toRadians( angle+1 ));
		double ypos2 = centrey + length * Math.cos( toRadians( angle+1 ));
		Color background = gc.getBackground();
		gc.setBackground( getColour( distance ));
		gc.fillPolygon(new int[]{(int) centrex, (int)centrey, (int)xpos1, (int)ypos1, (int)xpos2, (int)ypos2});
		gc.setBackground(background);
	}

	@Override
	protected void onDrawEnd(GC gc) {
		double centrex = super.getCentre().x;
		double centrey = super.getCentre().y;
		double length = (centrex < centrey )? centrex: centrey;
		double clip = (length + bar);
		gc.fillOval((int)(centrex-clip/2), (int)(centrey-clip/2), (int)(clip), (int)clip);
		super.onDrawEnd(gc);
	}
	
	
}