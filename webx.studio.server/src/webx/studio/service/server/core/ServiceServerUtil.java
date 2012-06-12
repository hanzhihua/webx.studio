package webx.studio.service.server.core;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;

import webx.studio.projectcreation.ui.ProjectCreationConstants;
import webx.studio.server.ServerPlugin;
import webx.studio.utils.JdtUtil;
import webx.studio.utils.PathUtil;
import webx.studio.utils.ProjectUtil;

public class ServiceServerUtil {

	public static ServiceServer[] getServiceServers() {
		return getResourceManager().getServiceServers();
	}

	private final static ServiceServerResourceManager getResourceManager() {
		return ServiceServerResourceManager.getInstance();
	}

	public static ServiceServer findServer(String id) {
		return getResourceManager().getServiceServer(id);
	}

	public static ServiceServerChild[] getServerChildren(
			ServiceServer serviceServer) {
		if (serviceServer == null)
			return new ServiceServerChild[0];
		String[] names = serviceServer.getServiceProjects();
		ServiceServerChild[] array = new ServiceServerChild[names.length];
		for (int i = 0; i < names.length; i++) {
			array[i] = new ServiceServerChild(names[i], serviceServer);
		}
		return array;
	}

	public static ServiceServer getServer(ILaunchConfiguration configuration)
			throws CoreException {
		String serverId = configuration.getAttribute(
				ServiceServer.ATTR_SERVER_ID, (String) null);

		if (serverId != null)
			return findServer(serverId);
		return null;
	}

	// public static void removeProjectFromServer(ServiceServer server,
	// String projectName) {
	// String[] names = server.getServiceProjects();
	// List<String> list = new ArrayList<String>();
	// for (String name : names) {
	// if (StringUtils.equals(name, projectName))
	// continue;
	// list.add(name);
	// }
	// server.setServiceProjects(list);
	// }

	public static IJavaProject[] getChildren(ServiceServer server) {
		if (server == null || server.getServiceProjects().length == 0) {
			return new IJavaProject[0];
		}
		Set<IJavaProject> jps = new HashSet<IJavaProject>();
		for (String projectName : server.getServiceProjects()) {
			IProject project = ProjectUtil.getProject(projectName);
			try {
				IJavaProject jp = JdtUtil.getJavaProject(project);
				if (jp != null) {
					jps.add(jp);
					for (IJavaProject dependJp : JdtUtil
							.getAllDependingJavaProjects(jp)) {
						jps.add(dependJp);
					}
				}

			} catch (CoreException e) {
				ServerPlugin.logError(e);
			}
		}
		return jps.toArray(new IJavaProject[0]);
	}

	public static IJavaProject[] getChildrenAndDependcyProjects(ServiceServer server) {
		if (server == null || server.getServiceProjects().length == 0) {
			return new IJavaProject[0];
		}
		Set<IJavaProject> jps = new HashSet<IJavaProject>();
		for (String projectName : server.getServiceProjects()) {
			IProject project = ProjectUtil.getProject(projectName);
			try {
				IJavaProject jp = JdtUtil.getJavaProject(project);
				if (jp != null) {
					jps.add(jp);
					for (IJavaProject dependJp : JdtUtil
							.getAllDependingJavaProjects(jp)) {
						jps.add(dependJp);
					}
				}
			} catch (CoreException e) {
				ServerPlugin.logError(e);
			}
		}
		return jps.toArray(new IJavaProject[0]);
	}

	public static void addJavaProjectToServiceServer(ServiceServer serviceServer,String projectName){
		if(StringUtils.isBlank(projectName))
			return;
		List<String> projects = new ArrayList<String>(
				Arrays.asList(serviceServer.getServiceProjects()));
		projects.add(projectName);
		serviceServer.setServiceProjects(projects);
		serviceServer.save();
	}

	public static void removeProjectFromServiceServer(
			ServiceServer serviceServer, String projectName) {
		if (StringUtils.isBlank(projectName))
			return;
		List<String> projects = new ArrayList<String>(
				Arrays.asList(serviceServer.getServiceProjects()));
		if (projects.contains(projectName)) {
			projects.remove(projectName);
			serviceServer.setServiceProjects(projects);
			serviceServer.save();
		}
	}

	public static String[] getClasspath(ILaunchConfiguration configuration){
		List<String> outputList = new ArrayList<String>();
		Set<String> cps = new HashSet<String>();
		try {
			final ServiceServer serviceServer = ServiceServerUtil.getServer(configuration);
			outputList.addAll(ServiceServerClasspathUtils.getRuntimeClasspath(serviceServer.getHome()));
			IJavaProject[] jps = ServiceServerUtil.getChildrenAndDependcyProjects(serviceServer);
			for(IJavaProject jp:jps){
				outputList.add(PathUtil.getLocation(jp.getOutputLocation()));
			}
			jps = ServiceServerUtil.getChildren(serviceServer);
			for(IJavaProject jp:jps){
				try {
					final IProject p = jp.getProject();
					final String m2Repo = ((Path) JavaCore
							.getClasspathVariable(ProjectCreationConstants.M2_REPO_KEY))
							.toFile().getCanonicalPath();
					final List<String> filtedPaths = new ArrayList<String>();
					if (StringUtils.isNotBlank(m2Repo)) {
						IRunnableWithProgress runnable = new IRunnableWithProgress(){


							public void run(IProgressMonitor monitor)
									throws InvocationTargetException, InterruptedException {
								monitor.beginTask("Computer the classpath of project["+p.getName()+"]", IProgressMonitor.UNKNOWN);
								try {
									filtedPaths.addAll(serviceServer.getApi()
											.getNonTestAndProvidedPaths(
													new File(p.getLocation().toOSString(),
															"pom.xml"), m2Repo));
								} catch (Exception e) {
									ServerPlugin.logError(e);
								}
								monitor.done();
							}

						};
						try {
							new ProgressMonitorDialog(ServerPlugin.getActiveWorkbenchShell()).run(true, false, runnable);
						} catch (Exception e) {
							ServerPlugin.logError(e);
						}

						for (String path : filtedPaths) {
							cps.add(path);
						}

					} else {

					}
				} catch (Exception e) {
					ServerPlugin.logError(e);
				}
			}
		} catch (CoreException e) {
			ServerPlugin.logError(e);
		}
		outputList.addAll(cps);
		return outputList.toArray(new String[0]);
	}

}
