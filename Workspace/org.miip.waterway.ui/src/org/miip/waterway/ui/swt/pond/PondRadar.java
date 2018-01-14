package org.miip.waterway.ui.swt.pond;

import java.util.Calendar;

import org.condast.commons.data.latlng.Field;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.miip.pond.core.PondSituationalAwareness;
import org.miip.waterway.model.IVessel;
import org.miip.waterway.model.def.IInhabitedEnvironment;
import org.miip.waterway.sa.ISituationalAwareness;
import org.miip.waterway.ui.swt.AbstractRadar;

public class PondRadar<I extends Object> extends AbstractRadar<I>{
	private static final long serialVersionUID = 1L;

	private static final int CORRECTION = 100;//To fill the screen
	
	public PondRadar(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * Predict the future in the given time (in seconds)
	 * @param time
	 * @param router
	 * @param bearing
	 * @param speed
	 */
	private void testPredictFuture( int time, IVessel reference, IVessel other ){
		Calendar current = Calendar.getInstance();
		LatLng position = reference.getLocation();
		//logger.info("START POSITION: " + position.toString());
		StringBuffer buffer = new StringBuffer();
		buffer.append("GOING FROM: " + position.toString() + "\n");
		LatLng newpos = reference.getLocation();
		LatLng newotherpos = other.getLocation();
		//logger.fine("Start Time: " + current.getTime());
		Calendar next = Calendar.getInstance();
		for( int i=0; i < time; i++ ){
			try {
				Field field = null;//super.getInput().getField();
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
				long diff = (long) LatLngUtils.distance(newpos, newotherpos);		
				int bearing = (int) LatLngUtils.getBearingInDegrees(newpos,newotherpos);		
				buffer.append( "\t bearing and distance is {" + bearing + ", " + diff + "}\n");
				next.set( Calendar.SECOND, current.get( Calendar.SECOND ) + 1 );
			}
			catch( Exception ex ) {
				buffer.append( ex.getMessage() + "\n");
				break;
			}
		}
		//logger.info( buffer.toString());
	}

	
	@Override
	protected void drawDegree( GC gc, int angle, double distance ){
		if( distance > super.getRange() )
			return;
		double centrex = getCentre().x;
		double centrey = getCentre().y;
		double length = CORRECTION + Math.sqrt( centrex * centrex + centrey * centrey);
		double offset = distance * length/getInput().getRange();
		
		double xpos1 = centrex + offset * Math.sin( toRadians( angle ));
		double ypos1 = centrey + offset * Math.cos( toRadians( angle ));
		gc.setForeground( getColour( distance ));
		gc.drawLine((int)centrex, (int)centrey, (int)xpos1, (int)ypos1);
		
	}
}
