package webx.studio.projectcreation.ui.pages;


import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;

import webx.studio.projectcreation.ui.ProjectCreationPlugin;
import webx.studio.projectcreation.ui.structure.BundleWarRootNode;
import webx.studio.projectcreation.ui.structure.ChildNode;
import webx.studio.projectcreation.ui.structure.DeployRootNode;
import webx.studio.projectcreation.ui.structure.IStructureRootNode;

public class ProjectStructureItemCheckListener implements ICheckStateListener {

	private CheckboxTreeViewer viewer;

	public ProjectStructureItemCheckListener(CheckboxTreeViewer viewer) {
		this.viewer = viewer;
	}


	public void checkStateChanged(CheckStateChangedEvent event) {

		Object obj = event.getElement();
		if (obj == null)
			return;
		boolean checked = event.getChecked();
		if (obj instanceof BundleWarRootNode || obj instanceof DeployRootNode) {

			MessageDialog.openWarning(
					ProjectCreationPlugin.getActiveWorkbenchShell(),
					"Operation is forbidden", "This item must be installed!");
			this.viewer.setChecked(obj, true);
			return;
		}

		if (obj instanceof ChildNode) {
			Object parent = ((ChildNode) obj).getParent();
			if (parent instanceof BundleWarRootNode
					|| parent instanceof DeployRootNode) {
				MessageDialog.openWarning(
						ProjectCreationPlugin.getActiveWorkbenchShell(),
						"Operation is forbidden",
						"This item must be installed!");
				this.viewer.setChecked(obj, true);
				return;
			}
		}
		if (obj instanceof IStructureRootNode) {
			synchChildren((IStructureRootNode) obj, checked);
		} else if (obj instanceof ChildNode) {
			synchParents((ChildNode) obj, checked);
		}
	}

	public void synchChildren(IStructureRootNode parent, boolean checked) {
		for (Object obj : parent.getChildren()) {
			this.viewer.setChecked(obj, checked);
		}
	}

	public void synchParents(ChildNode child, boolean checked) {
		IStructureRootNode parent = child.getParent();
		ChildNode[] children = parent.getChildren();
		if (children.length == 1) {
			this.viewer.setChecked(parent, checked);
		}
		for (ChildNode cn : children) {
			if (cn == child) {
				continue;
			}
			if (this.viewer.getChecked(cn) != checked) {
				this.viewer.setGrayChecked(parent, true);
				return;
			}
		}
		this.viewer.setGrayChecked(parent, false);
		this.viewer.setChecked(parent, checked);
	}
}
