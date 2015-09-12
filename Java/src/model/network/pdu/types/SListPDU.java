package model.network.pdu.types;

import model.network.pdu.*;

public class SListPDU extends PDU{
	private int size;
 
	@Override
	public byte[] toByteArray(int size) {
		this.size = size;
		byte[] bytes = new byte[size];

		//necessary?
		for(int i = 0; i < size; i++) {
			bytes[i] = 0;
		}

		return bytes;
	}

	@Override
	public int getSize() {
		return size;
	}

	public int getSequenceNr(byte[] bytes) {
	    return bytes[1];
	}

	public String getAddress() {
	    return null;
	}

}
