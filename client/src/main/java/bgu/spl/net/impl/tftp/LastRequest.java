package bgu.spl.net.impl.tftp;

import bgu.spl.net.impl.packets.BasePacket;
import bgu.spl.net.impl.packets.OpCode;
import bgu.spl.net.impl.packets.ReadRQPacket;
import bgu.spl.net.impl.tftp.transferdatapackets.DataReceiver;
import bgu.spl.net.impl.tftp.transferdatapackets.DataSender;
import bgu.spl.net.impl.tftp.transferdatapackets.FileReceiver;

public class LastRequest {
    private OpCode requestOpCode;
    private DataReceiver dataReciever;
    private DataSender dataSender;

    public void LastRequest() {
        requestOpCode = OpCode.UNDEFINED;
    }

    public synchronized void setOpcode(BasePacket packet) {
        this.requestOpCode = packet.getOpCode();
    }
    
    public synchronized void setOpcode(ReadRQPacket packet) {
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
