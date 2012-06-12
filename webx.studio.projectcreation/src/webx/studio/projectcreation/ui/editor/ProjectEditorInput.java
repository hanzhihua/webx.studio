package webx.studio.projectcreation.ui.editor;


import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;

import webx.studio.projectcreation.ui.core.JejuProject;
import webx.studio.projectcreation.ui.core.JejuProjectCore;

public class ProjectEditorInput implements IProjectEditorInput{

	private String projectId;

	public ProjectEditorInput(String projectId){
		this.projectId = projectId;
	}

	public boolean exists() {
		if (projectId != null && JejuProjectCore.findWebXProject(projectId) == null)
			return false;

		return true;
	}

	public ImageDescriptor getImageDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		JejuProject webxProject = JejuProjectCore.findWebXProject(projectId);
		return webxProject.getName();
	}

	public IPersistableElement getPersistable() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getToolTipText() {
		String s = null;
		if (projectId != null) {
			JejuProject webxProject = JejuProjectCore.findWebXProject(projectId);
			if (webxProject != null) {
				s= webxProject.getName();
			}
		}
		if (s == null)
			s = "";
		return s;
	}

	public Object getAdapter(Class adapter) {
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	public String getProjectId() {
		return projectId;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((projectId == null) ? 0 : projectId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProjectEditorInput other = (ProjectEditorInput) obj;
		if (projectId == null) {
			if (other.projectId != null)
				return false;
		} else if (!projectId.equals(other.projectId))
			return false;
		return true;
	}

}
