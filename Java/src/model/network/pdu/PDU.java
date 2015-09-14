package model.network.pdu;

public abstract class PDU {

    public final int udpPDUSize = 1500;
    public final int pduRowSize = 4;
	public abstract byte[] toByteArray();
	public abstract int getSize();

}
