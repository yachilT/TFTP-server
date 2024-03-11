package bgu.spl.net.impl.tftp.packets;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import bgu.spl.net.impl.tftp.TftpProtocol;

public class DeleteRQPacket extends BasePacket {
    private String fileName;

    public DeleteRQPacket(){
        super(OpCode.DELRQ);
        this.fileName = null;
    }

    public String getFileName() {
        return fileName;
    }
    @Override
    public void applyRequest(TftpProtocol protocol){
        if(protocol.isLoggedIn())
            protocol.processDelPacket(this);
        else
            protocol.sendsErrorNotLoggedIn();
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
        fileName = new String(byteArr, StandardCharsets.UTF_8);
        bytes.clear();

        return true;
    }
    
}
