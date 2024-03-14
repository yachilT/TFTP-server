package bgu.spl.net.impl.tftp;

import java.io.IOException;
import java.util.NoSuchElementException;

import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.impl.packets.AcknowledgePacket;
import bgu.spl.net.impl.packets.BasePacket;
import bgu.spl.net.impl.packets.BroadCastPacket;
import bgu.spl.net.impl.packets.DataPacket;
import bgu.spl.net.impl.packets.ErrorPacket;
import bgu.spl.net.impl.packets.OpCode;
import bgu.spl.net.impl.tftp.transferdatapackets.DataSender;
import bgu.spl.net .impl.tftp.transferdatapackets.FileManager;
import bgu.spl.net.impl.tftp.transferdatapackets.FileReceiver;

public class TftpMessagingProtocol implements MessagingProtocol<BasePacket>{
 
    private boolean terminate;
    private final KeyboardLocker locker;
    private final LastRequest lastRequest;
    public TftpMessagingProtocol(KeyboardLocker locker, LastRequest lastRequest) {
        this.locker = locker;
        this.lastRequest = lastRequest;
    }
    @Override
    public BasePacket process(BasePacket message) {
        BasePacket packetToSend = null;
        packetToSend = message.applyRequest(this);
        return packetToSend;
    }

    @Override
    public boolean shouldTerminate() {
        return terminate;
    }

    public BasePacket handleReceivingData(DataPacket message) {

        BasePacket packetToSend = null;
        try {
            packetToSend = lastRequest.getDataReceiver().receive(message);

            if (message.getSize() < DataPacket.MAX_DATA_SIZE) {
                lastRequest.close();
                locker.notFutureNotify();
            }

        } catch (Exception e /* IOExecption e */) {
            packetToSend = new ErrorPacket((short)2, "Access Violation - File cannot be written");
            try {
                lastRequest.error();
            } catch (IOException e1) {}
            
        }
        
        return packetToSend;
    }
    public BasePacket handleAckPacket(AcknowledgePacket message) {
        System.out.println(message);
        if (lastRequest.getOpCode() == OpCode.WRQ){ 
            try {
                return lastRequest.getDataSender().sendNext(message);
            } catch (IllegalArgumentException e) {
                try { lastRequest.error(); } catch (IOException e1) {}
                return new ErrorPacket((short)0, "Incorrect block number from ACK");

            } catch (IOException e) {
                return new ErrorPacket((short)2, "Access violation - File cannot be read");
            } catch (NoSuchElementException e) {
                try { lastRequest.close(); } catch (IOException e1) {}
                return null;
            }
        }
        else
            return new ErrorPacket((short)0, "Unexpected packet, expected ACK packet");
         
    }
    public BasePacket handleBCASTPacket(BroadCastPacket packet){
        System.out.println(packet.toString());
        return null;
    }

    public BasePacket handleErrorPacket(ErrorPacket packet) {
        System.out.println(packet.toString());
        try {
            lastRequest.error();
        } catch (IOException e) {}
        return null;
    }
}
