package client;

import java.io.BufferedReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.StringTokenizer;

import server.ServerAddr;

public class TCPClient {
	private ArrayList<ServerAddr> servers;
	private ServerAddr current_server;
	public Integer port;
	public Integer receive_length = 1024;
	private InetAddress ia;
	private String host;
	protected PrintWriter out;
	protected BufferedReader in;

	
	Socket socket;
	
	public TCPClient() throws UnknownHostException, IOException{
		
		System.out.println("Initializing TCP Client");

		//import list of servers
		servers = new ArrayList<ServerAddr>();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader("client_servers.txt"));

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
				System.out.println(addr.port);
				this.port = Integer.parseInt(addr.port);
				this.ia = InetAddress.getByName(addr.hostname);
				
				
				socket = new Socket(this.ia, this.port);
				socket.setSoTimeout(5000);
				out = new PrintWriter(socket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				
				//if we get here, we have a valid hostname
				break;

			} catch (UnknownHostException e) {
				System.err.println("Could not connect to server " + addr.hostname + ":" + addr.port + e);
			} catch (SocketException se) {
				System.err.println("Could not connect to server " + addr.hostname + ":" + addr.port + se);
			}
		}
		
		
	}

	public void close() throws IOException{
		this.out.close();
		this.in.close();
		this.socket.close();
	}
	
	
}

