package webx.studio.service.server.ui.actions;

import java.util.Iterator;


import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.SelectionProviderAction;

import webx.studio.service.server.core.ServiceServer;

public abstract class AbstractServerAction extends SelectionProviderAction {
	protected Shell shell;

	public AbstractServerAction(ISelectionProvider selectionProvider, String text) {
		this(null, selectionProvider, text);
	}

	public AbstractServerAction(Shell shell, ISelectionProvider selectionProvider, String text) {
		super(selectionProvider, text);
		this.shell = shell;
		setEnabled(false);
	}

	public boolean accept(ServiceServer server) {
		return server.getServerState() ==ServiceServer.STATE_STOPPED;
	}

	public abstract void perform(ServiceServer server);

	public void run() {
		Iterator iterator = getStructuredSelection().iterator();
		Object obj = iterator.next();
		if (obj instanceof ServiceServer) {
			ServiceServer server = (ServiceServer) obj;
			if (accept(server))
				perform(server);
			selectionChanged(getStructuredSelection());
		}
	}

	public void selectionChanged(IStructuredSelection sel) {
		if (sel.isEmpty()) {
			setEnabled(false);
			return;
		}
		boolean enabled = false;
		Iterator iterator = sel.iterator();
		while (iterator.hasNext()) {
			Object obj = iterator.next();
			if (obj instanceof ServiceServer) {
				ServiceServer server = (ServiceServer) obj;
				if (accept(server))
					enabled = true;
			} else {
				setEnabled(false);
				return;
			}
		}
		setEnabled(enabled);
	}
}
