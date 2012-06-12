package webx.studio.service.server.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import webx.studio.server.ServerPlugin;
import webx.studio.server.Trace;
import webx.studio.server.core.Server;
import webx.studio.service.server.ui.cnf.IServiceServerLifecycleListener;
import webx.studio.xml.XMLMemento;


public class ServiceServerResourceManager {


	public static final String SERVICE_SERVER_DATA_FILE = "service_servers.xml";

	private static ServiceServerResourceManager instance = new ServiceServerResourceManager();

	private List<ServiceServer> serviceServers;

	private static boolean initialized;
	private static boolean initializing;

	private static final byte EVENT_ADDED = 0;
	private static final byte EVENT_CHANGED = 1;
	private static final byte EVENT_REMOVED = 2;

	private List<IServiceServerLifecycleListener> serviceServerListeners = new ArrayList<IServiceServerLifecycleListener>(3);

	private ServiceServerResourceManager() {
		super();
	}

	protected synchronized void init() {
		if (initialized || initializing)
			return;
		initializing = true;

		serviceServers = new ArrayList<ServiceServer>();
		loadServiceServers();
		initialized = true;
	}

	public ServiceServer[] getServiceServers() {
		if (!initialized)
			init();

		ServiceServer[] servers2 = new ServiceServer[serviceServers.size()];
		serviceServers.toArray(servers2);

		return servers2;
	}

	protected void loadServiceServers() {
		String filename = ServerPlugin.getInstance().getStateLocation()
				.append(SERVICE_SERVER_DATA_FILE).toOSString();
		try {
			XMLMemento memento = XMLMemento.loadMemento(filename);

			XMLMemento[] children = memento.getChildren("service-server");
			int size = children.length;
			for (int i = 0; i < size; i++) {
				ServiceServer serviceServer = new ServiceServer();
				serviceServer.loadFromMemento(children[i], null);
				serviceServers.add(serviceServer);
			}
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Could not load servers", e);
		}
	}

	private void saveServiceServers() {
		String filename = ServerPlugin.getInstance().getStateLocation().append(SERVICE_SERVER_DATA_FILE).toOSString();
		try {
			XMLMemento memento = XMLMemento.createWriteRoot("service-servers");
			Iterator iterator = serviceServers.iterator();
			while (iterator.hasNext()) {
				ServiceServer serviceServer = (ServiceServer) iterator.next();
				XMLMemento child = memento.createChild("service-server");
				serviceServer.save(child);
			}
			memento.saveToFile(filename);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not save servers", e);
		}

	}

	public static ServiceServerResourceManager getInstance() {
		return instance;
	}

	protected void addServer(ServiceServer serviceServer) {
		if (!initialized)
			init();
		if (serviceServer == null)
			return;
		if (!serviceServers.contains(serviceServer)){
			registerServiceServer(serviceServer);
		}else{
			fireServerEvent(serviceServer, EVENT_CHANGED);
		}
		saveServiceServers();
	}

	private void registerServiceServer(ServiceServer serviceServer) {
		if (!initialized)
			init();
		if (serviceServer == null)
			return;
		serviceServers.add(serviceServer);
		fireServerEvent(serviceServer, EVENT_ADDED);
	}

	protected void removeServiceServer(ServiceServer serviceServer) {
		if (!initialized)
			init();
		if (serviceServers.contains(serviceServer)) {
			fireServerEvent(serviceServer, EVENT_REMOVED);
			serviceServers.remove(serviceServer);
			saveServiceServers();
		}
	}

	private void fireServerEvent(final ServiceServer serviceServer, byte b) {

		if (serviceServerListeners.isEmpty())
			return;

		List<IServiceServerLifecycleListener> clone = new ArrayList<IServiceServerLifecycleListener>();
		clone.addAll(serviceServerListeners);
		for (IServiceServerLifecycleListener srl : clone) {

			try {
				if (b == EVENT_ADDED)
					srl.serviceServerAdded(serviceServer);
				else if (b == EVENT_CHANGED)
					srl.serviceServerChanged(serviceServer);
				else
					srl.serviceServerRemoved(serviceServer);
			} catch (Exception e) {

			}
		}

	}

	public void addServiceServerLifecycleListener(IServiceServerLifecycleListener listener) {

		synchronized (serviceServerListeners) {
			serviceServerListeners.add(listener);
		}
	}

	public void removeServiceServerLifecycleListener(IServiceServerLifecycleListener listener) {

		synchronized (serviceServerListeners) {
			serviceServerListeners.remove(listener);
		}
	}

	public ServiceServer getServiceServer(String id) {
		if (!initialized)
			init();

		if (id == null)
			throw new IllegalArgumentException();

		Iterator iterator = serviceServers.iterator();
		while (iterator.hasNext()) {
			ServiceServer serviceServer = (ServiceServer) iterator.next();
			if (id.equals(serviceServer.getId()))
				return serviceServer;
		}
		return null;
	}

	protected void removeServer(ServiceServer server) {
		if (!initialized)
			init();
		if (serviceServers.contains(server)) {
			fireServerEvent(server, EVENT_REMOVED);
			serviceServers.remove(server);
			saveServiceServers();
		}
	}






}
