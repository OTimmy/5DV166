package model.network.pdu.types;

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

    public UJoinPDU(byte[] bytes) {
        parser(bytes);

    }

    private void parser(byte[] bytes) {
        for(int i = 0; i < 40; i++) {
            System.out.println(bytes[i]);
        }
        //Time stamp
        byte[] timeBytes = new byte[TIME_STAMP_LENGTH];
        int end = TIME_STAMP_START + TIME_STAMP_LENGTH;
//        for(int i = 0,j = TIME_STAMP_START; j <  end; j++) {
//            timeBytes[i] = bytes[j];
//
//        }
        int seconds = ((bytes[4]& 0xff) << 8  ) | ((bytes[5] & 0xff) << 8)
                       | ((bytes[6] & 0xff) << 8) | ((bytes[7] & 0xff) << 8);

        date = DateUtils.toDate(seconds);


        //Nick
        int nickLength = (bytes[NICK_LENGTH_BYTE] & 0xff);

        byte[] nickBytes = new byte[nickLength];
        end = nickLength + NICK_START;

        for(int i = 0, j = NICK_START; j < end; i++,j++) {
            nickBytes[i]= (byte) (bytes[j]& 0xff) ;
        }
        nick = new String(nickBytes, StandardCharsets.UTF_8);



    }

    public String getNick() {
        return nick;
    }

    public Date getDate() {
        return date;
    }

    public boolean isValid() {
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

}
