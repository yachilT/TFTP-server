package bgu.spl.net.impl.tftp.packets;

import bgu.spl.net.impl.tftp.TftpProtocol;

public class DisconnectRQPacket extends BasePacket {

    public DisconnectRQPacket(){
        super(OpCode.DISC);
    }
    @Override
    public void applyRequest(TftpProtocol protocol){
        protocol.processDisconnectRQPacket(this);
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
