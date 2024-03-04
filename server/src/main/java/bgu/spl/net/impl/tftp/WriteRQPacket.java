package bgu.spl.net.impl.tftp;

import java.io.UnsupportedEncodingException;

public class WriteRQPacket extends BasePacket {
    String fileName;
    public WriteRQPacket(short opcode, short length, String fileName){
        super(opcode, length);
        this.fileName = fileName;
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
