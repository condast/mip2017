package org.miip.waterway.ui;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.widgets.Text;
import org.miip.waterway.ui.lang.MIIPLanguage;

/**
 * @author Kees
 *
 */
public class FrontEndTabComposite extends Composite {
	private static final long serialVersionUID = 1L;

	private static final String RWT_FRONTEND = "frontend";
	private static final String RWT_FRONTEND_TITLE = "frontend-title";
	
	private static final String TAB_TEXT_KC_DHS = "Kenniscentrum Duurzame HavenStad";
	
	//Text fields
	private enum Fields{
		TITLE;

		public String toString(){
			return MIIPLanguage.getInstance().getString( this );
		}
	}

	private Label lblTitle;
	
	private Composite body;
	
	private CTabFolder tabFolder;
	
	private Composite comp_info;
	private Text text_id;
	private MIIPFrontend selected;
	
	public FrontEndTabComposite( Composite parent, int style) {
		super(parent, style);
		this.createComposite( parent, style );
		this.initComposite();
	}

	private void createComposite(Composite parent, int style) {
		setLayout(new GridLayout(2, false));
		
		Composite titleComposite = new Composite(this, SWT.NONE);
		titleComposite.setData(RWT.CUSTOM_VARIANT, RWT_FRONTEND );	
		titleComposite.setLayout( new GridLayout(3, false ));
		GridData gd_title = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		//gd_title.widthHint = 411;
		gd_title.heightHint = 100;
		titleComposite.setLayoutData( gd_title );

		this.lblTitle = new Label( titleComposite, SWT.NONE );
		lblTitle.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		this.lblTitle.setData( RWT.CUSTOM_VARIANT, RWT_FRONTEND_TITLE );	
		this.lblTitle.setText( Fields.TITLE.toString() );
		
		comp_info = new Composite(titleComposite, SWT.NONE);
		comp_info.setData(RWT.CUSTOM_VARIANT, RWT_FRONTEND );	
		GridData gd_comp_info = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
		gd_comp_info.widthHint = 350;
		comp_info.setLayoutData(gd_comp_info);
		comp_info.setLayout(new GridLayout(1, false));
		
		text_id = new Text(comp_info, SWT.MULTI | SWT.NO_SCROLL );
		text_id.setData(RWT.CUSTOM_VARIANT, RWT_FRONTEND );	
		text_id.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
				
		body = new Composite( this, SWT.NONE );
		body.setLayout(new FillLayout(SWT.HORIZONTAL));
		GridData gd_body = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_body.horizontalIndent = 0;
		gd_body.verticalIndent = 0;
		body.setLayoutData( gd_body);

		tabFolder = new CTabFolder(body, SWT.BORDER);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		// Add an event listener to write the selected tab to stdout
		tabFolder.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			public void widgetSelected(org.eclipse.swt.events.SelectionEvent event) {
				setSelected();
			}
		});	
		CTabItem item = new CTabItem(tabFolder, SWT.NONE);
		item.setText( TAB_TEXT_KC_DHS);
		selected = new MIIPFrontend(tabFolder, SWT.BORDER );
		item.setControl( selected );	
		tabFolder.setSelection(0);
	}	

	protected void initComposite(){
		//selected.initComposite();
		//setSelected();
		refresh();
	}
	
	protected void setSelected(){
		//selected = (Composite) tabFolder.getSelection().getControl();
		//selected.initComposite();
	}	
	
	protected void refresh(){
		layout(true);
	}

	@Override
	public void dispose() {
		super.dispose();
	}
}