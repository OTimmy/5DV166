package model.network.pdu;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import model.network.pdu.types.*;

public abstract class PDU {


    public static PDU fromInputStream(InputStream inStream) throws IOException {

        //read first byte
        //then ask how many are alaible, then run trought that loop
//
//
//        inStream.read(byteList);
//
//        inStream.

       // if(inStream != null) {

            byte opByte = (byte) inStream.read();
        	if(opByte != -1) {    //if its disconnected

                OpCode op = OpCode.getOpCodeBy(opByte);
                switch(op) {

                case MESSAGE:

                    MessagePDU msgPDU = new MessagePDU(inStream);
                    System.out.println(msgPDU.getMsg());
                    return msgPDU;
////                    MessagePDU msgPDU = new MessagePDU(bytes);
//
//                    if(msgPDU.isValid()) {
//                        return msgPDU;
//                    }
//                    System.out.println("Invalid msg");
//                    break;
                case NICKS:

                    NicksPDU nicksPDU = new NicksPDU(inStream);
                    return nicksPDU;
                    //System.out.println("getting list");
//                    if(nicksPDU.isValid()) {
//                        return nicksPDU;
//                    }
                    //System.out.println("Invalid nicks");
                  //  break;
                case SLIST:
                    SListPDU sListPDU = new SListPDU(inStream);

                   // if(sListPDU.isValid()) {
                        return sListPDU;
                    //}
               //     System.out.println("Invalid slist");
                 //   break;
                case UCNICK:
                    UCNickPDU uCNickPDU = new UCNickPDU(inStream);
                    return uCNickPDU;
//
//                    if(uCNickPDU.isValid()) {
//                        return new UCNickPDU(bytes);
//
//                    }
//                    System.out.println("Invalid ucnick");
                    //break;
                case UJOIN:
                    UJoinPDU uJoinPDU = new UJoinPDU(inStream);
                    return uJoinPDU;
//
//                    if(uJoinPDU.isValid()) {
//                        return new UJoinPDU(bytes);
//                    }
//                    System.out.println("Invalid ujoin");
//                    break;
                case ULEAVE:


                    ULeavePDU uLeavePDU = new ULeavePDU(inStream);
                    return uLeavePDU;
//                    if(uLeavePDU.isValid()) {
//                        return new ULeavePDU(bytes);
//                    }
//                    System.out.println("Invalid uleave");
//                    break;
                case QUIT:   return new QuitPDU();

                default:
                    break;
                }
        	} else {
        	    System.out.println("fuuuk");
        	}
      //  }
        System.out.println("Stream null");

        return null;
    }

    /**
     *
     * @return return the correct number of padding.
     */
    public static int padLengths(int length) {
        return (4 - length % 4) % 4;
    }



    public abstract byte[] toByteArray();

    public abstract int getSize();

    public abstract byte getOpCode();
}
