package org.miip.waterway.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class NavigationComposite extends Composite {
	private static final long serialVersionUID = 1L;

	private static final String RWT_NAVIGATION = "vg_navigation";

	private Button imageButton;
	private Text text;
	private Map<String,String> links;

	public NavigationComposite(Composite parent, int style) {
		super(parent, style);
		links = new HashMap<>();
		this.createComposite(parent, style);
	}

	protected void createComposite( Composite parent, int style ){
		this.setData(RWT.CUSTOM_VARIANT, RWT_NAVIGATION );
		setLayout(new GridLayout(1, false));
		imageButton = new Button(this, SWT.FLAT);
		imageButton.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
			}
		});
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
		layoutData.heightHint = 130;
		layoutData.widthHint = 120;
		imageButton.setLayoutData(layoutData);
 	}

	public void addItem( String itemName, String link, boolean select ){
		links.put(itemName, link);
	}

	public Image getImage(){
		return this.imageButton.getImage();
	}

	public void setImage( Image image ){
		this.imageButton.setImage(image);
	}

	public String getText(){
		return this.text.getText();
	}

	public void setText( String text ){
		this.text.setText(text);
	}
}
