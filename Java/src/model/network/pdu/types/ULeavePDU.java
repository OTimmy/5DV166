package model.network.pdu.types;

import java.nio.charset.StandardCharsets;
import java.util.Date;


import model.network.pdu.DateUtils;
import model.network.pdu.PDU;
import model.network.pdu.OpCode;
public class ULeavePDU extends PDU{
	private final int NICK_LENGTH = 1;
	private final int TIME_STAMP_START  = 4;
	private final int TIME_LENGTH = 4;
	private String nick;
	private Date date;
	public ULeavePDU(byte[] bytes) {
		parse(bytes);
	}


	private void parse(byte[] bytes) {

		int seconds = (bytes[4] & 0xff) << 24 | (bytes[5] & 0xff) << 16 | (bytes[6] & 0xff) << 8
		              | (bytes[7] & 0xff);
		date = DateUtils.toDate(seconds);


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

}
