package server;

import java.io.*;
public interface MsgHandler {
    public void handleMsg(Msg m, int srcsId, String tag);
    public Msg receiveMsg(int fromId) throws IOException;
}