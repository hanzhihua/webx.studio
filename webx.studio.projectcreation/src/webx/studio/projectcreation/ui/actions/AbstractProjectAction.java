package webx.studio.projectcreation.ui.actions;

import java.util.Iterator;


import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.SelectionProviderAction;

import webx.studio.projectcreation.ui.core.JejuProject;

/**
 *  @author zhihua.han@alibaba-inc.com
 *
 */
public abstract class AbstractProjectAction extends SelectionProviderAction {
	protected Shell shell;

	public AbstractProjectAction(ISelectionProvider selectionProvider, String text) {
		this(null, selectionProvider, text);
	}

	public AbstractProjectAction(Shell shell, ISelectionProvider selectionProvider, String text) {
		super(selectionProvider, text);
		this.shell = shell;
		setEnabled(false);
	}

	public abstract void perform(JejuProject project);

	public void run() {
		Iterator iterator = getStructuredSelection().iterator();
		Object obj = iterator.next();
		if (obj instanceof JejuProject) {
			JejuProject project = (JejuProject) obj;
			perform(project);
			selectionChanged(getStructuredSelection());
		}
	}

	public void selectionChanged(IStructuredSelection sel) {
		if (sel.isEmpty()) {
			setEnabled(false);
			return;
		}
		boolean enabled = false;
		Iterator iterator = sel.iterator();
		while (iterator.hasNext()) {
			Object obj = iterator.next();
			if (obj instanceof JejuProject) {
				JejuProject server = (JejuProject) obj;
				enabled = true;
			} else {
				setEnabled(false);
				return;
			}
		}
		setEnabled(enabled);
	}
}