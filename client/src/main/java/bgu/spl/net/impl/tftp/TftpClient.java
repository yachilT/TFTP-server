package bgu.spl.net.impl.tftp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Scanner;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.impl.packets.*;
import bgu.spl.net.impl.tftp.transferdatapackets.FileManager;

public class TftpClient {
    private Scanner scanner = new Scanner(System.in);

    private int serverPort = 7777;
    private String serverIp = "127.0.0.1";

    private Socket sock;
    private BufferedOutputStream out;
    private LastRequest lastRequest;
    private KeyboardLocker keyboardLocker;

    private MessageEncoderDecoder<BasePacket> encdec;
    private MessagingProtocol<BasePacket> protocol;

    public TftpClient() {
        scanner = new Scanner(System.in);

        keyboardLocker = new KeyboardLocker();
        protocol = new TftpMessagingProtocol(keyboardLocker, lastRequest);
        lastRequest = new LastRequest();

        encdec = new TftpMessageEncoderDecoder();
    }  
    public void run() {
        try {
            sock = new Socket(serverIp, serverPort);
            out = new BufferedOutputStream(sock.getOutputStream());
        } 
        catch (UnknownHostException e) {e.printStackTrace();}
        catch (IOException e) { e.printStackTrace();}
        
        try {
            ServerListener serverListener = new ServerListener(new BufferedInputStream(sock.getInputStream()), out, encdec, protocol, keyboardLocker);
            new Thread(serverListener).start();

            while(!protocol.shouldTerminate()) {
                String input = scanner.nextLine();
                BasePacket result = classifyInput(input);
                if (result != null) {
                    try {
                        synchronized (out) {
                            out.write(encdec.encode(result));
                            out.flush();
                        }
                    } 
                    catch (IOException e) { e.printStackTrace();}
                }
                keyboardLocker.wait();
            }
        } catch (IOException | InterruptedException e) {} 
        
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
        lastRequest.setRequest(packet);
       return packet;
        
    }
    public BasePacket handleDIRQ(){
        DirectoryRQPacket packet = new DirectoryRQPacket();
        lastRequest.setRequest(packet);
        return packet;
    }
    public BasePacket handleLOGRQ(String username){
        LoginRQPacket packet = new LoginRQPacket(username);
        lastRequest.setRequest(packet);
        return packet;
    }
    public BasePacket handleDELRQ(String filename){
        DeleteRQPacket packet = new DeleteRQPacket();     
        lastRequest.setRequest(packet);
        return packet;
    }
    public BasePacket handleRRQ(String filename){
        ReadRQPacket packet = new ReadRQPacket(filename);
        try {
            lastRequest.setRequest(packet);
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
            lastRequest.setRequest(packet);
        } catch (FileNotFoundException e) {
            System.out.println(filename + " doesn't exist");
        }
        return packet;
    }
    public static void main(String[] args) {
        TftpClient client = new TftpClient();
        client.run();
    }
}
