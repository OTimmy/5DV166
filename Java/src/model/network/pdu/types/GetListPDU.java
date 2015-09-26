package model.network.pdu.types;

import model.network.pdu.PDU;

public class GetListPDU extends PDU{
    private final int  size = 4;

	public byte[] toByteArray() {
	    byte[] bytes = new byte[size];
		bytes[0] = 3; //getList code

		for(int i = 1; i < size; i++) {
			bytes[i] = 0;
		}

		return bytes;
	}


	public int getSize() {
		return size;
	}
}
