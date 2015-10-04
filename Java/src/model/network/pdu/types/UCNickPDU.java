package model.network.pdu.types;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import model.network.pdu.OpCode;
import model.network.pdu.PDU;

public class UCNickPDU extends PDU{

    private final int NICK_LENGTH1       = 1;
    private final int NICK_LENGTH2       = 2;
    private final int TIME_STAMP_START   = 4;
    private final int NICK_START         = 8;
    private final int TIME_STAMP_LENGTH  = 4;

    private String oldNick;
    private String newNick;
    private Date date;

    public UCNickPDU(byte[] bytes) {

        parse(bytes);
    }

    private void parse(byte[] bytes ) {

        //time stamp
        int timeStart = TIME_STAMP_START;
        byte[] timeBytes = new byte[TIME_STAMP_LENGTH];

        for(int i = 0,j = timeStart; j < (timeStart + TIME_STAMP_LENGTH); i++,j++ ) {
            timeBytes[i] = bytes[j];
        }

        SimpleDateFormat format  = new SimpleDateFormat("yyy.MM.dd");
        try {
            date = format.parse(timeBytes.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }



        //Old nick
        int length = (bytes[NICK_LENGTH1] & 0xff);
        int start  = (bytes[NICK_START] & 0xff);
        int end = length + start;

        String nick = "";
        for(int i = start; i < end; i++, start++) {
            nick += (char) (bytes[i] & 0xff);
        }

        start += padLengths(length);

        oldNick = nick;

        //New nick
        length = (bytes[NICK_LENGTH2] & 0xff);
        end = (bytes[NICK_LENGTH2] & 0xff) + start;
        nick = "";
        for(int i = start; i < end; i++, start++) {
            nick += (char) (bytes[i] & 0xff);
        }

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
        // TODO Auto-generated method stub
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

}