package bgu.spl.net.impl.tftp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Supplier;

import bgu.spl.net.api.*;
import bgu.spl.net.impl.tftp.packets.BasePacket;
import bgu.spl.net.srv.Server;


public class TftpServer implements Server<BasePacket>{
    
    private final int port;
    private final Supplier<BidiMessagingProtocol<BasePacket>> protocolFactory;
    private final Supplier<MessageEncoderDecoder<BasePacket>> encdecFactory;
    private ServerSocket sock;
    private int connectionsCounter;

    public TftpServer(
            int port,
            Supplier<BidiMessagingProtocol<BasePacket>> protocolFactory,
            Supplier<MessageEncoderDecoder<BasePacket>> encdecFactory) {

        this.port = port;
        this.protocolFactory = protocolFactory;
        this.encdecFactory = encdecFactory;
		this.sock = null;
        connectionsCounter = 0;
    }

    @Override
    public void serve() {

        try (ServerSocket serverSock = new ServerSocket(port)) {
			System.out.println("Server started");

            this.sock = serverSock; //just to be able to close

            while (!Thread.currentThread().isInterrupted()) {

                Socket clientSock = serverSock.accept();
                BidiMessagingProtocol<BasePacket> protocol = protocolFactory.get(); 
                protocol.start(connectionsCounter++, null);
                TftpBlockingConnectionHandler<BasePacket> handler = new TftpBlockingConnectionHandler<>(
                        clientSock,
                        encdecFactory.get(),
                        protocolFactory.get());
                execute(handler);
            }
        } catch (IOException ex) {
        }

        System.out.println("server closed!!!");
    }

    @Override
    public void close() throws IOException {
		if (sock != null)
			sock.close();
    }
    protected void execute(TftpBlockingConnectionHandler<BasePacket>  handler){
            new Thread(handler).start();
    }
}
