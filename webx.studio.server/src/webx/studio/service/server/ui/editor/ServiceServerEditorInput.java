package webx.studio.service.server.ui.editor;


import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import webx.studio.service.server.core.ServiceServer;
import webx.studio.service.server.core.ServiceServerUtil;

public class ServiceServerEditorInput implements IEditorInput{

	public static final String EDITOR_ID = "jeju.service.server.ui.editor";

	private final ServiceServer serviceServer;

	public ServiceServerEditorInput(ServiceServer serviceServer){
		this.serviceServer = serviceServer;
	}

	public ServiceServer getServiceServer(){
		return this.serviceServer;
	}


	public Object getAdapter(Class adapter) {
		return null;
	}


	public boolean exists() {
		if (serviceServer != null && ServiceServerUtil.findServer(serviceServer.getId()) == null)
			return false;

		return true;
	}


	public ImageDescriptor getImageDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}


	public String getName() {
		// TODO Auto-generated method stub
		return serviceServer.getName();
	}


	public IPersistableElement getPersistable() {
		// TODO Auto-generated method stub
		return null;
	}


	public String getToolTipText() {
		// TODO Auto-generated method stub
		return serviceServer.getName();
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof ServiceServerEditorInput))
			return false;
		ServiceServerEditorInput other = (ServiceServerEditorInput) obj;
		if (serviceServer == null) {
			if (other.serviceServer != null)
				return false;
		} else if (!StringUtils.equals(serviceServer.getId(), other.serviceServer.getId()))
			return false;
		return true;
	}

}
