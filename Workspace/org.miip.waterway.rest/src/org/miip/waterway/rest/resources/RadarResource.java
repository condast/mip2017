package org.miip.waterway.rest.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.condast.commons.data.binary.SequentialBinaryTreeSet;
import org.condast.commons.data.latlng.Vector;
import org.condast.commons.strings.StringUtils;
import org.miip.waterway.rest.model.RadarData;
import org.miip.waterway.rest.model.RadarData.Choices;
import org.miip.waterway.rest.service.SADispatcher;
import org.miip.waterway.sa.ISituationalAwareness;

import com.google.gson.Gson;

@Path("/sa")
public class RadarResource{
		
	private Logger logger = Logger.getLogger( this.getClass().getName());

	public RadarResource() {
		super();
	}

	// This method is called if TEXT_PLAIN is request
	@GET
	@Path("/setup")
	@Produces(MediaType.APPLICATION_JSON)
	public String setupRadar( @QueryParam("id") String id, @QueryParam("token") String token ) {
		logger.info("Query for Radar " + id );
		RadarData[] data = new RadarData[ 1];
		data[0] = new RadarData(Choices.COLOUR_WIPE_BLUE);
		Gson gson = new Gson();
		return gson.toJson(data);
	}
	
	// This method is called if TEXT_PLAIN is request
	@GET
	@Path("/radar")
	@Produces(MediaType.APPLICATION_JSON)
	public String getRadar( @QueryParam("id") String id, @QueryParam("token") String token, @QueryParam("leds") String leds ) {
		logger.info("Query for Radar " + id );
		ISituationalAwareness sa = SADispatcher.getInstance().getSituationalAwareness();
		if( sa == null )
			return "[]";
		SequentialBinaryTreeSet<Vector<Integer>>  data = sa.getBinaryView();
		if(( data == null ) ||  data.isEmpty())
			return "[]";
		
		int scale = StringUtils.isEmpty( leds )? 0: Integer.parseInt( leds );
		Iterator<Vector<Integer>> iterator  = data.getValues( data.scale( scale )).iterator();
		Collection<RGB> rgbs = new ArrayList<RGB>();
		while( iterator.hasNext() ){
			Map.Entry<Integer, Double> entry = iterator.next();
			rgbs.add( getColour(sa, entry.getKey(), entry.getValue()));
		}
		Gson gson = new Gson();
		return gson.toJson( rgbs.toArray( new RGB[ rgbs.size()]));
	}

	protected RGB getColour( ISituationalAwareness sa, int angle, double distance ){
		if( sa == null)
			return new RGB( angle, 0, 0, 0, 0 );
	
		if( distance <= sa.getSensitivity() )
			return new RGB( angle, 255, 0, 0, 0 );
		if( distance > sa.getRange())
			return new RGB( angle, 255, 0, 0, 255 );
		return getLinearColour( angle, (int) distance, sa.getRange(), (int)sa.getSensitivity() );
	}
	
	private RGB getLinearColour( int angle, int distance, int range, int sensitivity ){
		boolean far = ( distance > ( range - sensitivity ));
		int red = far? 50: (int)( 255 * ( 1 - distance/range ));
		int green = far? 255: (int)( 255 * distance/range );
		int blue = 50;
		return new RGB( angle, red, green, blue, 0 );
	}

	@SuppressWarnings("unused")
	private class RGB{
		private int a;
		private byte r,g,b;
		private byte t;

		RGB( int angle, int r, int g, int b, int transparency) {
			super();
			this.a = angle;
			this.r = (byte) r;
			this.g = (byte) g;
			this.b = (byte) b;
			this.t= (byte) transparency;
		}
	}
}