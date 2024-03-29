package bgu.spl.net.impl.packets;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import bgu.spl.net.impl.tftp.TftpMessagingProtocol;


public class WriteRQPacket extends BasePacket {
    private String fileName;
    public WriteRQPacket(){
        super(OpCode.WRQ);
        this.fileName = null;
    }
    public WriteRQPacket(String fileName){
        super(OpCode.WRQ);
        this.fileName = fileName;
    }
    @Override
    public BasePacket applyRequest(TftpMessagingProtocol protocol){
        return null;
    }

    public String getFileName() {
        return fileName;
    }
    @Override
    public byte[] encodePacket() {
        byte[] result;
        result = convertShortToBytes((short)opcode.ordinal()); // opcode
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
            return false;
        }
        byte[] byteArr = convertListToByteArr(bytes);
        bytes.clear();
        fileName = new String(byteArr, StandardCharsets.UTF_8);
        return true;
    }
    
    @Override
    public String toString() {
        return "WRQ " + this.fileName;
    }
}
