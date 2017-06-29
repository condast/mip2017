package org.miip.waterway.ui;

import org.condast.commons.ui.wizard.ButtonEvent;
import org.condast.commons.ui.wizard.IFlowControlWizard;
import org.condast.commons.ui.wizard.IButtonSelectionListener;
import org.condast.commons.ui.wizard.IButtonWizardContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

/**
 * @author Kees
 *
 */
public class FrontEndComposite extends Composite {
	private static final long serialVersionUID = 1L;

	private static final String S_EETMEE_SITE = "http://www.eetmee.nl";

	private Composite body;
	private IFlowControlWizard<?> current;	
	
	private SelectionListener navListener = new SelectionAdapter(){
		private static final long serialVersionUID = 1L;

		@Override
		public void widgetSelected(SelectionEvent e) {
			super.widgetSelected(e);
			/*
			NavigationComposite.Selection selection = navigationBar.getSelection();
			switch( selection ){
			case DEFAULT:
				helper.disconnect();
			case ENTRY:
			case NAME_AND_ADDRESS:
			case PROFILES:
			case MATCH:
			case ADVANCED:
				prefs.clear();
				ProfilePreferences.getInstance().clear();
				changeScreen(body);
				break;
			default:
				break;
			}
			*/
		}
	};

	@SuppressWarnings("rawtypes")
	private IButtonSelectionListener buttonListener = new IButtonSelectionListener(){

		@Override
		public void notifyButtonPressed( final ButtonEvent event) {
			if( Display.getCurrent().isDisposed() )
				return;
			Display.getCurrent().asyncExec( new Runnable(){

				@Override
				public void run() {	
					try{
						IButtonWizardContainer.Buttons button = event.getButton();
						switch( button ){
						case CONTINUE:
							break;
						case PREVIOUS:
							break;
						case SAVE:						
							break;
						case FINISH:
						case CANCEL:
							if( current != null )
								current.clear();
							changeScreen(body);
							break;
						default:
							break;
						}
					}
					catch( Exception ex ){
						ex.printStackTrace();
					}
				}	
			});
		}
	};

	public FrontEndComposite( Composite parent, int style) {
		super(parent, style);
		this.createComposite( parent, style );
	}

	private void createComposite(Composite parent, int style) {
		setLayout(new GridLayout(2, false));

		//status_bar.setImage( MIIPImages.getInstance().getImage( Images.MIIP ));
	}

		
	protected final Browser setDefaultScreen(){
		Browser browser = new Browser( body, SWT.NONE );
		browser.setUrl( S_EETMEE_SITE );
		return browser;
	}
	
	//public void setManager(AuthenticationManager manager) {
	//	this.navigationBar.setManager( manager );
	//}

	protected void refresh(){
		boolean open = true;//ServiceComponent.isReady( Composites.APPLICATION_PERSON );
		//Images img = open? Images.CORROSION: Images.ECE_OFFSHORE;
		//status_bar.setImage( MIIPImages.getInstance().getImage( img ));
		layout(false);
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	protected void changeScreen( final Composite parent ){
		Display.getDefault().asyncExec( new Runnable(){

			@SuppressWarnings({ "unchecked" })
			@Override
			public void run() {
				try{
					for( Control control: parent.getChildren() ){
						control.dispose();
					}
					if( current != null )
						current.removeListener( buttonListener);
					Composite comp = null;
					current = null;
/*					
					Selection selection = navigationBar.getSelection();
					IWizard wizard =null;
					IHeadlessWizardContainer container = null;
					switch( selection ){
					case DEFAULT:
						helper.disconnect();
						comp = setDefaultScreen();
						break;
					case ENTRY:
						helper.addHelper( Services.NA_SERVICE );
						helper.addHelper( Services.PROFILE_SERVICE );
						helper.connect();
						wizard = new EntryWizard();
						wizard.createPageControls( parent );
						comp = (Composite) current;
						container = (IHeadlessWizardContainer) wizard.getContainer();
						comp = container.getActiveComposite();
						current = (IFlowControlWizard<?>) wizard;
						break;
					case NAME_AND_ADDRESS:
						helper.addHelper( Services.NA_SERVICE );
						helper.connect();
						wizard = new NASearchWizard();
						wizard.createPageControls( parent );
						container = (IHeadlessWizardContainer) wizard.getContainer();
						comp = container.getActiveComposite();
						current = (IFlowControlWizard<?>) wizard;
						break;
					case PROFILES:
						helper.addHelper( Services.NA_SERVICE );
						helper.addHelper( Services.PROFILE_SERVICE );
						helper.connect();
						wizard = ServiceComponent.getWizard( ISupportedServices.Wizards.PROFILE_SEARCH, parent, SWT.NONE );
						IFlowControlWizard<?> fw = (IFlowControlWizard<?>) wizard;
						fw.addWizardPageListener( new IAddWizardPageListener(){

							@Override
							public IWizardPage notifyWizardPageAction(PageActionEvent event) {
								if( PageActions.NULL.equals( event.getEvent() )){
									String title = VGLanguage.getInstance().getString( NAWizardPage.NA_DETAILS  );
									String msg = VGLanguage.getInstance().getMessage( NAWizardPage.NA_DETAILS  );
									ProfileNAWizardPage page = new ProfileNAWizardPage((IWizard) event.getSource(), event.getFlow(), event.getPageName(), title, msg );
									page.setInput( prefs.getApplication(), prefs.getAp());
									return page;
								}
								return null;
							}
							
						});
						wizard.createPageControls( parent );
						container = (IHeadlessWizardContainer) wizard.getContainer();
						comp = container.getActiveComposite();
						current = (IFlowControlWizard<?>) wizard;
						IQueryDataContainer<?> qdcq = (IQueryDataContainer<?>) wizard;
						pqcq = new PerformQuery();
						pqcq.addQueryListener( new QueryListener());
						qdcq.setPerformer( pqcq );
						break;
					case MATCH:
						helper.addHelper( Services.NA_SERVICE );
						helper.addHelper( Services.PROFILE_SERVICE );
						helper.connect();
						wizard = ServiceComponent.getWizard( ISupportedServices.Wizards.MATCH_SEARCH, parent, SWT.NONE );
						wizard.createPageControls( parent );
						pqcq = setupQueryPerformer(wizard);
						current = (IFlowControlWizard<?>) wizard;
						container = (IHeadlessWizardContainer) wizard.getContainer();
						comp = container.getActiveComposite();
						break;
					case ADVANCED:
						comp = new AdvancedComposite(body, SWT.BORDER);
						break;
					default:
						break;
					}
		*/
					if( current != null )
						current.addListener( buttonListener);
					if( comp != null ){
						parent.setSize(computeSize( comp.getSize().x, SWT.DEFAULT ));
						layout( true );
					}
				}
				catch( Exception ex ){
					ex.printStackTrace();
				}
			}
		});
	}
}