/**
 * Project: webx3.projectcreation
 *
 * File Created at 2011-4-26
 * $Id: ProjectCreationWizard.java 155511 2012-03-15 01:19:27Z zhihua.hanzh $
 *
 * Copyright 1999-2100 Alibaba.com Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Alibaba Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Alibaba.com.
 */
package webx.studio.projectcreation.ui;

import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;


import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.Model;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;

import webx.studio.maven.MavenExecuteException;
import webx.studio.projectcreation.ui.core.JejuProject;
import webx.studio.projectcreation.ui.core.JejuProjectCore;
import webx.studio.projectcreation.ui.pages.ProjectInformationWizardPage;
import webx.studio.projectcreation.ui.pages.ProjectStructureWizardPage;
import webx.studio.projectcreation.ui.project.MavenHelper;
import webx.studio.projectcreation.ui.project.ProjectHelper;
import webx.studio.projectcreation.ui.structure.WebxStructureModel;
import webx.studio.utils.PathUtil;
import webx.studio.utils.UIUtil;

/**
 *
 * @author zhihua.hanzh
 */
public class ProjectCreationWizard extends BaseWizard {

	private ProjectInformationWizardPage projectInfomationWizardPage;
	private ProjectStructureWizardPage projectStructureWizardPage;

	private static final boolean _DEBUG = ProjectCreationPlugin
			.isDebug("ProjectCreationWizard");
	private long time;

	public ProjectCreationWizard() {

		setWindowTitle("Jeju Project Creation");
		setDefaultPageImageDescriptor(ProjectCreationUIImages.DESC_WIZ_PROJECT);

	}

	@Override
	public BaseWizardPage[] getWizardPages() {
		projectInfomationWizardPage = new ProjectInformationWizardPage();
		projectStructureWizardPage = new ProjectStructureWizardPage();
		return new BaseWizardPage[] { projectInfomationWizardPage,
				projectStructureWizardPage };
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		super.init(workbench, selection);
		setDialogSettings(ProjectCreationPlugin.getDefault()
				.getDialogSettings());
		setNeedsProgressMonitor(true);
	}

	@Override
	public boolean performFinish() {
		if (_DEBUG)
			time = System.currentTimeMillis();
		final NameRule nameRule = new NameRule(
				projectInfomationWizardPage.getProjectGroupId(),
				projectInfomationWizardPage.getProjectName());
		final Model parentModel = projectInfomationWizardPage
				.getModel(nameRule);
		projectStructureWizardPage.updateModel(parentModel, nameRule);
		final IPath location = Path.fromOSString(projectInfomationWizardPage
				.getPathValue());

		final JejuProject webxProject = new JejuProject(
				nameRule.getProjectName());
		webxProject
				.setWebxVersion(projectInfomationWizardPage.getWebxVersion());
		final WebxStructureModel webxStructureModel = projectStructureWizardPage
				.getWebxStructureModel();
		final String settingFilePath = projectInfomationWizardPage
				.getSeetingFilePath();
		final String antxPropertiesPath = projectInfomationWizardPage
				.getAntxPropertiesPath();
		final String autoconfigCharset = projectInfomationWizardPage.getAutoconfigCharset();
		webxProject.setSettingFile(StringUtils.trimToEmpty(settingFilePath));
		webxProject.setAntxPropertiesFile(StringUtils
				.trimToEmpty(antxPropertiesPath));
		webxProject.setAutoconfigCharset(StringUtils.trimToEmpty(autoconfigCharset));
		webxProject.setWebRoot(getWebRoot(location, nameRule));
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			protected void execute(IProgressMonitor monitor)
					throws CoreException {
				IOConsole mc = getIOConsole();
				IOConsoleOutputStream consoleStream = mc.newOutputStream();

				System.setIn(mc.getInputStream());
				System.setErr(new PrintStream(consoleStream));
				System.setOut(new PrintStream(consoleStream));
				try {
					if (_DEBUG) {
						time = System.currentTimeMillis();
					}
					monitor.beginTask(
							"Create a Jeju project[ "
									+ nameRule.getProjectName() + " ]", 20);
					List<IProject> projects = ProjectHelper.createProject(
							location, parentModel, webxStructureModel,
							nameRule, new SubProgressMonitor(monitor, 10));
					if (_DEBUG) {
						System.out.println("Creating eclipse project costs "+(System.currentTimeMillis() - time)
								/ 1000 + " second.");
						time = System.currentTimeMillis();
					}

					if (JavaCore
							.getClasspathVariable(ProjectCreationConstants.M2_REPO_KEY) == null) {
						JavaCore.setClasspathVariable(
								ProjectCreationConstants.M2_REPO_KEY, new Path(
										MavenHelper.getLocalRepository()));
					}
					File parentPomFile = new File(location.toFile(),
							nameRule.getProjectName() + "/pom.xml");
					MavenHelper.generateEclipse(parentPomFile.getParent(),
							settingFilePath,
							new SubProgressMonitor(monitor, 10));
					if (_DEBUG)
						System.out.println("Running maven command costs "+(System.currentTimeMillis() - time)
								/ 1000 + " second.");
					for (IProject project : projects) {
						webxProject.getProjectNames().add(project.getName());
					}
					Job job = new Job("Save Metadata") {

						protected IStatus run(IProgressMonitor monitor) {
							JejuProjectCore.addWebXProject(webxProject);
							return Status.OK_STATUS;
						}

					};
					job.schedule();
				} catch (Exception e) {
					throw new CoreException(new Status(IStatus.ERROR,
							ProjectCreationPlugin.PLUGIN_ID, -1,
							e.getMessage(), e));
				}finally{
					System.setIn(System.in);
					System.setErr(System.err);
					System.setOut(System.out);
					try {
						// mc.getInputStream().close();
						consoleStream.close();
					} catch (Exception e) {
						ProjectCreationPlugin.logThrowable(e);
					}
				}
			}
		};

