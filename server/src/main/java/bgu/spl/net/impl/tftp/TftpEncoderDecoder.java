package bgu.spl.net.impl.tftp;

import java.util.LinkedList;

import bgu.spl.net.api.MessageEncoderDecoder;

public class TftpEncoderDecoder implements MessageEncoderDecoder<byte[]> {
    //TODO: Implement here the TFTP encoder and decoder
    final byte endByte = 0;
    final LinkedList<Byte> currentBytes;

    public TftpEncoderDecoder() {
        currentBytes = new LinkedList<>();
    }

    @Override
    public byte[] decodeNextByte(byte nextByte) {
        currentBytes.add(nextByte);
        if (nextByte == endByte && !currentBytes.isEmpty()) {
            
            byte[] res = new byte[currentBytes.size()];
            int i = 0;
            for (Byte b : currentBytes) {
                res[i] = b.byteValue();
                i++;
            }
            return res;
        }
        return null;

    }

    @Override
    public byte[] encode(byte[] message) {
        return message;
    }


    
}