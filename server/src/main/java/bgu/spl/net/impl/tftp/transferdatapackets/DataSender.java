package bgu.spl.net.impl.tftp.transferdatapackets;
import java.io.IOException;

import bgu.spl.net.impl.tftp.packets.AcknowledgePacket;
import bgu.spl.net.impl.tftp.packets.DataPacket;

public abstract class DataSender {

    protected DataPacket lastDataPacket;

    public DataSender() {
        lastDataPacket = null;
    }

    protected abstract DataPacket getNextPacket(short blockNumber) throws IOException;

    public DataPacket sendFirst() throws IOException { // definde what to do if there isnt data on first try
        lastDataPacket = getNextPacket((short)0);
        return lastDataPacket;
    }

    public DataPacket sendNext(AcknowledgePacket ACKPacket) throws IOException, IllegalArgumentException {
        if (ACKPacket.getBlockNumber() != lastDataPacket.getBlockNumber()) {
            throw new IllegalArgumentException("Incorrect ACKPacket");
        }
        lastDataPacket = getNextPacket((short)(lastDataPacket.getBlockNumber() + 1));
        return lastDataPacket;
    }

    public abstract void error() throws IOException;
    public abstract void close() throws IOException;
}
