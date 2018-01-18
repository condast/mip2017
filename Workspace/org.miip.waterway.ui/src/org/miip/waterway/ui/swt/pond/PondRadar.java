package org.miip.waterway.ui.swt.pond;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.condast.commons.data.latlng.Field;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;
import org.condast.commons.data.latlng.Vector;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.miip.waterway.model.IVessel;
import org.miip.waterway.model.def.IPhysical;
import org.miip.waterway.ui.swt.AbstractRadar;

public class PondRadar<I extends Object> extends AbstractRadar<IPhysical>{
	private static final long serialVersionUID = 1L;

	private int  totalTime = 360000;//3 mins
	
	public PondRadar(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * Predict the future in the given time (in seconds)
	 * @param interval
	 * @param router
	 * @param bearing
	 * @param speed
	 */
	private Map<Long, Vector<Double>> predictFuture( int time, IVessel reference, IVessel other ){
		Calendar current = Calendar.getInstance();
		LatLng position = reference.getLocation();
		//logger.info("START POSITION: " + position.toString());
		StringBuffer buffer = new StringBuffer();
		buffer.append("GOING FROM: " + position.toString() + "\n");
		LatLng newpos = reference.getLocation();
		LatLng newotherpos = other.getLocation();
		//logger.fine("Start Time: " + current.getTime());
		Calendar next = Calendar.getInstance();
		Map<Long, Vector<Double>> timemap = new HashMap<Long, Vector<Double>>();
		for( int i=0; i < time; i++ ){
			try {
				Field field = getInput().getField();
				long interval = ( next.getTimeInMillis() - current.getTimeInMillis())*i;
				buffer.append("Interval: " + ( interval/1000) + ":\t" + field.printCoordinates(newpos, false ));
				buffer.append( "\t" + field.printCoordinates(newotherpos, false ));
				newpos = reference.plotNext( interval );
				if(!field.isInField( newpos, 0 )) {
					buffer.append(" outside field \n");
					break;
				}
				//ogger.fine(newpos.toString());
				newotherpos = other.plotNext(interval);
				if(!field.isInField( newotherpos, 10 ))
					continue;
				double diff = LatLngUtils.distance(newpos, newotherpos);		
				double bearing = LatLngUtils.getBearingInDegrees(newpos,newotherpos);		
				buffer.append( "\t bearing and distance is {" + bearing + ", " + diff + "}\n");
				Vector<Double> vector = new Vector<Double>( bearing, diff );
				timemap.put(interval,  vector );
				next.set( Calendar.SECOND, current.get( Calendar.SECOND ) + 1 );
			}
			catch( Exception ex ) {
				buffer.append( ex.getMessage() + "\n");
				break;
			}
		}
		return timemap;
	}

	
	@Override
	protected void drawObject( GC gc, IPhysical physicalobj ){
		IVessel reference = (IVessel) getInput().getReference(); 
		double centrex = getCentre().x;
		double centrey = getCentre().y;
		double length = Math.sqrt( centrex * centrex + centrey * centrey);
		//double offset = distance * length/getRange();
		
		if(!( physicalobj instanceof IVessel ))
			return;
				
		Map<Long, Vector<Double>> timemap = predictFuture(this.totalTime, reference, (IVessel) physicalobj);
		Iterator<Map.Entry<Long, Vector<Double>>> iterator = timemap.entrySet().iterator();
		int counter = 0;
		//int y = getClientArea().height;
		int offset = (int)(this.totalTime/getClientArea().width);
		int xpos = 0;
		int ypos = 0;
		while( iterator.hasNext() ) {
			Map.Entry<Long, Vector<Double>> entry = iterator.next();
			double angle = entry.getValue().getKey();
			double distance = entry.getValue().getValue() * length/getRange();
			int xpos1 = counter*offset;
			int ypos1 = (int)( angle );
			gc.setForeground( getColour( distance ));
			gc.drawLine(xpos, ypos, xpos1, ypos1);
			xpos = xpos1;
			ypos = ypos1;
			counter++;
		}
	}
}
