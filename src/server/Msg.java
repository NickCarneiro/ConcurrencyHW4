package server;

import java.util.*;
public class Msg {
    int srcId, dest;
    String tag;
    String msgBuf;
    public Msg(int s, int t, String msgType, String buf) {
        this.srcId = s;
        dest = t;
        tag = msgType;
        msgBuf = buf;
    }
    public String getMsgBuf() {
        return msgBuf;
    }
    public int getMessageInt() {
         StringTokenizer st = new StringTokenizer (msgBuf);
         return Integer.parseInt(st.nextToken());
    }

    public String toString(){
        String s = String.valueOf(srcId)+" " +
                    String.valueOf(dest)+ " " +
                    tag + " " + msgBuf.toString() ;
        return s;
    }
	public int getSrcId() {
		return srcId;
	}
	public String getTag() {
		return tag;
	}
}
