package webx.studio.service.server.ui.cnf;

import java.util.ArrayList;
import java.util.List;


import org.eclipse.jface.viewers.ITreeContentProvider;

import webx.studio.server.ui.cnf.BaseContentProvider;
import webx.studio.service.server.core.ServiceServer;
import webx.studio.service.server.core.ServiceServerChild;
import webx.studio.service.server.core.ServiceServerUtil;

public class ServerContentProvider extends BaseContentProvider implements
		ITreeContentProvider {

	public static Object INITIALIZING = new Object();

	public Object[] getElements(Object element) {
		List<ServiceServer> list = new ArrayList<ServiceServer>();
		ServiceServer[] servers = ServiceServerUtil.getServiceServers();
		if (servers != null) {
			int size = servers.length;
			for (int i = 0; i < size; i++) {
				list.add(servers[i]);
			}
		}
		return list.toArray();
	}

	public Object[] getChildren(Object element) {

		if (element instanceof ServiceServer) {
			return ServiceServerUtil.getServerChildren((ServiceServer)element);
		} else
			return new Object[0];

	}

	public Object getParent(Object element) {
		if (element instanceof ServiceServerChild) {
			return ((ServiceServerChild) element).getServer();
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof ServiceServer) {
			return ((ServiceServer) element).getServiceProjects().length > 0;
		}
		return false;
	}

}
