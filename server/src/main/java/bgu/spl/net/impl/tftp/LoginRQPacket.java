package bgu.spl.net.impl.tftp;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

public class LoginRQPacket extends BasePacket{
    String username;
    public LoginRQPacket(short opcode, short length, String username){
        super(opcode, length);
        this.username = username;
    }
    @Override
    public void applyRequest(){

    }
    @Override
    public byte[] encodePacket(){
        byte[] result;
        result = convertShortToBytes(opcode); // opcode
        try {
            result = mergeArrays(result, username.getBytes("UTF-8")); //username
        } catch (UnsupportedEncodingException e) {}
        result = mergeArrays(result, ZERO); // 0 byte
        
        return result;
    }
}
