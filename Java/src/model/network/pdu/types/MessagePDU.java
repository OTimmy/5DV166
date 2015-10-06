package model.network.pdu.types;


import java.nio.charset.StandardCharsets;
import java.util.Date;

import model.network.pdu.ByteSequenceBuilder;
import model.network.pdu.Checksum;
import model.network.pdu.OpCode;
import model.network.pdu.PDU;


public class MessagePDU extends PDU{
    private final int TIME_START  = 8;
    private final int TIME_LENGTH = 4;
    private final byte PAD = 0;

    private byte[] bytes;
    private boolean validFlag;

    private String msg;
    private String nick;
    private Date date;

    public MessagePDU(byte[] bytes) {
        validFlag = true;
        parseIn(bytes);
    //    validFlag = true;
    }

	public MessagePDU(String message) {
	    bytes = parseOut(message);

	}

	public void parseIn(byte[] bytes) {

		if(Checksum.computeChecksum(bytes) != 0  && !checkPadding(bytes) ) {
		    validFlag = false;
		}

	    int nickLength = (int) (bytes[2] & 0xff);
	    int msgLength = (int) (((bytes[4] & 0xff)<<8) | ( bytes[5] & 0xff) );

	    //Time stamp
	    long seconds = (bytes[8] & 0xff) << 24 | (bytes[9] & 0xff) << 16
	                  | (bytes[10] &0xff) << 8 | (bytes[11] & 0xff);

	    date = new Date(seconds * 1000);

	    //Message
	    int index = TIME_START + TIME_LENGTH;
	    int end = msgLength + index;
	    byte[] msgBytes = new byte[msgLength];
	    for(int i = 0; index < end; i++,index++) {
	        msgBytes[i] = bytes[index];
	    }

	    msg = new String(msgBytes, StandardCharsets.UTF_8);
	    index+= padLengths(msgBytes.length);

	    //Nick
	    byte[] nickBytes = new byte[nickLength];
	    for(int i = 0, j = index; j < (nickLength + index); i++,j++) {
	        nickBytes[i] = bytes[j];
	    }
	    nick = new String(nickBytes, StandardCharsets.UTF_8);

	}

	private byte[] parseOut(String msg) {

		//OP-code,pad,nick length, check sum 0 default.
		ByteSequenceBuilder builder = new ByteSequenceBuilder(OpCode.MESSAGE.value,
									  PAD, PAD,(byte)0);


		byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_8);
		System.out.println("Message out length: "+ msgBytes.length);
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

	private boolean checkStringPadding(byte[] bytes, int start,  int length) {

	    int endOfPadding = start + padLengths(length);

        for(int i  = start;i < endOfPadding; i++ ) {
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
