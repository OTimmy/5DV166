package network.pdu.types;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.Date;

import network.pdu.DateUtils;
import network.pdu.OpCode;
import network.pdu.PDU;

/**
 *
 */
public class UCNickPDU extends PDU{

    private final int ROW_SIZE           = 4;


    private String oldNick;
    private String newNick;
    private Date date;

    private String error;
    public UCNickPDU(InputStream inStream) throws IOException {
        error = parse(inStream);
    }

    private String parse(InputStream inStream ) throws IOException {

        int nickLength1 = inStream.read();
        int nickLength2 = inStream.read();
  
        //int pad = inStream.read();
        byte[] padBytes = readExactly(1, inStream);
        
        if(!isPaddedBytes(padBytes)) {
        	return "Incorrect padding";
        }
        
        //Reading time stamp
//        byte[] timeBytes = new byte[ROW_SIZE];
        byte[] timeBytes = readExactly(ROW_SIZE, inStream);
//        inStream.read(timeBytes, 0, timeBytes.length);

        date = DateUtils.getDateByBytes(timeBytes);;

        //Reading old nick
        byte[] oldNickBytes = readExactly(nickLength1, inStream);

        //Read padding
        padBytes     = readExactly(padLengths(nickLength1), inStream);

        if(!isPaddedBytes(padBytes)) {
        	return "Incorrect nick1 padding";
        }
        
        //Reading new nick
        byte[] newNickBytes = readExactly(nickLength2, inStream);

        //Read padding
        padBytes            = readExactly(padLengths(nickLength2), inStream);
        
        if(!isPaddedBytes(padBytes)) {
        	return "Incorrect nick2 padding";
        }
        
        oldNick = new String(oldNickBytes,StandardCharsets.UTF_8);

        newNick = new String(newNickBytes,StandardCharsets.UTF_8);

        return null;
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
        return OpCode.UCNICK.value;
    }

    public String getOldNick() {
        return oldNick;
    }

    public String getNewNick() {
        return newNick;
    }

    public Date getDate(){
        return date;
    }

	@Override
	public String getError() {
		return error;
	}
}