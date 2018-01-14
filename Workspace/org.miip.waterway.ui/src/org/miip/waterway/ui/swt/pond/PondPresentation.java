package org.miip.waterway.ui.swt.pond;

import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.miip.pond.core.PondSituationalAwareness;
import org.miip.waterway.model.IVessel;
import org.miip.waterway.model.Location;
import org.miip.waterway.model.def.IInhabitedEnvironment;
import org.miip.waterway.sa.ISituationalAwareness;
import org.miip.waterway.ui.images.MIIPImages;

import java.util.Map;

import org.condast.commons.data.latlng.Field;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.ui.swt.IInputWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public class PondPresentation extends Canvas implements IInputWidget<IInhabitedEnvironment<IVessel[]>>{
	private static final long serialVersionUID = 1L;

	public static final int GRIDX = 100;//meters
	public static final int GRIDY = 20;//meters
	
	private PaintListener listener = new PaintListener(){
		private static final long serialVersionUID = 1L;

		@Override
		public void paintControl(PaintEvent event) {
			try{
				drawField( event.gc );
			}
			catch( Exception ex ){
				ex.printStackTrace();
			}
		}
	};

	private IInhabitedEnvironment<IVessel[]> environment;
	private ISituationalAwareness<IInhabitedEnvironment<IVessel[]>> sa;
	

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public PondPresentation(Composite parent, Integer style) {
		super(parent, style);
		setBackground(Display.getCurrent().getSystemColor( SWT.COLOR_WHITE));
		super.addPaintListener(listener);
		sa = new PondSituationalAwareness();
	}

	@Override
	public Composite getParent(){
		return super.getParent();
	}
	
	@Override
	public IInhabitedEnvironment<IVessel[]> getInput() {
		return this.environment;
	}

	@Override
	public void setInput( IInhabitedEnvironment<IVessel[]> environment){
		this.environment = environment;
		sa.setInput(environment);
	}
	
/*	
	protected Point drawOffset( CentreShip ship, Point centre ){
		if( ship == null )
			return centre;
		Vector<Integer> vector = ship.getOffset();
		if( vector == null )
			return centre;
		//double angle = Math.sin( Math.toRadians( vector.getKey()));
		int xoffset = (int) vector.getValue().doubleValue();//(int)((float)20 * vector.getValue() *  angle );
		int yoffset = vector.getKey();//(int)((float)20 * vector.getValue() *  Math.cos( Math.toRadians( vector.getKey())));
		return new Point( centre.x + xoffset, centre.y + yoffset );
	}
 */

	protected void drawField( GC gc ){
		if( environment == null )
			return;
		Rectangle clientArea = getClientArea();

		try {
			//The ship in the centre
			IVessel[] vessels = this.environment.getInhabitant();
			IVessel vessel = vessels[0];
			Point point = ( vessel == null )? new Point( (int)( clientArea.width/2), (int)(clientArea.height/2)):
				scaleToCanvas(vessel.getLocation());
			drawImage( gc, point, MIIPImages.Images.SHIP);

			if(!environment.isInitialsed() )
				return;

			//The raster
			Color color = gc.getForeground();
			gc.setForeground( getDisplay().getSystemColor( SWT.COLOR_WIDGET_LIGHT_SHADOW ));

			Field field = environment.getField();
			int i = 0;
			while( i < field.getLength() ){
				int xpos = scaleXToDisplay( i ); 
				int ypos1 = 0;
				int ypos2 = scaleYToDisplay( (int) (field.getWidth() ));
				i += GRIDX;
				gc.drawLine( xpos, ypos1, xpos, ypos2 );
			}
			i =0;
			while( i < field.getWidth()){
				int xpos1 = scaleXToDisplay( 0 );
				int xpos2 = scaleXToDisplay( (int) field.getLength());
				int ypos = scaleYToDisplay( i ); 
				i += GRIDY;
				gc.drawLine( xpos1, ypos, xpos2, ypos );
			}
			gc.setForeground(color);

			IVessel ship;
			for( int j=1; j<vessels.length; j++ ){
				ship = vessels[j];
				if( !field.isInField( ship.getLocation(), 0))
					continue;
				MIIPImages.Images img = ( ship.getBearing() < LatLng.Compass.SOUTH.getAngle() )? MIIPImages.Images.SHIP_GRN: MIIPImages.Images.SHIP_RED;	
				drawImage(gc, scaleToCanvas( ship.getLocation() ), img );
			}
		}catch( Exception ex ) {
			ex.printStackTrace();
		}

		gc.dispose();
	}

	protected Image drawImage( GC gc, Point point, MIIPImages.Images image ){
		Image img = null;
		try{
			img = MIIPImages.getImageFromResource( getDisplay(), image );
			Rectangle bounds = img.getBounds();
			Point centre = new Point(( int )( bounds.width/2), (int)( bounds.height/2 ));
			int posx = (point.x-centre.x)<0? 0: point.x-centre.x;
			int posy = (point.y-centre.y)<0? 0: point.y-centre.y;
			gc.drawImage( img, posx, posy);
			img.dispose();
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
		return img;
	}

	public Point scaleToCanvas( LatLng location ){
		Rectangle clientArea = getClientArea();
		Field field = this.environment.getField();
		Map.Entry<Double, Double> vector = field.getVector(location);
		int x= (int)(clientArea.width * vector.getKey()/field.getLength());
		int y = (int)(clientArea.height * vector.getValue()/field.getWidth());
		return new Point((int) x, (int) y );
	}

	public Point scaleToCanvas( Location location ){
		Rectangle clientArea = getClientArea();
		Field field = this.environment.getField();
		int x=  (int)(location.getX() * clientArea.width/field.getLength());
		int y = (int)(location.getY() * clientArea.height/field.getWidth());
		return new Point((int) x, (int) y );
	}

	protected int scaleYToDisplay( int width ){
		Rectangle clientArea = getClientArea();
		float scale = ((float)environment.getField().getWidth())/clientArea.height;
		return (int)( width/scale );
	}

	protected int scaleXToDisplay( int length ){
		Rectangle clientArea = getClientArea();
		float scale = ((float)environment.getField().getLength())/clientArea.width;
		return (int)( length/scale );
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
