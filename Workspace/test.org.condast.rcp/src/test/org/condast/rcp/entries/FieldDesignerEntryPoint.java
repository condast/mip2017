package test.org.condast.rcp.entries;

import org.condast.commons.data.plane.FieldData;
import org.condast.commons.messaging.http.ResponseEvent;
import org.condast.commons.ui.controller.EditEvent;
import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.satr.arnac.ui.map.FieldDesignerComposite;

public class FieldDesignerEntryPoint extends AbstractEntryPoint{
	private static final long serialVersionUID = 1L;

	private FieldDesignerComposite fieldDesigner;
	
	@Override
	protected void createContents(Composite parent) {
		parent.setLayout(new FillLayout());
		fieldDesigner = new FieldDesignerComposite(parent, SWT.NONE);
		//fieldDesigner.setInput(context, store.getLoginUser());
		fieldDesigner.addEditListener(e->onEditEvent(e));
	}

	protected void onEditEvent( EditEvent<ResponseEvent<FieldData.Requests, FieldData>> event ) {
		switch( event.getData().getRequest()) {
		case GET_ALL:
			//if( store.getField() != null )
			//	fieldDesigner.setInput(store.getField());
			break;
		case SELECT:
			//store.setField(event.getData().getData());
			break;
		default:
			break;
		}
	}


	public void close() {
		fieldDesigner.removeEditListener(e->onEditEvent(e));
	}
}