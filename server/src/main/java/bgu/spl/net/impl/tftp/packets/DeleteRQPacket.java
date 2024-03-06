package bgu.spl.net.impl.tftp.packets;

import java.io.UnsupportedEncodingException;

public class DeleteRQPacket extends BasePacket {
    String fileName;
    public DeleteRQPacket(short opcode, String filename){
        super(opcode,(short) (2 + filename.length()));
        this.fileName = filename;
    }
    @Override
    public void applyRequest(){

    }
    @Override
    public byte[] encodePacket() {
        byte[] result;
        result = convertShortToBytes(opcode); // opcode
        try {
            result = mergeArrays(result, fileName.getBytes("UTF-8")); // file name
        } catch (UnsupportedEncodingException e) {}
        result = mergeArrays(result, ZERO); // 0 byte
        
        return result;
    }
    
}
