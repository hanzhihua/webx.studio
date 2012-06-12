package webx.studio.service.server.ui.actions;


import java.util.Iterator;


import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.SelectionProviderAction;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;

import webx.studio.server.ServerPlugin;
import webx.studio.service.server.core.ServiceServer;
import webx.studio.service.server.core.ServiceServerChild;

public class DeleteAction extends SelectionProviderAction {

	protected ServiceServer[] servers;
	private Shell shell;

	public DeleteAction(Shell shell, ISelectionProvider selectionProvider) {
		super(selectionProvider, "Delete");
		this.shell = shell;
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED));
		setActionDefinitionId(IWorkbenchActionDefinitionIds.DELETE);
		setEnabled(false);
	}

	@Override
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
			}else if(obj instanceof ServiceServerChild){
				enabled = true;
			}else {
				setEnabled(false);
				return;
			}
			if (iterator.hasNext()) {
				setEnabled(false);
				return;
			}
		}
		setEnabled(enabled);
	}

	@Override
	public void run() {
		ServiceServer server = null;
		ServiceServerChild child = null;
		IStructuredSelection sel = getStructuredSelection();
		// filter the selection
		if (!sel.isEmpty()) {
			Iterator iterator = sel.iterator();
			Object obj = iterator.next();
			if (obj instanceof ServiceServer){
				server = (ServiceServer) obj;
			}else if(obj instanceof ServiceServerChild){
				child = (ServiceServerChild)obj;
			}
			if (iterator.hasNext()) {
				server = null;
				child = null;
			}
		}

		if (server != null){
			if (server.getServerState() != ServiceServer.STATE_STOPPED) {
				MessageDialog.openError(ServerPlugin.getActiveWorkbenchShell(),"Delete Service Server",
						"The server must be stopped before you delete it .");
				return;
			}
			 new DeleteServerDialog(shell,server).open();
		}else if(child != null){
			new DeleteServerDialog(shell,child).open();
		}


	}
}
