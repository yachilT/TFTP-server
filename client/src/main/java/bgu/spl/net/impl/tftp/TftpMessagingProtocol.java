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

public class TftpMessagingProtocol implements MessagingProtocol<BasePacket>{
 
    private boolean terminate;
    private final CurrentRequest currentRequest;
    public TftpMessagingProtocol(CurrentRequest currentRequest) {
        this.currentRequest = currentRequest;
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
            packetToSend = currentRequest.getDataReceiver().receive(message);

            if (message.getSize() < DataPacket.MAX_DATA_SIZE) {
                currentRequest.close();
                currentRequest.finishRequest();
            }

        } catch (Exception e) {
            packetToSend = new ErrorPacket((short)2, "Access Violation - File cannot be written");
            try {
                currentRequest.error();
            } catch (IOException e1) {}
            currentRequest.finishRequest();
            
        }
        
        return packetToSend;
    }

    public BasePacket handleAckPacket(AcknowledgePacket message) {
        System.out.println(message);
        if (currentRequest.getOpCode() == OpCode.WRQ){ 
            try {
                if (message.getBlockNumber() == 0) {
                    return currentRequest.getDataSender().sendFirst();
                }
                else
                    return currentRequest.getDataSender().sendNext(message);
                
            } catch (IllegalArgumentException e) {

                try { currentRequest.error(); } catch (IOException e1) {}
                return new ErrorPacket((short)0, "Incorrect block number from ACK");

            } catch (IOException e) {

                return new ErrorPacket((short)2, "Access violation - File cannot be read");

            } catch (NoSuchElementException e) {

                try { currentRequest.close(); } catch (IOException e1) {}
                
            }
        }
        else if (currentRequest.getOpCode() == OpCode.DISC) {
            terminate = true;
        }

        currentRequest.markAsDone();
        return null;
    }
    public BasePacket handleBCASTPacket(BroadCastPacket packet){
        System.out.println(packet.toString());
        return null;
    }

    public BasePacket handleErrorPacket(ErrorPacket packet) {
        System.out.println(packet);
        try {
            currentRequest.error();
        } catch (IOException e) {}
        currentRequest.markAsDone();
        return null;
    }
}
