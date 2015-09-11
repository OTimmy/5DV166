package model.network.pdu;

public abstract class PDU {

    public final int pduRowSize = 4;
	public abstract byte[] toByteArray(int size);
	public abstract int getSize();

}
