package server;

import java.util.*;
import java.net.*;
import java.io.*;

import defaultpackage.PortAddr;
import defaultpackage.Symbols;
public class Connector {
    ServerSocket listener;
    Socket [] link;
    public void Connect(String basename, int myId, int numProc,
    BufferedReader[] dataIn,
    PrintWriter[] dataOut) throws Exception {
        Name myNameclient = new Name();

        link = new Socket[numProc];/* Get as many sockets as # of processes */
        int localport = getLocalPort(myId);/* create a serverSocket for myself */
        listener = new ServerSocket(localport);

        /* register in the name server */
        myNameclient.insertName(basename + myId,
        (InetAddress.getLocalHost()).getHostName(), localport);

        /* accept connections from all the smaller processes */
        for (int i = 0; i < myId; i++) {
            Socket s = listener.accept();
            BufferedReader dIn = new BufferedReader(
            new InputStreamReader(s.getInputStream()));
            String getline = dIn.readLine();
            StringTokenizer st = new StringTokenizer(getline);
            int hisId = Integer.parseInt(st.nextToken());
            int destId = Integer.parseInt(st.nextToken());
            String tag = st.nextToken();
            if (tag.equals("hello")) {
                link[hisId] = s;
                dataIn[hisId] = dIn;
                dataOut[hisId] = new PrintWriter(s.getOutputStream());
            }
        }
        /* contact all the bigger processes */
        for (int i = myId + 1; i < numProc; i++) {
        	PortAddr addr;
            do {
                addr = myNameclient.searchName(basename + i);
                Thread.sleep(100);
            } while (addr.getportnum() == -1);
            link[i] = new Socket(addr.gethostname(), addr.getportnum());
            dataOut[i] = new PrintWriter(link[i].getOutputStream());
            dataIn[i] = new BufferedReader(new
            InputStreamReader(link[i].getInputStream()));
            /* send a hello message to P_i */
            dataOut[i].println(myId + " " + i + " " + "hello" + " " + "null");
            dataOut[i].flush();
        }
    }
    int getLocalPort(int id) {
        return Symbols.ServerPort + 10 + id;
    }
    public void closeSockets(){
        try {
            listener.close();
            for (int i=0;i<link.length; i++)
                link[i].close();
        } catch (Exception e) {System.err.println(e);}
    }
}
