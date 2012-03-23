package server;

import java.util.*;
import java.io.*;
import java.net.Socket;

import defaultpackage.PortAddr;
import defaultpackage.Symbols;

public class Name {
    BufferedReader din;
    PrintStream pout;
    public void getSocket() throws IOException {
        Socket server = new Socket(Symbols.nameServer, Symbols.ServerPort);
        din = new BufferedReader(
        		new InputStreamReader(server.getInputStream()));
        pout = new PrintStream(server.getOutputStream());
    }
    public int insertName(String name, String hname, int portnum)
            throws IOException {
        getSocket();
        pout.println("insert " + name + " " + hname + " " + portnum);
        pout.flush();
        return Integer.parseInt(din.readLine());
    }
    public PortAddr searchName(String name) throws IOException {
        getSocket();
        pout.println("search " + name);
        pout.flush();
        String result = din.readLine();
        StringTokenizer st = new StringTokenizer(result);
        int portnum = Integer.parseInt(st.nextToken());
        String hname = st.nextToken();
        return new PortAddr(hname, portnum);
    }
    /*public void clear() throws IOException {
        getSocket();
        pout.println("clear " );
        pout.flush();
        server.close();
    }
    */
    public static void main(String[] args) {
        Name myClient = new Name();
        try {
            myClient.insertName("hello1", "oak.ece.utexas.edu", 1000);
            PortAddr pa = myClient.searchName("hello1");
            System.out.println(pa.gethostname() + ":" + pa.getportnum());
        } catch (Exception e) {
            System.err.println("Server aborted:" + e);
        }
    }
}

