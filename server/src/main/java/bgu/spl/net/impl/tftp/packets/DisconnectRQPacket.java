package bgu.spl.net.impl.tftp.packets;

public class DisconnectRQPacket extends BasePacket {

    public DisconnectRQPacket(short opcode){
        super(opcode, (short)2);
    }
    @Override
    public void applyRequest(){

    }
    @Override
    public byte[] encodePacket() {
        return convertShortToBytes(opcode); // opcode
    }
    
}
