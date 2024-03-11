package bgu.spl.net.impl.packets;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import bgu.spl.net.impl.packets.OpCode;

public class DeleteRQPacket extends BasePacket {
    private String fileName;

    public DeleteRQPacket(){
        super(OpCode.DELRQ);
        this.fileName = null;
    }
    public DeleteRQPacket(String fileName){
        super(OpCode.DELRQ);
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
    // @Override
    // public void applyRequest(TftpProtocol protocol){
    //     if(protocol.isLoggedIn())
    //         protocol.processDelPacket(this);
    //     else
    //         protocol.sendsErrorNotLoggedIn();
    // }
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
        fileName = new String(byteArr, StandardCharsets.UTF_8);
        bytes.clear();

        return true;
    }
    
}
