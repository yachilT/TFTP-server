package bgu.spl.net.impl.tftp.packets;

import java.util.LinkedList;
import java.util.List;

import bgu.spl.net.impl.tftp.TftpProtocol;

public abstract class BasePacket {
    protected final static byte[] ZERO = new byte[]{(byte) 0};
    protected final static byte[] ONE = new byte[]{(byte) 1};

    protected OpCode opcode;
    protected int length;
    protected final List<Byte> bytes = new LinkedList<>();

    public BasePacket(OpCode opcode){
        this.opcode = opcode;
        this.length = 2;
    }
    public BasePacket(OpCode opcode, short length){
        this.opcode = opcode;
        this.length = length;
    }



    public abstract void applyRequest(TftpProtocol protocol);
    public abstract byte[] encodePacket();
    public abstract boolean decodeNextByte(byte nextByte);

    public OpCode getOpCode()
    {
        return opcode;
    }
    protected byte[] convertShortToBytes(short num){
        return new byte[]{(byte) (num >> 8), (byte) (num & 0xff)};
    }

    protected short convert2BytesToShort(byte b1, byte b2) {
        return (short) ((short)((b1 & 0xFF) << 8) | (short)(b2 & 0xFF));
    } 
    
    protected byte[] mergeArrays(byte[] arr1, byte[] arr2){
        byte[] merge = new byte[arr1.length + arr2.length];
        int index = 0;
        for (byte b : arr1) {
            merge[index++] = b; 
        }
        for (byte b : arr2) {
            merge[index++] = b;
        }
        return merge;
    }

    protected byte[] convertListToByteArr(List<Byte> bytes) {
        byte[] arr = new byte[bytes.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = bytes.remove(0);
        }

        return arr;
    }

    @Override
    public String toString() {
        return opcode.name();
    }

}
