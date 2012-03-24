package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class TCPServer implements Runnable{
	Theater t;
	ServerSocket listener;
	private int port;
	private int id;
	private ArrayList<ServerAddr> servers;
	private DirectClock clock;
	private LamportMutex mutex;
	public TCPServer(Integer port, Integer id, ArrayList<ServerAddr> servers){
		this.port = port;
		this.id = id;
		this.servers = servers;
		this.clock = new DirectClock(servers.size(), id);
		this.mutex = new LamportMutex(clock, servers, id);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("Running TCP Server");

		t = new Theater(100);
		try{
			
			listener = new ServerSocket(this.port);
		    while(true){
		    	
		    	//System.out.println("Waiting for a client connection...");
			    Socket clientSocket = listener.accept();
			    //System.out.println("Got a client connection.");
			    Thread thread = new Thread(new TCPHandleClient(clientSocket, t, clock, servers, mutex, this.id));
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
}
