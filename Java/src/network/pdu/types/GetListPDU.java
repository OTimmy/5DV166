package network.pdu.types;

/**
 * @author c12ton
 */
public class GetListPDU {

	
	public byte[] toByteArrat() {
		byte[] pdu = new byte[4];

		pdu[0] = 3;
		pdu[1] = 0;
		pdu[2] = 0;
		pdu[3] = 0;

		return pdu;
	}

}
