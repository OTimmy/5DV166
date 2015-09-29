package model.network.pdu.types;

import java.util.ArrayList;

import model.network.pdu.OpCode;
import model.network.pdu.PDU;

public class NicksPDU extends PDU{

    private ArrayList<String> nicks;
    public NicksPDU(byte[] bytes) {

    }


    private ArrayList parse(byte[] bytes) {
        ArrayList<String> nicks = new ArrayList<String>();
        int nrOfNicks = (byte) bytes[2];


        return null;
    }

    @Override
    public byte[] toByteArray() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getSize() {
        // TODO Auto-generated method stub
        return PDU.pduSize();
    }

    @Override
    public byte getOpCode() {
        return OpCode.NICKS.value;
    }

    public void getNicks() {

    }

    public int getNrOfNicks() {
        return 0;
    }

}
