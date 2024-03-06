package bgu.spl.net.impl.tftp.packets;

import bgu.spl.net.impl.tftp.OpCode;
import bgu.spl.net.impl.tftp.TftpProtocol;

public class DisconnectRQPacket extends BasePacket {

    public DisconnectRQPacket(short opcode){
        super(OpCode.DISC);
    }
    @Override
    public void applyRequest(TftpProtocol protocol){

    }
    @Override
    public byte[] encodePacket() {
        return convertShortToBytes(opcode); // opcode
    }
    @Override
    public boolean decodeNextByte(byte nextByte){
        if(nextByte != 0){
            
        }
    }
    
}
