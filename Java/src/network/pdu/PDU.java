package network.pdu;

import java.io.IOException;
import java.io.InputStream;

import network.pdu.types.*;

public abstract class PDU {

	public final String ERROR_PADDING_PDU  = "Incorrect padding of PDU";
	public final String ERROR_PADDING_NICK = "Incorrect padding of nick";
	public final String ERROR_PADDING_MSG  = "Incorrect padding of message";
	public final String ERROR_PADDING_SERVER_NAME = "Incorrect padding of name";
	
    public static PDU fromInputStream(InputStream inStream) throws IOException {

        byte opByte = (byte) inStream.read();
        OpCode op = OpCode.getOpCodeBy(opByte);

        if(opByte != -1 && op != null) {

            switch(op) {

                case MESSAGE: return new MessagePDU(inStream);

                case NICKS:   return new NicksPDU(inStream);

                case SLIST:   return new SListPDU(inStream);

                case UCNICK:  return new UCNickPDU(inStream);

                case UJOIN:   return new UJoinPDU(inStream);

                case ULEAVE:  return new ULeavePDU(inStream);

                case QUIT:    return new QuitPDU();

            }
        }

        return null;
    }

    /**
     * @param the length of the string that should be padded
     * @return return the correct number of padding.
     */
    public static int padLengths(int length) {
        return (4 - length % 4) % 4;
    }

    public boolean isPaddedBytes(byte[] bytes) {
        for(byte b:bytes) {
            if(b != 0) {
                return false;
            }
        }
        return true;
    }


    public abstract byte[] toByteArray();

    public abstract int getSize();

    public abstract byte getOpCode();

    public abstract String getError();


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
