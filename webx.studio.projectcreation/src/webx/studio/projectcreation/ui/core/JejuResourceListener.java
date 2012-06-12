package webx.studio.projectcreation.ui.core;

import java.util.List;


import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IStartup;

import webx.studio.projectcreation.ui.ProjectCreationPlugin;
import webx.studio.utils.ProjectUtil;

public class JejuResourceListener implements IStartup,
		IResourceChangeListener {

	private static JejuResourceListener INSTANCE;

	public void earlyStartup() {
		INSTANCE = this;
		startListening();
	}

	public static JejuResourceListener getInstance() {
		return INSTANCE;
	}

	private void startListening() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(
				this,
				IResourceChangeEvent.POST_CHANGE
						| IResourceChangeEvent.PRE_CLOSE
						| IResourceChangeEvent.PRE_DELETE);
	}

	public void resourceChanged(IResourceChangeEvent event) {

		try {
			switch (event.getType()) {
//			case IResourceChangeEvent.PRE_CLOSE:
			case IResourceChangeEvent.PRE_DELETE: {
				IResource resource = event.getResource();
				if (resource instanceof IProject) {
					IProject project = (IProject) resource;
					if (!project.hasNature(JejuProjectCore.NATURE_ID))
						return;
					List<String> projectNames = JejuProjectCore
							.removeProject(project.getName());
					boolean flag = ((Workspace) project.getWorkspace())
							.isTreeLocked();
					if (flag) {
						((Workspace) project.getWorkspace())
								.setTreeLocked(!flag);
					}
					for (String projectName : projectNames) {
						IProject p = ProjectUtil.getProject(projectName);
						JejuProjectCoreUtils.removeProjectNature(p,
								JejuProjectCore.NATURE_ID,
								new NullProgressMonitor());
						p.refreshLocal(IResource.DEPTH_INFINITE,
								new NullProgressMonitor());
					}
					if (flag) {
						((Workspace) project.getWorkspace())
								.setTreeLocked(flag);
					}

				}
			}
			}
		} catch (CoreException ex) {
			ProjectCreationPlugin.logThrowable(ex);
		}
	}

}
