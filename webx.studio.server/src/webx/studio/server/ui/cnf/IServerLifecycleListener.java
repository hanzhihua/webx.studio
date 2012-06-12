package webx.studio.server.ui.cnf;

import webx.studio.server.core.Server;

public interface IServerLifecycleListener {

	public void serverAdded(Server server);

	public void serverChanged(Server server);

	public void serverRemoved(Server server);

}
