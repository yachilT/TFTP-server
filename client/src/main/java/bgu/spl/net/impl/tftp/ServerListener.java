package bgu.spl.net.impl.tftp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.impl.packets.*;
import bgu.spl.net.impl.packets.BasePacket;

public class ServerListener implements Runnable{

    private final MessagingProtocol<BasePacket> protocol;
    private final MessageEncoderDecoder<BasePacket> encdec;
    private final Socket sock;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private volatile boolean connected = true;

    private BlockingQueue<BasePacket> packetsToSend;

    public ServerListener(Socket sock, MessageEncoderDecoder<BasePacket> reader, MessagingProtocol<BasePacket> protocol, BlockingQueue<BasePacket> packetsToSend) {
        this.sock = sock;
        this.encdec = reader;
        this.protocol = protocol;
        this.packetsToSend = packetsToSend;
    }

    @Override
    public void run() {
        try (Socket sock = this.sock) { //just for automatic closing
            int read;

            in = new BufferedInputStream(sock.getInputStream());
            out = new BufferedOutputStream(sock.getOutputStream());

            while (!protocol.shouldTerminate() && connected && (read = in.read()) >= 0) {
                BasePacket nextMessage = encdec.decodeNextByte((byte) read);
                if (nextMessage != null) {
                    BasePacket response = protocol.process(nextMessage);
                    if (response != null) {
                        synchronized(out){
                        out.write(encdec.encode(response));
                        out.flush();
                        }
                    }
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

}
