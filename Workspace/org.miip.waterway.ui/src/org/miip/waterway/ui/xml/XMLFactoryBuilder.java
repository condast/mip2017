/*******************************************************************************
 * Copyright (c) 2014 Chaupal.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0.html
 *******************************************************************************/
package org.miip.waterway.ui.xml;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.EnumSet;

import org.condast.commons.strings.StringStyler;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.swt.IStyle;
import org.condast.commons.ui.widgets.StatusBar;
import org.condast.commons.xml.AbstractXMLBuilder;
import org.condast.commons.xml.AbstractXmlHandler;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.miip.waterway.ui.FrontEndComposite;
import org.miip.waterway.ui.NavigationComposite;
import org.miip.waterway.ui.images.MIIPImages;
import org.miip.waterway.ui.lang.MIIPLanguage;
import org.xml.sax.Attributes;

public class XMLFactoryBuilder extends AbstractXMLBuilder<Composite, XMLFactoryBuilder.Selection> {

	public static String S_DEFAULT_FOLDER = "/design";
	public static String S_DEFAULT_DESIGN_FILE = "design.xml";
	public static String S_SCHEMA_LOCATION =  S_DEFAULT_FOLDER + "/rdm-schema.xsd";
	
	public static enum Selection{
		DESIGN,
		DEFAULT,
		ENTRY,
		FRONTEND,
		NAVIGATION,
		IMAGE,
		ITEM,
		BODY,
		STATUS_BAR,
		REPORTS,
		ADVANCED,
		HELP,
		//LOG_IN,
		LOG_OFF;

		@Override
		public String toString() {
			return MIIPLanguage.getInstance().getString( super.toString() );
		}
	}

	public enum AttributeNames{
		ID,
		NAME,
		URL,
		LINK,
		SELECT,
		STYLE,
		SCOPE,
		TYPE,
		DATA,
		DESCRIPTION,
		HEIGHT,
		WIDTH,
		SIZE;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}

		public String toXmlStyle() {
			return StringStyler.xmlStyleString( super.toString() );
		}

		public static boolean isAttribute( String value ){
			if( StringUtils.isEmpty( value ))
				return false;
			for( AttributeNames attr: values() ){
				if( attr.toString().equals( value ))
					return true;
			}
			return false;
		}
	}

	public XMLFactoryBuilder( Composite parent ) {
		this( parent, XMLFactoryBuilder.class.getResource( S_DEFAULT_FOLDER + File.separator + S_DEFAULT_DESIGN_FILE) );
	}

	/**
	 * Build the factories from the given resource in the class file and add them to the container
	 * @param bundleId
	 * @param clss
	 * @param location
	 * @param builder
	 */
	public XMLFactoryBuilder( Composite parent, URL url ) {
		super( new XMLHandler( parent ), url );
	}

	public static String getLocation( String defaultLocation ){
		if( !StringUtils.isEmpty( defaultLocation ))
			return defaultLocation;
		return defaultLocation;
	}

	@Override
	public Composite[] getUnits() {
		return getHandler().getUnits();
	}
	
	private static class XMLHandler extends AbstractXmlHandler<Composite,XMLFactoryBuilder.Selection>{
		
		private Composite composite;
		private Composite parent;
		private NavigationComposite navcomp;
		
		public XMLHandler( Composite parent ) {
			super( EnumSet.allOf( XMLFactoryBuilder.Selection.class));
			this.parent = parent;
		}

		@Override
		public Composite[] getUnits() {
			Composite[] comps = new Composite[1];
			comps[0] = composite;
			return comps;
		}

		@Override
		protected Composite parseNode( Selection node, Attributes attributes) {
			Composite retval = null;
			String style_str = getAttribute( attributes, AttributeNames.STYLE );
			String name = getAttribute( attributes, AttributeNames.NAME );
			String url = getAttribute( attributes, AttributeNames.URL );
			String data = getAttribute( attributes, AttributeNames.DATA );
			String height_str = getAttribute( attributes, AttributeNames.HEIGHT );
			int height = StringUtils.isEmpty( height_str )? 50: Integer.parseInt( height_str );
			String size_str = getAttribute( attributes, AttributeNames.SIZE );
			int size = StringUtils.isEmpty( size_str )? 50: Integer.parseInt( size_str );

			//String width_str = getAttribute( attributes, AttributeNames.WIDTH );
			//int width = StringUtils.isEmpty( width_str )? 50: Integer.parseInt( width_str );

			Composite current = null;
			Composite widget = null;
			switch( node ){
			case FRONTEND:
				widget = new FrontEndComposite(parent, IStyle.SWT.convert( style_str ));
				composite = (Composite) widget;
				retval = widget;
				break;
			case NAVIGATION:
				current = (Composite) super.getCurrentData();
				int style = IStyle.SWT.convert( style_str );
				boolean horizontal = SWT.HORIZONTAL == style;
				navcomp = new NavigationComposite( current, style);
				widget = navcomp;
				GridData gd_nav = new GridData(SWT.FILL, SWT.FILL, horizontal, !horizontal);
				if( !StringUtils.isEmpty( size_str ))
					gd_nav.widthHint = size;
				widget.setLayoutData( gd_nav);
				//navcomp.addSelectionListener(listener);
				retval = widget;
				break;
			case IMAGE:
				navcomp = (NavigationComposite) super.getCurrentData();
				navcomp.setImage( MIIPImages.getInstance().getImageFromName(url));
				break;
			case ITEM:
				navcomp = (NavigationComposite) super.getCurrentData();
				String select_str = getAttribute( attributes, AttributeNames.SELECT );
				boolean select=  StringUtils.isEmpty( select_str )?false: Boolean.parseBoolean( select_str );
				String link = getAttribute( attributes, AttributeNames.LINK );
				navcomp.addItem( name, link, select );
				break;
			case BODY:
				current = (Composite) super.getCurrentData();
				widget = new Composite(current, IStyle.SWT.convert( style_str ));
				widget.setLayout(new FillLayout());
				GridData gd_body = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
				gd_body.horizontalIndent = 0;
				gd_body.verticalIndent = 0;
				widget.setLayoutData( gd_body);
				String sellink = navcomp.getSelectedLink();
				if( !StringUtils.isEmpty( sellink )){
					createComposite(sellink, widget, IStyle.SWT.convert( style_str ));
				}
				break;
			case STATUS_BAR:
				current = (Composite) super.getCurrentData();
				StatusBar bar = new StatusBar(current, IStyle.SWT.convert( style_str ));
				if( !StringUtils.isEmpty( name ))				
					bar.setLabelText( name );
				widget = bar;
				widget.setLayout(new FillLayout(SWT.HORIZONTAL));
				GridData gd_text_status = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
				gd_text_status.heightHint = height;
				bar.setLayoutData(gd_text_status);
				break;
			default:
				break;
			}
			if( widget != null ){
				if( !StringUtils.isEmpty( data ))
					widget.setData( RWT.CUSTOM_VARIANT, data );
				retval = widget;
			}
			return retval;
		}

		@Override
		protected void completeNode(Enum<Selection> node) {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void addValue(Enum<Selection> node, String value) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Composite getUnit(String id) {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	protected static Composite createComposite( String className, Composite parent, int style ){
		if( StringUtils.isEmpty( className ))
			return null;
		Composite composite = null;
		try{
			Class<Composite> cls = (Class<Composite>) Class.forName( className );
			Constructor<Composite> cons = cls.getConstructor( Composite.class, Integer.class);          
			composite = cons.newInstance( parent, style );
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
		return composite;
	}


}