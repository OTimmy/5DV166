package model.network.pdu;

import java.io.IOException;
import java.io.InputStream;
import model.network.pdu.types.SListPDU;


public abstract class PDU {

    private final static int pduBuffSize = 65539;
    public static PDU fromInputStream(InputStream inStream) throws IOException {
        byte[] bytes = new byte[pduBuffSize];
        
        if(inStream != null) {
        	inStream.read(bytes,0,bytes.length);
        	OpCode op = OpCode.getOpCodeBy(bytes[0]);
        	
        	switch(op) {
        	case ACK:
        		break;
        	case ALIVE:
        		break;
        	case CHNICK:
        		break;
        	case JOIN:
        		break;
        	case MESSAGE:
        		break;
        	case NICKS: System.out.println("Name:"+(char) bytes[4]);
        		break;
        	case NOTREG:
        		break;
        	case QUIT:
        		break;
        	case REG:
        		break;
        	case SLIST: return new SListPDU(bytes);
        	case UCNICK:
        		break;
        	case UJOIN:
        		break;
        	case ULEAVE:
        		break;
        	default:
        		break;
        	}
        }

        return null;
    }

    public static int pduSize() {
        return pduBuffSize;
    }

    public abstract byte[] toByteArray();

    public abstract int getSize();

}
