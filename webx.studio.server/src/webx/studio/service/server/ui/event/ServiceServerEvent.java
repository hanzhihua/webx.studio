package webx.studio.service.server.ui.event;

import webx.studio.service.server.core.ServiceServer;

public class ServiceServerEvent {

	private ServiceServer serviceServer;
	private int state;

	public ServiceServerEvent(ServiceServer serviceServer, int state){
		this.serviceServer = serviceServer;
		this.state = state;
	}

	public ServiceServer getServiceServer() {
		return serviceServer;
	}
	public void setServiceServer(ServiceServer serviceServer) {
		this.serviceServer = serviceServer;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}

}