		try {
			getContainer().run(true, true, op);
			UIUtil.refreshPackageExplorer();
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			e.printStackTrace();

			Throwable t = e.getTargetException();
			if (t instanceof CoreException) {
				CoreException ce = (CoreException) t;
				Throwable tmpt = ce.getStatus().getException();
				String errMessage = null;
				if (tmpt instanceof MavenExecuteException) {
					errMessage = "Maven command execute failed!";
				}
				ErrorDialog.openError(
						getShell(),
						NLS.bind("Failed to create project \"{0}\"",
								nameRule.getProjectName()), errMessage,
						((CoreException) t).getStatus());
			} else {
				MessageDialog.openError(getShell(), "Creation Problems",
						NLS.bind("Internal error: {0}", t.getMessage()));
			}
			ProjectCreationPlugin.logThrowable(t);
			return false;
		}
		return true;

	}

	private String getWebRoot(IPath location,NameRule nameRule){
		IPath bundleWarPath = location.append(nameRule.getProjectName())
		.append("bundle/war/src/main/webapp");
//		return PathUtil.getLocation(bundleWarPath);
		return bundleWarPath.toOSString();
	}


	private static final String CONSOLE_NAME = " Jeju Project Creation Console ";
	private IOConsole getIOConsole() {
		org.eclipse.debug.ui.console.IConsole.class.toString();

		IOConsole mc = null;
		IConsoleManager consoleManager = ConsolePlugin.getDefault()
				.getConsoleManager();
		IConsole[] consoles = consoleManager.getConsoles();
		if (consoles != null) {
			for (IConsole console : consoles) {
				if (CONSOLE_NAME.equalsIgnoreCase(console.getName())) {
					mc = (IOConsole) console;
					break;
				}
			}
		}
		if (mc == null) {
			mc = new IOConsole(CONSOLE_NAME, JavaPlugin.getDefault()
					.getWorkbench().getSharedImages().getImageDescriptor(
							"IMG_OBJS_TASK_TSK"));
			ConsolePlugin.getDefault().getConsoleManager().addConsoles(
					new IConsole[] { mc });

		}
		ConsolePlugin.getDefault().getConsoleManager().showConsoleView(mc);
		mc.clearConsole();
		return mc;
	}
}
