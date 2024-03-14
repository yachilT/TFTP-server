package bgu.spl.net.impl.tftp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.impl.packets.BasePacket;

public class ServerListener implements Runnable{

    private final MessagingProtocol<BasePacket> protocol;
    private final MessageEncoderDecoder<BasePacket> encdec;
    private final BufferedInputStream in;
    private final BufferedOutputStream out;
    private volatile boolean connected = true;
    private final CurrentRequest currentRequest;

    
    public ServerListener(BufferedInputStream in, BufferedOutputStream out, MessageEncoderDecoder<BasePacket> reader, MessagingProtocol<BasePacket> protocol, CurrentRequest currentRequest) {
        this.in = in;
        this.out = out;
        this.encdec = reader;
        this.protocol = protocol;
        this.currentRequest = currentRequest;
    }



    @Override
    public void run() {
        int read;
        try {
            System.out.println("client started");
            while (!protocol.shouldTerminate() && connected && (read = in.read()) >= 0) {
                BasePacket nextMessage = encdec.decodeNextByte((byte) read);
                if (nextMessage != null) {
                    BasePacket response = protocol.process(nextMessage);
                    if (response != null) {
                        synchronized (out) {
                            byte[] b = encdec.encode(response);
                            out.write(b);
                            out.flush();
                        }
                        if(currentRequest.shouldFinish())
                            currentRequest.markAsDone();
                    }
                }
            }
        } catch (IOException e) {}

    }

}
