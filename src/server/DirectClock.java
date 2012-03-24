package server;
public class DirectClock {
    public int[] clock;
    int myId;
    public DirectClock(int numProc, int id) {
        myId = id;
        clock = new int[numProc];
        for (int i = 0; i < numProc; i++){
        	clock[i] = 0;
        }
        clock[myId] = 1;
    }
    public synchronized int getValue(int i) {
        return clock[i];
    }
    public synchronized void tick() {
        clock[myId]++;
    }
    public void sendAction() {
        // sentValue = clock[myId];
        tick();
    }
    public synchronized void receiveAction(int sender, int sentValue) {
        clock[sender] = java.lang.Math.max(clock[sender], sentValue);
        clock[myId] = java.lang.Math.max(clock[myId], sentValue) + 1;
    }
}
