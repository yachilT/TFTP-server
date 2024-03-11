package bgu.spl.net.impl.tftp.packets;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import bgu.spl.net.impl.tftp.OpCode;
import bgu.spl.net.impl.tftp.TftpProtocol;

public class ReadRQPacket extends BasePacket {
    private String fileName;
    public ReadRQPacket(){
        super(OpCode.RRQ);
        this.fileName = null;
    }
    @Override
    public void applyRequest(TftpProtocol tftp){
        if(tftp.isLoggedIn())
            tftp.processReadRQPacket(this);
        else   
            tftp.sendsErrorNotLoggedIn();
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public byte[] encodePacket() {
        byte[] result;
        result = convertShortToBytes((short)opcode.ordinal()); // opcode
        try {
            result = mergeArrays(result, fileName.getBytes("UTF-8")); //file name
        } catch (UnsupportedEncodingException e) {}
        result = mergeArrays(result, ZERO); // 0 byte

        return result;
    }
    @Override
    public boolean decodeNextByte(byte nextByte){
        System.out.print(nextByte + " ");
        if(nextByte != 0){
            bytes.add(nextByte);
            length++;
            return false;
        }
        byte[] byteArr = convertListToByteArr(bytes);
        bytes.clear();
        fileName = new String(byteArr, StandardCharsets.UTF_8);
        System.out.println("\n" + fileName);
        return true;
    }
}
