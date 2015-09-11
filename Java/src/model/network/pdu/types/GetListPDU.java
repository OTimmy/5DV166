package model.network.pdu.types;

import model.network.pdu.PDU;

public class GetListPDU extends PDU{
    private int size;

	@Override
	public byte[] toByteArray(int size) {
		this.size = size;
	    byte[] bytes = new byte[size];

		bytes[0] = 3; //getList code

		for(int i = 1; i < size; i++) {
			bytes[i] = 0;
		}

		return bytes;
	}

	@Override
	public int getSize() {
		return size;
	}

}
