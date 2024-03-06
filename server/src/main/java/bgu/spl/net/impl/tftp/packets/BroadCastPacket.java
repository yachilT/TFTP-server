package bgu.spl.net.impl.tftp.packets;

import java.io.UnsupportedEncodingException;

import bgu.spl.net.impl.tftp.OpCode;
import bgu.spl.net.impl.tftp.TftpProtocol;

public class BroadCastPacket extends BasePacket {
    boolean added;
    String fileName;
    public BroadCastPacket(){
        super(OpCode.BCAST);
        this.added = false;
        this.fileName = null;
    }
    @Override
    public void applyRequest(TftpProtocol protocol){
        
    }
    @Override
    public byte[] encodePacket() {
        byte[] result;
        result = convertShortToBytes((short)opcode.ordinal()); // opcode
        result = mergeArrays(result, added ? ONE : ZERO); // deleted / added
        try {
            result = mergeArrays(result, fileName.getBytes("UTF-8")); // file name
        } catch (UnsupportedEncodingException e) {}
        result = mergeArrays(result, ZERO); // 0 byte

        return result;
    }
    @Override 
    public boolean decodeNextByte(byte nextByte){
        if(nextByte != 0){
            bytes.add(nextByte);
            length++;
        }
        if(length == 3){
            short num = convert2BytesToShort(bytes.get(0), bytes.get(1));
            added = num == 1 ? true : false;
            bytes.clear();
        }
    }
}
