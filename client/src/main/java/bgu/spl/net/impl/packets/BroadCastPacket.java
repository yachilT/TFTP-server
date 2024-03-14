package bgu.spl.net.impl.packets;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import bgu.spl.net.impl.tftp.TftpMessagingProtocol;


public class BroadCastPacket extends BasePacket {
    private Boolean added;
    private String fileName;
    

    public BroadCastPacket(){
        super(OpCode.BCAST);
        this.added = null;
        this.fileName = null;
    }

    public BroadCastPacket(boolean added, String fileName) {
        super(OpCode.BCAST);
        this.added = added;
        this.fileName = fileName;

    }
    @Override
    public BasePacket applyRequest(TftpMessagingProtocol protocol){
        return protocol.handleBCASTPacket(this);
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
    public boolean decodeNextByte(byte nextByte) {
        if (added == null) {
            added = nextByte == 1;
            bytes.clear();
        }
        else if(nextByte != 0){
            bytes.add(nextByte);
        }
        else if(nextByte == 0){
            fileName = new String(convertListToByteArr(bytes), StandardCharsets.UTF_8);
            bytes.clear();
            return true;
        }
        
        return false;
    }

    @Override
    public String toString() {
        return "BCAST " + (added ? "add ":"del ") + fileName;
    }
}
