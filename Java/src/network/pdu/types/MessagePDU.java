package network.pdu.types;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import network.pdu.ByteSequenceBuilder;
import network.pdu.Checksum;
import network.pdu.DateUtils;
import network.pdu.OpCode;
import network.pdu.PDU;


public class MessagePDU extends PDU{
    private final int ROW_SIZE = 4;
    private final byte PAD = 0;

    private byte[] bytes;
    private boolean validFlag;

    private String msg;
    private String nick;
    private Date date;

    public MessagePDU(InputStream inStream) throws IOException {
        validFlag = parseIn(inStream);

    }

	public MessagePDU(String message) {
	    bytes = parseOut(message);

	}

	public boolean parseIn(InputStream inStream) throws IOException {


	    //Reading rest of header
	    byte[] headerBytes = new byte[ROW_SIZE -1];
	    inStream.read(headerBytes, 0, headerBytes.length);

	    //{"Address","Port","Connected","Topic"};
	    int pad = headerBytes[0];
//
//	    if(pad != 0) {
//	        return false;
//	    }

	    int nickLength = (int) (headerBytes[1] & 0xff);
	    int checkSum = (int) (headerBytes[2] & 0xff);

	    //Reading message length + padding
	    byte[] tempBytes = new byte[ROW_SIZE];
	    inStream.read(tempBytes, 0, tempBytes.length);

	    int msgLength = (int) (((tempBytes[0] & 0xff) << 8 ) | (tempBytes[1] & 0xff));
	    pad  = (int) (((tempBytes[2] & 0xff) << 8)  | (tempBytes[3] & 0xff)); //Should be zero

	    System.out.println("SIZE OF MSG: "+msgLength);
//	    if(pad != 0) {
//	        return false;
//	    }

	    //Reading time stamp
	    byte[] timeBytes = new byte[ROW_SIZE];
	    inStream.read(timeBytes, 0, timeBytes.length);

	    date = DateUtils.getDateByBytes(timeBytes); //getDateByBytes(timeBytes);


	    // Reading message
	    byte[] msgBytes = readExactly(msgLength,inStream);

        msg = new String(msgBytes, StandardCharsets.UTF_8);

        //Padding of message
        tempBytes = new byte[padLengths(msgLength)];
        inStream.read(tempBytes, 0, tempBytes.length);

        for(byte b: tempBytes) {

        }

        //check padding of message


        //nick name
        byte[] nickBytes = new byte[nickLength];
        inStream.read(nickBytes, 0, nickBytes.length);

        nick = new String(nickBytes, StandardCharsets.UTF_8);

        //padding of nick
        tempBytes = new byte[padLengths(nickLength)];
        inStream.read(tempBytes, 0, tempBytes.length);

        return true;
	}

	private byte[] parseOut(String msg) {

		//OP-code,pad,nick length, check sum 0 default.
		ByteSequenceBuilder builder = new ByteSequenceBuilder(OpCode.MESSAGE.value,
									  PAD, PAD,(byte)0);


		byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_8);

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


	/**
	 * @param start of padding, length of message or nicks
	 * @return true if padding is correct otherwise false.
	 */
	 private boolean checkPadding(byte[] bytes) {

	    if((bytes[1] != 0 || bytes[6] != 0 || bytes[7] != 0)) {
	        return false;
	    }

        int msgLength  = (int) (bytes[4]<<8  & 0xffff) | ( bytes[5] &  0xffff);
        int msgStart   = 12;


        int msgPaddingStart = msgLength + msgStart;

	    //message padding
	    if(!checkStringPadding(bytes, msgPaddingStart,msgLength)) {
	        return false;
	    }

	    int nickLength = (int) bytes[2];
        int nickStart  = msgStart + msgLength + padLengths(msgLength);

        int nickPaddingStart = nickLength + nickStart;

	    if(!checkStringPadding(bytes, nickPaddingStart,nickLength)) {
	        return false;
	    }

	    return true;
	}

	 /**
	  *
	  */
	private boolean checkStringPadding(byte[] bytes, int start,  int length) {

	    int endOfPadding = start + padLengths(length);

        for(int i  = start;i < endOfPadding && i < bytes.length; i++ ) {
            if(bytes[i] != 0) {
                return false;
            }
        }

        return true;
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

	public boolean isValid() {
	    return validFlag;
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
}