package webx.studio.server.ui.actions;


import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import webx.studio.projectcreation.ui.ProjectCreationPlugin;
import webx.studio.utils.UIUtil;

public class ShowProjectViewAction   extends Action {

	public ShowProjectViewAction(){
		super();
		setText("Show Jeju Project View");
	}

	public void run(){
//		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow() ;
//		if (window != null) {
//			IWorkbenchPage page = window.getActivePage();
//			if (page != null) {
//				IWorkbenchPart part = page.findView("jeju.projectcreation.view");
//				if (part == null) {
//					try {
//						part = page.showView("jeju.projectcreation.view");
//					} catch (PartInitException e) {
//						ProjectCreationPlugin.logThrowable(e);
//					}
//				}
//				if (part != null) {
//					page.activate(part);
//
//				}
//			}
//		}
		UIUtil.showView("jeju.projectcreation.view");
	}

}