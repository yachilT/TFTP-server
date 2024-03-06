package bgu.spl.net.impl.tftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import bgu.spl.net.api.BidiMessagingProtocol;
import bgu.spl.net.impl.tftp.packets.*;
import bgu.spl.net.srv.Connections;

public class TftpProtocol implements BidiMessagingProtocol<BasePacket>  {
    private Connections<BasePacket> connections;
    private int currentClientId;

    private class SendingHandler {
        private File workingFile;
        private FileInputStream reader;

        private DataPacket lastDataPacket;

        public SendingHandler(File workingFile) throws FileNotFoundException {
            this.workingFile = workingFile;
            this.reader = new FileInputStream(workingFile);
            lastDataPacket = null;
        }

        public DataPacket sendFirst() {
            byte[] data = reader.readNBytes(DataPacket.MAX_DATA_SIZE);
            lastDataPacket = new DataPacket(0, data);
            return lastDataPacket;
        }

        public DataPacket sendNext(AcknowledgePacket ACKPacket) throws IOException, IllegalArgumentException {
            if (ACKPacket.getBlockNumber() != lastDataPacket.getBlockNumber()) {
                throw new IllegalArgumentException("Incorrect ACKPacket");
            }

            byte[] data = reader.readNBytes(DataPacket.MAX_DATA_SIZE);
            lastDataPacket = new DataPacket(lastDataPacket.getBlockNumber() + 1, data);
            return lastDataPacket;
        }

        public void error() throws IOException {
            reader.close();
            
        }

        public void close() throws IOException {
            reader.close();
        }
    }
    

    private boolean receivingData;
    private FileOutputStream writer;

    @Override
    public void start(int connectionId, Connections<BasePacket> connections) {
        this.currentClientId = connectionId;
        this.connections = connections;
    }

    @Override
    public void process(BasePacket message) {

        if (sendingData){
            if (message.getOpCode() == OpCode.ACK.ordinal() && ((AcknowledgePacket) message).) {

            }
        }
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

    private void reset() {

        sendingData = false;
        receivingData = false;
        reader = null;
        writer = null;
    }
    public void processReadRQPacket(ReadRQPacket readPacket){
        /*** load content of file in readPacket to var */
        /** */
        sendingData = true;
        // send ACK packet via Connections
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
