package webx.studio.server.ui.viewers;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.FilteredTree;

/**
 * @author zhihua.hanzh
 *
 */
public abstract class AbstractTreeComposite extends Composite {
	
	protected FilteredTree tree;
	protected TreeViewer treeViewer;
	protected Label description;

	public AbstractTreeComposite(Composite parent) {
		super(parent, SWT.NONE);

		createWidgets();
	}

	protected void createWidgets() {
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 2;
		setLayout(layout);
		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label label = new Label(this, SWT.WRAP);
		label.setText(getTitleLabel());
		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);
		data.horizontalSpan = 2;
		label.setLayoutData(data);

		createTree();
		data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 2;
		tree.setLayoutData(data);

		treeViewer = tree.getViewer();
		treeViewer.setSorter(new ViewerSorter());
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection s = (IStructuredSelection) event.getSelection();
				Object element = s.getFirstElement();
				if (treeViewer.isExpandable(element))
					treeViewer.setExpandedState(element, !treeViewer.getExpandedState(element));
		    }
		});

		tree.forceFocus();
	}

	protected void createTree() {
		tree = new FilteredTree(this, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE, new ServerPatternFilter());
	}

	protected abstract String getTitleLabel();

	protected boolean hasDescription() {
		return true;
	}

	protected void setDescription(String text) {
		if (description != null && text != null)
			description.setText(text);
	}

	protected TreeViewer getTreeViewer() {
		return treeViewer;
	}

	protected Object getSelection(ISelection sel2) {
		IStructuredSelection sel = (IStructuredSelection) sel2;
		return sel.getFirstElement();
	}

	public void refresh() {
		treeViewer.refresh();
	}

	public void refresh(Object obj) {
		treeViewer.refresh(obj);
	}

	public void remove(Object obj) {
		treeViewer.remove(obj);
	}

}
