package model.network.pdu.types;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import model.network.pdu.OpCode;
import model.network.pdu.PDU;

public class NicksPDU extends PDU{
    //TODO FIX VALIDATION!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private final int NR_NICKS          = 1;
    private final int FIRST_NICK        = 4;
    private final int FIRST_BYTE_LENGTH = 2;
    private final int SEC_BYTE_LENGTH   = 3;

    private ArrayList<String> nicks;
    private boolean validFlag;

    public NicksPDU(byte[] bytes) {

        nicks = parse(bytes);
        validFlag = true;
    }

    private ArrayList<String> parse(byte[] bytes) {
        ArrayList<String> nicks = new ArrayList<String>();
        int nrOfNicks = (byte) bytes[NR_NICKS];
        int totalLength = (byte) (((bytes[FIRST_BYTE_LENGTH] & 0xff) << 8)
                            | (bytes[SEC_BYTE_LENGTH] & 0xff));
        int start = FIRST_NICK;

        for(int i = 0; i < nrOfNicks; i++) {
            String nick = "";

            int length = 0;

            for(int j = start; bytes[j] != 0 && j < bytes.length; j++,length++);

            nick = new String(bytes,start,length,StandardCharsets.UTF_8);

            start += length + padLengths(length);

            nicks.add(nick);
        }

        if(nicks.size() != nrOfNicks || checkPadding(bytes,FIRST_NICK
                + totalLength, totalLength)) {
            validFlag = false;
        }

        return nicks;
    }

    @Override
    public byte[] toByteArray() {
        return null;
    }

    @Override
    public int getSize() {
        return PDU.pduSize();
    }

    @Override
    public byte getOpCode() {
        return OpCode.NICKS.value;
    }

    public ArrayList<String> getNicks() {
        return nicks;
    }

    /**
     * @param start of padding, and length of nick.
     */
    private boolean checkPadding(byte[] bytes, int start, int length) {

        int padded = padLengths(length) + start;

        for(int i = start; i < padded && i < bytes.length; i++) {
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
