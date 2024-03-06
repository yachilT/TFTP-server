package bgu.spl.net.impl.tftp;

import java.util.LinkedList;
import java.util.Queue;

import bgu.spl.net.api.BidiMessagingProtocol;
import bgu.spl.net.impl.tftp.packets.DataPacket;
import bgu.spl.net.impl.tftp.packets.*;
import bgu.spl.net.srv.Connections;

public class TftpProtocol implements BidiMessagingProtocol<BasePacket>  {
    private Connections<BasePacket> connections;
    private int currentClientId;

    private boolean sendingData;
    private final Queue<DataPacket> filesToSend = new LinkedList<>();
    private boolean receivingData;
    @Override
    public void start(int connectionId, Connections<BasePacket> connections) {
        this.currentClientId = connectionId;
        this.connections = connections;
    }

    @Override
    public void process(BasePacket message) {
        // TODO implement this
        throw new UnsupportedOperationException("Unimplemented method 'process'");

        // if (sendingData)
            // check for the right ACK (with OpCode of argument)and send next one, if didnt receive valid ACK, send error and cancel sending (reset)
            // return;

        //if (receivingData) 
            // check for DATA packet (with opCode of argument) and store in file
            // return;
        
        

    }

    @Override
    public boolean shouldTerminate() {
        // TODO implement this
        throw new UnsupportedOperationException("Unimplemented method 'shouldTerminate'");
    } 
    public void processReadRQPacket(ReadRQPacket readPacket){
        /*** load content of file in readPacket to var */
        /** */
        sendingData = true;
        //send ACK packet via Connections
        // send first DATA packet 
    }

    public void processDirPacket(){
        //same as read
    }

    public void processWriteRQPacket(WriteRQPacket writePacket) {
        receivingData = true;
        // send ACK
        
    }

    
}
