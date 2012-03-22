package server;
import java.io.*;
public interface MsgHandler {
    public void handleMsg(Message m, int srcsId, String tag);
    public Message receiveMsg(int fromId) throws IOException;
}