package model.network.pdu.types;

import java.util.Date;

import model.network.pdu.PDU;

public class MessagePDU extends PDU{

	public MessagePDU(String message) {
		
	}
	
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

}
