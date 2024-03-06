package bgu.spl.net.impl.tftp.packets;

public class DataPacket extends BasePacket {
    short blockNumber;
    byte[] data;
    public DataPacket(short opcode, short length, short blockNumber, byte[] data){
        super(opcode, length);
        this.blockNumber = blockNumber;
        this.data = data;
    }
    @Override
    public void applyRequest(){

    }
    @Override
    public byte[] encodePacket() {
        byte[] result;
        result = convertShortToBytes(opcode); // opcode
        result = mergeArrays(result, convertShortToBytes(length)); // size
        result = mergeArrays(result, convertShortToBytes(blockNumber)); // block number
        result = mergeArrays(result, data); // data

        return result;
    }
    
}
