package webx.studio.server.core;


import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;

import webx.studio.server.Trace;

/**
 * @author zhihua.hanzh
 *
 */
public class ServerType {

	private final IConfigurationElement element;

	private String id;
	private String name;
	private String vendor;
	private String version;
	private String launchConfigId;


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getVendor() {
		return vendor;
	}


	public void setVendor(String vendor) {
		this.vendor = vendor;
	}


	public String getVersion() {
		return version;
	}


	public void setVersion(String version) {
		this.version = version;
	}


	public ServerType(IConfigurationElement ce){
		this.element = ce;
		this.id = element.getAttribute("id");
		this.name = element.getAttribute("name");
		this.vendor = element.getAttribute("vendor");
		this.version = element.getAttribute("version");
		this.launchConfigId = element.getAttribute("launchConfigId");
	}


	public String getLaunchConfigId() {
		return launchConfigId;
	}


	public void setLaunchConfigId(String launchConfigId) {
		this.launchConfigId = launchConfigId;
	}
	
	public ILaunchConfigurationType getLaunchConfigurationType() {
		try {
			String launchConfigId = element.getAttribute("launchConfigId");
			if (launchConfigId == null)
				return null;
			ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
			return launchManager.getLaunchConfigurationType(launchConfigId);
		} catch (Exception e) {
			Trace.trace(Trace.CONFIG, e.getMessage(),e);
			return null;
		}
	}
	
	protected ServerBehaviourDelegate createServerBehaviourDelegate() throws CoreException {
		try {
			return (ServerBehaviourDelegate) element.createExecutableExtension("behaviourClass");
		} catch (Exception e) {
			return null;
		}
	}

}
