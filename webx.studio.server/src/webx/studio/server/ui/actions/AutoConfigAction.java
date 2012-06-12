package webx.studio.server.ui.actions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.SelectionProviderAction;

import webx.studio.server.ServerPlugin;
import webx.studio.server.core.Server;
import webx.studio.server.core.ServerChild;

public class AutoConfigAction  extends SelectionProviderAction {

	public AutoConfigAction(Shell shell, ISelectionProvider selectionProvider) {
		super(selectionProvider, "Auto Config");
		this.shell = shell;
		setEnabled(false);
	}
	protected Server[] servers;
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
			if (obj instanceof ServerChild) {
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

		Set<String> webxProjectNames = new HashSet<String>();
		IStructuredSelection sel = getStructuredSelection();
		if (!sel.isEmpty()) {
			Iterator iterator = sel.iterator();
			Object obj = iterator.next();
			if (obj instanceof Server){
				webxProjectNames.addAll(Arrays.asList(((Server)obj).getOnlyWebXProjectNames()));
			}else if(obj instanceof ServerChild){
				webxProjectNames.add(((ServerChild)obj).getName());
			}
		}
		if(webxProjectNames.size() == 0){
			MessageDialog.openError(ServerPlugin.getActiveWorkbenchShell(), "Run Auto config",
			" you can't run Auto config without any webx project. ");
			return;
		}
		new AutoConfig(webxProjectNames).run();

	}

}
