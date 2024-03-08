package bgu.spl.net.impl.tftp.packets;

import java.nio.charset.StandardCharsets;

import bgu.spl.net.impl.tftp.OpCode;
import bgu.spl.net.impl.tftp.TftpProtocol;

public class DirectoryRQPacket extends BasePacket{
    public DirectoryRQPacket(){
        super(OpCode.DIRQ);
    }
    @Override
    public BasePacket applyRequest(TftpProtocol protocol){ 

    }
    @Override
    public byte[] encodePacket() {
        return convertShortToBytes((short)opcode.ordinal()); // opcode
    }
        @Override
    public boolean decodeNextByte(byte nextByte){
        return true;
    }
    
}
