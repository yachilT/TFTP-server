package bgu.spl.net.impl.tftp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

import bgu.spl.net.impl.packets.*;
import bgu.spl.net.impl.tftp.transferdatapackets.*;

public class LastRequest {
    private OpCode requestOpCode;
    private DataReceiver dataReciever;
    private DataSender dataSender;

    public void LastRequest() {
        requestOpCode = OpCode.UNDEFINED;
        this.dataReciever = null;
        this.dataSender = null;
    }

    public synchronized void setRequest(BasePacket packet) {
        this.requestOpCode = packet.getOpCode();
    }
    
    public synchronized void setRequest(ReadRQPacket packet) throws IOException, FileAlreadyExistsException {
        this.requestOpCode = packet.getOpCode();
        dataReciever = new FileReceiver(packet.getFileName());
    }
    public synchronized void setRequest(WriteRQPacket packet) throws FileNotFoundException{
        this.requestOpCode = packet.getOpCode();
        dataSender = new FileSender(packet.getFileName());
    }
    public synchronized void setRequest(DirectoryRQPacket packet){
        this.requestOpCode = packet.getOpCode();
        dataReciever = new DirectoryReciever();
    }
    private void reset() {
        this.requestOpCode = OpCode.UNDEFINED;
        dataReciever = null;
        dataSender = null;
    }
    public void close() throws IOException {
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
        return requestOpCode;
    }

    public synchronized DataReceiver getDataReceiver() {
        return dataReciever;
    }

    public synchronized DataSender getDataSender() {
        return dataSender;
    }
    
}
