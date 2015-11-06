package network.pdu.types;


import network.pdu.OpCode;
import network.pdu.PDU;

public class QuitPDU extends PDU{

    private byte[] bytes;

    public QuitPDU() {
        bytes = new byte[4];

        bytes[0] = OpCode.QUIT.value;
    }


    @Override
    public byte[] toByteArray() {
        return bytes;
    }

    @Override
    public int getSize() {
        return bytes.length;
    }

    @Override
    public byte getOpCode() {
        return OpCode.QUIT.value;
    }

    @Override
    public String getError() {
        return null;
    }

}
