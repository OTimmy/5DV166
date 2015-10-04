package model.network.pdu.types;

import java.nio.charset.StandardCharsets;

import model.network.MessageData;
import model.network.pdu.ByteSequenceBuilder;
import model.network.pdu.Checksum;
import model.network.pdu.OpCode;
import model.network.pdu.PDU;

//TODO
public class MessagePDU extends PDU{

    private final int timeEnd = 12;


    private byte[] bytes;
    private MessageData msgData;

    public MessagePDU(byte[] bytes) {
        this.bytes = bytes;
        msgData = parseIn(bytes);
    }

	public MessagePDU(String message, String nick) {
	    bytes = parseOut(message,nick);

	}

	public MessageData parseIn(byte[] bytes) {

//		
//		if(Checksum.computeChecksum(bytes) != 0  && !checkPadding() ) {
//			return null;
//		}

	    int nickLength = (int) bytes[2];
	    int msgLength = (int) (bytes[4]<<8) | ( bytes[5] & 0xff);

	    int index = 8;

	    String timeStamp  = "";
	    for( ; index < timeEnd; index++ ) {
	        timeStamp += (char) bytes[index];
	    }

	    byte[] msgBytes = new byte[msgLength];

	    for(int i = 0; index < msgLength+timeEnd; i++,index++) {
	        msgBytes[i] = bytes[index];
	    }
	    String msg = new String(msgBytes, StandardCharsets.UTF_8);;

	    String nick = "";
	    for(; index < (nickLength + msgLength + timeEnd); index++) {
	        nick += bytes[index];
	    }


	    return new MessageData(nick,msg,timeStamp);
	}

	private byte[] parseOut(String msg,String nick) {

		byte[] nickBytes = nick.getBytes();
		//OP-code,pad,nick length, check sum 0 default.
		ByteSequenceBuilder builder = new ByteSequenceBuilder(OpCode.MESSAGE.value,
									(byte) 0, (byte) nickBytes.length,(byte)0);
		
		byte[] msgBytes = msg.getBytes();
		//msg length to two bytes, then pad.
		builder.appendShort((byte) msgBytes.length).pad();
		
		//time stamp zero (over 4 bytes)
		builder.appendInt(0);
		
		//message
		builder.append(msgBytes);
		
		//padding
		builder.pad();
		
		//nick
		builder.append(nickBytes);
		
		//padding
		builder.pad();
		
		//Done
		byte[] bytes = builder.toByteArray();
		

		bytes[3] = Checksum.computeChecksum(bytes);
		
	    return bytes;
	}


	/**
	 *
	 *
	 *
	 * @param start of padding, length of message or nicks
	 * @return true if padding is correct otherwise false.
	 */
	public static boolean checkPadding(byte[] bytes) {

	    if((bytes[1] != 0 || bytes[6] != 0 || bytes[7] != 0)) {
	        return false;
	    }

        int msgLength  = (int) (bytes[4]<<8 & 0xff) | ( bytes[5] & 0xff);
        int msgStart   = 12;

	    //message padding
	    if(!checkStringPadding(bytes, msgStart,msgLength)) {
	        return false;
	    }

	    int nickLength = (int) bytes[2];
        int nickStart  = msgStart + padLengths(msgLength);

	    if(!checkStringPadding(bytes, nickStart,nickLength)) {
	        return false;
	    }

	    return true;
	}

	private static  boolean checkStringPadding(byte[] bytes, int start, int length) {
        int end = start + padLengths(length);

        for(;start < end; start++ ) {
            if(bytes[start] != 0) {
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
		return PDU.pduSize();
	}

	@Override
	public byte getOpCode() {
		return OpCode.MESSAGE.value;
	}

	public MessageData getMessageData() {
	    return msgData;
	}

}
