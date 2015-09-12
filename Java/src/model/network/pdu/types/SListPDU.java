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
