package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.lang.Integer;


public class UDPHandleClient implements Runnable{

	private DatagramPacket datapacket;
	public Theater t;
	int port;
	private DatagramSocket datasocket;
	
	public UDPHandleClient(DatagramPacket datapacket, Theater t, DatagramSocket datasocket) {
		this.datapacket = datapacket;
		this.t = t;
		this.datasocket = datasocket;
	}
	
	public void run() {
		try {
			String getLine = new String(datapacket.getData(), 0, datapacket.getLength());
		    StringTokenizer st = new StringTokenizer(getLine);
		    
		    String response;
		    String tag = st.nextToken();
		    String name = st.nextToken();
		    
		    if(tag.equals("search")) { //search reserve delete
		    	System.out.println("searching for " + name);
		    	response = t.searchName(name);
		    }
		    else if(tag.equals("reserve")){
		    	System.out.println("reserve for " + name);
		    	response = t.reserveSeat(name);
		    }
		    else if(tag.equals("delete")){
		    	System.out.println("delete for " + name);
		    	response = t.deleteReservation(name);
		    }
		    else if(tag.equals("bookSeat")){
		    	int num = Integer.parseInt(st.nextToken());
		    	response = t.reserveSeat(name, num);
		    }
		    else {
		    	response = "Error: Invalid Request. Please type either\nreserve <name>\n bookSeat <name> <num>\nsearch <name>\ndelete <name>\n";
		    }
		    DatagramPacket returnpacket = new DatagramPacket(
                    response.getBytes(),
                    response.length(),
                    datapacket.getAddress(),
                    datapacket.getPort());
            datasocket.send(returnpacket);
		} 
		catch (IOException e) {
		   e.printStackTrace();
		}
	}

}

