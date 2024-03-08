package bgu.spl.net.impl.tftp.packets;


import bgu.spl.net.impl.tftp.OpCode;
import bgu.spl.net.impl.tftp.TftpProtocol;

public class AcknowledgePacket extends BasePacket {
    private short blockNumber;


    public AcknowledgePacket(){
        super(OpCode.ACK);
        this.blockNumber = -1;
    }
    public AcknowledgePacket(short blockNumber){
        super(OpCode.ACK, (short)-1);
        this.blockNumber = blockNumber;
    }
    @Override
    public BasePacket applyRequest(TftpProtocol protocol){
        return null;
    }
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
