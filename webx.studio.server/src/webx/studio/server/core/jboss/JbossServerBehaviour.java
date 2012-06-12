package webx.studio.server.core.jboss;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

import webx.studio.server.core.ServerBehaviourDelegate;


public class JbossServerBehaviour   extends ServerBehaviourDelegate{

	@Override
	public void setupLaunchConfiguration(
			ILaunchConfigurationWorkingCopy workingCopy,
			IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getRuntimeClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setupLaunch(ILaunch launch, String launchMode,
			IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop(boolean force) {
		// TODO Auto-generated method stub
		
	}

}
