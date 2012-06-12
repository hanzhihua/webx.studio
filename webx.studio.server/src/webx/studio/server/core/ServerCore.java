package webx.studio.server.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import webx.studio.projectcreation.ui.core.JejuProject;
import webx.studio.server.ServerPlugin;
import webx.studio.server.ui.cnf.IServerLifecycleListener;


public class ServerCore {

	private static final String EXTENSION_SERVER_TYPE = "types";

	private static List<ServerType> serverTypes;

	public static ServerType[] getServerTypes() {
		if (serverTypes == null)
			loadServerTypes();

		ServerType[] st = new ServerType[serverTypes.size()];
		serverTypes.toArray(st);
		return st;
	}

	private static synchronized void loadServerTypes() {
		if (serverTypes != null)
			return;

		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(
				ServerPlugin.PLUGIN_ID, EXTENSION_SERVER_TYPE);
		List<ServerType> list = new ArrayList<ServerType>(cf.length);
		addServerTypes(cf, list);
		serverTypes = list;

	}

	private static synchronized void addServerTypes(IConfigurationElement[] cf,
			List<ServerType> list) {
		for (IConfigurationElement ce : cf) {
			try {
				list.add(new ServerType(ce));
			} catch (Exception e) {
				ServerPlugin.logError(e);
			}
		}
	}

	public static Server[] getServers() {
		return getResourceManager().getServers();
	}

	private final static ServerResourceManager getResourceManager() {
		return ServerResourceManager.getInstance();
	}

	public static void addServerLifecycleListener(IServerLifecycleListener listener) {
		getResourceManager().addServerLifecycleListener(listener);
	}

	public static void removeServerLifecycleListener(IServerLifecycleListener listener) {
		getResourceManager().removeServerLifecycleListener(listener);
	}

	public static ServerType findServerType(String id) {
		if (id == null)
			throw new IllegalArgumentException();

		if (serverTypes == null)
			loadServerTypes();

		Iterator iterator = serverTypes.iterator();
		while (iterator.hasNext()) {
			ServerType serverType = (ServerType) iterator.next();
			if (id.equals(serverType.getId()))
				return serverType;
		}
		return null;
	}

	public static Server findServer(String id) {
		return getResourceManager().getServer(id);
	}
	
	public static Server newServer(){
		return new Server();
	}
	
	public static Server getServer(JejuProject jejuProject){
		String serverId = jejuProject.getServerId();
		if(StringUtils.isBlank(serverId)){
			return null;
		}
		return findServer(serverId);
		
	}

}
