package bgu.spl.net.impl.tftp.transferdatapackets;

import java.io.IOException;
import bgu.spl.net.impl.packets.AcknowledgePacket;
import bgu.spl.net.impl.packets.DataPacket;

public abstract class DataReceiver {

    public AcknowledgePacket receive(DataPacket packet) throws IOException {
        saveData(packet.getData());
        return new AcknowledgePacket(packet.getBlockNumber());
    }

    protected abstract void saveData(byte[] data) throws IOException;
    public abstract void close() throws IOException;
    public abstract void error() throws IOException;


}
