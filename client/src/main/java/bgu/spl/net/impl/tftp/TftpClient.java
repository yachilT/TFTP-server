package bgu.spl.net.impl.tftp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.FileAlreadyExistsException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.impl.packets.*;

public class TftpClient {
    private Scanner scanner = new Scanner(System.in);

    public int serverPort = 7777;
    public String serverIp = "127.0.0.1";
    private CurrentRequest currentRequest;

    private MessageEncoderDecoder<BasePacket> encdec;
    private MessagingProtocol<BasePacket> protocol;

    private Queue<String> inputQueue;

    public TftpClient() {
        scanner = new Scanner(System.in);

        currentRequest = new CurrentRequest();
        protocol = new TftpMessagingProtocol(currentRequest);

        encdec = new TftpMessageEncoderDecoder();
        inputQueue = new LinkedList<>();
    }  
    public void run() throws IOException {
        try (Socket socket = new Socket(serverIp, serverPort); 
            BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
            BufferedInputStream in = new BufferedInputStream(socket.getInputStream());) {
        
        
        
            ServerListener serverListener = new ServerListener(in, out, encdec, protocol, currentRequest);
            Thread threadServerListener = new Thread(serverListener);
            threadServerListener.start();
            
            while(!protocol.shouldTerminate()) {

                while (System.in.available() == 0 & (inputQueue.isEmpty() | !currentRequest.isDone()) & !protocol.shouldTerminate());

                if (System.in.available() > 0) {
                    inputQueue.add(scanner.nextLine());
                }

                if (!inputQueue.isEmpty() & currentRequest.isDone()) {
                    BasePacket result = classifyInput(inputQueue.remove());
                    if (result != null) {
                        try {
                            synchronized (out) {
                                byte[] b = encdec.encode(result);
                                out.write(b);
                                out.flush();
                            }
                        }
                        catch (IOException e) { e.printStackTrace();}
                    }
                }
                threadServerListener.interrupt();
            }
        } 
        
    }
        
    

    public BasePacket classifyInput(String input){
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

    public BasePacket handleDISC(){
        DisconnectRQPacket packet = new DisconnectRQPacket();
        currentRequest.setRequest(packet);
       return packet;
        
    }
    public BasePacket handleDIRQ(){
        DirectoryRQPacket packet = new DirectoryRQPacket();
        currentRequest.setRequest(packet);
        return packet;
    }
    public BasePacket handleLOGRQ(String username){
        LoginRQPacket packet = new LoginRQPacket(username);
        currentRequest.setRequest(packet);
        return packet;
    }
    public BasePacket handleDELRQ(String filename){
        DeleteRQPacket packet = new DeleteRQPacket(filename);     
        currentRequest.setRequest(packet);
        return packet;
    }
    public BasePacket handleRRQ(String filename){
        ReadRQPacket packet = new ReadRQPacket(filename);
        try {
            currentRequest.setRequest(packet);
        } catch (FileAlreadyExistsException e) {
            System.out.println(filename + " already exists");
            return null;
        } catch (IOException e) {
            System.out.println("Couldn't download");
            return null;
        } 
        return packet;
    }
    public BasePacket handleWRQ(String filename){
        WriteRQPacket packet = new WriteRQPacket(filename);
        try {
            currentRequest.setRequest(packet);
        } catch (FileNotFoundException e) {
            System.out.println(filename + " doesn't exist");
        }
        return packet;
    }
    public static void main(String[] args) throws IOException {
        TftpClient client = new TftpClient();
        client.serverIp = args[0];
        client.serverPort = Integer.valueOf(args[1]);
        client.run();
    }
}
