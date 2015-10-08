package model.network.pdu.types;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import model.network.pdu.DateUtils;
import model.network.pdu.OpCode;
import model.network.pdu.PDU;

public class UJoinPDU extends PDU{


    private Date date;
    private String nick;
    private boolean valid;

    public UJoinPDU(InputStream inStream) throws IOException {
        parser(inStream);
    }

    private void parser(InputStream inStream) throws IOException {


        int nickLength = inStream.read();

        //reading the pad
        byte[] tempBytes = new byte[2];
        inStream.read(tempBytes, 0, tempBytes.length);
        //check padding


        //Reading time stamp
        tempBytes = new byte[4];
        inStream.read(tempBytes, 0, tempBytes.length);

        long seconds = (tempBytes[0] & 0xff) << 24 | (tempBytes[1] & 0xff) << 16
                       | (tempBytes[2] & 0xff) << 8 | (tempBytes[3] & 0xff);

        date = new Date(seconds * 1000);

        //Reading the nick
        byte[] nickBytes = new byte[nickLength];
        inStream.read(nickBytes, 0, nickBytes.length);

        //Read padding
        tempBytes = new byte[padLengths(nickLength)];
        inStream.read(tempBytes, 0, tempBytes.length);
        //Check padding

        nick = new String(nickBytes, StandardCharsets.UTF_8);

    }

    public String getNick() {
        return nick;
    }

    public Date getDate() {
        return date;
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
