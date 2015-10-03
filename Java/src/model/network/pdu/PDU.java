package model.network.pdu;

import java.io.IOException;
import java.io.InputStream;

import model.network.pdu.types.*;

//TODO byte buffer
public abstract class PDU {

    private final static int pduBuffSize = 65539;
    public static PDU fromInputStream(InputStream inStream) throws IOException {
        byte[] bytes = new byte[pduBuffSize];

        if(inStream != null) {
        	inStream.read(bytes,0,bytes.length);
        	OpCode op = OpCode.getOpCodeBy(bytes[0]);

        	switch(op) {
        	case ACK: System.out.println("Ack");
        		break;
        	case ALIVE: System.out.println("Alive");
        		break;
        	case CHNICK: System.out.println("Change nick");
        		break;
        	case MESSAGE: return new MessagePDU(bytes);
        	case NICKS:   return new NicksPDU(bytes);
        	case NOTREG: System.out.println("Not reg");
        		break;
        	case QUIT: System.out.println("quit");
        		break;
        	case SLIST:  return new SListPDU(bytes);
        	case UCNICK: System.out.println("ucnick");
        		break;
        	case UJOIN: System.out.println("UJoin");
        		break;
        	case ULEAVE: System.out.println("u leave");
        		break;
        	default:
        		break;
        	}
        }

        return null;
    }

//    /**
//     * @param bytes from inputstream, index start of String
//     * @return false if paddings is incorrect else true
//     */
//    public boolean checkPadding(byte[] bytes, int index,int length) {
//
//        int indexPad = padLengths(length) + index;
//        for(;indexPad < bytes.length;indexPad++) {
//            if(bytes[indexPad] != 0) {
//                return false;
//            }
//        }
//
//        return true;
//    }


    public abstract boolean checkPadding(byte[] bytes);
    /**
     *
     * @return 0 for correct padding or numb
     */
    public static int padLengths(int length) {
        return length +  (4 - length % 4) % 4;
    }

    public static int pduSize() {
        return pduBuffSize;
    }

    public abstract byte[] toByteArray();

    public abstract int getSize();

    public abstract byte getOpCode();
}
