package network.pdu;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import network.pdu.types.*;

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
            OpCode op = OpCode.getOpCodeBy(opByte);
            System.out.println("!!!!!!!opByte: "+opByte);
            if(opByte != -1 && op != null) {    // OpCode is not working!!


                switch(op) {

                case MESSAGE:

                    MessagePDU msgPDU = new MessagePDU(inStream);
                    System.out.println("Got message");
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
                    System.out.println("Unknown pdu: "+op.value);
                    break;
                }
        	} else {
        	    System.out.println("bytes remaining: " + inStream.available());
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

    public Date getDateByBytes(byte[] bytes) {
    	long seconds = (bytes[0] & 0xff) << 24 | (bytes[1] & 0xff) << 16;
    	seconds |= (bytes[2] &0xff) << 8;
    	seconds |= (bytes[3] & 0xff);
    	return new Date(seconds * 1000);
    }


    public abstract byte[] toByteArray();

    public abstract int getSize();

    public abstract byte getOpCode();

    /**
     * Reads exactly the specified amount of bytes from the stream, blocking
     * until they are available even though some bytes are.
     *
     * @param is The InputStream to read from.
     * @param len The number of bytes to read.
     */
    public byte[] readExactly(int length,InputStream inStream) throws IOException {

        byte[] buffer = new byte[length];

        int readCount = 0;
        while (readCount < length) {
            int readBytes = inStream.read(buffer, readCount, length - readCount);
            readCount += readBytes;
        }

        return buffer;

    }
}
