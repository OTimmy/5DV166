package model.network.pdu.types;

import java.util.Date;

import model.network.pdu.OpCode;
import model.network.pdu.PDU;

public class MessagePDU extends PDU{
	
	public MessagePDU(String message, String nickname,Date timestamp) {
		
	}
	
	@Override
	public byte[] toByteArray() {
		return null;
	}

	@Override
	public int getSize() {
		return 0;
	}

	@Override
	public byte getOpCode() {
		return OpCode.MESSAGE.value;
	}
}
