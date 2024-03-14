package bgu.spl.net.impl.tftp;

public class KeyboardLocker {
    private boolean notify;
    public KeyboardLocker(){
        notify = true;
    }
    
    public synchronized void futureNotify() {
        notify = true;
    }

    public synchronized void notFutureNotify() {
        notify = false;
    }
    
}
