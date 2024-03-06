package bgu.spl.net.impl.tftp.packets;

import java.io.UnsupportedEncodingException;

public class BroadCastPacket extends BasePacket {
    boolean added;
    String fileName;
    public BroadCastPacket(short opcode, boolean added, String fileName){
        super(opcode, (short)(3 + fileName.length()));
        this.added = added;
        this.fileName = fileName;
    }
    @Override
    public void applyRequest(){
        
    }
    @Override
    public byte[] encodePacket() {
        byte[] result;
        result = convertShortToBytes(opcode); // opcode
        result = mergeArrays(result, added ? ONE : ZERO); // deleted / added
        try {
            result = mergeArrays(result, fileName.getBytes("UTF-8")); // file name
        } catch (UnsupportedEncodingException e) {}
        result = mergeArrays(result, ZERO); // 0 byte

        return result;
    }
}
