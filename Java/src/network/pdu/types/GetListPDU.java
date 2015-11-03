package network.pdu.types;


import network.pdu.OpCode;
import network.pdu.PDU;

public class GetListPDU extends PDU{
    private final int  size = 4;

	public byte[] toByteArray() {
	    byte[] bytes = new byte[size];
		bytes[0] = OpCode.GETLIST.value; //getList code

		for(int i = 1; i < size; i++) {
			bytes[i] = 0;
		}

		return bytes;
	}


	public int getSize() {
		return size;
	}

	@Override
	public byte getOpCode() {
		return OpCode.GETLIST.value;
	}

	@Override
	public String getError() {
		// TODO Auto-generated method stub
		return null;
	}
}
