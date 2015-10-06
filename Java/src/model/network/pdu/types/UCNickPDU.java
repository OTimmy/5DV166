package model.network.pdu.types;


import java.nio.charset.StandardCharsets;

import java.util.Date;

import model.network.pdu.OpCode;
import model.network.pdu.PDU;

public class UCNickPDU extends PDU{

    private final int NICK_LENGTH1       = 1;
    private final int NICK_LENGTH2       = 2;
    private final int NICK_START         = 8;


    private String oldNick;
    private String newNick;
    private Date date;

    private boolean validFlag;

    public UCNickPDU(byte[] bytes) {

        parse(bytes);
    }

    private void parse(byte[] bytes ) {

        int nickLength1 = (bytes[NICK_LENGTH1] & 0xff);
        int nickLength2 = (bytes[NICK_LENGTH2] & 0xff);

        validFlag = checkPadding(bytes, nickLength1, nickLength2);

        if(validFlag) {

            //Time stamp
            long seconds = (bytes[4] & 0xff) << 24 | (bytes[5] & 0xff) << 16
                          | (bytes[6] & 0xff) <<8 | (bytes[7] & 0xff);

            date = new Date(seconds * 1000);

            //Old nick
            int length = (bytes[NICK_LENGTH1] & 0xff);
            int start  = NICK_START;
            int end = length + start;

            byte[] nickBytes = new byte[length];
            for(int i = 0, j = start; j < end; j++,i++, start++) {
                nickBytes[i] = bytes[j];
            }

            oldNick = new String(nickBytes,StandardCharsets.UTF_8);

            start += padLengths(length);

            //New nick
            length = (bytes[NICK_LENGTH2] & 0xff);
            end = length + start;

            nickBytes = new byte[length];
            for(int i= 0,j= start; j < end; i++, j++, start++) {
                nickBytes[i] = bytes[j];
            }

            newNick = new String(nickBytes,StandardCharsets.UTF_8);

        }

    }


   // public

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
        int padded = endOfNick1 + padLengths(nickLength1);

        //pad for old nick
        for(int i = endOfNick1; i < padded; i++) {
            if(bytes[i] != 0) {
                return false;
            }
        }

        int endOfNick2 = endOfNick1 + nickLength2;
        padded = endOfNick2 + padLengths(nickLength2);

        for(int i = endOfNick2; i < padded; i++) {
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