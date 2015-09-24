package model.network.pdu.types;

import java.nio.charset.StandardCharsets;

import model.network.pdu.ByteSequenceBuilder;
import model.network.pdu.OpCode;
import model.network.pdu.PDU;

public class JoinPDU extends PDU{
    private byte[] bytes;

    public JoinPDU(String nick) {

        byte[] nickbytes = nick.getBytes(StandardCharsets.UTF_8);
        ByteSequenceBuilder builder = new ByteSequenceBuilder(OpCode.JOIN.value,
                                                       (byte) nickbytes.length);
        bytes = builder.append(nickbytes).pad().toByteArray();

    }

    @Override
    public byte[] toByteArray() {
        return bytes;
    }

    @Override
    public int getSize() {
        return bytes.length;
    }
}