package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class LamportMutex {
	DirectClock v;
	int[] queue; // request queue
	int myId;
	private DirectClock clock;
	private ArrayList<ServerAddr> servers;
	public LamportMutex(DirectClock clock, ArrayList<ServerAddr> servers) {
		this.clock = clock;
		this.v = new DirectClock(servers.size(), myId);
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
		v.tick();
		queue[myId] = v.getValue(myId);
		broadcastMsg("server request", queue[myId]);
		while (!okayCS()){
			wait();
		}

	}
	public synchronized void releaseCS() {
		q[myId] = Integer.MAX_VALUE;
		broadcastMsg("release", v.getValue(myId));
	}
	boolean okayCS() {
		for (int j = 0; j < N; j++){
			if (isGreater(q[myId], myId, q[j], j))
				return false;
			if (isGreater(q[myId], myId, v.getValue(j), j))
				return false;
		}
		return true;
	}
	boolean isGreater(int entry1, int pid1, int entry2, int pid2) {
		if (entry2 == Symbols.Infinity) return false;
		return ((entry1 > entry2)
				|| ((entry1 == entry2) && (pid1 > pid2)));
	}
	// String command contains the granted Theater command from a server with CS permission.
	public synchronized void handleMsg(Msg m, int src, String tag, String command) {
		int timeStamp = m.getMessageInt();
		v.receiveAction(src, timeStamp);
		if (tag.equals("request")) {
			q[src] = timeStamp;
			sendMsg(src, "ack", v.getValue(myId));
		} else if (tag.equals("release"))
			q[src] = Symbols.Infinity;
		notify(); // okayCS() may be true now
	}

	public synchronized void handleMsg(Msg m, int src, String tag) {
		int timeStamp = m.getMessageInt();
		v.receiveAction(src, timeStamp);
		if (tag.equals("request")) {
			q[src] = timeStamp;
			sendMsg(src, "ack", v.getValue(myId));
		} else if (tag.equals("release"))
			q[src] = Symbols.Infinity;
		notify(); // okayCS() may be true now
	}

	public void broadcastMsg(String message, int priority){
		//send message to everyone
		for(ServerAddr server : servers){
			sendMessage(message, priority, server);
		}
	}

	/**
	 * 
	 * @param message
	 * @param priority
	 * @param dest
	 */
	public void sendMessage(String message, int priority, ServerAddr dest){
		try {
			//open a TCP socket to dest, send the message, and close the socket.
			InetAddress ia = InetAddress.getByName(dest.hostname);


			Socket socket;

			socket = new Socket(ia, Integer.parseInt(dest.port));

			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Could not send server message to " + dest.hostname + ":" + dest.port);
		}

	}
}
