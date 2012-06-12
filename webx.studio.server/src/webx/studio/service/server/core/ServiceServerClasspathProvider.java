package webx.studio.service.server.core;

import hidden.org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.StandardClasspathProvider;

import webx.studio.projectcreation.ui.ProjectCreationConstants;
import webx.studio.server.ServerPlugin;
import webx.studio.utils.JdtUtil;

public class ServiceServerClasspathProvider extends StandardClasspathProvider {

	public IRuntimeClasspathEntry[] computeUnresolvedClasspath(
			ILaunchConfiguration configuration) throws CoreException {

		ServiceServer serviceServer = ServiceServerUtil
				.getServer(configuration);
		IJavaProject[] jps = ServiceServerUtil.getChildren(serviceServer);
		if (jps.length != 1)
			return super.computeUnresolvedClasspath(configuration);

		IRuntimeClasspathEntry[] classpath = super
				.computeUnresolvedClasspath(configuration);
		boolean useDefault = configuration.getAttribute(
				IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, true);
		if (useDefault) {
			classpath = addServiceClasspath(classpath, configuration);
		} else {
			classpath = recoverRuntimePath(configuration,
					IJavaLaunchConfigurationConstants.ATTR_CLASSPATH);
		}
		List<IRuntimeClasspathEntry> runtimeClasspath = new ArrayList<IRuntimeClasspathEntry>();
		runtimeClasspath.addAll(Arrays.asList(classpath));

		IJavaProject javaProject = jps[0];
		runtimeClasspath.add(JavaRuntime
				.newArchiveRuntimeClasspathEntry(javaProject
						.getOutputLocation()));

		List<IJavaProject> dependencyProjects = JdtUtil
				.getAllDependingJavaProjects(javaProject);
		for (IJavaProject jp : dependencyProjects) {
			runtimeClasspath.add(JavaRuntime
					.newArchiveRuntimeClasspathEntry(jp.getOutputLocation()));
		}
		try {
			IProject p = javaProject.getProject();
			String m2Repo = ((Path) JavaCore
					.getClasspathVariable(ProjectCreationConstants.M2_REPO_KEY))
					.toFile().getCanonicalPath();
			List<String> filtedPaths = new ArrayList<String>();
			if (StringUtils.isNotBlank(m2Repo)) {
				filtedPaths = serviceServer.getApi()
						.getNonTestAndProvidedPaths(
								new File(p.getLocation().toOSString(),
										"pom.xml"), m2Repo);
				for (String path : filtedPaths) {
					runtimeClasspath.add(JavaRuntime.newArchiveRuntimeClasspathEntry(new Path(path)));
				}

			} else {

			}
		} catch (Exception e) {
			ServerPlugin.logError(e);
		}

		return runtimeClasspath.toArray(new IRuntimeClasspathEntry[0]);

	}

	private IRuntimeClasspathEntry[] addServiceClasspath(
			IRuntimeClasspathEntry[] existing, ILaunchConfiguration config)
			throws CoreException {

		List<IRuntimeClasspathEntry> entries = new ArrayList<IRuntimeClasspathEntry>();
		entries.addAll(Arrays.asList(existing));
		entries.addAll(ServiceServerClasspathUtils
				.getRuntimeClasspath(new Path(config.getAttribute("home", "."))));
		return entries.toArray(new IRuntimeClasspathEntry[0]);

	}

}
