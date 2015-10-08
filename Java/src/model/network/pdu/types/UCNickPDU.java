package model.network.pdu.types;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import java.util.Date;

import model.network.pdu.OpCode;
import model.network.pdu.PDU;

public class UCNickPDU extends PDU{

    private final int NICK_LENGTH1       = 1;
    private final int NICK_LENGTH2       = 2;
    private final int NICK_START         = 8;
    private final int ROW_SIZE           = 4;


    private String oldNick;
    private String newNick;
    private Date date;

    private boolean validFlag;

    public UCNickPDU(InputStream inStream) throws IOException {
        parse(inStream);
    }

    private void parse(InputStream inStream ) throws IOException {

        int nickLength1 = inStream.read();
        int nickLength2 = inStream.read();
        int pad = inStream.read();

        //Reading time stamp
        byte[] timeBytes = new byte[ROW_SIZE];
        inStream.read(timeBytes, 0, timeBytes.length);

        long seconds = (((timeBytes[3] & 0xff) << 24) | ((timeBytes[2] & 0xff) << 16)
                       |((timeBytes[1] & 0xff) << 8) | (timeBytes[0] & 0xff));

        date = new Date(seconds);

        //Reading old nick
        byte[] oldNickBytes = new byte[nickLength1];
        inStream.read(oldNickBytes, 0, oldNickBytes.length);

        //Read padding
        byte[] padBytes = new byte[padLengths(nickLength1)];
        inStream.read(padBytes, 0, padBytes.length);

        //Reading new nick
        byte[] newNickBytes = new byte[nickLength2];
        inStream.read(newNickBytes, 0, newNickBytes.length);

        //Read padding
        padBytes = new byte[padLengths(nickLength2)];
        inStream.read(padBytes, 0, padBytes.length);

        oldNick = new String(oldNickBytes,StandardCharsets.UTF_8);

        newNick = new String(newNickBytes,StandardCharsets.UTF_8);

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

    public boolean checkPadding(byte[] bytes, int nickLength1, int nickLength2)  {

        if(bytes[3] != 0) {
            return false;
        }

        int endOfNick1 = NICK_START + nickLength1;
        int paddedNick1 = endOfNick1 + padLengths(nickLength1);

        if(bytes.length < paddedNick1) {
            return false;
        }

        //pad for old nick
        for(int i = endOfNick1; i < paddedNick1; i++) {
            if(bytes[i] != 0) {
                return false;
            }
        }

        int endOfNick2 = paddedNick1 + nickLength2;
        int paddedNick2 = endOfNick2 + padLengths(nickLength2);

        if(bytes.length <  paddedNick2) {
            return false;
        }

        for(int i = endOfNick2; i < paddedNick2; i++) {
            if(bytes[i] != 0) {
                return false;
            }
        }

        return true;
    }

    public boolean isValid() {
        return validFlag;
    }
}