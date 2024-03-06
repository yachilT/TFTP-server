package bgu.spl.net.impl.tftp.packets;

import bgu.spl.net.impl.tftp.OpCode;
import bgu.spl.net.impl.tftp.TftpProtocol;

public class DataPacket extends BasePacket {
    private short blockNumber;
    private short size;
    private byte[] data;
    public DataPacket(short blockNumber, byte[] data) {
        super(OpCode.DATA);
        this.blockNumber = blockNumber;
        this.size = (short)data.length;
        this.data = data;
    }

    public DataPacket(){
        super(OpCode.DATA, (short)-1);
        this.size = -1;
        this.blockNumber = -1;
        this.data = null;
    }

    @Override
    public void applyRequest(TftpProtocol protocol){

    }
    @Override
    public byte[] encodePacket() {
        byte[] result;
        result = convertShortToBytes((short)opcode.ordinal()); // opcode
        result = mergeArrays(result, convertShortToBytes((short)size)); // size
        result = mergeArrays(result, convertShortToBytes(blockNumber)); // block number
        result = mergeArrays(result, data); // data

        return result;
    }
    @Override
    public boolean decodeNextByte(byte nextByte){
        bytes.add(nextByte);
        length++;
        if(length == 4){
            size = convert2BytesToShort(bytes.get(0), bytes.get(1));
            bytes.clear();
            return false;
        }
        if(length == 6){
            blockNumber = convert2BytesToShort(bytes.get(0), bytes.get(1));
            bytes.clear();
            return false;
        }
        if(bytes.size() == size){
            data = convertListToByteArr(bytes);
            bytes.clear();
            return true;
        }

        return false;
    }
    
}
