package bgu.spl.net.impl.tftp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.impl.packets.BasePacket;

public class ServerListener implements Runnable{

    private final MessagingProtocol<BasePacket> protocol;
    private final MessageEncoderDecoder<BasePacket> encdec;
    private final BufferedInputStream in;
    private final BufferedOutputStream out;
    private volatile boolean connected = true;
    private KeyboardLocker locker;

    private volatile boolean ackAfterWRQ;
    private volatile boolean ackAfterRRQ;

    public ServerListener(BufferedInputStream in, BufferedOutputStream out, MessageEncoderDecoder<BasePacket> reader, MessagingProtocol<BasePacket> protocol, KeyboardLocker locker) {
        this.in = in;
        this.out = out;
        this.encdec = reader;
        this.protocol = protocol;
        this.locker = locker;
    }



    @Override
    public void run() {
        int read;
        try {
            while (!protocol.shouldTerminate() && connected && (read = in.read()) >= 0) {
                BasePacket nextMessage = encdec.decodeNextByte((byte) read);
                if (nextMessage != null) {
                    BasePacket response = protocol.process(nextMessage);
                    if (response != null) {
                        synchronized (out) {
                            out.write(encdec.encode(response));
                            out.flush();
                        }
                        // if(locker.shouldNotify())
                        //     locker.notifyAll();
                    }
                }
            }
        } catch (IOException e) {}

    }

}
