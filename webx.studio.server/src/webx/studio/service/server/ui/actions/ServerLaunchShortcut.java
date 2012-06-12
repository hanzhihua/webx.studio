package webx.studio.service.server.ui.actions;

import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;

public class ServerLaunchShortcut implements ILaunchShortcut {

	public void launch(ISelection selection, String mode) {
		RunOnBoortActionDelegate ros = new RunOnBoortActionDelegate();
		ros.setLaunchMode(mode);
		IAction action = new Action() {
		};
		ros.selectionChanged(action, selection);
		ros.run(action);
	}

	public void launch(IEditorPart editor, String mode) {
	}

}
