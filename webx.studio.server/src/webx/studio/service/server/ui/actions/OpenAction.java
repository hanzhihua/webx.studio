package webx.studio.service.server.ui.actions;


import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;

import webx.studio.server.ServerPlugin;
import webx.studio.server.ui.editor.IServerEditorInput;
import webx.studio.service.server.core.ServiceServer;
import webx.studio.service.server.ui.editor.ServiceServerEditorInput;

public class OpenAction extends AbstractServerAction {

	public OpenAction(ISelectionProvider sp) {
		super(sp, "Open");
	}

	public void perform(ServiceServer server) {
		try {
			editServer(server);
		} catch (Exception e) {
			ServerPlugin.logError(e);
		}
	}

	public boolean accept(ServiceServer server) {
		return true;
	}

	protected void editServer(ServiceServer server) {
		if (server == null)
			return;

		IWorkbenchWindow workbenchWindow = ServerPlugin.getInstance()
				.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = workbenchWindow.getActivePage();

		try {
			ServiceServerEditorInput input = new ServiceServerEditorInput(
					server);
			page.openEditor(input, ServiceServerEditorInput.EDITOR_ID);
		} catch (Exception e) {
			ServerPlugin.logError(e);
		}
	}

	public static void open(ServiceServer server) {

		IWorkbenchWindow workbenchWindow = ServerPlugin.getInstance()
				.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = workbenchWindow.getActivePage();
		try {
			ServiceServerEditorInput input = new ServiceServerEditorInput(
					server);
			page.openEditor(input, ServiceServerEditorInput.EDITOR_ID);
		} catch (Exception e) {
			ServerPlugin.logError(e);
		}
		// }
	}

}
