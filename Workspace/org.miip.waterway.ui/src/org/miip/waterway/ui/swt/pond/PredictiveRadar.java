package org.miip.waterway.ui.swt.pond;

import java.util.Collection;
import java.util.Iterator;

import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.sa.ISituationalAwareness;
import org.condast.commons.autonomy.sa.radar.RadarData;
import org.condast.commons.autonomy.ui.radar.AbstractSWTRadar;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.miip.waterway.model.IVessel;

public class PredictiveRadar<I extends Object> extends AbstractSWTRadar<IPhysical, IVessel>{
	private static final long serialVersionUID = 1L;

	private int  totalTime = 35000;//35 sec
	
	int count;
	
	public PredictiveRadar(Composite parent, int style) {
		super(parent, style);
		count = 0;
	}

	@Override
	protected void onDrawStart(GC gc) {
		count = 0;
		getInput().clear();//the radar controls the situational awareness cache.
		super.onDrawStart(gc);
	}

	@Override
	protected void drawObject( GC gc, IPhysical physicalobj ){
		if( count > 1 )
			return;
		count++;
		IVessel reference = (IVessel) getInput().getReference(); 
		int centrex = getCentre().x;
		int centrey = getCentre().y;
		gc.drawOval(centrex, centrey, 10, 10);
		if(!( physicalobj instanceof IVessel ))
			return;
		
		ISituationalAwareness<IPhysical, IVessel> psa = getInput();
		psa.clear();
		Collection<RadarData<IPhysical>> timemap = psa.predictFuture( null, this.totalTime, reference, (IVessel) physicalobj);
		if( timemap.isEmpty() )
			return;
		double offset = ((double)getClientArea().width)/getInput().getView().getLength();
		Iterator<RadarData<IPhysical>> iterator = timemap.iterator();
		RadarData<IPhysical> ref = iterator.next();
		double angle = ref.getAngle();//0-360, north=0
		double distance = ref.getDistance();
		int startx = centrex + (int)(offset * distance * Math.sin( Math.toRadians(angle)));
		int starty = centrey - (int)(offset * distance * Math.cos( Math.toRadians(angle)));
		
		int xposf=startx, yposf = starty;
		int xposb=startx, yposb = starty;
		while( iterator.hasNext() ) {
			RadarData<IPhysical> data = iterator.next();
			angle = data.getAngle();//0-360, north=0
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
