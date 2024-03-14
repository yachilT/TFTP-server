package bgu.spl.net.impl.tftp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

import bgu.spl.net.impl.packets.*;
import bgu.spl.net.impl.tftp.transferdatapackets.*;

public class CurrentRequest {
    private BasePacket requestPacket;
    private DataReceiver dataReciever;
    private DataSender dataSender;
    private volatile boolean done;
    private volatile boolean shouldFinish;

    public CurrentRequest() {
        this.requestPacket = null;
        this.dataReciever = null;
        this.dataSender = null;
        this.done = true;
    }

    private void startRequest(BasePacket packet) {
        this.requestPacket = packet;
        this.done = false;
    }
    public synchronized void setRequest(BasePacket packet) {
        startRequest(packet);
    }
    
    public synchronized void setRequest(ReadRQPacket packet) throws IOException, FileAlreadyExistsException {
        startRequest(packet);
        dataReciever = new FileReceiver(packet.getFileName());
    }
    public synchronized void setRequest(WriteRQPacket packet) throws FileNotFoundException{
        startRequest(packet);
        dataSender = new FileSender(packet.getFileName());
    }
    public synchronized void setRequest(DirectoryRQPacket packet){
        startRequest(packet);
        dataReciever = new DirectoryReciever();
    }
    private void reset() {
        this.requestPacket = null;;
        dataReciever = null;
        dataSender = null;
        done = false;
    }
    public void close() throws IOException {
        if (getOpCode() == OpCode.RRQ)
            System.out.println(requestPacket.toString() + " complete");

        if (getOpCode() == OpCode.WRQ) 
            System.out.println(requestPacket.toString() + " complete");
        if(dataReciever != null)
            dataReciever.close();
        if(dataSender != null)
            dataSender.close();
        reset();
    }
    public void error() throws IOException {
        if(dataReciever != null)
            dataReciever.error();
        if(dataSender != null)
            dataSender.error();
        reset();
    }
    public synchronized OpCode getOpCode() {
        return requestPacket.getOpCode();
    }

    public synchronized DataReceiver getDataReceiver() {
        return dataReciever;
    }

    public synchronized DataSender getDataSender() {
        return dataSender;
    }

    public synchronized boolean isDone() {
        return done;
    }

    public synchronized void finishRequest() {
        shouldFinish = true;
    }

    public synchronized boolean shouldFinish() {
        return shouldFinish;
    }

    public synchronized void markAsDone() {
        done = true;
        shouldFinish = false;
    }
    
}
