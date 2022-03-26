package test.org.condast.rcp;

import java.util.Map;
import javax.servlet.http.HttpSession;
import org.condast.commons.ui.entry.IDataEntryPoint;
import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import test.org.condast.rcp.store.SessionStore;

public class DataEntryPoint extends AbstractEntryPoint implements IDataEntryPoint<SessionStore>{
	private static final long serialVersionUID = 1L;

	public static final int DEFAULT_HANDLERS = 1;
	public static final long DEFAULT_DELAY = 100;
	
	private SessionStore store;
	
	public DataEntryPoint() {
		super();
	}

	@Override
    protected void createContents(Composite parent) {
        parent.setLayout(new GridLayout(2, false));
        Button checkbox = new Button(parent, SWT.CHECK);
        checkbox.setText("Hello");
	}
	
	protected SessionStore getData() {
		return store;
	}

	
	@Override
	public void setData(Map<HttpSession, SessionStore> stores, SessionStore store) {
		this.store = store;
	}
}
