/**
 * Project: webx3.projectcreation
 *
 * File Created at 2011-7-15
 * $Id: ProjectHelper.java 115747 2011-10-10 01:43:59Z zhihua.hanzh $
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
package webx.studio.projectcreation.ui.project;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.osgi.util.NLS;

import webx.studio.projectcreation.ui.GeneralUtils;
import webx.studio.projectcreation.ui.NameRule;
import webx.studio.projectcreation.ui.ProjectCreationConstants;
import webx.studio.projectcreation.ui.ProjectCreationPlugin;
import webx.studio.projectcreation.ui.structure.AbstractStructureNodeModel;
import webx.studio.projectcreation.ui.structure.BundleWarNodeModel;
import webx.studio.projectcreation.ui.structure.DeployNodeModel;
import webx.studio.projectcreation.ui.structure.ProjectFolder;
import webx.studio.projectcreation.ui.structure.WebStructureNodeModel;
import webx.studio.projectcreation.ui.structure.WebxStructureModel;
import webx.studio.utils.ProgressUtil;

/**
 * TODO Comment of ProjectHelper
 *
 * @author zhihua.hanzh
 */
public abstract class ProjectHelper {

	public static List<IProject> createProject(IPath location,
			Model parentModel, WebxStructureModel structureModel,
			NameRule nameRule, IProgressMonitor monitor) throws Exception {

		final List<IProject> createdProjectList = new ArrayList<IProject>();
		monitor.beginTask("Create parent pom.xml",
				structureModel.getProjectSize() + 1);
		File parentPomFile = new File(location.toFile(),
				nameRule.getProjectName() + "/pom.xml");
		MavenHelper.createParentPom(parentPomFile, parentModel);
		monitor.worked(1);

		List<ModuleProjectInformation> webModuleProjectList = new ArrayList<ModuleProjectInformation>();
		ModuleProjectInformation deployModuleProjectInformation = new ModuleProjectInformation();
		for (AbstractStructureNodeModel nodeModel : structureModel) {
			monitor.subTask(NLS.bind("Creating project {0}",
					nameRule.getModelArtifactId(nodeModel)));
			ModuleProjectInformation moduleProjectInformation = new ModuleProjectInformation();
			moduleProjectInformation.inputName = nodeModel.getName();
			moduleProjectInformation.artfiactId = nameRule
					.getModelArtifactId(nodeModel);
			moduleProjectInformation.projectName = moduleProjectInformation.artfiactId
					+ "-" + parentModel.getVersion();
			List<? extends AbstractStructureNodeModel> dependenceNodeModelList = structureModel
					.getDependencyNodeMode(nodeModel);
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(moduleProjectInformation.projectName);
			String projectName = project.getName();
			IProjectDescription description = ResourcesPlugin.getWorkspace()
					.newProjectDescription(projectName);
			IPath projectPath = location.append(nameRule.getProjectName())
					.append(nodeModel.getName().replace('.', '/'));
			moduleProjectInformation.projectDir = projectPath.toFile();
			if (nodeModel instanceof WebStructureNodeModel) {
				webModuleProjectList.add(moduleProjectInformation);
			}
			description.setLocation(projectPath);
			project.create(description, monitor);
			project.open(monitor);
			IFile pomFile = project.getFile("pom.xml");
			MavenHelper.createPom(pomFile,
					nodeModel.getModel(parentModel, nameRule),
					dependenceNodeModelList);
			for (ProjectFolder folder : nodeModel.getProjectFolders()) {
				createFolder(project.getFolder(folder.getPath()), false);
			}

			if (nodeModel instanceof BundleWarNodeModel) {
				final Properties context = new Properties();
				context.setProperty(ProjectCreationConstants.ARTIFACT_ID_KEY,
						parentModel.getArtifactId());
				context.setProperty(ProjectCreationConstants.GROUP_ID_KEY,
						parentModel.getGroupId());
				context.setProperty(ProjectCreationConstants.VERSION_KEY,
						parentModel.getVersion());
				context.setProperty(ProjectCreationConstants.PROJECT_NAME_KEY,
						nameRule.getProjectName());
				context.setProperty(
						ProjectCreationConstants.TEMPLATE_ROOT_KEY,
						GeneralUtils
								.getTemplatesRoot(deployModuleProjectInformation));
				context.setProperty(
						ProjectCreationConstants.ROOT_MODULE_PACKAGE_KEY,
						GeneralUtils.getRootModulePackage(nameRule));
				IPath webapp = project.getFolder(
						AbstractStructureNodeModel.WEBAPP.getPath())
						.getLocation();
				List<String> templateNameList = new ArrayList<String>();
				templateNameList.add("webx.xml");
				templateNameList.add("webx-app.xml");
				templateNameList.add("auto-config.xml");
				templateNameList.add("config.properties");
				templateNameList.add("web.xml");
				templateNameList.add("resources.xml");
				templateNameList.add("HelloWorld.java");
				templateNameList.add("helloWorld.vm");
				try {
					GeneralUtils.processTemplate(
							ProjectCreationPlugin.getWarTemplate(),
							webapp.toFile(), context, templateNameList,
							nameRule, webModuleProjectList,
							deployModuleProjectInformation);
				} catch (Exception e) {
					ProjectCreationPlugin.logThrowable(e);
				}

			}
			if (nodeModel instanceof DeployNodeModel) {
				deployModuleProjectInformation = moduleProjectInformation;
				final Properties context = new Properties();
				List<String> templateNameList = new ArrayList<String>();
				try {
					GeneralUtils.processTemplate(ProjectCreationPlugin
							.getDeployTemplate(), project.getLocation()
							.toFile(), context, templateNameList, nameRule);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
			createdProjectList.add(project);
			monitor.worked(1);
		}
		return createdProjectList;

	}

	public static void createFolder(IFolder folder, boolean derived)
			throws CoreException {
		if (!folder.exists()) {
			IContainer parent = folder.getParent();
			if (parent != null && !parent.exists()) {
				createFolder((IFolder) parent, false);
			}
			folder.create(true, true, null);
		}
		if (folder.isAccessible() && derived) {
			folder.setDerived(true);
		}
	}

	public static void changeWebXVersion(final File topDir,final String settingsFile,
			final String webxVersion) throws Exception {
		if (topDir == null || topDir.isFile())
			return;
		final File totalPomFile = new File(topDir, "pom.xml");
		if (!totalPomFile.exists() || !totalPomFile.isFile())
			return;
		if (StringUtils.isBlank(webxVersion))
			return;
		final Model model = MavenHelper.getModel(totalPomFile);

		final String origWebXVersion = model.getProperties().getProperty(
				"webx3-version");
		if (StringUtils.isBlank(origWebXVersion)
				|| StringUtils.equals(origWebXVersion, webxVersion)) {
			return;
		}
//		Job job = new WorkspaceJob("Change the WebX project version ,from "+origWebXVersion+" to "+webxVersion+", Please wait!") {
//
//			@Override
//			public IStatus runInWorkspace(IProgressMonitor monitor)
//					throws CoreException {
//
//				try {
//					model.getProperties().setProperty("webx3-version",
//							webxVersion);
//					FileWriter sw = new FileWriter(totalPomFile);
//					new MavenXpp3Writer().write(sw, model);
//					MavenHelper.generateEclipse(topDir.getCanonicalPath(),
//							settingsFile, ProgressUtil.getMonitorFor(null));
//					ResourcesPlugin
//							.getWorkspace()
//							.getRoot()
//							.refreshLocal(IResource.DEPTH_INFINITE,
//									ProgressUtil.getMonitorFor(null));
//					return Status.OK_STATUS;
//				} catch (Exception e) {
//					throw new CoreException(new Status(IStatus.ERROR,
//							ProjectCreationPlugin.PLUGIN_ID, -1,
//							e.getMessage(), e));
//				}
//			}
//
//		};
//		job.schedule();
		
		final ProgressMonitorDialog dialog = new ProgressMonitorDialog(ProjectCreationPlugin.getActiveWorkbenchShell());
		dialog.setBlockOnOpen(false);
		dialog.setCancelable(true);
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) {
				try {
					monitor.beginTask("Change the WebX project version ,from "+origWebXVersion+" to "+webxVersion+", Please wait!", 100);
					model.getProperties().setProperty("webx3-version",
							webxVersion);
					FileWriter sw = new FileWriter(totalPomFile);
					new MavenXpp3Writer().write(sw, model);
					monitor.worked(20);
					MavenHelper.generateEclipse(topDir.getCanonicalPath(),
							settingsFile, ProgressUtil.getMonitorFor(null));
					monitor.worked(80);
					ResourcesPlugin
							.getWorkspace()
							.getRoot()
							.refreshLocal(IResource.DEPTH_INFINITE,
									ProgressUtil.getMonitorFor(null));
					monitor.done();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		dialog.run(true, true, runnable);

	}

}
