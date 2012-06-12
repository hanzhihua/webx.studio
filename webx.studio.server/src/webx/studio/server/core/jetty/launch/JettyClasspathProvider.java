package webx.studio.server.core.jetty.launch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry2;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.StandardClasspathProvider;

import webx.studio.server.core.jetty.JettyServerConstants;

public class JettyClasspathProvider extends StandardClasspathProvider {

	public IRuntimeClasspathEntry[] computeUnresolvedClasspath(
			ILaunchConfiguration configuration) throws CoreException {


		IRuntimeClasspathEntry[] classpath = super
				.computeUnresolvedClasspath(configuration);
		classpath = addJetty(classpath, configuration);


		List<IRuntimeClasspathEntry> runtimeClasspath = new ArrayList<IRuntimeClasspathEntry>();
		runtimeClasspath.addAll(Arrays.asList(classpath));
		return runtimeClasspath.toArray(new IRuntimeClasspathEntry[0]);

	}

	private IRuntimeClasspathEntry[] addJetty(
			IRuntimeClasspathEntry[] existing, ILaunchConfiguration config) {

		List<IRuntimeClasspathEntry> entries = new ArrayList<IRuntimeClasspathEntry>();
		 for (int i = 0; i < existing.length; i++) {
			 IRuntimeClasspathEntry entry = existing[i];
			 if(JettyServerConstants.CONTAINER_JEJU_JETTY.equals(entry.getVariableName())){

			 }else{
				 entries.add(entry);
			 }
		 }
		entries.add(new JettyContainerClasspathEntry(
				JettyServerConstants.CONTAINER_JEJU_JETTY, IRuntimeClasspathEntry.USER_CLASSES));

		return entries.toArray(new IRuntimeClasspathEntry[0]);

	}

	 public IRuntimeClasspathEntry[] resolveClasspath(
			  IRuntimeClasspathEntry[] entries, ILaunchConfiguration configuration)
	  throws CoreException {

		  Set<IRuntimeClasspathEntry> all = new LinkedHashSet<IRuntimeClasspathEntry>(
				  entries.length);
		  for (int i = 0; i < entries.length; i++) {
			  IRuntimeClasspathEntry entry = entries[i];
			  IResource resource = entry.getResource();
			  if (resource instanceof IProject) {
				  continue;
			  }

			  if(entry instanceof JettyContainerClasspathEntry){
				  all.addAll(Arrays.asList(((JettyContainerClasspathEntry)entry).getRuntimeClasspathEntries(configuration)));
			  }else {
				  all.addAll(Arrays.asList(JavaRuntime.resolveRuntimeClasspathEntry(entry, configuration)));
			  }
		  }
		  return all.toArray(new IRuntimeClasspathEntry[0]);
	  }

}
