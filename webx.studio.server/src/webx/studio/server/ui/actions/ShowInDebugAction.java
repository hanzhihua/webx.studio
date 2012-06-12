package webx.studio.server.ui.actions;


import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.IDebugView;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.IViewDescriptor;
import org.eclipse.ui.views.IViewRegistry;

import webx.studio.server.Trace;
import webx.studio.server.core.Server;


public class ShowInDebugAction  extends AbstractServerAction {
	/**
	 * ShowInConsoleAction constructor.
	 *
	 * @param sp a selection provider
	 */
	public ShowInDebugAction(ISelectionProvider sp) {
		super(sp, "Console!");

		IViewRegistry reg = PlatformUI.getWorkbench().getViewRegistry();
		IViewDescriptor desc = reg.find(IDebugUIConstants.ID_DEBUG_VIEW);
		setText(desc.getLabel());
		setImageDescriptor(desc.getImageDescriptor());
	}

	public boolean accept(Server server) {
		return server.getLaunch() != null;
	}

	public void perform(Server server) {
		try {
			ILaunch launch = server.getLaunch();
			selectProcess(launch.getProcesses()[0]);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error showing in debug", e);
		}
	}

	protected void selectProcess(IProcess process) {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow() ;
		if (window != null) {
			IWorkbenchPage page = window.getActivePage();
			if (page != null) {
				IWorkbenchPart part = page.findView(IDebugUIConstants.ID_DEBUG_VIEW);
				if (part == null) {
					try {
						part = page.showView(IDebugUIConstants.ID_DEBUG_VIEW);
					} catch (PartInitException e) {
						Trace.trace(Trace.SEVERE, "Could not open debug view");
					}
				}
				if (part != null) {
					IDebugView view = (IDebugView)part.getAdapter(IDebugView.class);
					if (view != null) {
						view.setFocus();
						Viewer viewer = view.getViewer();
						if (viewer != null) {
							viewer.setSelection(new StructuredSelection(process));
						}
					}
				}
			}
		}
	}
}