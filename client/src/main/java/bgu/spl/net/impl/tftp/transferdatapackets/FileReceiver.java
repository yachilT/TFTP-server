package bgu.spl.net.impl.tftp.transferdatapackets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

public class FileReceiver extends DataReceiver{
    private String fileName;
    private File workingFile;
    private FileOutputStream writer;

    public FileReceiver(String name) throws IOException {
        this.fileName = name;
        workingFile = new File(name);
        if (!workingFile.createNewFile())
            throw new FileAlreadyExistsException(name);
            
        writer = new FileOutputStream(workingFile);
    }

    public String getfileName() {
        return fileName;
    }


    public void saveData(byte[] data) throws IOException {
        writer.write(data);
    }

    public void error() throws IOException {
        close();
        workingFile.delete();
    }

    public void close() throws IOException {
        writer.close();
    }
    

}
