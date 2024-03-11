package bgu.spl.net.impl.tftp.packets;

import bgu.spl.net.impl.tftp.OpCode;
import bgu.spl.net.impl.tftp.TftpProtocol;

public class DirectoryRQPacket extends BasePacket{
    public DirectoryRQPacket(){
        super(OpCode.DIRQ);
    }
    @Override
    public void applyRequest(TftpProtocol protocol){ 
        if(protocol.isLoggedIn())
            protocol.processDirPacket(this);
        else
            protocol.sendsErrorNotLoggedIn();
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
