package org.miip.waterway.ui.banner;

import java.util.Collection;

import org.condast.commons.ui.banner.AbstractBanner;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.UrlLauncher;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.miip.waterway.ui.dialog.SettingsDialog;
import org.miip.waterway.ui.factory.ICompositeFactory;
import org.miip.waterway.ui.images.BannerImages;

public class Banner extends AbstractBanner {
	private static final long serialVersionUID = 1L;

	public static final String S_MIIP_URL = "http://www.maritiemland.nl/news/startbijeenkomst-maritieme-innovatie-impuls-projecten-2017/";
	public static final String S_NMT_URL = "http://www.maritiemland.nl/innovatie/projecten/maritieme-innovatie-impuls-projecten/";
	public static final String S_KC_DHS_URL = "https://www.hogeschoolrotterdam.nl/onderzoek/kenniscentra/duurzame-havenstad/over-het-kenniscentrum/";
	public static final String S_RDM_COE_URL = "http://www.rdmcoe.nl//";
	public static final String S_CONDAST_URL = "http://www.condast.com/";
	public static final String S_SMASH_URL = "https://smashnederland.nl/";
	public static final String S_DIRKSEN_URL = "http://www.dirksen.nl/";
	public static final String S_JUROD_URL = "https://www.jurod.nl/";

	private Collection<ICompositeFactory> factories;
	
	public Banner(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected void createImageBar(Composite comp, int style) {
		super.createImageBar(comp, style);
		Button button = createImageButton( comp, BannerImages.getImage( BannerImages.Images.MIIP ), SWT.FLAT );
		button.setData( S_MIIP_URL );
		button = createImageButton( comp, BannerImages.getImage( BannerImages.Images.NMT ), SWT.FLAT );
		button.setData( S_NMT_URL );
		button = createImageButton( comp, BannerImages.getImage( BannerImages.Images.RDM_COE ), SWT.FLAT );
		button.setData( S_RDM_COE_URL );
		button = createImageButton( comp, BannerImages.getImage( BannerImages.Images.KC_DHS ), SWT.FLAT );
		button.setData( S_KC_DHS_URL );
		button = createImageButton( comp, BannerImages.getImage( BannerImages.Images.DIRKSEN ), SWT.FLAT );
		button.setData( S_DIRKSEN_URL);
		button = createImageButton( comp, BannerImages.getImage( BannerImages.Images.SMASH ), SWT.FLAT );
		button.setData( S_SMASH_URL  );
		button = createImageButton( comp, BannerImages.getImage( BannerImages.Images.CONDAST ), SWT.FLAT );
		button.setData( S_CONDAST_URL );
		button = createImageButton( comp, BannerImages.getImage( BannerImages.Images.JUROD ), SWT.FLAT );
		button.setData( S_JUROD_URL );
	}

	@Override
	protected Dialog createSettingsDialog(Shell shell) {
		SettingsDialog dialog = new SettingsDialog( getParent(), factories.toArray( new ICompositeFactory[ factories.size()]) );
		if( Dialog.OK == dialog.open()){
			
		}
		return null;
	}

	public void setFactories(Collection<ICompositeFactory> factories) {
		this.factories = factories;
	}

	@Override
	protected void onHandleButtonSelect( final Button button) {
		Display.getCurrent().asyncExec( new Runnable(){

			@Override
			public void run() {
				try{
					UrlLauncher launcher = RWT.getClient().getService( UrlLauncher.class );
					String url = (String) button.getData();
					launcher.openURL( url );
				}
				catch( Exception ex ){
					ex.printStackTrace();
				}
			}					
		});
	}
}