package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class LamportMutex {
	int[] queue; // request queue
	int myId;
	private DirectClock clock;
	private ArrayList<ServerAddr> servers;
	public LamportMutex(DirectClock clock, ArrayList<ServerAddr> servers) {
		this.clock = clock;
		this.queue = new int[servers.size()];
		this.servers = servers;

		//each slot in queue array represents process id at that index.
		//the integer value in each slot determines priority

		//initialize every process to lowest possible priority
		for (int j = 0; j < servers.size(); j++){
			queue[j] = Integer.MAX_VALUE;
		}

	}
	public synchronized void requestCS() {
		try {
			//ticks the clock
			clock.sendAction();
			
			queue[myId] = clock.getValue(myId);
			
			//syntax: mutex {request | release | ok} <my id> <my clock value> <my timestamp>
			broadcastMessage("mutex request " + myId + " " + clock.getValue(myId) + " " + queue[myId]);
			while (!okayCS()){
				printQueue();
				printClock();
				System.out.println("okayCS was false. Waiting...");
				wait();
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public synchronized void releaseCS() {
		queue[myId] = Integer.MAX_VALUE;
		//syntax: mutex {request | release | ok} <my id> <my clock value> <my timestamp>
		broadcastMessage("mutex release " + myId + " " + clock.getValue(myId) + " " + queue[myId]);
	}
	
	//returns true when this server has the lowest timestamp of all servers in the clock array
	boolean okayCS() {
		for (int j = 0; j < servers.size(); j++){
			if (isGreater(queue[myId], myId, queue[j], j)){
				return false;
			}
				
			if (isGreater(queue[myId], myId, clock.getValue(j), j)){
				return false;
			}
				
		}
		return true;
	}
	
	//returns true if first entry1 is greater
	boolean isGreater(int entry1, int pid1, int entry2, int pid2) {
		if (entry2 == Integer.MAX_VALUE){
			return false;
		}
		
		return ((entry1 > entry2) || ((entry1 == entry2) && (pid1 > pid2)));
	}


	//if the server gets any messages prefixed with "mutex", it sends them here
	//The server itself handles "command" messages that contain regular theater commands

	//syntax: mutex {request | release | ack} <my id> <my clock value> <my request timestamp>
	//examples: mutex request 0  3
	public synchronized void handleMutexMessage(String message) {
		String[] messageArray = message.split(" ");
		if(!messageArray[0].equals("mutex")){
			throw new IllegalArgumentException("Non-mutex command passed to handleMutexrMessage: " + message);
		}
		
		if(messageArray.length != 5){
			throw new IllegalArgumentException("Invalid number of tokens in mutex message: " + message);
		}
		
		String command = messageArray[1];
		int msg_id = Integer.parseInt(messageArray[2]);
		
		//clock timestamp
		int msg_clock = Integer.parseInt(messageArray[3]);
		
		//queue timestamp
		int msg_timestamp = Integer.parseInt(messageArray[4]);
		
		clock.receiveAction(msg_id, msg_clock);
		if (command.equals("request")) {
			queue[msg_id] = msg_clock;
			sendMessage("mutex ack " + myId + " " + clock.getValue(myId) + " " + queue[myId], servers.get(myId));
		} else if (command.equals("release")){
			queue[msg_id] = Integer.MAX_VALUE;
		} else if (command.equals("ack")) {
			// ack <my id> <my clock value>
			int ack_id = Integer.parseInt(messageArray[2]);
			int ack_clock = Integer.parseInt(messageArray[3]);
			
			queue[msg_id] = msg_timestamp;
			
		} else {
			throw new IllegalArgumentException("Bad mutex command syntax: " + message);
		}
			
		notify(); // okayCS() may be true now
		
		System.out.println("");
	}

	
	public void broadcastMessage(String message){
		//send message to everyone
		int server_index = 0;
		for(ServerAddr server : servers){
			//only send messages to servers that aren't me.
			if(server_index != myId){
				sendMessage(message, server);
			}
			
			server_index++;
		}
	}

	/**
	 * Sends a string to another server. makes no assumptions about content of the string. 
	 * Only concerned with delivery.
	 * @param message
	 * @param priority
	 * @param dest
	 */
	public void sendMessage(String message, ServerAddr dest){
		try {
			clock.sendAction();
			//open a TCP socket to dest, send the message, and close the socket.
			InetAddress ia = InetAddress.getByName(dest.hostname);


			Socket socket;

			socket = new Socket(ia, Integer.parseInt(dest.port));

			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

			out.write(message);
			out.flush();
			socket.close();

		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Could not send server message to " + dest.hostname + ":" + dest.port);
		}

	}
	
	/**
	 * debug function to see state of queue
	 */
	private void printQueue(){
		System.out.print("queue timestamps: ");
		for(int i = 0; i < queue.length; i++){
			System.out.print(i + "["+ queue[i] +"] ");
		}
	}
	
	private void printClock(){
		System.out.print("clock timestamps: ");
		for(int i = 0; i < clock.clock.length; i++){
			System.out.print(i + "["+ clock.getValue(i) +"] ");
		}
		System.out.println("");
	}
}
