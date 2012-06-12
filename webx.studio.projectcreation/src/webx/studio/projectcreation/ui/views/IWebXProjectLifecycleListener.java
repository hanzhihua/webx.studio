package webx.studio.projectcreation.ui.views;

import webx.studio.projectcreation.ui.core.JejuProject;

public interface IWebXProjectLifecycleListener {

	void projectAdded(JejuProject project);
	void projectChanged(JejuProject project);
	void projectRemoved(JejuProject project);

	void reloadProjects();

}
