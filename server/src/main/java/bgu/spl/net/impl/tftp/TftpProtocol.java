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
            if (reader.available() == 0) {
                return null;
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

        public ReceivingHandler(String name) throws IOException {
            workingFile = new File(name);
            if (!workingFile.createNewFile())
                throw new FileAlreadyExistsException(name);
                
            writer = new FileOutputStream(workingFile);
        }

        public AcknowledgePacket receive(DataPacket packet) throws IOException {
            byte[] b = packet.getData();
            writer.write(b);

            return new AcknowledgePacket(packet.getBlockNumber());
        }

        public void error() throws IOException {
            writer.close();
            workingFile.delete();
        }

        public void close() throws IOException {
            writer.close();
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
        BasePacket returnPacket = null;
        if (sendingData){
            if (message.getOpCode() == OpCode.ACK) {
                try {
                    returnPacket = sendingHandler.sendNext((AcknowledgePacket)message);
                } catch (IllegalArgumentException e) {
                    returnPacket = new ErrorPacket((short)0, "Incorrect block number from ACK");
                } catch (IOException e) {
                    returnPacket = new ErrorPacket((short)0, "Failed to read data from requested file");
                }
                if (returnPacket == null)
                    sendingData = false;
            }
            else
                returnPacket = new ErrorPacket((short)0, "Unexpected packet, expected ACK packet");
        }

        if (receivingData) {
            if (message.getOpCode() == OpCode.DATA) {
                try {
                    returnPacket = receivingHandler.receive((DataPacket)message);
                } catch (IOException e) {
                    returnPacket = new ErrorPacket((short)0, "Failed to write data from requested file");
                }
            }
            else {
                receivingData = false;
                try {
                    receivingHandler.close();
                } catch (IOException e) {}
            }
        }

        if (message.getOpCode() == OpCode.RRQ) {
            returnPacket = processReadRQPacket((ReadRQPacket)message);
        }

        if (message.getOpCode() == OpCode.WRQ) {

        }
        //should synchronize acording to type of packet (data)
        connections.send(currentClientId, returnPacket);
    }

    @Override
    public boolean shouldTerminate() {
        // TODO implement this
        throw new UnsupportedOperationException("Unimplemented method 'shouldTerminate'");
    } 

    public BasePacket processReadRQPacket(ReadRQPacket readPacket){
        try {
            sendingHandler = new SendingHandler(readPacket.getFileName());
            sendingData = true;
            connections.send(currentClientId, new AcknowledgePacket((short)0));
            return sendingHandler.sendFirst();

        } catch (FileNotFoundException e) {
            return new ErrorPacket((short)1, "File not found");
        } catch (IOException e) {
            return new ErrorPacket((short)0, "Failed to read data from requested file");
        }
    }

    public void processDirPacket(){
        //same as read
    }

    public BasePacket processWriteRQPacket(WriteRQPacket writePacket) {
        try {
            receivingHandler = new ReceivingHandler(writePacket.getFileName());
            receivingData = true;
            return new AcknowledgePacket((short)0);
        } catch (FileAlreadyExistsException e) {
            return new ErrorPacket((short)5, "File already exists");
        } catch (IOException e) {
            return new ErrorPacket((short)0, "Failed to write to file");
        }
    }

    
}
