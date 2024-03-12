package bgu.spl.net.impl.tftp.transferdatapackets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;

import bgu.spl.net.impl.tftp.packets.DataPacket;

public class FileSender extends DataSender {
    private FileInputStream reader;

    public FileSender(String name, FileManager fileManager) throws FileNotFoundException {
        this.reader = new FileInputStream(fileManager.getFile(name));
        System.out.println("Found " + name);
        lastDataPacket = null;
    }

    @Override
    protected DataPacket loadNextPacket(short blockNumber) throws IOException, NoSuchElementException {
        if (reader.available() == 0) {
            throw new NoSuchElementException();
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
