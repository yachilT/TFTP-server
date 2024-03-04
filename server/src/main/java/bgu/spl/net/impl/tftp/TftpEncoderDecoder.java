package bgu.spl.net.impl.tftp;

import java.util.LinkedList;

import bgu.spl.net.api.MessageEncoderDecoder;

public class TftpEncoderDecoder implements MessageEncoderDecoder<byte[]> {
    //TODO: Implement here the TFTP encoder and decoder
    final LinkedList<Byte> currentBytes;
    private short desiredLength;
    private OpCode opCode;

    public TftpEncoderDecoder() {
        currentBytes = new LinkedList<>();
        desiredLength = -1;
        opCode = OpCode.UNDEFINED;
    }

    @Override
    public byte[] decodeNextByte(byte nextByte) {
        currentBytes.add(nextByte);
        if (currentBytes.size() == 2) {
            short incomingOpCode = convert2BytesToShort(currentBytes.get(0), currentBytes.get(1));
            if (incomingOpCode == OpCode.RRQ.ordinal()) {
                opCode = OpCode.RRQ;
                return null;
            }

            if (incomingOpCode == OpCode.WRQ.ordinal()) {
                opCode = OpCode.WRQ;
                return null;
            }

            if (incomingOpCode == OpCode.DATA.ordinal()) {
                opCode = OpCode.DATA;
                return null;
            }

            if (incomingOpCode == OpCode.ACK.ordinal()) {
                opCode = OpCode.ACK;
                return null;
            }

            if (incomingOpCode == OpCode.ERROR.ordinal()) {
                opCode = OpCode.ERROR;
                return null;
            }

            if (incomingOpCode == OpCode.DIRQ.ordinal() | incomingOpCode == OpCode.DISC.ordinal()) {
                opCode = OpCode.UNDEFINED;
                return convertToByteArr();
            }

            if (incomingOpCode == OpCode.LOGRQ.ordinal()) {
                opCode = OpCode.LOGRQ;
                return null;
            }

            if (incomingOpCode == OpCode.DELRQ.ordinal()) {
                opCode = OpCode.DELRQ;
                return null;
            }

            if (incomingOpCode == OpCode.BCAST.ordinal()) {
                opCode = OpCode.BCAST;
                return null;
            }
        } 
        else if (opCode == OpCode.ACK && currentBytes.size() == 4) {
            opCode = OpCode.UNDEFINED;
            return convertToByteArr();
        }
        else if (opCode == OpCode.DATA && currentBytes.size() == 4) {
            desiredLength = convert2BytesToShort(currentBytes.get(3), currentBytes.get(4));
            return null;
        }
        else if (opCode == OpCode.DATA & currentBytes.size() == desiredLength) {
            desiredLength = -1;
            opCode = OpCode.UNDEFINED;
            return convertToByteArr();
        }
        else if ((opCode == OpCode.RRQ 
        | opCode == OpCode.WRQ 
        | opCode == OpCode.LOGRQ 
        | opCode == OpCode.DELRQ 
        | (opCode == OpCode.ERROR & currentBytes.size() > 4)
        | (opCode == OpCode.BCAST & currentBytes.size() > 3)) & nextByte == 0) {
            opCode = OpCode.UNDEFINED;
            return convertToByteArr();
        } 
        return null;

    }

    @Override
    public byte[] encode(byte[] message) {
        return message;
    }

    private short convert2BytesToShort(byte b1, byte b2) {
        return (short) ((short)(b1 << 8) | (short)b2);
    } 

    private byte[] convertToByteArr() {
        byte[] arr = new byte[currentBytes.size()];
        int i = 0;
        for (byte b : currentBytes) {
            arr[i] = b;
        }

        return arr;
    }



}