package model.network.pdu.types;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import model.network.pdu.OpCode;
import model.network.pdu.PDU;

public class UJoinPDU extends PDU{
    private final int NICK_LENGTH_BYTE = 1;
    private final int NICK_START = 8;


    private Date date;
    private String nick;
    private byte[] bytes;

    public UJoinPDU(byte[] bytes) {
        this.bytes = bytes;
        parser(bytes);

    }

    private void parser(byte[] bytes) {
        int nickLength = (bytes[NICK_LENGTH_BYTE] & 0xff);

        byte[] nickBytes = new byte[nickLength];
        int end = nickLength + NICK_START;

        for(int i = 0, j = NICK_START; j < end; i++,j++) {
            nickBytes[i]= (byte) (bytes[j]& 0xff) ;
        }
        nick = new String(nickBytes, StandardCharsets.UTF_8);

    }

    public String getNick() {
        return nick;
    }

    public Date getDate() {
        return null;
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
