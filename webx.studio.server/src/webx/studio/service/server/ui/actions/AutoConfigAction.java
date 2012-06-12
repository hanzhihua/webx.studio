package webx.studio.service.server.ui.actions;

import hidden.edu.emory.mathcs.backport.java.util.Arrays;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.SelectionProviderAction;

import webx.studio.server.ServerPlugin;
import webx.studio.service.server.core.ServiceServer;
import webx.studio.service.server.core.ServiceServerChild;
import webx.studio.service.server.core.ServiceServerUtil;

public class AutoConfigAction  extends SelectionProviderAction {

	public AutoConfigAction(Shell shell, ISelectionProvider selectionProvider) {
		super(selectionProvider, "Auto Config");
		this.shell = shell;
		setEnabled(false);
	}

	private Shell shell;

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
				enabled = true;
			}
			if (iterator.hasNext()) {
				setEnabled(false);
				return;
			}
		}
		setEnabled(enabled);
	}

	public void run() {

		IStructuredSelection sel = getStructuredSelection();
		List<ServiceServerChild> children = new ArrayList<ServiceServerChild>();
		if (!sel.isEmpty()) {
			Iterator iterator = sel.iterator();
			Object obj = iterator.next();
			if (obj instanceof ServiceServer){
				ServiceServer server = (ServiceServer)obj;
				children.addAll(Arrays.asList(ServiceServerUtil.getServerChildren(server)));
			}
		}
		if(children.size() == 0){
			MessageDialog.openError(ServerPlugin.getActiveWorkbenchShell(), "Run Auto config",
			" you can't run Auto config without any projects. ");
			return;
		}
		new AutoConfig(children).run();

	}


}
