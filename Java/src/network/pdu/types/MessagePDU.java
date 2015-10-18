package network.pdu.types;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;

import network.pdu.ByteSequenceBuilder;
import network.pdu.Checksum;
import network.pdu.DateUtils;
import network.pdu.OpCode;
import network.pdu.PDU;


public class MessagePDU extends PDU{
    private final int ROW_SIZE = 4;
    private final byte PAD = 0;

    private String error;
    
    private byte[] bytes;
    private ArrayList<String> errors;

    private String msg;
    private String nick;
    private Date date;

    public MessagePDU(InputStream inStream) throws IOException {
        error = parseIn(inStream);

    }

    public MessagePDU(String message) {
        bytes = parseOut(message);

    }

    public String parseIn(InputStream inStream) throws IOException {

        //TODO temporary arraylist containing all the bytes of the pdu
        ArrayList<Byte>bytes = new ArrayList<Byte>();
        bytes.add(getOpCode());

        //reading pdu pad
        byte[] paddedBytes = readExactly(1, inStream);
        
        if(!isPaddedBytes(paddedBytes)) {
            return ERROR_PADDING_PDU;
        }
		
        bytes.add(paddedBytes[0]);
        
        byte[] nickLenghByte = readExactly(1, inStream);
        int nickLength = nickLenghByte[0] & 0xff;
        bytes.add(nickLenghByte[0]);
	    
        //checksum
        bytes.add(readExactly(1, inStream)[0]);
    
        //reading length
        byte[] msgLenghBytes = readExactly(2, inStream);
        for(byte b:msgLenghBytes) {
            bytes.add(b);
        }
	    
        int msgLength = (int) (((msgLenghBytes[0] & 0xff) << 8 ) 
                              | (msgLenghBytes[1] & 0xff));

        //Reading two padding bytes
        paddedBytes = readExactly(2, inStream);
        if(!isPaddedBytes(paddedBytes)) {
            return ERROR_PADDING_PDU;
        }
        for(byte b:paddedBytes) {
            bytes.add(b);
        }

        //Reading time stamp
        byte[] timeBytes = readExactly(ROW_SIZE, inStream);
        date = DateUtils.getDateByBytes(timeBytes);
        for(byte b:timeBytes) {
            bytes.add(b);
        }

        // Reading message
        byte[] msgBytes = readExactly(msgLength,inStream);
        for(byte b:msgBytes) {
            bytes.add(b);
        }
        
        //Padding of message
        paddedBytes = readExactly(padLengths(msgLength), inStream);
        if(!isPaddedBytes(paddedBytes)) {
            return ERROR_PADDING_MSG;
        }
        for(byte b:paddedBytes) {
            bytes.add(b);
        }
        
        msg = new String(msgBytes, StandardCharsets.UTF_8);

        //nick name
        byte[] nickBytes = readExactly(nickLength, inStream);
        for(byte b:nickBytes) {
            bytes.add(b);
        }

        //padding of nick
        paddedBytes = readExactly(padLengths(nickLength), inStream);
        if(!isPaddedBytes(paddedBytes)) {
            return ERROR_PADDING_NICK;
        }
        for(byte b:paddedBytes) {
            bytes.add(b);
        }
        
        if(nickBytes.length == 0) {
            nick = "Server";
        } else {
            nick = new String(nickBytes, StandardCharsets.UTF_8);
        }

        //Create byte array
        byte[] pduBytes = new byte[bytes.size()];
        for(int i = 0; i < bytes.size(); i++) {
            pduBytes[i] = bytes.get(i);
        }

        if(Checksum.computeChecksum(pduBytes) != 0) {
            System.out.println("invalid checksum");
            return "Incorrect checksum";
        }

        return null;
    }

    private byte[] parseOut(String msg) {

        //OP-code,pad,nick length, check sum 0 default.
        ByteSequenceBuilder builder = new ByteSequenceBuilder(OpCode.MESSAGE.value,
                                                              PAD, PAD,(byte)0);

        byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_8);
        System.out.println("Sending bytes: "+msgBytes.length);
        //msg length to two bytes, then pad.
        builder.appendShort((short) msgBytes.length);
        //pad remaining 2 bytes
        builder.pad();

        //time stamp zero (over 4 bytes)
        builder.appendInt(0);

        //message
        builder.append(msgBytes);

        //padding
        builder.pad();

        //Done
        byte[] bytes = builder.toByteArray();

        bytes[3] = Checksum.computeChecksum(bytes);

        return bytes;
    }

    @Override
    public byte[] toByteArray() {
        return bytes;
    }

    @Override
    public int getSize() {
        return bytes.length;
    }

    @Override
    public byte getOpCode() {
        return OpCode.MESSAGE.value;
    }
    
    public ArrayList<String> getErrors() {
        return errors;
    }

    public String getMsg() {
        return msg;
    }

    public String getNick() {
        return nick;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String getError() {
        return error;
    }
}