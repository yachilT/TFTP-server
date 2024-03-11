package bgu.spl.net.impl.packets;


public class AcknowledgePacket extends BasePacket {
    private short blockNumber;


    public AcknowledgePacket(){
        super(OpCode.ACK);
        this.blockNumber = -1;
    }
    public AcknowledgePacket(short blockNumber){
        super(OpCode.ACK);
        this.blockNumber = blockNumber;
    }
    // @Override
    // public void applyRequest(TftpProtocol protocol){
        
    // }
    @Override
    public byte[] encodePacket() {
        byte[] result;
        result = convertShortToBytes((short)opcode.ordinal()); // opcode
        result = mergeArrays(result, convertShortToBytes(blockNumber)); // block number

        return result;
    }
    @Override
    public boolean decodeNextByte(byte nextByte){
        bytes.add(nextByte);
        length++;
        if(bytes.size() == 2){
            blockNumber = convert2BytesToShort(bytes.get(0), bytes.get(1));
            bytes.clear();
            return true; 
        }
        return false;
    }

    public short getBlockNumber() {
        return blockNumber;
    }
    
}
