package network.pdu.types;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

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

    private boolean validFlag;

    public UCNickPDU(InputStream inStream) throws IOException {
        validFlag = parse(inStream);
    }

    private boolean parse(InputStream inStream ) throws IOException {

        int nickLength1 = inStream.read();
        int nickLength2 = inStream.read();
        int pad = inStream.read();

        //Reading time stamp
        byte[] timeBytes = new byte[ROW_SIZE];
        inStream.read(timeBytes, 0, timeBytes.length);

        date = DateUtils.getDateByBytes(timeBytes);;

        //Reading old nick
        byte[] oldNickBytes = readExactly(nickLength1, inStream);

        //Read padding
        byte[] padBytes     = readExactly(padLengths(nickLength1), inStream);

        //Reading new nick
        byte[] newNickBytes = readExactly(nickLength2, inStream);

        //Read padding
        padBytes            = readExactly(padLengths(nickLength2), inStream);

        oldNick = new String(oldNickBytes,StandardCharsets.UTF_8);

        newNick = new String(newNickBytes,StandardCharsets.UTF_8);

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

    public boolean isValid() {
        return validFlag;
    }
}