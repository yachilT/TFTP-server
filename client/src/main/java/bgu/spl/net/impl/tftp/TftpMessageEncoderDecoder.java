package bgu.spl.net.impl.tftp;

import java.util.LinkedList;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.packets.*;

public class TftpMessageEncoderDecoder implements MessageEncoderDecoder<BasePacket> {
    final LinkedList<Byte> currentBytes;
    private BasePacket packet;

    public TftpMessageEncoderDecoder() {
        currentBytes = new LinkedList<>();
        packet = null;
    }

    @Override
    public BasePacket decodeNextByte(byte nextByte) {
        if (packet == null) {
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
                    packet = null;
                    currentBytes.clear();
                    return new DirectoryRQPacket();
                }
                if(incomingOpCode == OpCode.DISC.ordinal()){
                    packet = null;
                    currentBytes.clear();
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
                return null;
            }
            return null; 
        }
        else {
            if(packet.decodeNextByte(nextByte)) {
                BasePacket finishedPacket = packet;
                packet = null;
                currentBytes.clear();
                return finishedPacket;
            }
            return null;
        }
    
    }

    @Override
    public byte[] encode(BasePacket message) {
        return message.encodePacket();
    }

    private short convert2BytesToShort(byte b1, byte b2) {
        return (short) ((short)((b1 & 0xFF) << 8) | (short)(b2 & 0xFF));
    }
    
}