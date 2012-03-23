package server;

public interface Lock extends MsgHandler {
    public void requestCS(); //may block
    public void releaseCS();
}
