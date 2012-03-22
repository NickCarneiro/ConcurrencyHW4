package client;
import java.net.*;
import java.util.ArrayList;
import java.io.*;

public class UDPClient {
	private ArrayList<ServerAddr> servers;
	private ServerAddr current_server;
	public Integer port;
	public Integer receive_length = 1024;

	public DatagramSocket datasocket;
	public InetAddress ia;
	public DatagramPacket sPacket, rPacket;

	public UDPClient(){
		System.out.println("Initializing UDP Client");

		//import list of servers
		servers = new ArrayList<ServerAddr>();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader("servers.txt"));

			String line;
			//read in servers.txt and save in arraylist
			while ((line = br.readLine()) != null) {
				ServerAddr address = new ServerAddr();
				address.parseAddr(line);
				servers.add(address);
			}
			
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//now we have a list of servers. Try to connect to them sequentially.
		for(ServerAddr addr : servers){
			try {
				//keep track of current server in case we need it later.
				current_server = addr;
				this.port = Integer.parseInt(addr.port);
				this.ia = InetAddress.getByName(addr.hostname);
				this.datasocket = new DatagramSocket();
				//if we get here, we have a valid hostname
				break;

			} catch (UnknownHostException e) {
				System.err.println("Could not connect to server " + addr.hostname + ":" + addr.port + e);
			} catch (SocketException se) {
				System.err.println(se);
			}
		}
		
	}




	public static void run() {
		UDPClient client = new UDPClient();
		String line;
		try {
			BufferedReader br = new BufferedReader(new FileReader("testfile.txt"));

			//read in testfile.txt and send the commands to the server.
			while ((line = br.readLine()) != null) {
				byte[] command = line.getBytes();
				System.out.println("Sending command: " + line);
				client.sPacket = new DatagramPacket(command, command.length, client.ia, client.port);
				client.datasocket.send(client.sPacket);

				//create space for response
				byte[] rbuffer = new byte[client.receive_length];
				client.rPacket = new DatagramPacket(rbuffer, rbuffer.length);
				//block while waiting for response
				client.datasocket.receive(client.rPacket);
				//convert bytes to a digestible string
				String retstring = new String(client.rPacket.getData(), 0, client.rPacket.getLength());
				System.out.println(retstring);

			}
		} catch (IOException e) {
			e.printStackTrace();
		} // end while
	}  // end main

	class ServerAddr{
		public String hostname;
		public String port;
		ServerAddr(String hostname, String port){
			this.hostname = hostname;
			this.port = port;
		}
		
		ServerAddr(){
			
		}

		//parses string like "192.168.1.101:3033"
		public void parseAddr(String addr){
			String[] addrSplit = addr.split(":");
			if(addrSplit.length != 2){
				throw new IllegalArgumentException("Bad address format.");
			} else {
				this.hostname = addrSplit[0];
				this.port = addrSplit[0];
			}

		}
	}

}
