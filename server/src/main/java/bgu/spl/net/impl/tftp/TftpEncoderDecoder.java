package bgu.spl.net.impl.tftp;

import java.util.LinkedList;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.tftp.packets.*;

public class TftpEncoderDecoder implements MessageEncoderDecoder<BasePacket> {
    //TODO: Implement here the TFTP encoder and decoder
    final LinkedList<Byte> currentBytes;
    private short desiredLength;
    private BasePacket packet;

    public TftpEncoderDecoder() {
        currentBytes = new LinkedList<>();
        desiredLength = -1;
        packet = null;
    }

    @Override
    public BasePacket decodeNextByte(byte nextByte) {
        currentBytes.add(nextByte);
        if (currentBytes.size() == 2) {
            short incomingOpCode = convert2BytesToShort(currentBytes.get(0), currentBytes.get(1));
            if (incomingOpCode == OpCode.RRQ.ordinal()) {
                packet = new ReadRQPacket();
                return null;
            }

            if (incomingOpCode == OpCode.WRQ.ordinal()) {
                packet = new WriteRQPacket();
                return null;
            }

            if (incomingOpCode == OpCode.DATA.ordinal()) {
                packet = new DataPacket();
                return null;
            }

            if (incomingOpCode == OpCode.ACK.ordinal()) {
                packet = new AcknowledgePacket();
                return null;
            }

            if (incomingOpCode == OpCode.ERROR.ordinal()) {
                packet = new ErrorPacket();
                return null;
            }

            if (incomingOpCode == OpCode.DIRQ.ordinal()) {
                return new DirectoryRQPacket();
            }
            if(incomingOpCode == OpCode.DISC.ordinal()){
                return new DisconnectRQPacket();
            }

            if (incomingOpCode == OpCode.LOGRQ.ordinal()) {
                packet = new LoginRQPacket();
                return null;
            }

            if (incomingOpCode == OpCode.DELRQ.ordinal()) {
                packet = new DeleteRQPacket();
                return null;
            }

            if (incomingOpCode == OpCode.BCAST.ordinal()) {
                packet = new BroadCastPacket();
                return null;
            }
        } 
        if(packet.decodeNextByte(nextByte))
            return packet;
        else
            return null;
    }

    @Override
    public byte[] encode(BasePacket message) {
        return message.encodePacket();
    }

    private short convert2BytesToShort(byte b1, byte b2) {
        return (short) ((short)((b1 & 0xFF) << 8) | (short)(b2 & 0xFF));
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