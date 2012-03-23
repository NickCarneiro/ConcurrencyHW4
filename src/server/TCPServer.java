package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
	Theater t;
	ServerSocket listener;
	private int port;
	
	public TCPServer(Integer port){
		System.out.println("Initializing TCP Server");

		this.port = port;
		t = new Theater(100);
		try{
			
			listener = new ServerSocket(this.port);
		    while(true){
		    	
		    	System.out.println("Waiting for a client connection...");
			    Socket clientSocket = listener.accept();
			    System.out.println("Got a client connection.");
			    Thread thread = new Thread(new TCPHandleClient(clientSocket, t));
			    thread.start();
			    
			   
		    }
		    
		} catch (IOException e) {
			   System.out.println(e.getMessage());
		} finally {
			System.out.println("Shutting down Socket");
		    try {
				listener.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args){
		TCPServer server = new TCPServer(8080);
		System.out.println("Starting TCP Server");
	}
}
