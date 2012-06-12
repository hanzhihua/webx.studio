package webx.studio.projectcreation.ui.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.List;


import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import webx.studio.projectcreation.ui.ProjectCreationPlugin;
import webx.studio.projectcreation.ui.StructureAnalyze;
import webx.studio.projectcreation.ui.core.JejuProject;
import webx.studio.projectcreation.ui.core.JejuProjectCore;
import webx.studio.projectcreation.ui.core.JejuProjectCoreUtils;
import webx.studio.projectcreation.ui.preference.PreferenceConstants;
import webx.studio.utils.JdtUtil;
import webx.studio.utils.ProgressUtil;
import webx.studio.utils.ProjectUtil;
import webx.studio.utils.UIUtil;

/**
 * @author zhihua.hanzh
 *
 */
public class ConvertToWebXProjectAction implements IObjectActionDelegate {

	private IProject project;

	public ConvertToWebXProjectAction() {
		// TODO Auto-generated constructor stub
	}

	public void run(IAction action) {
		if (project == null || !project.isOpen())
			return;
		if (JejuProjectCoreUtils.hasJejuNature(project)) {
			return;
		}
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				try {
					monitor.beginTask("Convert the project to Jeju project...", IProgressMonitor.UNKNOWN);
					String webRootArray = ProjectCreationPlugin.getDefault().getPreferenceStore().getString(PreferenceConstants.WEB_ROOT_ARRAY);
					String webappdir = ProjectUtil
							.detectDefaultWebappdir(project,webRootArray);
					if (webappdir == null || webappdir.trim().length() == 0) {
						MessageDialog
								.openWarning(
										ProjectCreationPlugin
												.getActiveWorkbenchShell(),
										"It failed to covert project to Jeju project!",
										"This project isn't a web project,\r\n beacause it desn't contain ["+webRootArray+"]");
						return;
					}
					IJavaProject javaProject = JdtUtil.getJavaProject(project);
					if (javaProject == null) {
						MessageDialog
								.openWarning(
										ProjectCreationPlugin
												.getActiveWorkbenchShell(),
										"It failed to covert project to Jeju project!",
										"This project isn't a java project");
						return;
					}

					String warEclipseProjectName = project.getName();

					//fix a bug@JEJU-38
//					String name = StringUtils.split(warEclipseProjectName,"-")[0];
					String name = warEclipseProjectName;
					List<IJavaProject> dependJavaProjects = JdtUtil
							.getAllDependingJavaProjects(javaProject);

					JejuProject webxProject = new JejuProject(name);
					webxProject.setWarProjectName(warEclipseProjectName);
					webxProject.addProjectName(warEclipseProjectName);
					for (IJavaProject p : dependJavaProjects) {
						webxProject.addProjectName(p.getProject().getName());
					}
					webxProject.setWebRoot(webappdir);
					StructureAnalyze analyze = new StructureAnalyze(project.getLocation().toOSString());
					try {
						analyze.analyzeFileStructure();
						if(analyze.isStandard()){
							String tmp = StringUtils.split(warEclipseProjectName,"-")[0];
							tmp = StringUtils.removeEnd(tmp,".bundle.war");
							tmp = StringUtils.removeEnd(tmp,".bundle.web");
							tmp = StringUtils.removeEnd(tmp,".bundle");
							tmp = StringUtils.removeEnd(tmp,".war");
							tmp = StringUtils.removeEnd(tmp,".web");
							for(String deployEclipseProjectName:analyze.guessDeployEclipseProjectName(tmp)){
								if(ProjectUtil.getProject(deployEclipseProjectName) != null){
									webxProject.addProjectName(deployEclipseProjectName);
									break;
								}
							}
							webxProject.setWebxVersion(StringUtils.trimToEmpty(analyze.getWebxVersion()));
						}
					} catch (Exception e) {
						ProjectCreationPlugin.logThrowable(e);
					}

					JejuProjectCore.addWebXProject(webxProject);
					ResourcesPlugin
							.getWorkspace()
							.getRoot()
							.refreshLocal(IResource.DEPTH_INFINITE,
									ProgressUtil.getMonitorFor(null));

					UIUtil.refreshPackageExplorer();

				} catch (Exception e) {
					throw new InvocationTargetException(e);
				}finally{
					monitor.done();
				}
			}
		};

		try {
			new ProgressMonitorDialog(
					ProjectCreationPlugin.getActiveWorkbenchShell()).run(false,
					false, op);
		} catch (InvocationTargetException e) {
			ProjectCreationPlugin.logThrowable(e);
			Throwable t = e.getTargetException();
			MessageDialog.openError(
					ProjectCreationPlugin.getActiveWorkbenchShell(),
					"It failed to covert project to WebX project!",
					t.getMessage());
		} catch (Exception e) {
			ProjectCreationPlugin.logThrowable(e);
		}

	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			boolean enabled = true;
			if (((IStructuredSelection) selection).size() != 1) {
				enabled = false;
			} else {
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();
				if (obj instanceof IJavaProject) {
					obj = ((IJavaProject) obj).getProject();
				}
				if (obj instanceof IProject) {
					this.project = (IProject) obj;
					if (!this.project.isOpen()) {
						enabled = false;
					}
				} else {
					enabled = false;
				}
			}
			action.setEnabled(enabled);
		}

	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub

	}

}
