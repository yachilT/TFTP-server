package bgu.spl.net.impl.tftp.packets;

import java.util.LinkedList;
import java.util.List;

import bgu.spl.net.impl.tftp.TftpProtocol;

public class AcknowledgePacket extends BasePacket {
    private short blockNumber;
    private final List<Byte> bytes = new LinkedList<>();


    public AcknowledgePacket(short opcode, short blockNumber){
        super(opcode, (short) 4);
        this.blockNumber = blockNumber;
    }

    @Override
    public void applyRequest(TftpProtocol protocol){

    }
    @Override
    public byte[] encodePacket() {
        byte[] result;
        result = convertShortToBytes(opcode); // opcode
        result = mergeArrays(result, convertShortToBytes(blockNumber)); // block number

        return result;
    }
    @Override
    public boolean decodeNextByte(byte nextByte){
        if(nextByte != 0)
            bytes.add(nextByte);
            
    }
    
}
