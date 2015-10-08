package network.pdu.types;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import network.pdu.OpCode;
import network.pdu.PDU;


/**
 * Reads from given inputstream, and parse it accordingly to the protocol
 * If padding is incorrect, the parsing will terminate, when discovered. And
 * validFlag will be set to false. However if no error can be discovered,
 * the parsed value can be found in the attribute nick and date.
 *
 * @author c12ton
 * @version 0.0
 */
public class UJoinPDU extends PDU{
    private final int TIME_SIZE = 4;
    private final int PAD_SIZE  = 2;   //Known padded size

    private Date date;
    private String nick;
    private boolean validFlag;


    public UJoinPDU(InputStream inStream) throws IOException {
        validFlag = parser(inStream);
    }

    /**
     * @param Reads from inputstream by the given standard.
     * @return a flag.
     */
    private boolean parser(InputStream inStream) throws IOException {

        int nickLength = inStream.read();

        //reading the pad
        byte[] padBytes = new byte[PAD_SIZE];
        inStream.read(padBytes, 0, padBytes.length);

        if(!isPaddedBytes(padBytes)) {
            return false;
        }

        //Reading time stamp
        byte[] timeBytes = new byte[TIME_SIZE];
        inStream.read(timeBytes, 0, timeBytes.length);

        date = getDateByBytes(timeBytes);

        //Reading the nick
        byte[] nickBytes = new byte[nickLength];
        inStream.read(nickBytes, 0, nickBytes.length);

        //Read padding
        padBytes = new byte[padLengths(nickLength)];
        inStream.read(padBytes, 0, padBytes.length);

        if(!isPaddedBytes(padBytes)) {
            return false;
        }


        nick = new String(nickBytes, StandardCharsets.UTF_8);

        return true;

    }

    public String getNick() {
        return nick;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public byte[] toByteArray() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getSize() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public byte getOpCode() {
        return OpCode.UJOIN.value;
    }

    public boolean isValid() {
        return validFlag;
    }
}
