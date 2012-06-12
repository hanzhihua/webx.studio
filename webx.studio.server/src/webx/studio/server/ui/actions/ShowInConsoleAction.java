package webx.studio.server.ui.actions;


import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.views.IViewDescriptor;
import org.eclipse.ui.views.IViewRegistry;

import webx.studio.server.Trace;
import webx.studio.server.core.Server;


public class ShowInConsoleAction  extends AbstractServerAction {
	/**
	 * ShowInConsoleAction constructor.
	 *
	 * @param sp a selection provider
	 */
	public ShowInConsoleAction(ISelectionProvider sp) {
		super(sp, "Console");

		IViewRegistry reg = PlatformUI.getWorkbench().getViewRegistry();
		IViewDescriptor desc = reg.find(IConsoleConstants.ID_CONSOLE_VIEW);
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
			Trace.trace(Trace.SEVERE, "Error opening console", e);
		}
	}

	protected void selectProcess(IProcess process) {
		// see bug 250999 - debug UI must be loaded before looking for debug consoles
		org.eclipse.debug.ui.console.IConsole.class.toString();

		IConsoleManager consoleManager = ConsolePlugin.getDefault().getConsoleManager();
		IConsole[] consoles = consoleManager.getConsoles();
		int size = consoles.length;
		IConsole console = null;
		for (int i = 0; i < size; i++) {
			if (consoles[i] instanceof org.eclipse.debug.ui.console.IConsole) {
				org.eclipse.debug.ui.console.IConsole con = (org.eclipse.debug.ui.console.IConsole) consoles[i];
				if (process.equals(con.getProcess()))
					console = consoles[i];
			}
		}

		if (console == null)
			return;

		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow() ;
		if (window != null) {
			IWorkbenchPage page = window.getActivePage();
			if (page != null) {
				IWorkbenchPart part = page.findView(IConsoleConstants.ID_CONSOLE_VIEW);
				if (part == null) {
					try {
						part = page.showView(IConsoleConstants.ID_CONSOLE_VIEW);
					} catch (PartInitException e) {
						Trace.trace(Trace.SEVERE, "Could not open console view");
					}
				}
				if (part != null) {
					page.activate(part);
					IConsoleView view = (IConsoleView) part.getAdapter(IConsoleView.class);
					if (view != null) {
						view.setFocus();
						view.display(console);
					}
				}
			}
		}
	}
}