package webx.studio.service.server.core;




public class ServiceServerChild {

	private String name;
	private ServiceServer server;

	public ServiceServerChild(String name,ServiceServer server){
		this.name = name;
		this.server = server;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ServiceServer getServer() {
		return server;
	}
	public void setServer(ServiceServer server) {
		this.server = server;
	}



}
