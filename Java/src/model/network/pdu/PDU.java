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
        	inStream.read(bytes,0,bytes.length);
        	OpCode op = OpCode.getOpCodeBy(bytes[0]);

        	switch(op) {
        	case MESSAGE:
        	    MessagePDU msgPDU = new MessagePDU(bytes);

        	    if(msgPDU.isValid()) {
        	        return msgPDU;
        	    }

        	    break;
        	case NICKS: 

        	    NicksPDU nicksPDU = new NicksPDU(bytes);

        	    if(nicksPDU.isValid()) {
        	        return nicksPDU;
        	    }

        	    break;
        	case SLIST:
        	    SListPDU sListPDU = new SListPDU(bytes);

        	    if(sListPDU.isValid()) {
        	        return sListPDU;
        	    }

        	    break;
        	case UCNICK: System.out.println("ucnick");
        	    return new UCNickPDU(bytes);

        	case UJOIN: return new UJoinPDU(bytes);


        	case ULEAVE: System.out.println("u leave");
        		break;
        	default:
        		break;
        	}
        }

        return null;
    }

    /**
     *
     * @return 0 for correct padding or numb
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
