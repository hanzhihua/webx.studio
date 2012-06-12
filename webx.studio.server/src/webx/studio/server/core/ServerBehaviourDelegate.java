package webx.studio.server.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

public abstract class ServerBehaviourDelegate {
	
	protected Server server;
	
	public abstract void setupLaunchConfiguration(ILaunchConfigurationWorkingCopy workingCopy, IProgressMonitor monitor);
	
	final void initialize(Server newServer, IProgressMonitor monitor) {
		server = newServer;
		initialize(monitor);
	}
	
	protected void initialize(IProgressMonitor monitor) {
		// do nothing
	}
	
	public abstract String getRuntimeClass();
	
	public abstract void setupLaunch(ILaunch launch, String launchMode, IProgressMonitor monitor) throws CoreException;
	
	public abstract void stop(boolean force);


}
