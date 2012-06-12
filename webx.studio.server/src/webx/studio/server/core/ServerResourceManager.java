package webx.studio.server.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import webx.studio.server.ServerPlugin;
import webx.studio.server.Trace;
import webx.studio.server.ui.cnf.IServerLifecycleListener;
import webx.studio.xml.XMLMemento;



/**
 * @author zhihua.hanzh
 *
 */
public class ServerResourceManager {

	public static final String SERVER_DATA_FILE = "webx_servers.xml";

	private static final byte EVENT_ADDED = 0;
	private static final byte EVENT_CHANGED = 1;
	private static final byte EVENT_REMOVED = 2;

	private static ServerResourceManager instance = new ServerResourceManager();
	private List<IServerLifecycleListener> serverListeners = new ArrayList<IServerLifecycleListener>(3);

	private List<Server> servers;

	private static boolean initialized;
	private static boolean initializing;

	private ServerResourceManager() {
		super();
	}

	protected synchronized void init() {
		if (initialized || initializing)
			return;
		initializing = true;

		servers = new ArrayList<Server>();
		loadServersList();
		initialized = true;
	}

	public Server[] getServers() {
		if (!initialized)
			init();

		Server[] servers2 = new Server[servers.size()];
		servers.toArray(servers2);

		return servers2;
	}

	protected void loadServersList() {
		Trace.trace(Trace.FINEST, "Loading server info");
		String filename = ServerPlugin.getInstance().getStateLocation()
				.append(SERVER_DATA_FILE).toOSString();
		try {
			XMLMemento memento = XMLMemento.loadMemento(filename);

			XMLMemento[] children = memento.getChildren("server");
			int size = children.length;
			for (int i = 0; i < size; i++) {
				Server server = new Server();
				server.loadFromMemento(children[i], null);
				servers.add(server);
			}
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Could not load servers", e);
		}
	}

	private void saveServersList() {
		String filename = ServerPlugin.getInstance().getStateLocation().append(SERVER_DATA_FILE).toOSString();
		try {
			XMLMemento memento = XMLMemento.createWriteRoot("servers");
			Iterator iterator = servers.iterator();
			while (iterator.hasNext()) {
				Server server = (Server) iterator.next();
				XMLMemento child = memento.createChild("server");
				server.save(child);
			}
			memento.saveToFile(filename);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not save servers", e);
		}

	}

	public static ServerResourceManager getInstance() {
		return instance;
	}

	protected void addServer(Server server) {
		if (!initialized)
			init();
		if (server == null)
			return;
		if (!servers.contains(server)){
			registerServer(server);
		}else{
			fireServerEvent(server, EVENT_CHANGED);
		}
		saveServersList();
	}

	private void registerServer(Server server) {
		if (!initialized)
			init();
		if (server == null)
			return;
		servers.add(server);
		fireServerEvent(server, EVENT_ADDED);
	}

	protected void removeServer(Server server) {
		if (!initialized)
			init();
		if (servers.contains(server)) {
			fireServerEvent(server, EVENT_REMOVED);
			servers.remove(server);
			saveServersList();
		}
	}

	private void fireServerEvent(final Server server, byte b) {

		if (serverListeners.isEmpty())
			return;

		List<IServerLifecycleListener> clone = new ArrayList<IServerLifecycleListener>();
		clone.addAll(serverListeners);
		for (IServerLifecycleListener srl : clone) {

			try {
				if (b == EVENT_ADDED)
					srl.serverAdded(server);
				else if (b == EVENT_CHANGED)
					srl.serverChanged(server);
				else
					srl.serverRemoved(server);
			} catch (Exception e) {

			}
		}

	}

	public void addServerLifecycleListener(IServerLifecycleListener listener) {

		synchronized (serverListeners) {
			serverListeners.add(listener);
		}
	}

	public void removeServerLifecycleListener(IServerLifecycleListener listener) {

		synchronized (serverListeners) {
			serverListeners.remove(listener);
		}
	}

	public Server getServer(String id) {
		if (!initialized)
			init();

		if (id == null)
			throw new IllegalArgumentException();

		Iterator iterator = servers.iterator();
		while (iterator.hasNext()) {
			Server server = (Server) iterator.next();
			if (id.equals(server.getId()))
				return server;
		}
		return null;
	}


}
