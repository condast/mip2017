package org.miip.waterway.ui.swt;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.ui.radar.AbstractSWTRadar;
import org.condast.commons.messaging.http.AbstractHttpRequest;
import org.condast.commons.messaging.http.ResponseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.miip.waterway.model.IVessel;
import org.miip.waterway.radar.RestRadar;

public class LedRingRest<I> extends AbstractSWTRadar<IVessel,IPhysical> {
	private static final long serialVersionUID = 1L;

//	private static final String REST_URL = "http://localhost:10081/miip2017/sa";
	private static final String REST_URL = "http://www.condast.com:8080/miip2017/sa";

	public enum Requests{
		RADAR;

		@Override
		public String toString() {
			return super.name().toLowerCase();
		}
	}

	public enum Attributes{
		ID,
		TOKEN,
		LEDS,
		RANGE,
		SENSE;

		@Override
		public String toString() {
			return super.name().toLowerCase();
		}
	}

	public static final int NR_OF_LEDS = 24;
	public static final int RADIUS = 10;
	public static final int DEGREES = 360;
	
	private int leds;
	
	private Map<Integer, RestRadar.RadarData> scan;
	
	private Logger logger = Logger.getLogger(this.getClass().getName());

	public LedRingRest(Composite parent, int style ) {
		super(parent, style);
		this.leds = NR_OF_LEDS;
		scan = new TreeMap<>();
	}
	
	@Override
	protected void onDrawStart(GC gc) {
		this.scan.clear();
		WebClient client = new WebClient( REST_URL);
		Map<String, String> parameters = new HashMap<>();
		parameters.put( Attributes.ID.toString(), "1");
		parameters.put( Attributes.TOKEN.toString(), "2");
		parameters.put( Attributes.LEDS.toString(), String.valueOf(leds));
		parameters.put( Attributes.RANGE.toString(), String.valueOf(super.getRange()));
		parameters.put( Attributes.SENSE.toString(), String.valueOf(super.getSensitivity()));
		try {
			for( int i= 0; i<leds; i++ )
				client.sendGet( Requests.RADAR, parameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onDrawStart(gc);
	}
	
	@Override
	protected void drawObject( GC gc, IPhysical ship ){
		/* NOTHING */
	}

	protected Color getColour( int key, RestRadar.RadarData data) {
		int[] colour = ( data == null ) ? new int[] {0, 255, 0 }: data.getColor();
		return new Color( getDisplay(), colour[0], colour[1], colour[2]);
	}

	@Override
	protected boolean onDrawEnd(GC gc) {
		if(( getDisplay() == null ) || ( getDisplay().isDisposed()))
			return true;
		double centrex = super.getCentre().x;
		double centrey = super.getCentre().y;
		double length = (centrex < centrey )? centrex: centrey;
		length = (( length - RADIUS ) < 0)? 0: length - RADIUS;
		RestRadar.RadarData data = null;
		for( int i=0; i< this.leds; i++ ) {
			data = scan.get( i );
			double phi = i * 2 * Math.PI/this.leds;
			double x = length * Math.sin( phi );
			double y = length * Math.cos( phi );
			gc.setBackground( getColour( i, data ));
			gc.fillOval((int)(centrex + x), (int)(centrey-y), RADIUS, RADIUS );			
		}
		return super.onDrawEnd(gc);
	}

	private class WebClient extends AbstractHttpRequest<LedRingRest.Requests, Object>{

		public WebClient(String path) {
			super(path);
		}
	
		@Override
		public void sendGet( Requests request, Map<String, String> parameters) throws Exception {
			super.sendGet(request, parameters);
		}

		@Override
		protected String onHandleResponse(ResponseEvent<LedRingRest.Requests,Object> response, Object data ) throws IOException {
			String result = response.getResponse();
			Gson gson = new Gson();
			RestRadar.RadarData post = gson.fromJson(result, RestRadar.RadarData.class);
			scan.put(post.getAngle(), post);
			logger.fine(result);
			return result;
		}	
	}
}