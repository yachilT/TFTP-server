package bgu.spl.net.impl.tftp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import bgu.spl.net.api.*;
import bgu.spl.net.impl.tftp.packets.BasePacket;
import bgu.spl.net.impl.tftp.transferdatapackets.FileManager;
import bgu.spl.net.srv.Connections;
import bgu.spl.net.srv.ConnectionsImpl;
import bgu.spl.net.srv.Server;


public class TftpServer implements Server<BasePacket>{
    
    private final int port;
    private final Supplier<BidiMessagingProtocol<BasePacket>> protocolFactory;
    private final Supplier<MessageEncoderDecoder<BasePacket>> encdecFactory;
    private final Connections<BasePacket> connections;
    private ServerSocket sock;
    private int connectionsCounter;

    public TftpServer(
            int port,
            Supplier<BidiMessagingProtocol<BasePacket>> protocolFactory,
            Supplier<MessageEncoderDecoder<BasePacket>> encdecFactory) {
        connections = new ConnectionsImpl<>();
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
                protocol.start(connectionsCounter, connections);
                TftpBlockingConnectionHandler<BasePacket> handler = new TftpBlockingConnectionHandler<>(
                        clientSock,
                        encdecFactory.get(),
                        protocol);
                execute(handler);
                connections.connect(connectionsCounter++, handler);
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

    public static void main(String[] args) {
        Map<Integer, String> users = new ConcurrentHashMap<>();
        FileManager fileManager;
        try {
            fileManager = new FileManager();
        } catch (FileNotFoundException e) {
            System.out.println("files directory not found");
            return;
        }
        Server<BasePacket> server = new TftpServer(Integer.valueOf(args[0]), () -> new TftpProtocol(users, fileManager), () -> new TftpEncoderDecoder());
        server.serve();
        try {
            server.close();
        } catch (IOException e) {}
    }
}
