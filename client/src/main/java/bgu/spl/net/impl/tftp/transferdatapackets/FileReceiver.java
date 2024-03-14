package bgu.spl.net.impl.tftp.transferdatapackets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Arrays;

import bgu.spl.net.impl.packets.AcknowledgePacket;
import bgu.spl.net.impl.packets.DataPacket;

public class FileReceiver extends DataReceiver{
    private String fileName;
    private File workingFile;
    private FileOutputStream writer;
    private FileManager fileManager;

    public FileReceiver(String name, FileManager fileManager) throws IOException {
        this.fileManager = fileManager;
        this.fileName = name;
        workingFile = new File(this.fileManager.getPath() + "/" + name);
        if (!workingFile.createNewFile())
            throw new FileAlreadyExistsException(name);
            
        writer = new FileOutputStream(workingFile);
    }

    public String getfileName() {
        return fileName;
    }

    public AcknowledgePacket receive(DataPacket packet) throws IOException {
        System.out.println("received data " + packet.getBlockNumber() + " size: " + packet.getSize());

        byte[] b = packet.getData();
        System.out.println(Arrays.toString(b) + " size: " + b.length);
        writer.write(b);
        System.out.println("done writing");
        return new AcknowledgePacket(packet.getBlockNumber());
    }

    public void error() throws IOException {
        close();
        workingFile.delete();
    }

    public void close() throws IOException {
        writer.close();
    }
    

}
