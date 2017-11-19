package org.miip.waterway.ui.swt;

import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.miip.waterway.model.CentreShip;
import org.miip.waterway.model.Location;
import org.miip.waterway.model.Ship;
import org.miip.waterway.model.Ship.Bearing;
import org.miip.waterway.model.eco.Bank;
import org.miip.waterway.model.eco.MIIPEnvironment;
import org.miip.waterway.ui.images.MIIPImages;
import org.condast.commons.data.latlng.Vector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public class MIIPPresentation extends Canvas{
	private static final long serialVersionUID = 1L;

	private PaintListener listener = new PaintListener(){
		private static final long serialVersionUID = 1L;

		@Override
		public void paintControl(PaintEvent event) {
			drawField( event.gc );
		}
	};

	MIIPEnvironment environment;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public MIIPPresentation(Composite parent, Integer style) {
		super(parent, style);
		setBackground(Display.getCurrent().getSystemColor( SWT.COLOR_WHITE));
		super.addPaintListener(listener);
	}

	@Override
	public Composite getParent(){
		return super.getParent();
	}
	
	public void setInput( MIIPEnvironment environment){
		this.environment = environment;
	}
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
	
	protected void drawField( GC gc ){
		if( environment == null )
			return;
		Display display = super.getDisplay();
		Rectangle clientArea = getClientArea();
		int bankSize = scaleYToDisplay( environment.getBankWidth());
		gc.drawLine( 0, bankSize, clientArea.width, bankSize );

		Rectangle rect = new Rectangle(0, 0, clientArea.width, bankSize);
		gc.setBackground(display.getSystemColor(SWT.COLOR_DARK_GREEN));
		gc.fillRectangle( rect );

		rect = new Rectangle(0, clientArea.height-bankSize, clientArea.width, clientArea.height );
		gc.setBackground(display.getSystemColor(SWT.COLOR_DARK_GREEN ));
		gc.fillRectangle( rect );
		gc.drawLine( 0, clientArea.height-bankSize, clientArea.width, clientArea.height-bankSize);

		Point point = new Point( (int)( clientArea.width/2), (int)(clientArea.height/2));
		CentreShip cship = this.environment.getShip();
		drawImage( gc, drawOffset( cship, point ), MIIPImages.Images.SHIP);

		if(!environment.isInitialsed() )
			return;

		Color color = gc.getForeground();
		gc.setForeground( getDisplay().getSystemColor( SWT.COLOR_WIDGET_LIGHT_SHADOW ));
		Rectangle waterway = getWaterway();
		for( Integer loc: environment.getXCoordinates()){
			int xpos = scaleXToDisplay( loc ); 
			gc.drawLine( xpos, waterway.y, xpos, waterway.height );
		}
		for( Integer loc: environment.getYCoordinates()){
			int ypos = scaleYToDisplay( loc ); 
			if( ypos > waterway.y )
				gc.drawLine( 0, ypos, getClientArea().width, ypos );
		}
		gc.setForeground(color);

		for( Bank bank: environment.getBanks()){
			for( Location tree: bank.getShoreObjects() )
				drawImage(gc, scaleToCanvas( tree ), MIIPImages.Images.TREE );
		}

		for( Ship ship: environment.getWaterway().getShips()){
			MIIPImages.Images img = Bearing.EAST.equals( ship.getBearing())? MIIPImages.Images.SHIP_GRN: MIIPImages.Images.SHIP_RED;	
			drawImage(gc, scaleWaterwayToCanvas( environment.getLocation( ship )), img );
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

	protected Point scaleWaterwayToCanvas( Location location ){
		Rectangle clientArea = getClientArea();
		float bankWidth = (float)clientArea.height * environment.getBankWidth()/environment.getWidth();
		int x = scaleXToDisplay((int) location.getX() );
		float y = bankWidth + scaleYToDisplay((int)location.getY() );
		return new Point((int) x, (int) y );
	}

	protected Rectangle getWaterway(){
		Rectangle clientArea = getClientArea();
		int bankWidth = (int)((float)clientArea.height * environment.getBankWidth()/environment.getWidth());
		return new Rectangle(0, bankWidth, getClientArea().width, getClientArea().height - bankWidth );
	}

	protected Point scaleToCanvas( Location location ){
		int x = scaleXToDisplay((int) location.getX() );
		int y = scaleYToDisplay((int)location.getY() );
		return new Point((int) x, (int) y );
	}

	protected int scaleYToDisplay( int length ){
		Rectangle clientArea = getClientArea();
		float scale = ((float)environment.getWidth() + 2*environment.getBankWidth() )/clientArea.height;
		return (int)( length/scale );
	}

	protected int scaleXToDisplay( int length ){
		Rectangle clientArea = getClientArea();
		float scale = environment.getLength()/clientArea.width;
		return (int)( length/scale );
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
