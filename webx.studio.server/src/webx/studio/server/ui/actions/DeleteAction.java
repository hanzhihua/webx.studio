package webx.studio.server.ui.actions;

import java.util.Iterator;


import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.SelectionProviderAction;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;

import webx.studio.server.ServerPlugin;
import webx.studio.server.core.Server;
import webx.studio.server.core.ServerChild;
import webx.studio.server.ui.cnf.DeleteServerDialog;


public class DeleteAction  extends SelectionProviderAction {

	protected Server[] servers;
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
			if (obj instanceof Server) {
				enabled = true;
			}else if(obj instanceof ServerChild){
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
		Server server = null;
		ServerChild webxProjectModel = null;
		IStructuredSelection sel = getStructuredSelection();
		// filter the selection
		if (!sel.isEmpty()) {
			Iterator iterator = sel.iterator();
			Object obj = iterator.next();
			if (obj instanceof Server){
				server = (Server) obj;
			}else if(obj instanceof ServerChild){
				webxProjectModel = (ServerChild)obj;
			}
			if (iterator.hasNext()) {
				server = null;
				webxProjectModel = null;
			}
		}

		if (server != null){
			if (server.getServerState() != Server.STATE_STOPPED) {
				MessageDialog.openError(ServerPlugin.getActiveWorkbenchShell(),"Delete Server",
						"The server must be stopped before you delete it .");
				return;
			}
			 new DeleteServerDialog(shell,server).open();
		}else if(webxProjectModel != null){
			new DeleteServerDialog(shell,webxProjectModel).open();
		}


	}

	
}