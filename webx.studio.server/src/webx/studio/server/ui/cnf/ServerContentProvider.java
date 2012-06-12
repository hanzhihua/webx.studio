package webx.studio.server.ui.cnf;

import java.util.ArrayList;
import java.util.List;


import org.eclipse.jface.viewers.ITreeContentProvider;

import webx.studio.server.core.Server;
import webx.studio.server.core.ServerChild;
import webx.studio.server.core.ServerCore;

/**
 * @author zhihua.hanzh
 *
 */
public class ServerContentProvider extends BaseContentProvider implements
		ITreeContentProvider {

	
	public static Object INITIALIZING = new Object();

	public Object[] getElements(Object element) {
		List<Server> list = new ArrayList<Server>();
		Server[] servers = ServerCore.getServers();
		if (servers != null) {
			int size = servers.length;
			for (int i = 0; i < size; i++) {
				list.add(servers[i]);
			}
		}
		return list.toArray();
	}

	public Object[] getChildren(Object element) {

		if (element instanceof Server) {
			return ServerChild.getServerChildren(
					((Server) element).getOnlyWebXProjectNames(),
					(Server) element);
		} else
			return new Object[0];
	}

	public Object getParent(Object element) {
		if (element instanceof ServerChild) {
			return ((ServerChild) element).getServer();
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof Server) {
			return ((Server) element).getWebXProjectNames().length > 0;
		}
		return false;
	}
}
