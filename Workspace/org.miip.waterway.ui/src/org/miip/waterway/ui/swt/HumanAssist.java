package org.miip.waterway.ui.swt;

import org.condast.commons.autonomy.sa.radar.VesselRadarData;
import org.condast.commons.autonomy.ui.radar.AbstractSWTRadar;
import org.condast.commons.data.latlng.LatLngUtils;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.miip.waterway.model.IVessel;

public class HumanAssist<I> extends AbstractSWTRadar<IVessel> {
	private static final long serialVersionUID = 1L;

	public static final int BAR_WIDTH = 20;

	private int bar;

	public HumanAssist(Composite parent, int style ) {
		super(parent, style);
		this.bar = BAR_WIDTH;
	}

	@Override
	protected void drawObject( GC gc, VesselRadarData ship ){
		int centrex = super.getCentre().x;
		int centrey = super.getCentre().y;
		double length = (centrex < centrey )? centrex: centrey;

		IVessel reference = null;//getInput().getReference();
		double distance = LatLngUtils.getDistance(reference.getLocation(), ship.getLocation());
		double angle = LatLngUtils.getHeading(reference.getLocation(), ship.getLocation());

		int xpos1 = (int) (centrex + length * Math.sin( angle ));
		int ypos1 = (int) (centrey - length * Math.cos( angle ));

		double diff = Math.toRadians(1);
		int xpos2 = (int) (centrex + length * Math.sin( angle+diff));
		int ypos2 = (int) (centrey - length * Math.cos( angle+diff));
		gc.setBackground( getColour( distance ));
		gc.fillPolygon(new int[]{centrex, centrey, xpos1, ypos1, xpos2, ypos2});
	}

	@Override
	protected boolean onDrawEnd(GC gc) {
		double centrex = super.getCentre().x;
		double centrey = super.getCentre().y;
		double length = (centrex < centrey )? centrex: centrey;
		double clip = (length + bar);
		gc.fillOval((int)(centrex-clip/2), (int)(centrey-clip/2), (int)(clip), (int)clip);
		return super.onDrawEnd(gc);
	}


}