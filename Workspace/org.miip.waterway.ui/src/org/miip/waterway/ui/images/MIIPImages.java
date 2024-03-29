/*******************************************************************************
 * Copyright (c) 2014 Chaupal.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0.html
 *******************************************************************************/
package org.miip.waterway.ui.images;

import org.condast.commons.strings.StringStyler;
import org.condast.commons.ui.image.AbstractImages;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * @See: https://www.iconfinder.com/savlon
 * @author Condast
 *
 */
public class MIIPImages extends AbstractImages{

	public static final String BUNDLE_ID = "org.miip.waterway.ui";

	public static final String S_ICON_PATH = "/resources/";

	public enum Images{
		MIIP,
		NMT,
		CONDAST,
		KC_DHS,
		RDM_COE,
		SHIP,
		SHIP_ORNG,
		SHIP_RED,
		SHIP_YLW,
		SHIP_GRN,
		TREE,
		SETTINGS;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}

		public static String getResource( Images image ){
			String str = null;
			switch( image ){
			case MIIP:
				str = "miip.png";
				break;
			case NMT:
				str = "nmt.jpg";
				break;
			case CONDAST:
				str = "condast.png";
				break;
			case KC_DHS:
				str = "kc-dhs.png";
				break;
			case RDM_COE:
				str = "rdm-coe.jpg";
				break;
			default:
				str = image.name().toLowerCase();
				str = str.replace("_", "-");
				str += "-32.png";
				break;
			}
			return str;
		}
	}

	private static MIIPImages images = new MIIPImages();

	private MIIPImages() {
		super( S_ICON_PATH, BUNDLE_ID );
	}

	/**
	 * Get an instance of this map
	 * @return
	 */
	public static MIIPImages getInstance(){
		return images;
	}

	@Override
	public void initialise(){
		for( Images img: Images.values() )
			setImage( Images.getResource( img ));
	}

	public void setImage( String name, String url ){
		super.setImage(name);
	}

	/**
	 * Get the image
	 * @param desc
	 * @return
	 */
	public static Image getImage( Images image ){
		return getInstance().getImageFromName( Images.getResource(image));
	}

	/**
	 * Get the image
	 * @param desc
	 * @return
	 */
	public static Image getImage( String name ){
		return getInstance().getImageFromName( name);
	}

	public static Image getImageFromResource( Display display, Images image ){
		return getImageFromResource(display, MIIPImages.class, S_ICON_PATH + Images.getResource(image));
	}
}