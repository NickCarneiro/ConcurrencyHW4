package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;
import java.lang.Integer;


public class TCPHandleClient implements Runnable{

	private Socket clientSocket;
	public Theater t;
	
	public TCPHandleClient(Socket clientSocket, Theater t) {
		this.clientSocket = clientSocket;
		this.t = t;
	}
	
	public void run() {
		try {
		    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
		    BufferedReader in =  new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		    String getLine = in.readLine();
		    StringTokenizer st = new StringTokenizer(getLine);
		    
		    String response;
		    String tag = st.nextToken();
		    String name = st.nextToken();
		    
		    if(tag.equals("search")) { //search reserve delete
		    	System.out.println("searching for " + name);
		    	response = t.searchName(name);
		    	out.write(response);
		    }
		    else if(tag.equals("reserve")){
		    	System.out.println("reserve for " + name);
		    	response = t.reserveSeat(name);
		    	out.write(response);
		    }
		    else if(tag.equals("delete")){
		    	System.out.println("delete for " + name);
		    	response = t.deleteReservation(name);
		    	out.write(response);
		    }
		    else if(tag.equals("bookSeat")){
		    	int num = Integer.parseInt(st.nextToken());
		    	response = t.reserveSeat(name, num);
		    	out.write(response);
		    }
		    else {
		    	out.write("Error: Invalid Request. Please type either\nreserve <name>\n bookSeat <name> <num>\nsearch <name>\ndelete <name>\n");
		    }
		    out.flush();
		    out.close();
		    in.close();
		} 
		catch (IOException e) {
		   System.out.println(e.getMessage());
		}
	}

}
