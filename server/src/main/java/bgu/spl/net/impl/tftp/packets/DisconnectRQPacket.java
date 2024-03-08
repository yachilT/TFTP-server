package bgu.spl.net.impl.tftp.packets;

import bgu.spl.net.impl.tftp.OpCode;
import bgu.spl.net.impl.tftp.TftpProtocol;

public class DisconnectRQPacket extends BasePacket {

    public DisconnectRQPacket(){
        super(OpCode.DISC);
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
