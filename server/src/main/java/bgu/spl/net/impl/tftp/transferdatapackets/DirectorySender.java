package bgu.spl.net.impl.tftp.transferdatapackets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import bgu.spl.net.impl.tftp.packets.DataPacket;

public class DirectorySender extends DataSender {

    private List<Byte> bytes;
    private FileManager fileManager;

    public DirectorySender(FileManager manager) throws FileNotFoundException {
        fileManager = manager;
        File dirPath = new File (manager.getPath());
        
        if (!dirPath.exists()) {
            throw new FileNotFoundException();
        }
        bytes = new LinkedList<>();
        for (String dir : fileManager.getAvailableFiles()) {
            byte[] data = dir.getBytes(StandardCharsets.UTF_8);
            for (byte b : data) {
                bytes.add(b);
            }
            bytes.add((byte)0);
        }
        bytes.remove(bytes.size() - 1);
    }

    @Override
    protected DataPacket loadNextPacket(short blockNumber) throws IOException, NoSuchElementException {
        if (bytes.isEmpty())
            throw new NoSuchElementException();
        byte[] dataToSend = new byte[Math.min(bytes.size(), DataPacket.MAX_DATA_SIZE)];
        
        for (int i = 0; i < dataToSend.length; i++) {
            dataToSend[i] = bytes.remove(0);
        }
        return new DataPacket(blockNumber, dataToSend);
    }

    @Override
    public void error() throws IOException {
        close();
    }

    @Override
    public void close() throws IOException {
    }
}
