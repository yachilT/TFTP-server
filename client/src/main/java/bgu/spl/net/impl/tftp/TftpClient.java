package bgu.spl.net.impl.tftp;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.packets.*;

public class TftpClient {
    //TODO: implement the main logic of the client, when using a thread per client the main logic goes here
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean terminate = false;
        int serverPort = 7777;
        String serverIp = "127.0.0.1";
        Socket sock = null;
        BufferedOutputStream out = null;
        BlockingQueue<BasePacket> packetsToSend = new LinkedBlockingDeque<>();
        MessageEncoderDecoder<BasePacket> encdec = new TftpMessageEncoderDecoder();

        try {
            sock = new Socket(serverIp, serverPort);
            out = new BufferedOutputStream(sock.getOutputStream());
        } 
        catch (UnknownHostException e) {e.printStackTrace();}
        catch (IOException e) { e.printStackTrace();}
        Thread listenerServerThread = new Thread(new ServerListener(sock , new TftpMessageEncoderDecoder(), new TftpMessagingProtocol(), packetsToSend));
        listenerServerThread.start();
        
        while(!terminate){
            String input = scanner.nextLine();
            BasePacket result = classifyInput(input);
            if (result != null) {
                try {
                    out.write(encdec.encode(result));
                    out.flush();
                } 
                catch (IOException e) { e.printStackTrace();}
                }
            }
        }
        
    

    public static BasePacket classifyInput(String input){
        String[] parts = input.split(" ", 2);
        if(parts.length == 0){
            System.out.println("Invalid command");
            return null;
        }
        String method = parts[0];
        String name = parts.length > 1 ? parts[1] : "";
        switch (method) {
            case "DISC":
                return handleDISC();
            case "DIRQ":
                return handleDIRQ();
            case "LOGRQ":
                return handleLOGRQ(name);
            case "DELRQ":
                return handleDELRQ(name);
            case "RRQ":
                return handleRRQ(name);
            case "WRQ":
                return handleWRQ(name);
            default:
                System.out.println("Invalid command");
                return null;
        }

    }
    public static BasePacket handleDISC(){
       return new DisconnectRQPacket();
        
    }
    public static BasePacket handleDIRQ(){
        return new DirectoryRQPacket();
    }
    public static BasePacket handleLOGRQ(String username){
        return new LoginRQPacket(username);
    }
    public static BasePacket handleDELRQ(String filename){
        return new DeleteRQPacket(filename);
    }
    public static BasePacket handleRRQ(String filename){
        return new ReadRQPacket(filename);
    }
    public static BasePacket handleWRQ(String filename){
        return new WriteRQPacket(filename);
    }
}
