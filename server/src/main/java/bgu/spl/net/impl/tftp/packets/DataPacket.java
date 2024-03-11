package bgu.spl.net.impl.tftp.packets;

import bgu.spl.net.impl.tftp.TftpProtocol;

public class DataPacket extends BasePacket {
    public static final short MAX_DATA_SIZE = 512;

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
        this.data = new byte[0];
    }

    @Override

    public void applyRequest(TftpProtocol protocol){
    }

    public short getBlockNumber() {
        return blockNumber;
    }

    public byte[] getData(){
        return data;
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

        if (blockNumber != -1 && size == bytes.size()) {
            data = convertListToByteArr(bytes);
            bytes.clear();
            return true;
        }
        else if (bytes.size() == 2) {
            if (size == -1) {
                size = convert2BytesToShort(bytes.get(0), bytes.get(1));
                bytes.clear();
            }
            else if (blockNumber == -1) {
                blockNumber = convert2BytesToShort(bytes.get(0), bytes.get(1));
                bytes.clear();
            }
        }
        return false;
    }
    public short getSize(){
        return size;
    }
    
}
