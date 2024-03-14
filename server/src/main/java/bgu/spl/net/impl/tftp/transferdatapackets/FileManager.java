package bgu.spl.net.impl.tftp.transferdatapackets;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class FileManager {
    private final String workingDirectory = "Flies";
    public List<String> availableFiles;

    public FileManager() throws FileNotFoundException {
        availableFiles = new LinkedList<>();
        File f = new File(workingDirectory);
        if(!f.exists())
            throw new FileNotFoundException();
        
        availableFiles = new LinkedList<>(Arrays.asList(f.list()));
    }

    public File getFile(String name) throws FileNotFoundException {
        if (!availableFiles.contains(name)) 
            throw new FileNotFoundException();
        return new File(workingDirectory + "/" + name);
    }

    public void addFile(String name) {
        availableFiles.add(name);
    }

    public void removeFile(String name) {
        availableFiles.remove(name);
    }
    public String getPath(){
        return workingDirectory;
    }
    public List<String> getAvailableFiles(){
        return availableFiles;
    }
}
