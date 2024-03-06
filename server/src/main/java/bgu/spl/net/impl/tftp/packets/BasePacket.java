package bgu.spl.net.impl.tftp.packets;

import bgu.spl.net.impl.tftp.TftpProtocol;

public abstract class BasePacket {
    protected final static byte[] ZERO = new byte[]{(byte) 0};
    protected final static byte[] ONE = new byte[]{(byte) 1};

    short opcode;
    short length;

    public BasePacket(short opcode){
        this.opcode = opcode;
        this.length = -1;
    }

    public BasePacket(short opcode, short length){
        this.opcode = opcode;
        this.length = length;
    }


    public abstract void applyRequest(TftpProtocol protocol);
    public abstract byte[] encodePacket();
    public abstract boolean decodeNextByte(byte nextByte);


    protected byte[] convertShortToBytes(short num){
        return new byte[]{(byte) (num >> 8), (byte) (num & 0xff)};
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
}
