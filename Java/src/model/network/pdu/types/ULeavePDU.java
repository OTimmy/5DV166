package model.network.pdu.types;

import java.nio.charset.StandardCharsets;
import java.util.Date;


import model.network.pdu.PDU;
import model.network.pdu.OpCode;
public class ULeavePDU extends PDU{
	private final int NICK_LENGTH = 1;
	private final int TIME_STAMP_START  = 4;
	private final int TIME_LENGTH = 4;
	private final int NICK_START = 8;

	private String nick;
	private Date date;

	private boolean validFlag;


	public ULeavePDU(byte[] bytes) {
		validFlag = true;
	    parse(bytes);
	}


	private void parse(byte[] bytes) {

	    validFlag = checkPadding(bytes);

	    if(validFlag) {

	        long seconds = (bytes[4] & 0xff) << 24 | (bytes[5] & 0xff) << 16
	                | (bytes[6] & 0xff) << 8 | (bytes[7] & 0xff);

	        date = new Date(seconds * 1000);


	        //Nick name
	        int start  = TIME_STAMP_START + TIME_LENGTH;
	        int length = (bytes[NICK_LENGTH] & 0xff);
	        int end   = TIME_STAMP_START + TIME_LENGTH + length;


	        byte[] nickBytes = new byte[length];
	        for(int i = 0, j = start; j < end;i++,j++) {
	            nickBytes[i] = (byte) (bytes[j]);
	        }

	        nick = new String(nickBytes,StandardCharsets.UTF_8);
	    }

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

	/**
	 *  @param bytes form inputstream, length is the length of the nickname
	 */
	private boolean checkPadding(byte[] bytes) {

	    if(bytes[2] != 0 || bytes[3] != 0) {
	        return false;
	    }

	    int length = (bytes[NICK_LENGTH] & 0xff);
	    int padded = length + padLengths(length) + NICK_START;
	    int endOfNick = length + NICK_START;

	    for(int i = endOfNick; i < padded; i++) {
	        if(bytes[i] != 0) {
	            return false;
	        }
	    }

	    return true;
	}

	public boolean isValid() {
	    return validFlag;
	}

}
