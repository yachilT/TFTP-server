package bgu.spl.net.impl.tftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

import bgu.spl.net.api.BidiMessagingProtocol;
import bgu.spl.net.impl.tftp.packets.*;
import bgu.spl.net.srv.Connections;

public class TftpProtocol implements BidiMessagingProtocol<BasePacket>  {
    private Connections<BasePacket> connections;
    private int currentClientId;

    private class SendingHandler {
        private FileInputStream reader;

        private DataPacket lastDataPacket;

        public SendingHandler(String name) throws FileNotFoundException {
            this.reader = new FileInputStream(name);
            lastDataPacket = null;
        }

        public DataPacket sendFirst() throws IOException {
            byte[] data = reader.readNBytes(DataPacket.MAX_DATA_SIZE);
            lastDataPacket = new DataPacket((short)0, data);
            return lastDataPacket;
        }

        public DataPacket sendNext(AcknowledgePacket ACKPacket) throws IOException, IllegalArgumentException {
            if (ACKPacket.getBlockNumber() != lastDataPacket.getBlockNumber()) {
                throw new IllegalArgumentException("Incorrect ACKPacket");
            }

            byte[] data = reader.readNBytes(DataPacket.MAX_DATA_SIZE);
            lastDataPacket = new DataPacket((short)(lastDataPacket.getBlockNumber() + 1), data);
            return lastDataPacket;
        }

        public void error() throws IOException {
            reader.close();
            
        }

        public void close() throws IOException {
            reader.close();
        }
    }

    private class ReceivingHandler {
        private File workingFile;
        private FileOutputStream writer;

        public ReceivingHandler(String name) throws FileAlreadyExistsException, FileNotFoundException {
            workingFile = new File(name);
            if (workingFile.exists())
                throw new FileAlreadyExistsException(name);
            writer = new FileOutputStream(workingFile);
        }

        public AcknowledgePacket receive(DataPacket packet) throws IOException {
            byte[] b = packet.getData();
            writer.write(b);

            return new AcknowledgePacket(packet.getBlockNumber());
        }

    }

    boolean sendingData;
    SendingHandler sendingHandler;

    boolean receivingData;
    ReceivingHandler receivingHandler;

    @Override
    public void start(int connectionId, Connections<BasePacket> connections) {
        this.currentClientId = connectionId;
        this.connections = connections;
    }

    @Override
    public void process(BasePacket message) {

        if (sendingData){
            if (message.getOpCode() == OpCode.ACK) {
                    try {
                        sendingHandler.sendNext((AcknowledgePacket)message);
                    } catch (IllegalArgumentException e) {
                        ErrorPacket errorPacket = new ErrorPacket((short)0, "Incorrect block number from ACK");
                    } catch (IOException e) {
                        ErrorPacket errorPacket = new ErrorPacket((short)0, "Unexpected IO exception");
                    }
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
