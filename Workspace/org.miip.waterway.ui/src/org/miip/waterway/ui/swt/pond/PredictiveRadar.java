package org.miip.waterway.ui.swt.pond;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.sa.ISituationalAwareness;
import org.condast.commons.autonomy.sa.radar.VesselRadarData;
import org.condast.commons.autonomy.ui.radar.AbstractSWTRadar;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.miip.waterway.model.IVessel;

public class PredictiveRadar<I extends Object> extends AbstractSWTRadar<IVessel>{
	private static final long serialVersionUID = 1L;

	int count;

	public PredictiveRadar(Composite parent, int style) {
		super(parent, style);
		count = 0;
	}

	@Override
	protected void onDrawStart(GC gc) {
		count = 0;
		//getInput().clear();//the radar controls the situational awareness cache.
		super.onDrawStart(gc);
	}

	@Override
	protected void drawObject( GC gc, VesselRadarData physicalobj ){
		if( count > 1 )
			return;
		count++;
		int centrex = getCentre().x;
		int centrey = getCentre().y;
		gc.drawOval(centrex, centrey, 10, 10);
		if(!( physicalobj instanceof IVessel ))
			return;

		ISituationalAwareness<VesselRadarData> psa = null;//getInput();
		psa.clear();
		Collection<VesselRadarData> timemap = new ArrayList<>();//TODO psa.predictFuture( null, this.totalTime, reference, (IVessel) physicalobj);
		if( timemap.isEmpty() )
			return;
		double offset = ((double)getClientArea().width);//getInput().getView().getLength();
		Iterator<VesselRadarData> iterator = timemap.iterator();
		VesselRadarData ref = iterator.next();
		double angle = ref.getHeading();//0-360, north=0
		double distance = ref.getDistance();
		int startx = centrex + (int)(offset * distance * Math.sin( Math.toRadians(angle)));
		int starty = centrey - (int)(offset * distance * Math.cos( Math.toRadians(angle)));

		int xposf=startx, yposf = starty;
		int xposb=startx, yposb = starty;
		while( iterator.hasNext() ) {
			VesselRadarData data = iterator.next();
			angle = data.getHeading();//0-360, north=0
			distance = data.getDistance();

			int xpos1 = (int)(offset * distance * Math.sin( Math.toRadians(angle)));
			int ypos1 = (int)(offset * distance * Math.cos( Math.toRadians(angle)));
			int xtemp, ytemp;
			gc.setForeground( getColour( distance ));
			if( ref.isLater(data) ) {
				xtemp = xposf + xpos1;
				ytemp = yposf - ypos1;
				gc.drawLine( xposf, yposf, xtemp, ytemp);
				xposf = xtemp;
				yposf = ytemp;
			}else if( ref.isEarlier(data)) {
				xtemp = xposb - xpos1;
				ytemp = yposb + ypos1;
				gc.drawLine( xposb, yposb, xtemp, ytemp);
				xposb = xtemp;
				yposb = ytemp;
			}
		}
	}
}
