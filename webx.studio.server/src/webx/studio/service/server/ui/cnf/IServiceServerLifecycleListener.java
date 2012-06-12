package webx.studio.service.server.ui.cnf;

import webx.studio.server.core.Server;
import webx.studio.service.server.core.ServiceServer;

public interface IServiceServerLifecycleListener {

	public void serviceServerAdded(ServiceServer serviceServer);

	public void serviceServerChanged(ServiceServer serviceServer);

	public void serviceServerRemoved(ServiceServer serviceServer);


}
