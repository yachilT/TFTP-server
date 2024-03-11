package bgu.spl.net.impl.tftp.packets;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import bgu.spl.net.impl.tftp.OpCode;
import bgu.spl.net.impl.tftp.TftpProtocol;

public class BroadCastPacket extends BasePacket {
    private boolean added;
    private String fileName;
    public BroadCastPacket(){
        super(OpCode.BCAST);
        this.added = false;
        this.fileName = null;
    }

    public BroadCastPacket(boolean added, String fileName) {
        super(OpCode.BCAST);
        this.added = added;
        this.fileName = fileName;

    }
    @Override
    public void applyRequest(TftpProtocol protocol){
        protocol.processBcastPacket(this);
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
            return false;
        }
        if(nextByte == 0){
            fileName = new String(convertListToByteArr(bytes), StandardCharsets.UTF_8);
            bytes.clear();
            return true;
        }
        
        return false;
    }
}
