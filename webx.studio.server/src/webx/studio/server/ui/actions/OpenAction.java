package webx.studio.server.ui.actions;


import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;

import webx.studio.server.ServerPlugin;
import webx.studio.server.core.Server;
import webx.studio.server.ui.editor.IServerEditorInput;
import webx.studio.server.ui.editor.ServerEditorInput;


public class OpenAction  extends AbstractServerAction {

	public OpenAction(ISelectionProvider sp) {
		super(sp, "Open");
	}

	public void perform(Server server) {
		try {
			editServer(((Server)server).getId());
		} catch (Exception e) {
			ServerPlugin.logError(e);
		}
	}

	public boolean accept(Server server) {
		return true;
	}

	protected void editServer(String serverId) {
		if (serverId == null)
			return;

		IWorkbenchWindow workbenchWindow = ServerPlugin.getInstance().getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = workbenchWindow.getActivePage();

		try {
			IServerEditorInput input = new ServerEditorInput(serverId);
			page.openEditor(input, IServerEditorInput.EDITOR_ID);
		} catch (Exception e) {
			ServerPlugin.logError(e);
		}
	}

	public static void open(Server server){
//		if(server.canStart(null).isOK()){
			String serverId = ((Server)server).getId();
			if (serverId == null)
				return;
//			ServerPlugin.logError(new Exception("just a test"));	
			IWorkbenchWindow workbenchWindow = ServerPlugin.getInstance().getWorkbench().getActiveWorkbenchWindow();
			IWorkbenchPage page = workbenchWindow.getActivePage();
			try {
				IServerEditorInput input = new ServerEditorInput(serverId);
				page.openEditor(input, IServerEditorInput.EDITOR_ID);
			} catch (Exception e) {
				ServerPlugin.logError(e);
			}
//		}
	}
}