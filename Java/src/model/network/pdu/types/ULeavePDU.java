package model.network.pdu.types;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;


import model.network.pdu.PDU;
import model.network.pdu.OpCode;
public class ULeavePDU extends PDU{
	private final int ROW_SIZE = 4;

	private String nick;
	private Date date;

	private boolean validFlag;


	public ULeavePDU(InputStream inStream) throws IOException {
		validFlag = true;
	    parse(inStream);
	}


	private void parse(InputStream inStream) throws IOException {

	    int nickLength = inStream.read();

	    //reading pad
	    byte[] tempBytes = new byte[2];
	    inStream.read(tempBytes, 0, tempBytes.length);

	    //Reading time stamp
	    tempBytes = new byte[ROW_SIZE];
	    inStream.read(tempBytes, 0, tempBytes.length);

	    long seconds = (tempBytes[0] & 0xff) << 24 | (tempBytes[1] & 0xff) << 16
	                   |(tempBytes[2] & 0xff) << 8 | (tempBytes[3] & 0xff);

	    date = new Date(seconds);

	    //Reading nick
	    byte[] nickBytes = new byte[nickLength];
	    inStream.read(nickBytes, 0, nickBytes.length);

	    //Reading pad of nick
	    tempBytes = new byte[padLengths(nickLength)];
	    inStream.read(tempBytes, 0, tempBytes.length);

	    nick = new String(nickBytes, StandardCharsets.UTF_8);
	}

	@Override
	public byte[] toByteArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte getOpCode() {
		return OpCode.ULEAVE.value;
	}

	public String getNick() {
		return nick;
	}

	public Date getDate() {
	    return date;
	}

//	/**
//	 *  @param bytes form inputstream, length is the length of the nickname
//	 */
//	private boolean checkPadding(byte[] bytes) {
//
//	    if(bytes[2] != 0 || bytes[3] != 0) {
//	        return false;
//	    }
//
//	    int length = (bytes[NICK_LENGTH] & 0xff);
//	    int padded = length + padLengths(length) + NICK_START;
//	    int endOfNick = length + NICK_START;
//
//	    for(int i = endOfNick; i < padded; i++) {
//	        if(bytes[i] != 0) {
//	            return false;
//	        }
//	    }
//
//	    return true;
//	}

	public boolean isValid() {
	    return validFlag;
	}

}
