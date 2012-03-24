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
		this.clock = new DirectClock(servers.size(), myId);
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
			clock.tick();
			queue[myId] = clock.getValue(myId);
			
			//syntax: mutex {request | release | ok} <my id> <my clock value> <my timestamp>
			broadcastMessage("mutex request " + myId + " " + clock.getValue(myId) + " " + queue[myId]);
			while (!okayCS()){
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
		broadcastMessage("mutex release " + myId + clock.getValue(myId) + " " + queue[myId]);
	}
	boolean okayCS() {
		for (int j = 0; j < servers.size(); j++){
			if (isGreater(queue[myId], myId, queue[j], j))
				return false;
			if (isGreater(queue[myId], myId, clock.getValue(j), j))
				return false;
		}
		return true;
	}
	boolean isGreater(int entry1, int pid1, int entry2, int pid2) {
		if (entry2 == Integer.MAX_VALUE) return false;
		return ((entry1 > entry2)
				|| ((entry1 == entry2) && (pid1 > pid2)));
	}


	//if the server gets any messages prefixed with "mutex", it sends them here
	//The server itself handles "command" messages that contain regular theater commands

	//syntax: mutex {request | release | ok} <my id> <my clock value> <my timestamp>
	//examples: mutex request 0  3
	public synchronized void handleServerMessage(String message) {
		String[] messageArray = message.split(" ");
		String command = messageArray[1];
		int msg_id = Integer.parseInt(messageArray[2]);
		int msg_clock = Integer.parseInt(messageArray[3]);
		int timestamp;
		if(messageArray.length == 5){
			timestamp = Integer.parseInt(messageArray[4]);
		}
		clock.receiveAction(msg_id, msg_clock);
		if (command.equals("request")) {
			queue[msg_id] = msg_clock;
			sendMessage("mutex ack " + myId + " " + clock.getValue(myId), servers.get(myId));
		} else if (command.equals("release"))
			queue[msg_id] = Integer.MAX_VALUE;
		notify(); // okayCS() may be true now
	}

	public void broadcastMessage(String message){
		//send message to everyone
		for(ServerAddr server : servers){
			sendMessage(message, server);
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
}
