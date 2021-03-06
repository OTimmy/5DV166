package network.pdu.types;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import network.pdu.DateUtils;
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

    private String error;
    private Date date;
    private String nick;

    public UJoinPDU(InputStream inStream) throws IOException {
        error = parser(inStream);
    }

    /**
     * @param Reads from inputstream by the given standard.
     * @return a flag.
     */
    private String parser(InputStream inStream) throws IOException {

        int nickLength = readExactly(1, inStream)[0] & 0xff;

        //reading the pad
        byte[] padBytes = readExactly(PAD_SIZE, inStream);
        
        if(!isPaddedBytes(padBytes)) {
            return ERROR_PADDING_PDU;
        }

        //Reading time stamp
        byte[] timeBytes = readExactly(TIME_SIZE, inStream); 

        date = DateUtils.getDateByBytes(timeBytes);

        //Reading the nick
        byte[] nickBytes = readExactly(nickLength, inStream); 

        //Read padding
        padBytes = readExactly(padLengths(nickLength), inStream);
        
        if(!isPaddedBytes(padBytes)) {
            return ERROR_PADDING_NICK;
        }

        nick = new String(nickBytes, StandardCharsets.UTF_8);

        return null;

    }

    public String getNick() {
        return nick;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public byte[] toByteArray() {
        return null;
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public byte getOpCode() {
        return OpCode.UJOIN.value;
    }

    @Override
    public String getError() {
        return error;
    }
}
