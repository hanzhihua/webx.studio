package webx.studio.server.core;

/**
 * @author zhihua.hanzh
 *
 */
public class ServerChild {

	private String name;
	private Server server;

	public ServerChild(String name,Server server){
		this.name = name;
		this.server = server;

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public static ServerChild[] getServerChildren(String[] names,Server server){
		if(names == null || server == null || names.length == 0 )
			return new ServerChild[0];
		ServerChild[] array = new ServerChild[names.length];
		for(int i=0;i<names.length;i++){
			array[i] = new ServerChild(names[i],server);
		}
		return array;
	}



}
