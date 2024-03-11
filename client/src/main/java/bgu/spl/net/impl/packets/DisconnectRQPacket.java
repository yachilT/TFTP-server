package bgu.spl.net.impl.packets;

import bgu.spl.net.impl.packets.OpCode;


public class DisconnectRQPacket extends BasePacket {

    public DisconnectRQPacket(){
        super(OpCode.DISC);
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
