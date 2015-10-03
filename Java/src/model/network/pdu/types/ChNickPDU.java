package model.network.pdu.types;

import model.network.pdu.PDU;

public class ChNickPDU {

    private byte bytes[];
    public ChNickPDU(String nickname) {

    }

    private byte[] parse() {
        byte bytes[] = new byte[PDU.pduSize()];
        return bytes;

    }
}
