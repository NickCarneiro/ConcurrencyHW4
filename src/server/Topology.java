package server;

import java.io.*;
import java.util.*;
public class Topology {
    public static void readNeighbors(int myId, int N,
                                     LinkedList<Integer> neighbors) {
        System.out.println("Reading topology");
        try {
            BufferedReader dIn = new BufferedReader(
                                new FileReader("topology" + myId));
            StringTokenizer st = new StringTokenizer(dIn.readLine());
            while (st.hasMoreTokens()) {
                int neighbor = Integer.parseInt(st.nextToken());
                neighbors.add(neighbor);
            }
        } catch (FileNotFoundException e) {
            for (int j = 0; j < N; j++)
                if (j != myId) neighbors.add(j);
        } catch (IOException e) {
            System.err.println(e);
        }
        System.out.println(neighbors.toString());
    }
}