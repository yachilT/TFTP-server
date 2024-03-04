package bgu.spl.net.impl.tftp;

public class DirectoryRQPacket extends BasePacket{
    public DirectoryRQPacket(short opcode){
        super(opcode, (short) 2);
    }
    @Override
    public void applyRequest(){ 

    }
    @Override
    public byte[] encodePacket() {
        return convertShortToBytes(opcode); // opcode
    }
    
}
