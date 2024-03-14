package bgu.spl.net.impl.tftp.transferdatapackets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

public class DirectoryReciever extends DataReceiver {
    public final List<String> dirNames;
    public final ByteArrayOutputStream buf;

    public DirectoryReciever() {
        dirNames = new LinkedList<>();
        buf = new ByteArrayOutputStream();
        
    }

    @Override
    protected void saveData(byte[] data) throws IOException {
        for (int i = 0; i < data.length; i++) {
            if (data[i] == 0) {
                dirNames.add(new String(buf.toByteArray(), StandardCharsets.UTF_8));
                buf.reset();
            }
            else {
                buf.write(data[i]);
            }
        }
    }

    @Override
    public void close() throws IOException {
        dirNames.add(new String(buf.toByteArray(), StandardCharsets.UTF_8));

        dirNames.forEach(System.out::println);
    }

    @Override
    public void error() throws IOException {
        buf.close();
    }

}
