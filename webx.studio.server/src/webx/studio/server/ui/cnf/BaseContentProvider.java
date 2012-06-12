package webx.studio.server.ui.cnf;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public abstract class BaseContentProvider implements IStructuredContentProvider {
	public BaseContentProvider() {
		super();
	}

	public void dispose() {
		// do nothing
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// do nothing
	}
}