package server;

public class ServerAddr{
	public String hostname;
	public String port;
	ServerAddr(String hostname, String port){
		this.hostname = hostname;
		this.port = port;
	}

	public ServerAddr(){

	}

	//parses string like "192.168.1.101:3033"
	public void parseAddr(String addr){
		String[] addrSplit = addr.split(":");
		if(addrSplit.length != 2){
			throw new IllegalArgumentException("Bad address format.");
		} else {
			this.hostname = addrSplit[0];
			this.port = addrSplit[1];
		}

	}
}