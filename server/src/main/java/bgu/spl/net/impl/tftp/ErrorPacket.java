package bgu.spl.net.impl.tftp;

import java.io.UnsupportedEncodingException;

public class ErrorPacket extends BasePacket {
    short errorCode;
    String errMsg;
    public ErrorPacket(short opcode, short errorCode, String errMsg){
        super(opcode, (short)(errMsg.length() + 4));
        this.errorCode = errorCode;
        this.errMsg = errMsg;
    }
    @Override
    public void applyRequest(){

    }
    @Override
    public byte[] encodePacket() {
        byte[] result;
        result = convertShortToBytes(opcode); //opcode
        result = mergeArrays(result, convertShortToBytes(errorCode)); // error code
        try {
            result = mergeArrays(result, errMsg.getBytes("UTF-8")); // error message
        } catch (UnsupportedEncodingException e) {} 
        result = mergeArrays(result, ZERO); // 0 byte

        return result;
    }
    
}
