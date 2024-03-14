package bgu.spl.net.impl.tftp.transferdatapackets;
import java.io.IOException;
import java.util.NoSuchElementException;

import bgu.spl.net.impl.packets.AcknowledgePacket;
import bgu.spl.net.impl.packets.DataPacket;

public abstract class DataSender {

    protected DataPacket lastDataPacket;

    public DataSender() {
        lastDataPacket = null;
    }

    protected abstract DataPacket loadNextPacket(short blockNumber) throws IOException, NoSuchElementException;

    public DataPacket sendFirst() throws IOException, NoSuchElementException { // definde what to do if there isnt data on first try
        lastDataPacket = loadNextPacket((short)1);
        return lastDataPacket;
    }

    public DataPacket sendNext(AcknowledgePacket ACKPacket) throws IOException, IllegalArgumentException, NoSuchElementException {
        if (ACKPacket.getBlockNumber() != lastDataPacket.getBlockNumber()) {
            throw new IllegalArgumentException("Incorrect ACK Packet");
        }
        try{
            lastDataPacket = loadNextPacket((short)(lastDataPacket.getBlockNumber() + 1));
        }
        catch(NoSuchElementException e){
            if(lastDataPacket.getSize() < DataPacket.MAX_DATA_SIZE)
                throw e;
            else{    
                lastDataPacket = new DataPacket((short)(lastDataPacket.getBlockNumber() + 1), new byte[0]);
            }

        }
            return lastDataPacket;
    }

    public abstract void error() throws IOException;
    public abstract void close() throws IOException;
}
