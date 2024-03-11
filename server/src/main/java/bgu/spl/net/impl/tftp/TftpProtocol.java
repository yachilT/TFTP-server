package bgu.spl.net.impl.tftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.NoSuchElementException;
import java.util.Set;

import bgu.spl.net.api.BidiMessagingProtocol;
import bgu.spl.net.impl.tftp.packets.*;
import bgu.spl.net.impl.tftp.transferdatapackets.DataSender;
import bgu.spl.net.impl.tftp.transferdatapackets.DirectorySender;
import bgu.spl.net.impl.tftp.transferdatapackets.FileReceiver;
import bgu.spl.net.impl.tftp.transferdatapackets.FileSender;
import bgu.spl.net.srv.Connections;

public class TftpProtocol implements BidiMessagingProtocol<BasePacket>  {
    private Connections<BasePacket> connections;
    private int currentClientId;
    private boolean isTerminated;
    private boolean sendingData;
    private DataSender dataSender;

    private boolean receivingData;
    private FileReceiver dataReceiver;
    private String username;

    public TftpProtocol(Connections<BasePacket> connections, int currentClientId){
        this.connections = connections;
        this.currentClientId = currentClientId;
        this.isTerminated = false;
        this.username = null;
    }
    public TftpProtocol() {
        this.sendingData = false;
        this.dataSender = null;
    
        this.receivingData = false;
        this.dataReceiver = null;
    }
    @Override
    public void start(int connectionId, Connections<BasePacket> connections) {
        this.currentClientId = connectionId;
        this.connections = connections;
    }

    @Override
    public void process(BasePacket message) {
        BasePacket returnPacket = null;
        if (sendingData){
            if (message.getOpCode() == OpCode.ACK) {
                try {
                    returnPacket = dataSender.sendNext((AcknowledgePacket)message);
                } catch (IllegalArgumentException e) {
                    returnPacket = new ErrorPacket((short)0, "Incorrect block number from ACK");
                } catch (IOException e) {
                    returnPacket = new ErrorPacket((short)0, "Failed to read data from requested file");
                } catch (NoSuchElementException e) {
                    sendingData = false;
                }
            }
            else
                returnPacket = new ErrorPacket((short)0, "Unexpected packet, expected ACK packet");
        }

        if (receivingData) {
            if (message.getOpCode() == OpCode.DATA) {
                try {
                    returnPacket = dataReceiver.receive((DataPacket)message);
                } catch (IOException e) {
                    returnPacket = new ErrorPacket((short)0, "Failed to write data from requested file");
                }
            }
            else {
                receivingData = false;
                try {
                    dataReceiver.close();
                } catch (IOException e) {}
            }
        }
        message.applyRequest(this);
    }

    @Override
    public boolean shouldTerminate() {
        return isTerminated;
    } 

    private void  terminate(){
        isTerminated = true;
    }
    @Override
    public void disconnect() {
        connections.disconnect(currentClientId);
    }
    
    public BasePacket processReadRQPacket(ReadRQPacket readPacket){
        try {
            dataSender = new FileSender(readPacket.getFileName());
            sendingData = true;
            connections.send(currentClientId, new AcknowledgePacket((short)0));
            return dataSender.sendFirst();

        } catch (FileNotFoundException e) {
            return new ErrorPacket((short)1, "File not found");
        } catch (IOException e) {
            return new ErrorPacket((short)0, "Failed to read data from requested file");
        }
    }

    public void processDirPacket(DirectoryRQPacket dirPacket){
        BasePacket returnPacket;
        try {
            dataSender = new DirectorySender();
            sendingData = true;
            connections.send(currentClientId, new AcknowledgePacket((short)0));
            returnPacket = dataSender.sendFirst();
        }
        catch (FileNotFoundException e) {
            returnPacket = new ErrorPacket((short)1, "Couldn't locate files directory");
        }
        catch (IOException e) {
            returnPacket = new ErrorPacket((short)0, "Failed to load files in directory");
        }

        connections.send(currentClientId, returnPacket);
    }


    public void processWriteRQPacket(WriteRQPacket writePacket) {
        BasePacket returnPacket;
        try {
            dataReceiver = new FileReceiver(writePacket.getFileName());
            receivingData = true;
            returnPacket = new AcknowledgePacket((short)0);
        } catch (FileAlreadyExistsException e) {
            returnPacket = new ErrorPacket((short)5, "File already exists");
        } catch (IOException e) {
            returnPacket = new ErrorPacket((short)0, "Failed to write to file");
        }

        connections.send(currentClientId, returnPacket);
    }


    public void processLoginRQPacket(LoginRQPacket loginPacket){
        BasePacket returnPacket;
        if(username != null)
            returnPacket = new ErrorPacket((short)7, "User already logged in");
        else {
            username = loginPacket.getUsername();
            returnPacket = new AcknowledgePacket((short)0);
        }

        connections.send(currentClientId, returnPacket);
    }

    public void processDelPacket(DeleteRQPacket deleteRQPacket) {
        File file = new File(deleteRQPacket.getFileName());
        if (!file.exists()) {
            connections.send(currentClientId, new ErrorPacket((short)1, "File not found"));
        }
        else if (!file.delete()) {
            connections.send(currentClientId, new ErrorPacket((short)2, "File cannot be deleted"));
        }
        else {
            connections.send(currentClientId, new AcknowledgePacket((short)0));

            BasePacket bcast = new BroadCastPacket()
            Set<Integer> keySet = connections.getKeys();
            Integer id = currentClientId;
            keySet.remove(id);
            for (Integer key : keySet) {
                connections.send(key, )
            }
        }
        
        
    }
    public void processDisconnectRQPacket(DisconnectRQPacket disconnectPacket){
        BasePacket returnPacket = new AcknowledgePacket();
        terminate();
        connections.send(currentClientId, returnPacket);
    }



    
}
