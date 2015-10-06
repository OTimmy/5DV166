package model.network.pdu;

import java.io.IOException;
import java.io.InputStream;

import model.network.pdu.types.*;
//TODO make a static method in pdu types, that check padding is correct, else return null
//TODO byte buffer
public abstract class PDU {

    private final static int pduBuffSize = 65539;
    public static PDU fromInputStream(InputStream inStream) throws IOException {
        byte[] bytes = new byte[pduBuffSize];

        if(inStream != null) {
           // inStream.read
        	inStream.read(bytes,0,bytes.length);
        	OpCode op = OpCode.getOpCodeBy(bytes[0]);
//        	System.out.println("PDU value: " +op.value);
        	switch(op) {

        	case MESSAGE:
        	    MessagePDU msgPDU = new MessagePDU(bytes);

        	    if(msgPDU.isValid()) {
        	        return msgPDU;
        	    }
        	    System.out.println("Invalid message pdu");
        	    break;
        	case NICKS:

        	    NicksPDU nicksPDU = new NicksPDU(bytes);

        	    if(nicksPDU.isValid()) {
        	        return nicksPDU;
        	    }
        	    System.out.println("Invalid nicks pdu");
        	    break;
        	case SLIST:
        	    SListPDU sListPDU = new SListPDU(bytes);

        	    if(sListPDU.isValid()) {
        	        return sListPDU;
        	    }

        	    break;
        	case UCNICK: return new UCNickPDU(bytes);

        	case UJOIN:  return new UJoinPDU(bytes);

        	case ULEAVE: return new ULeavePDU(bytes);

        	case QUIT:   return new QuitPDU();

        	default:
        		break;
        	}
        }

        return null;
    }

    /**
     *
     * @return return the correct number of padding.
     */
    public static int padLengths(int length) {
        return (4 - length % 4) % 4;
    }

    public static int pduSize() {
        return pduBuffSize;
    }

    public abstract byte[] toByteArray();

    public abstract int getSize();

    public abstract byte getOpCode();

}
