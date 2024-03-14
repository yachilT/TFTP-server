package bgu.spl.net.impl.packets;

import bgu.spl.net.impl.tftp.TftpMessagingProtocol;

public class DirectoryRQPacket extends BasePacket{
    public DirectoryRQPacket(){
        super(OpCode.DIRQ);
    }
    @Override
    public BasePacket applyRequest(TftpMessagingProtocol protocol){ 
        return null;
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
