package model.network.pdu;

import java.io.IOException;
import java.io.InputStream;
import model.network.pdu.types.SListPDU;


public abstract class PDU {

    public static PDU fromInputStream(InputStream inStream) throws IOException {

    	int length = inStream.available();
        byte[] bytes = new byte[length];

        inStream.read(bytes,0,length);

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
		case NICKS:
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

        return null;
    }


    public abstract byte[] toByteArray();

    public abstract int getSize();


}
