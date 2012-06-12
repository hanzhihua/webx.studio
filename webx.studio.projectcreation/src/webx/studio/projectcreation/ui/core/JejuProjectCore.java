package webx.studio.projectcreation.ui.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.part.FileEditorInput;

import webx.studio.projectcreation.ui.ProjectCreationPlugin;
import webx.studio.projectcreation.ui.StructureAnalyze;
import webx.studio.projectcreation.ui.preference.PreferenceConstants;
import webx.studio.projectcreation.ui.views.IWebXProjectLifecycleListener;
import webx.studio.utils.ProgressUtil;
import webx.studio.utils.ProjectUtil;

public class JejuProjectCore {

	public static final String PERFIX = ProjectCreationPlugin.PLUGIN_ID;

	public static final String NATURE_ID = PERFIX + ".jejunature";

	public static void addWebXProjectLifecycleListener(
			IWebXProjectLifecycleListener listener) {
		JejuProjectResourceManager.getInstance()
				.addWebXProjectLifecycleListener(listener);
	}

	public static void removeWebXProjectLifecycleListener(
			IWebXProjectLifecycleListener listener) {
		JejuProjectResourceManager.getInstance()
				.removeWebXProjectLifecycleListener(listener);
	}

	public static List<String> removeProject(String eclipseProjectName) {
		List<String> affectEclipseProjectNames = new ArrayList<String>();
		JejuProjectResourceManager manager = JejuProjectResourceManager
				.getInstance();
		for (JejuProject webxProject : manager.getProjects()) {
			if (webxProject.contain(eclipseProjectName)) {
				if (webxProject.isDeployProject(eclipseProjectName)
						|| webxProject.isWarProject(eclipseProjectName)) {
					affectEclipseProjectNames = webxProject.getProjectNames();
					manager.removeWebXProject(webxProject);
				} else {
					affectEclipseProjectNames = new ArrayList<String>();
					affectEclipseProjectNames.add(eclipseProjectName);
					webxProject.removeProject(eclipseProjectName);
					manager.updateWebXProject(webxProject);
				}
				break;
			}
		}
		return affectEclipseProjectNames;
	}

	public static List<JejuProject> getWebXProjectList() {
		return JejuProjectResourceManager.getInstance().getProjects();
	}

	public static boolean isWarProject(String eclipseProjectName){
		for(JejuProject jejuProject:getWebXProjectList()){
			if(jejuProject.isWarProject(eclipseProjectName))
				return true;
		}
		return false;
	}

	public static JejuProject getWebXProject(String webxProjectName) {
		return JejuProjectResourceManager.getInstance().getWebXProject(
				webxProjectName);
	}

	public static void saveWebXProject(JejuProject project) {
		project.save();
	}

	public static void deleteWebXProject(JejuProject webxProject) {
		for (String eclipseProjectName : webxProject.getProjectNames()) {
			IProject eclipseProject = ProjectUtil
					.getProject(eclipseProjectName);
			if (eclipseProject == null)
				continue;
			try {
				JejuProjectCoreUtils.removeJejuProjectNature(eclipseProject,
						ProgressUtil.getMonitorFor(null));
			} catch (CoreException e) {
				ProjectCreationPlugin.logThrowable(e);
			}
		}
		webxProject.delete();
	}

	public static void addWebXProject(JejuProject webxProject) {
		for (String eclipseProjectName : webxProject.getProjectNames()) {
			IProject eclipseProject = ProjectUtil
					.getProject(eclipseProjectName);
			if (eclipseProject == null)
				continue;
			try {
				JejuProjectCoreUtils.addJejuProjectNature(eclipseProject,
						ProgressUtil.getMonitorFor(null));
			} catch (CoreException e) {
				ProjectCreationPlugin.logThrowable(e);
			}
		}
		webxProject.save();
	}

	public static JejuProject findWebXProject(String id) {
		return JejuProjectResourceManager.getInstance().findWebXProject(id);
	}

	public static File getTopDir(String webxProjectId) {
		JejuProject webxProject = JejuProjectResourceManager.getInstance()
		.findWebXProject(webxProjectId);
		return getTopDir(webxProject);

	}

	public static File getTopDir(JejuProject webxProject) {
		try {
			if (webxProject == null)
				return null;
			String warEclipseProjectName = webxProject.getWarProjectName();
			if (StringUtils.isBlank(warEclipseProjectName))
				return null;
			StructureAnalyze analyze = new StructureAnalyze(ProjectUtil
					.getProject(warEclipseProjectName).getLocation()
					.toOSString());
			analyze.analyzeFileStructure();
			if (analyze.isStandard()) {
				return analyze.getTopFile();
			}else{
				File warFile = new File(ProjectUtil
						.getProject(warEclipseProjectName).getLocation()
						.toOSString());
				return warFile.getParentFile();
			}
		} catch (Exception e) {
			ProjectCreationPlugin.logThrowable(e);
		}
		return null;
	}

	public static JejuProject getJejuProjectByEclipseProjectName(String eclipseProjectName){
		for(JejuProject jejuProject:getWebXProjectList()){
			if (jejuProject.contain(eclipseProjectName)) {
				return jejuProject;
			}
		}
		return null;
	}

	public static JejuProject getJejuProjectByFileEditorInput(FileEditorInput fileEditorInput){
		String projectName = fileEditorInput.getFile().getProject().getName();
		return getJejuProjectByEclipseProjectName(projectName);
	}

	public static String getWebXConfigPath(JejuProject jp){
		String webdir = ProjectUtil.detectDefaultWebappdir(jp.getWarProjectName());
		return webdir+File.separator+"WEB-INF"+File.separator+"webx.xml";
	}

	public static String getWebConfigPath(JejuProject jp){
		String webdir = ProjectUtil.detectDefaultWebappdir(jp.getWarProjectName());
		return webdir+File.separator+"WEB-INF"+File.separator+"web.xml";
	}

//	public static String getWebXConfigPathFormWebXML(JejuProject jp){
//		String webdir = ProjectUtil.detectDefaultWebappdir(jp.getWarProjectName());
//		String webXML =  webdir+File.separator+"WEB-INF"+File.separator+"web.xml";
//
//	}

	public static boolean belongToJejuProject(JejuProject jejuProject,IProject project){
		if(jejuProject == null || project == null)
			return false;
		List<String> projects = jejuProject.getProjectNames();
		return projects.contains(project.getName());
	}

}
