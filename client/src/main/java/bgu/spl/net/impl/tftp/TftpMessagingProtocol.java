package bgu.spl.net.impl.tftp;

import java.io.IOException;
import java.util.NoSuchElementException;

import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.impl.packets.AcknowledgePacket;
import bgu.spl.net.impl.packets.BasePacket;
import bgu.spl.net.impl.packets.DataPacket;
import bgu.spl.net.impl.packets.ErrorPacket;
import bgu.spl.net.impl.packets.OpCode;
import bgu.spl.net.impl.tftp.transferdatapackets.DataSender;
import bgu.spl.net .impl.tftp.transferdatapackets.FileManager;
import bgu.spl.net.impl.tftp.transferdatapackets.FileReceiver;

public class TftpMessagingProtocol implements MessagingProtocol<BasePacket>{
    private boolean receivingData;
    private FileReceiver dataReceiver;

    private boolean sendingData;
    private DataSender dataSender;

    private final FileManager fileManager;
    private boolean terminate;
    private final KeyboardLocker locker;
    private final LastRequest lastRequest;
    public TftpMessagingProtocol(FileManager fileManger, KeyboardLocker locker, LastRequest lastRequest) {
        this.fileManager = fileManger;
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
            packetToSend = dataReceiver.receive(message);

            if (((DataPacket)message).getSize() < DataPacket.MAX_DATA_SIZE) {
                receivingData = false;
                locker.notFutureNotify();
                
                try { 
                    dataReceiver.close(); 
                    fileManager.addFile(dataReceiver.getfileName());
                } catch (IOException e1) {}
            }

        } catch (IOException e) {
            packetToSend = new ErrorPacket((short)2, "Access Violation - File cannot be written");
            receivingData = false;
            try {
                dataReceiver.error();
            } catch (IOException e1) {}
            
        }
        
        return packetToSend;
    }

    private BasePacket handleSendingData(BasePacket message) {
        if (message.getOpCode() == OpCode.ACK) {
            try {
                 return dataSender.sendNext((AcknowledgePacket)message);
            } catch (IllegalArgumentException e) {
                sendingData = false;
                try {
                    dataSender.error();
                } catch (IOException e1) {}
                return new ErrorPacket((short)0, "Incorrect block number from ACK");
            } catch (IOException e) {
                return new ErrorPacket((short)2, "Access violation - File cannot be read");
            } catch (NoSuchElementException e) {
                sendingData = false;
                return null;
            }
        }
        else
            return new ErrorPacket((short)0, "Unexpected packet, expected ACK packet");
    }

    public BasePacket handleAckPacket(AcknowledgePacket packet) {
        if (ackAfterWRQ) 
    }
}
