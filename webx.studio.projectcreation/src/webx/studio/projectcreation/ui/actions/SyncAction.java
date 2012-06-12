package webx.studio.projectcreation.ui.actions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;

import webx.studio.projectcreation.ui.ProjectCreationPlugin;
import webx.studio.projectcreation.ui.core.JejuProject;
import webx.studio.projectcreation.ui.core.JejuProjectCore;
import webx.studio.projectcreation.ui.core.JejuProjectCoreUtils;
import webx.studio.projectcreation.ui.core.JejuProjectResourceManager;
import webx.studio.utils.UIUtil;

/**
 * @author zhihua.hanzh
 * 
 */
public class SyncAction extends Action {

	public SyncAction() {
		super();
		setText("Sync");
	}

	public void run() {

		Job job = new WorkspaceJob("Synchronize WebX project model!") {

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor)
					throws CoreException {
				JejuProjectResourceManager.getInstance().reload();

				List<JejuProject> projects = JejuProjectCore
						.getWebXProjectList();

				Set<String> eclipseProjectNames = new HashSet<String>();

				for (JejuProject webxProject : projects) {
					eclipseProjectNames.addAll(webxProject.getProjectNames());
				}

				try {
					for (IProject project : ResourcesPlugin.getWorkspace()
							.getRoot().getProjects()) {
						try {
							if (eclipseProjectNames.contains(project.getName())) {
								JejuProjectCoreUtils.addJejuProjectNature(
										project, monitor);
							} else {
								JejuProjectCoreUtils.removeJejuProjectNature(
										project, monitor);
							}
						} catch (Exception e) {
							ProjectCreationPlugin.logThrowable(e);
						}
					}
					for (IProject project : ResourcesPlugin.getWorkspace()
							.getRoot().getProjects()) {
						project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
					}

				} catch (Exception e) {
					ProjectCreationPlugin.logThrowable(e);
				}
				UIUtil.refreshPackageExplorer();
				return Status.OK_STATUS;
			}

		};
		job.schedule();

	}
}
