package client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;


public class TCPClientLaunch implements Runnable{
	private String input_file;
	
	TCPClientLaunch(String input_file){
		this.input_file = input_file;
	}
	@Override
	public void run() {

		try {
			// testfile.txt is a text full of commands. 
			// A command is read from testfile.txt 
			// A TCPClient is created, one request is serviced,
			// then the client is shut down. 
			// The server then waits for a new client to send a request

			String req, list;
			BufferedReader br_req = new BufferedReader(new FileReader(input_file));
			String getLine;
			StringTokenizer st;		    
			String ip, port_id; 

			System.out.println("Starting client test");
			TCPClient client = new TCPClient();
			// Service commands as long as they are in the text file
			while((req = br_req.readLine()) != null){ 

				client.out.println(req);
				System.out.println("[" + input_file + "] Sending " + req);
				String res = client.in.readLine();
				System.out.println("[" + input_file + "] Response: " + res);
			}
			
			
			System.out.println("Finished processing " + input_file + ". Shutting down client.");
			client.close(); 
			
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	public static void main(String args[]){
		String[] input_files = {"test0.txt", "test1.txt"};
		for(String file : input_files){
			Thread t = new Thread(new TCPClientLaunch(file));
			t.start();
		}
		
		//System.exit(0);
	}

}
