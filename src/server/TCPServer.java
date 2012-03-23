package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;

public class TCPServer {
	//Theater t;
	ServerSocket serverSocket;
	private int port;
	
	public TCPServer(Integer port, Theater t, serverTextFile serverList){
		this.port = port;
		//this.t = t;
		try{
		    while(true){
		    	serverSocket = new ServerSocket(this.port);
		    	InetAddress ipAddress = serverSocket.getInetAddress();
		    	serverList.add(port, ipAddress);
		    	System.out.println("Waiting for a client connection...");
			    Socket clientSocket = serverSocket.accept();
			    System.out.println("Got a client connection.");
			    Thread thread = new Thread(new HandleClient(clientSocket, t));
			    thread.start();
			    System.out.println("Shutting down Socket");
			    serverSocket.close();
		    }
		} catch (IOException e) {
			   System.out.println(e.getMessage());
		}
	}
	
	
	
	public static void main(String[] args){
		Theater t = new Theater(100);
		serverTextFile serverList = new serverTextFile();
		serverList.clear();
		TCPServer server0 = new TCPServer(8080, t, serverList);
		TCPServer server1 = new TCPServer(8022, t, serverList);
		TCPServer server2 = new TCPServer(8013, t, serverList);

		System.out.println("Starting Server");
	}
}
