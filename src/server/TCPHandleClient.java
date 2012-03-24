package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.lang.Integer;


public class TCPHandleClient implements Runnable{

	private Socket clientSocket;
	public Theater t;
	private LamportMutex mutex;
	private DirectClock clock;
	private ArrayList<ServerAddr> servers;
	public TCPHandleClient(Socket clientSocket, Theater t, DirectClock clock, ArrayList<ServerAddr> servers, LamportMutex mutex) {
		this.clientSocket = clientSocket;
		this.t = t;
		this.clock = clock;
		this.servers = servers;
		this.mutex = mutex;
	}
	
	public void run() {
		try {
			
		    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
		    BufferedReader in =  new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		    String getLine;
		    while((getLine = in.readLine()) != null){
		    	System.out.println("got command " + getLine);
		    	String[] commandArray = getLine.split(" ");
		    	
		    	//special mutex commands are handled in LamportMutex,
		    	//but server and client commands are handled here.
		    	if(commandArray[0].equals("mutex")){
		    		//if we got a synchronization command, handle it inside LamportMutex
		    		mutex.handleMutexMessage(getLine);
		    	} else if(commandArray[0].equals("server")){
		    		//receiving a server command means another server has CS
		    		//and they made a change they want us to reflect without 
		    		//telling the world or worrying about mutex.
		    		
		    		//remove "server " from beginning of command 
		    		String command = getLine.replace("server", "");
		    		handleCommand(command, out);
		    		
		    	} else {
		    		//otherwise it's a command from a client. Has no prefix
		    		// need to request mutex to make changes, then need to broadcast as a server command
		    		// to all other servers.
		    		
		    		mutex.requestCS();
		    		
		    		handleCommand(getLine, out);
		    		//broadcast to the world
		    		String command = "server " + getLine;
		    		mutex.broadcastMessage(command);
		    		mutex.releaseCS();
		    		
		    		
		    	}
		    	
		    }
		    System.out.println("Closing client socket");
		    this.clientSocket.close();
		    
		    out.close();
		    in.close();
		} 
		catch (IOException e) {
		   System.out.println(e.getMessage());
		}
	}
	
	private void handleCommand(String command, PrintWriter out){
		//otherwise we got a normal client command
		StringTokenizer st = new StringTokenizer(command);
	    String response;
	    String tag = st.nextToken();
	    String name = st.nextToken();

	 
	    if(tag.equals("search")) { //search reserve delete
	    	System.out.println("searching for " + name);
	    	response = t.searchName(name);
	    	out.write(response + "\n");
	    }
	    else if(tag.equals("reserve")){
	    	System.out.println("reserve for " + name);
	    	response = t.reserveSeat(name);
	    	// Update all other theaters
	    	out.write(response + "\n");
	    }
	    else if(tag.equals("delete")){
	    	System.out.println("delete for " + name);
	    	response = t.deleteReservation(name);
	    	out.write(response + "\n");
	    }
	    else if(tag.equals("bookSeat")){
	    	int num = Integer.parseInt(st.nextToken());
	    	response = t.reserveSeat(name, num);
	    	out.write(response + "\n");
	    }
	    else {
	    	out.write("Error: Invalid Request. Please type either\nreserve <name>\n bookSeat <name> <num>\nsearch <name>\ndelete <name>\n");
	    }
	    out.flush();
	}

}
