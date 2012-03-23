package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.StringTokenizer;

public class TCPClient {
	private Integer port;
	private String host;
	PrintWriter out;
	BufferedReader in;
	BufferedReader list;
	
	Socket socket;
	
	public TCPClient(String host, Integer port) throws UnknownHostException, IOException{
		this.port = port;
		socket = new Socket(host, this.port);
		out = new PrintWriter(socket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	public void close() throws IOException{
		this.out.close();
		this.in.close();
		this.socket.close();
	}
	
	public static void main(String[] args){

		try {
			// testfile.txt is a text full of commands. 
			// A command is read from testfile.txt 
			// A TCPClient is created, one request is serviced,
			// then the client is shut down. 
			// The server then waits for a new client to send a request
			
			String req, list;
			BufferedReader br_req = new BufferedReader(new FileReader("testfile.txt"));
			BufferedReader br_list = new BufferedReader(new FileReader("serverList.txt"));
			ArrayList<String> serverList = new ArrayList<String>();
			HashMap<String,String> serverMap = new HashMap<String,String>();
			String getLine;
		    StringTokenizer st;		    
		    String ip, port_id; 
		    
		    while((getLine = br_list.readLine()) != null){
		    	st = new StringTokenizer(getLine);
		    	ip = st.nextToken();
		    	port_id = st.nextToken();
		    	serverList.add(ip);
		    	serverMap.put(ip, port_id);
		    }
		    Integer serverSize = serverList.size();
			// Service commands as long as they are in the text file
			while((req = br_req.readLine()) != null){ 
				// Select a server to use
				//Insert Code Here{
				// code
				//}
				System.out.println("Starting client");
				TCPClient client = new TCPClient("localhost", 8080);
				//System.out.println("Sending message.");			
				client.out.println(req);
				System.out.println(req);
				String res;
				while((res = client.in.readLine()) != null){
					System.out.println(res);
				}
				System.out.println("Got response from server. Shutting down client.");
				client.close(); 
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

	}
}

