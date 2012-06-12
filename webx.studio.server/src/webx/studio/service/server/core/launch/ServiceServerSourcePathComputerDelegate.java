package webx.studio.service.server.core.launch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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
import webx.studio.service.server.core.ServiceServer;
import webx.studio.service.server.core.ServiceServerUtil;
import webx.studio.utils.JdtUtil;
import webx.studio.utils.ProjectUtil;

public class ServiceServerSourcePathComputerDelegate implements
		ISourcePathComputerDelegate {


	public ISourceContainer[] computeSourceContainers(
			ILaunchConfiguration configuration, IProgressMonitor monitor)
			throws CoreException {

		ServiceServer serviceServer = (ServiceServer) ServiceServerUtil.getServer(configuration);
		List<IRuntimeClasspathEntry> runtimeClasspath = new ArrayList<IRuntimeClasspathEntry>();
		String[] projectNames = serviceServer.getServiceProjects();
		for(String projectName:projectNames){
			IJavaProject javaProject = JdtUtil.getJavaProject(ProjectUtil.getProject(projectName));
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
