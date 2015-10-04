package model.network.pdu.types;

import java.util.ArrayList;

import model.network.pdu.OpCode;
import model.network.pdu.PDU;

public class NicksPDU extends PDU{

    private ArrayList<String> nicks;
    public NicksPDU(byte[] bytes) {
        nicks = parse(bytes);
    }

    private ArrayList<String> parse(byte[] bytes) {
        ArrayList<String> nicks = new ArrayList<String>();
        int nrOfNicks = (byte) bytes[1];
        int length = (byte) ((bytes[2] << 8) | (bytes[3] & 0xff));
        int startIndex = 4;
        String nick = "";

        for(int i = 0; i < nrOfNicks; i++) {

            int endIndex = startIndex;
            for(; (endIndex < length) && (bytes[endIndex] != '\0'); endIndex++) {}
            nick = new String(bytes,startIndex,endIndex);
            startIndex = endIndex;

        }

        nicks.add(nick);

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

    public int getNrOfNicks() {
        return 0;
    }
    
    public static boolean checkPadding(byte[] bytes) {
    	//
    	return true;
    }
}
