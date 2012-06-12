package webx.studio.server.ui.editor;


import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;

import webx.studio.server.core.Server;
import webx.studio.server.core.ServerCore;

public class ServerEditorInput implements IServerEditorInput{

	private String serverId;

	public ServerEditorInput(String serverId) {
		super();
		this.serverId = serverId;
	}

	public boolean exists() {
		if (serverId != null && ServerCore.findServer(serverId) == null)
			return false;

		return true;
	}

	public ImageDescriptor getImageDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return this.serverId;
	}

	public IPersistableElement getPersistable() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getToolTipText() {
		String s = null;
		if (serverId != null) {
			Server server = ServerCore.findServer(serverId);
			if (server != null) {
				Server server2 = (Server) server;
				s= server.getName();
			}
		}
		if (s == null)
			s = "";
		return s;
	}

	public Object getAdapter(Class adapter) {
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	public String getServerId() {
		return serverId;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof ServerEditorInput))
			return false;
		ServerEditorInput other = (ServerEditorInput) obj;
		if (serverId == null) {
			if (other.serverId != null)
				return false;
		} else if (!serverId.equals(other.serverId))
			return false;
		return true;
	}

}
