package network.pdu.types;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import network.pdu.DateUtils;
import network.pdu.PDU;
import network.pdu.OpCode;


/**
 * Reads from given inputstream, and parse it accordingly to the protocol
 * If padding is incorrect, the parsing will terminate, when discovered. And
 * validFlag will be set to false. However if no error can be discovered,
 * the parsed value can be found in the attribute nick and date.
 *
 * @author c12ton
 * @version 0.0
 */
public class ULeavePDU extends PDU{
    private final int TIME_LENGTH = 4;
    private final int PAD_LENGTH = 2;   // Known padded size

	private String nick;
	private Date date;
	private boolean validFlag;


	public ULeavePDU(InputStream inStream) throws IOException {
	    validFlag = parse(inStream);
	}

	/**
	 * @param Reads from inputstream by the given standard of the pdu.
	 * @return a flag.
	 */
	private boolean parse(InputStream inStream) throws IOException {

	    int nickLength = inStream.read();

	    //reading pad
	    byte[] padBytes = readExactly(PAD_LENGTH, inStream);

	    if(!isPaddedBytes(padBytes)) {
	        return false;
	    }

	    //Reading time stamp
	    byte[] timeBytes = readExactly(TIME_LENGTH, inStream);

	    date = DateUtils.getDateByBytes(timeBytes); 

	    //Reading nick
	    byte[] nickBytes = readExactly(nickLength, inStream);

	    //Reading pad of nick
	    padBytes = readExactly(padLengths(nickLength), inStream);

	    if(!isPaddedBytes(padBytes)) {
	        return false;
	    }

	    nick = new String(nickBytes, StandardCharsets.UTF_8);

	    return true;
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
		return OpCode.ULEAVE.value;
	}

	public String getNick() {
		return nick;
	}

	public Date getDate() {
	    return date;
	}

	public boolean isValid() {
	    return validFlag;
	}
}
