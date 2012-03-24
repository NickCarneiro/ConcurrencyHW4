package server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/*
 * Use the main method to launch a server for every address specified in servers.txt
 */
public class TCPServerLaunch {
	private static ArrayList<ServerAddr> servers;

	public static void main(String[] args){
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader("servers.txt"));
			servers = new ArrayList<ServerAddr>();
			String line;
			while ((line = br.readLine()) != null) {
				ServerAddr address = new ServerAddr();
				address.parseAddr(line);
				servers.add(address);
			}

			//launch a server in a separate thread for each line in servers.txt

			for(int i = 0; i < servers.size(); i++){

				//give each server a unique id and a full list of servers
				Thread thread = new Thread(new TCPServer(Integer.parseInt(servers.get(i).port), i, servers));
				System.out.println("Starting TCP Server "+ i + " on port " + servers.get(i).port);
				thread.start();
			}

		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
