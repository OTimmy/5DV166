package model.network.pdu.types;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import model.network.pdu.DateUtils;
import model.network.pdu.OpCode;
import model.network.pdu.PDU;

public class UJoinPDU extends PDU{
    private final int NICK_LENGTH_BYTE     = 1;
    private final int NICK_START           = 8;
    private final int TIME_STAMP_START     = 4;
    private final int TIME_STAMP_LENGTH    = 4;

    private Date date;
    private String nick;
    private boolean valid;

    public UJoinPDU(InputStream inStream) {
        parser(inStream);
    }

    private void parser(InputStream inStream) {

        int nickLength = (bytes[NICK_LENGTH_BYTE] & 0xff);

        valid = checkPadding(bytes,nickLength);


        if(valid) {

            //Time stamp
            int end = TIME_STAMP_START + TIME_STAMP_LENGTH;

            long seconds = ((bytes[4]& 0xff) << 24  ) | ((bytes[5] & 0xff) << 16)
                           | ((bytes[6] & 0xff) << 8) | ((bytes[7] & 0xff));

            date = new Date(seconds * 1000);

            //Nick
            byte[] nickBytes = new byte[nickLength];
            end = nickLength + NICK_START;

            for(int i = 0, j = NICK_START; j < end; i++,j++) {
                nickBytes[i]= (byte) (bytes[j]& 0xff) ;
            }
            nick = new String(nickBytes, StandardCharsets.UTF_8);
        }

    }

    public String getNick() {
        return nick;
    }

    public Date getDate() {
        return date;
    }

    public boolean checkPadding(byte[] bytes, int nickLength) {

        int endOfNick = NICK_START + nickLength;
        int padded = endOfNick + padLengths(nickLength);

        for(int i = endOfNick; i < padded; i++) {
            if(bytes[i] != 0) {
                return false;
            }
        }

        return true;
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
        return valid;
    }
}
