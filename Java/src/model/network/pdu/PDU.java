package model.network.pdu;

import java.io.IOException;
import java.io.InputStream;

import model.network.pdu.types.*;

public abstract class PDU {

    private final static int pduBuffSize = 65539;
    public static PDU fromInputStream(InputStream inStream) throws IOException {
        byte[] bytes = new byte[pduBuffSize];

        if(inStream != null) {
        	inStream.read(bytes,0,bytes.length);
        	if(bytes != null) {    //if its disconnected

                OpCode op = OpCode.getOpCodeBy(bytes[0]);
                switch(op) {

                case MESSAGE:
                    MessagePDU msgPDU = new MessagePDU(bytes);

                    if(msgPDU.isValid()) {
                        return msgPDU;
                    }
                    System.out.println("Invalid msg");
                    break;
                case NICKS:

                    NicksPDU nicksPDU = new NicksPDU(bytes);

                    if(nicksPDU.isValid()) {
                        return nicksPDU;
                    }
                    System.out.println("Invalid nicks");
                    break;
                case SLIST:
                    SListPDU sListPDU = new SListPDU(bytes);

                    if(sListPDU.isValid()) {
                        return sListPDU;
                    }
                    System.out.println("Invalid slist");
                    break;
                case UCNICK:
                    UCNickPDU uCNickPDU = new UCNickPDU(bytes);

                    if(uCNickPDU.isValid()) {
                        return new UCNickPDU(bytes);

                    }
                    System.out.println("Invalid ucnick");
                    break;
                case UJOIN:
                    UJoinPDU uJoinPDU = new UJoinPDU(bytes);

                    if(uJoinPDU.isValid()) {
                        return new UJoinPDU(bytes);
                    }
                    System.out.println("Invalid ujoin");
                    break;
                case ULEAVE:
                    ULeavePDU uLeavePDU = new ULeavePDU(bytes);

                    if(uLeavePDU.isValid()) {
                        return new ULeavePDU(bytes);
                    }
                    System.out.println("Invalid uleave");
                    break;
                case QUIT:   return new QuitPDU();

                default:
                    break;
                }

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
