package model.network.pdu.types;

import model.network.pdu.PDU;

public class GetListPDU extends PDU{

	@Override
	public byte[] toByteArray(int size) {
		byte[] bytes = new byte[size];

		bytes[0] = 3; //getList code

		for(int i = 0; i < size; i++) {
			bytes[i] = 0;
		}

		return bytes;
	}

	@Override
	public int getLength() {
		// TODO Auto-generated method stub
		return 0;
	}

}
