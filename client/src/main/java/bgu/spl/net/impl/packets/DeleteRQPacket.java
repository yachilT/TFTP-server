package bgu.spl.net.impl.packets;

import java.nio.charset.StandardCharsets;

import bgu.spl.net.impl.tftp.TftpMessagingProtocol;


public class DeleteRQPacket extends BasePacket {
    private String fileName;

    public DeleteRQPacket(){
        super(OpCode.DELRQ);
        this.fileName = null;
    }
    public DeleteRQPacket(String filename){
        super(OpCode.DELRQ);
        this.fileName = filename;
    }
    public String getFileName() {
        return fileName;
    }
    @Override
    public BasePacket applyRequest(TftpMessagingProtocol protocol){
        return null;
    }
    @Override
    public byte[] encodePacket() {
        byte[] result;
        result = convertShortToBytes((short)opcode.ordinal()); // opcode
        result = mergeArrays(result, fileName.getBytes(StandardCharsets.UTF_8)); // file name
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
