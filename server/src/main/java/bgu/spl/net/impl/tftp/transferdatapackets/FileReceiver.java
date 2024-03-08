package bgu.spl.net.impl.tftp.transferdatapackets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

import bgu.spl.net.impl.tftp.packets.AcknowledgePacket;
import bgu.spl.net.impl.tftp.packets.DataPacket;

public class FileReceiver {
    private File workingFile;
    private FileOutputStream writer;

    public FileReceiver(String name) throws IOException {
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
