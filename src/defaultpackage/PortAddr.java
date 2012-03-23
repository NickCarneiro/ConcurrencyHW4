package defaultpackage;

public class PortAddr {
    String hostname;
    int portnum;
    public PortAddr(String s, int i) {
        hostname = new String(s);
        portnum = i;
    }
    public String gethostname() {
        return hostname;
    }
    public int getportnum() {
        return portnum;
    }
}
