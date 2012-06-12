package webx.studio.projectcreation.ui.actions;


import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import webx.studio.projectcreation.ui.ProjectCreationPlugin;
import webx.studio.utils.UIUtil;


public class ShowServerViewAction  extends Action {

	public ShowServerViewAction(){
		super();
		setText("Show Jeju Server View");
	}

	public void run(){
//		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow() ;
//		if (window != null) {
//			IWorkbenchPage page = window.getActivePage();
//			if (page != null) {
//				IWorkbenchPart part = page.findView("jeju.server.ui.ServersView");
//				if (part == null) {
//					try {
//						part = page.showView("jeju.server.ui.ServersView");
//					} catch (PartInitException e) {
//						ProjectCreationPlugin.logThrowable(e);
//					}
//				}
//				if (part != null) {
//					page.activate(part);
////					 view = (IConsoleView) part.getAdapter(IConsoleView.class);
////					if (view != null) {
////						view.setFocus();
////						view.display(console);
////					}
//				}
//			}
//		}
		UIUtil.showView("jeju.server.ui.ServersView");
	}

}
