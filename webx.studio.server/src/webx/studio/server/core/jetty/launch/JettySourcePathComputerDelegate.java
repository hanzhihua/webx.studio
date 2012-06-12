package webx.studio.server.core.jetty.launch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputerDelegate;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;

import webx.studio.server.core.Server;
import webx.studio.server.core.ServerUtil;
import webx.studio.utils.JdtUtil;

public class JettySourcePathComputerDelegate implements
		ISourcePathComputerDelegate {

	public ISourceContainer[] computeSourceContainers(
			ILaunchConfiguration configuration, IProgressMonitor monitor)
			throws CoreException {

		Server server = (Server) ServerUtil.getServer(configuration);
		List<IRuntimeClasspathEntry> runtimeClasspath = new ArrayList<IRuntimeClasspathEntry>();
		Collection<IProject> warProjects = ServerUtil
				.getAllRelativeWarProjects(server).values();
		for (IProject warProject : warProjects) {
			IJavaProject javaProject = JdtUtil.getJavaProject(warProject);
			if(javaProject != null){
				runtimeClasspath.add(JavaRuntime.newDefaultProjectClasspathEntry(javaProject));
			}
		}

		runtimeClasspath.addAll(Arrays.asList(JavaRuntime.computeUnresolvedSourceLookupPath(configuration)));
		IRuntimeClasspathEntry[] entries = (IRuntimeClasspathEntry[]) runtimeClasspath
				.toArray(new IRuntimeClasspathEntry[runtimeClasspath.size()]);
		IRuntimeClasspathEntry[] resolved = JavaRuntime.resolveSourceLookupPath(entries, configuration);
		return JavaRuntime.getSourceContainers(resolved);
	}

}
