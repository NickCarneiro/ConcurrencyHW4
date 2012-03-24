package client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class TestSuite {

	public static void main(String[] args){

		// run some tests and make sure the output is the same
		TCPTest();

	}

	/*
	 * Test basic correctness
	 */
	private static void TCPTest(){

		try {
			// testfile.txt is a text full of commands. 
			// A command is read from testfile.txt 
			// A TCPClient is created, one request is serviced,
			// then the client is shut down. 
			// The server then waits for a new client to send a request

			String req, list;
			BufferedReader br_req = new BufferedReader(new FileReader("testfile.txt"));
			String getLine;
			StringTokenizer st;		    
			String ip, port_id; 

			System.out.println("Starting client test");
			TCPClient client = new TCPClient();
			// Service commands as long as they are in the text file
			while((req = br_req.readLine()) != null){ 

				client.out.println(req);
				System.out.println("Sending " + req);
				String res = client.in.readLine();
				System.out.println("Response: " + res);
			}
			
			
			System.out.println("Got response from server. Shutting down client.");
			client.close(); 
			
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

	}

}
