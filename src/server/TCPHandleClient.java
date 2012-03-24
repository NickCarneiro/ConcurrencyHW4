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
	public TCPHandleClient(Socket clientSocket, Theater t, DirectClock clock, ArrayList<ServerAddr> servers) {
		this.clientSocket = clientSocket;
		this.t = t;
		this.clock = clock;
		this.servers = servers;
	}
	
	public void run() {
		try {
			mutex = new LamportMutex(clock, servers);
		    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
		    BufferedReader in =  new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		    String getLine;
		    while((getLine = in.readLine()) != null){
		    	String[] commandArray = getLine.split(" ");
		    	if(commandArray[0].equals("mutex")){
		    		//if we got a synchronization command, handle it inside LamportMutex
		    		mutex.handleServerMessage(getLine);
		    	} else {
		    		//otherwise we got a normal client command
		    		StringTokenizer st = new StringTokenizer(getLine);
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
				    	mutex.requestCS();
				    	response = t.reserveSeat(name);
				    	// Update all other theaters
				    	mutex.releaseCS();
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
		    System.out.println("Closing client socket");
		    this.clientSocket.close();
		    
		    out.close();
		    in.close();
		} 
		catch (IOException e) {
		   System.out.println(e.getMessage());
		}
	}

}
