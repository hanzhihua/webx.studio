package webx.studio.server.ui.event;

import webx.studio.server.core.Server;


public class ServerEvent {

	private Server server;
	private int state;

	public static final int STATE_CHANGE = 0x0001;

	public ServerEvent(Server server, int state){
		this.server = server;
		this.state = state;
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}



}
