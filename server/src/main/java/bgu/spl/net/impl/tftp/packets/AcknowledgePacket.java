package bgu.spl.net.impl.tftp.packets;

public class AcknowledgePacket extends BasePacket {
    short blockNumber;
    public AcknowledgePacket(short opcode, short blockNumber){
        super(opcode, (short) 4);
        this.blockNumber = blockNumber;
    }
    @Override
    public void applyRequest(){

    }
    @Override
    public byte[] encodePacket() {
        byte[] result;
        result = convertShortToBytes(opcode); // opcode
        result = mergeArrays(result, convertShortToBytes(blockNumber)); // block number

        return result;
    }
    
}
