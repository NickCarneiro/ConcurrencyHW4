package client;
import java.net.*;
import java.io.*;

public class UDPClient {
	private String hostname;
	public Integer port;
	public Integer receive_length = 1024;

	public DatagramSocket datasocket;
	public InetAddress ia;
	public DatagramPacket sPacket, rPacket;
	public UDPClient(String hostname, Integer port){
		System.out.println("Initializing UDP Client");

		try {
			this.port = port;
			this.ia = InetAddress.getByName(hostname);
			this.datasocket = new DatagramSocket();

		} catch (UnknownHostException e) {
			System.err.println(e);
		} catch (SocketException se) {
			System.err.println(se);
		}
	}


	public static void run() {
		UDPClient client = new UDPClient("localhost", 8080);
		String line;
		try {
		BufferedReader br = new BufferedReader(new FileReader("testfile.txt"));

		//read in testfile.txt and send the commands to the server.
			while ((line = br.readLine()) != null) {
				byte[] command = line.getBytes();
				System.out.println("Sending command: " + line);
				client.sPacket = new DatagramPacket(command, command.length, client.ia, client.port);
				client.datasocket.send(client.sPacket);
				
				//create space for response
				byte[] rbuffer = new byte[client.receive_length];
				client.rPacket = new DatagramPacket(rbuffer, rbuffer.length);
				//block while waiting for response
				client.datasocket.receive(client.rPacket);
				//convert bytes to a digestible string
				String retstring = new String(client.rPacket.getData(), 0, client.rPacket.getLength());
				System.out.println(retstring);

			}
		} catch (IOException e) {
			e.printStackTrace();
		} // end while
	}  // end main

}
