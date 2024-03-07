package bgu.spl.net.impl.tftp.transferdatapackets;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import bgu.spl.net.impl.tftp.packets.DataPacket;

public class FileSender extends DataSender {
    private FileInputStream reader;

    public FileSender(String name) throws FileNotFoundException {
        this.reader = new FileInputStream(name);
        lastDataPacket = null;
    }

    @Override
    protected DataPacket getNextPacket(short blockNumber) throws IOException {
        if (reader.available() == 0) {
            return null;
        }
        byte[] data = reader.readNBytes(DataPacket.MAX_DATA_SIZE);
        return new DataPacket((short)blockNumber, data);
    }

    @Override
    public void error() throws IOException {
        this.reader.close();
    }

    @Override
    public void close() throws IOException {
        this.reader.close();
    }
}
