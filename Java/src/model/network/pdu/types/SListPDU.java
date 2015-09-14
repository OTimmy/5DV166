package model.network.pdu.types;

import model.network.pdu.*;

public class SListPDU extends PDU{
	private final int pduSize = 1500;

	@Override
	public byte[] toByteArray() {
		byte[] bytes = new byte[pduSize];

		return bytes;
	}

	@Override
	public int getSize() {
		return pduSize;
	}

	public int getSequenceNr(byte[] bytes) {
	    return bytes[1];
	}

	public int getNrOfServers(byte[] bytes) {
		return bytes[2];
	}

	public String getAddress() {

		return null;
	}

	public int getPort() {
		return 0;
	}

	public int nrOfClients() {
		return 0;
	}

	public String getServerName() {
		return null;
	}


}
