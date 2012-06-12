package webx.studio.projectcreation.ui.views;

import java.util.ArrayList;
import java.util.List;


import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import webx.studio.projectcreation.ui.core.JejuProject;
import webx.studio.projectcreation.ui.core.JejuProjectCore;
import webx.studio.projectcreation.ui.core.JejuProjectResourceManager;

public class ProjectContentProvider implements IStructuredContentProvider,
		ITreeContentProvider {

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

	public Object[] getElements(Object inputElement) {
		return JejuProjectCore.getWebXProjectList().toArray(new JejuProject[0]);
	}

	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof JejuProject){
//			return ((WebXProject)parentElement).getProjectNames().toArray(new String[0]);
			List<WebxProjectChildNode> children = new ArrayList<WebxProjectChildNode>();
			for(String str:((JejuProject)parentElement).getProjectNames()){
				children.add(new WebxProjectChildNode(((JejuProject)parentElement), str));
			}
			return children.toArray(new WebxProjectChildNode[0]);
		}
		if(parentElement instanceof WebxProjectChildNode){
			return new Object[0];
		}
		return JejuProjectCore.getWebXProjectList().toArray(new JejuProject[0]);
	}

	public Object getParent(Object element) {
		if (element instanceof JejuProject)
			return null;
		if (element instanceof WebxProjectChildNode) {
			return ((WebxProjectChildNode) element).getWebxProject();

		}
		return null;

	}

	public boolean hasChildren(Object element) {
		if (element instanceof WebxProjectChildNode)
			return false;
		if (element instanceof JejuProject)
			return ((JejuProject) element).getProjectNames().size() > 0;
		return false;
	}

}
