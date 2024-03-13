package bgu.spl.net.impl.tftp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import bgu.spl.net.api.BidiMessagingProtocol;
import bgu.spl.net.impl.tftp.packets.*;
import bgu.spl.net.impl.tftp.transferdatapackets.DataSender;
import bgu.spl.net.impl.tftp.transferdatapackets.DirectorySender;
import bgu.spl.net.impl.tftp.transferdatapackets.FileManager;
import bgu.spl.net.impl.tftp.transferdatapackets.FileReceiver;
import bgu.spl.net.impl.tftp.transferdatapackets.FileSender;
import bgu.spl.net.srv.Connections;

public class TftpProtocol implements BidiMessagingProtocol<BasePacket>  {
    private Connections<BasePacket> connections;
    private Map<Integer, String> users;
    private int currentClientId;
    private boolean isTerminated;
    private boolean sendingData;
    private DataSender dataSender;
    private FileManager fileManager;

    private boolean receivingData;
    private FileReceiver dataReceiver;

    public TftpProtocol(Map<Integer, String> users, FileManager fileManager) {
        this.connections = null;
        this.currentClientId = -1;

        this.sendingData = false;
        this.dataSender = null;

        this.receivingData = false;
        this.dataReceiver = null;

        this.isTerminated = false;
        this.users = users;
        this.fileManager = fileManager;
    }
    @Override
    public void start(int connectionId, Connections<BasePacket> connections) {
        this.currentClientId = connectionId;
        System.out.println("given connectionId: " + this.currentClientId);
        this.connections = connections;
        this.users.put(currentClientId, "");
        System.out.println("protocol started");
    }

    @Override
    public void process(BasePacket message) {
        if (sendingData) {
            if (message.getOpCode() == OpCode.ACK) {
                try {
                    connections.send(currentClientId, dataSender.sendNext((AcknowledgePacket)message));
                } catch (IllegalArgumentException e) {
                    connections.send(currentClientId, new ErrorPacket((short)0, "Incorrect block number from ACK"));
                    sendingData = false;
                    try {
                        dataSender.error();
                    } catch (IOException e1) {}
                } catch (IOException e) {
                    connections.send(currentClientId, new ErrorPacket((short)2, "Access violation - File cannot be read"));
                } catch (NoSuchElementException e) {
                    sendingData = false;
                }
            }
            else
                connections.send(currentClientId, new ErrorPacket((short)0, "Unexpected packet, expected ACK packet"));
        }

        if (receivingData) {
            if (message.getOpCode() == OpCode.DATA) {
                try {
                    connections.send(currentClientId, dataReceiver.receive((DataPacket)message));

                    if (((DataPacket)message).getSize() < DataPacket.MAX_DATA_SIZE) {
                        receivingData = false;
                        
                        try { dataReceiver.close(); } catch (IOException e1) {}
                    }

                } catch (IOException e) {
                    connections.send(currentClientId, new ErrorPacket((short)2, "Access Violation - File cannot be written"));
                    receivingData = false;
                    try {
                        dataReceiver.error();
                    } catch (IOException e1) {}
                }
            }
            else {
                receivingData = false;
                try {
                    dataReceiver.close();
                    fileManager.addFile(dataReceiver.getfileName());
                } catch (IOException e) {}

                broadcast(true, dataReceiver.getfileName());
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
        users.remove(currentClientId);
        connections.disconnect(currentClientId);
    }
    
    public void processReadRQPacket(ReadRQPacket readPacket){
        try {
            dataSender = new FileSender(readPacket.getFileName(), fileManager);
            sendingData = true;
            connections.send(currentClientId, new AcknowledgePacket((short)0));
            connections.send(currentClientId, dataSender.sendFirst());

        } catch (FileNotFoundException e) {
            connections.send(currentClientId, new ErrorPacket((short)1, "File not found"));
        } catch (IOException e) {
            connections.send(currentClientId, new ErrorPacket((short)2, "Access violation - File cannot be read"));
        }
    }

    
    public void processDirPacket(DirectoryRQPacket dirPacket){
        BasePacket returnPacket;
        try {
            dataSender = new DirectorySender(fileManager);
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
            dataReceiver = new FileReceiver(writePacket.getFileName(), fileManager);
            receivingData = true;
            returnPacket = new AcknowledgePacket((short)0);
        } catch (FileAlreadyExistsException e) {
            returnPacket = new ErrorPacket((short)5, "File already exists");
        } catch (IOException e) {
            returnPacket = new ErrorPacket((short)2, "File cannot be written");
        }

        connections.send(currentClientId, returnPacket);
    }

    public void processDelPacket(DeleteRQPacket deleteRQPacket) {
        File file;
        try {
            file = fileManager.getFile(deleteRQPacket.getFileName());
            if (!file.delete()) {
                connections.send(currentClientId, new ErrorPacket((short)2, "File cannot be deleted"));
            }
            else {
                fileManager.removeFile(deleteRQPacket.getFileName());
                connections.send(currentClientId, new AcknowledgePacket((short)0));
                broadcast(false, deleteRQPacket.getFileName());
            }
        } catch (FileNotFoundException e) {
            connections.send(currentClientId, new ErrorPacket((short)1, "File not found"));
        }
    }

    private void broadcast(boolean added, String fileName) {
        BasePacket bcast = new BroadCastPacket(added, fileName);
        Set<Integer> keySet = connections.getKeys();

        keySet = keySet.stream().filter(x -> x != currentClientId && isLoggedIn()).collect(Collectors.toSet());
        for (Integer key : keySet) {
            connections.send(key, bcast);
        }
    }

    public boolean isLoggedIn() {
        return !users.get(currentClientId).equals("");
    }

    public void processLoginRQPacket(LoginRQPacket loginPacket){
        BasePacket returnPacket;
        System.out.println(users.entrySet());
        System.out.println("currentId: " + currentClientId);
        System.out.println(this.users.get(currentClientId));

        if(isLoggedIn())
            returnPacket = new ErrorPacket((short)7, "User already logged in");
        else {
            String username = loginPacket.getUsername();
            System.out.println("name in user? " + users.values().contains(username));
            if (!users.values().contains(username)) {
                users.put(currentClientId, username);
                returnPacket = new AcknowledgePacket((short)0);
                System.out.println("BUILT ACK");
            }
            else {
                returnPacket = new ErrorPacket((short)0, "username is not available");
            }
        }
        connections.send(currentClientId, returnPacket);
    }
    public void processDisconnectRQPacket(DisconnectRQPacket disconnectPacket){
        
        BasePacket returnPacket = new AcknowledgePacket((short)0);
        System.out.println("SENDING ACK AFTER DISC");
        connections.send(currentClientId, returnPacket);
        users.put(currentClientId, null);
        terminate();
    }

    public void sendsErrorNotLoggedIn(){
        connections.send(currentClientId, new ErrorPacket((short)6, "User not logged in"));
    }
}
