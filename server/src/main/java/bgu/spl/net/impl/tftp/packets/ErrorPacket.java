package bgu.spl.net.impl.tftp.packets;

import java.io.UnsupportedEncodingException;

import bgu.spl.net.impl.tftp.OpCode;
import bgu.spl.net.impl.tftp.TftpProtocol;

public class ErrorPacket extends BasePacket {
    short errorCode;
    String errMsg;
    public ErrorPacket(short errorCode, String errMsg){
        super(OpCode.ERROR);
        this.errorCode = errorCode;
        this.errMsg = errMsg;
    }
    public ErrorPacket(){
        super(OpCode.ERROR);
        errMsg = null;
        errorCode = -1;
    }
    @Override
    public void applyRequest(TftpProtocol protocol){

    }
    @Override
    public byte[] encodePacket() {
        byte[] result;
        result = convertShortToBytes((short)opcode.ordinal()); //opcode
        result = mergeArrays(result, convertShortToBytes(errorCode)); // error code
        try {
            result = mergeArrays(result, errMsg.getBytes("UTF-8")); // error message
        } catch (UnsupportedEncodingException e) {} 
        result = mergeArrays(result, ZERO); // 0 byte

        return result;
    }
    
}
