package bgu.spl.net.impl.tftp.packets;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import bgu.spl.net.impl.tftp.OpCode;
import bgu.spl.net.impl.tftp.TftpProtocol;

public class LoginRQPacket extends BasePacket{
    private String username;
    
    public LoginRQPacket(){
        super(OpCode.LOGRQ);
        this.length = 2;
        this.username = null;
    }
    @Override
    public void applyRequest(TftpProtocol tftp){
        tftp.processLoginRQPacket(this);
    }
    @Override
    public byte[] encodePacket(){
        byte[] result;
        result = convertShortToBytes((short)opcode.ordinal()); // opcode
        try {
            result = mergeArrays(result, username.getBytes("UTF-8")); //username
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
        username = new String(byteArr, StandardCharsets.UTF_8);
        bytes.clear();
        return true;
    }
    public String getUsername() {
        return username;
    }
}
