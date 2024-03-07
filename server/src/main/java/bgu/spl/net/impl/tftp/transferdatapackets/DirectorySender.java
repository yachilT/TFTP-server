package bgu.spl.net.impl.tftp.transferdatapackets;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import bgu.spl.net.impl.tftp.packets.DataPacket;

public class DirectorySender extends DataSender {

    private byte[] dirListData;
    private int mark; 

    private static final String FILES_PATH = "Flies";


    public DirectorySender() throws FileNotFoundException {
        File dirPath = new File(FILES_PATH);
        if (!dirPath.exists()) {
            throw new FileNotFoundException();
        }
        
        List<Byte> data = Arrays.stream(dirPath.list())
        .flatMap(str -> {
            byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
            Byte[] combined = new Byte[bytes.length + 1];
            
            for (int i = 0; i < bytes.length; i++) {
                combined[i] = bytes[i];
            }
            combined[bytes.length] = 0;
            return Stream.of(combined);
        }).collect(Collectors.toList());

        dirListData = new byte[data.size()];
        int i = 0;
        for (Byte b : data) {
            dirListData[i] = b;
            i++;
        }
        
        mark = 0;
    }

    @Override
    protected DataPacket getNextPacket(short blockNumber) throws IOException {
        int end = Math.min(mark + DataPacket.MAX_DATA_SIZE, dirListData.length);
        byte[] dataToSend = new byte[end - mark];
        
        for (int i = mark; i < end; i++) {
            dataToSend[i] = dirListData[i];
        }

        return new DataPacket(blockNumber, dataToSend);
    } // handle last block case

    @Override
    public void error() throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'error'");
    }

    @Override
    public void close() throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'close'");
    }
}
