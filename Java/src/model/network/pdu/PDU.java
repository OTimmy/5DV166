package model.network.pdu;

public abstract class PDU {

	public abstract byte[] toByteArray(int size);
	public abstract int getLength();

}
