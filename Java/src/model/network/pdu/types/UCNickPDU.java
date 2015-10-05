package model.network.pdu.types;


import java.nio.charset.StandardCharsets;
import java.util.Date;

import model.network.pdu.DateUtils;
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

        //Time stamp
        int seconds = (bytes[4] & 0xff) << 8 | (bytes[5] & 0xff) << 8
                      | (bytes[6] & 0xff) <<8 | (bytes[7] & 0xff) << 8;

        date = DateUtils.toDate(seconds);

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