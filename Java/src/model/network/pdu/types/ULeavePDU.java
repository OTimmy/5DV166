package model.network.pdu.types;

import java.nio.charset.StandardCharsets;


import model.network.pdu.PDU;
import model.network.pdu.OpCode;
public class ULeavePDU extends PDU{
	private final int NICK_LENGTH = 1;
	private final int TIME_STAMP  = 4;
	private final int NICK_INDEX  = 8;
	private final int ROW_SIZE    = 4;
	private String nick;
	
	public ULeavePDU(byte[] bytes) {
		parse(bytes);
	}
	
	
	private void parse(byte[] bytes) {
		
		//time stamp
		int start  = TIME_STAMP;
		int length = ROW_SIZE;
		int end    = start + length;
		
		byte[] timeBytes = new byte[ROW_SIZE];
		for(int i = 0,j = start; j < end; i++, j++) {
			timeBytes[i] = bytes[j];
		}
		
		//Nick name
		start  = end;
		length = (bytes[NICK_LENGTH] & 0xff);
		end   += length;
		
		byte[] nickBytes = new byte[length];
		for(int i = 0, j = start; j < end;i++,j++) {
			nickBytes[i] = (byte) (bytes[j]);
		}
		
		nick = new String(nickBytes,StandardCharsets.UTF_8);
		
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

}
