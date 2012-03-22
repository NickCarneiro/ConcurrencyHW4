package server;
import java.net.*;
import java.io.*;

public class UDPServer {
	public static void main(String[] args) {
		System.out.println("Starting UDP Server");
		int port = 8080; 
		int len = 1024;

		Theater	t = new Theater(100);
		DatagramPacket datapacket;
		try {
			DatagramSocket datasocket = new DatagramSocket(port);
			byte[] buf = new byte[len];
			datapacket = new DatagramPacket(buf, buf.length);
			while (true) {
				try {
					datasocket.receive(datapacket);
					UDPHandleClient stuffs = new UDPHandleClient(datapacket, t, datasocket);
					System.out.println("Got UDP request. Spawning thread.");
					new Thread(stuffs).run();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (SocketException se) {
			se.printStackTrace();
		}
	}	
}
