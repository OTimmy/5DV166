package network.pdu.types;

import java.nio.charset.StandardCharsets;

import network.pdu.ByteSequenceBuilder;
import network.pdu.OpCode;
import network.pdu.PDU;

public class JoinPDU extends PDU{
    private byte[] bytes;

    public JoinPDU(String nick) {
        
        byte[] nickbytes = nick.getBytes(StandardCharsets.UTF_8);
        ByteSequenceBuilder builder = new ByteSequenceBuilder(OpCode.JOIN.value,
                                                       (byte) nickbytes.length);
        builder.pad();
        builder.append(nickbytes);
        builder.pad();
        bytes = builder.toByteArray();

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
		return OpCode.JOIN.value;
	}

	@Override
	public String getError() {
		// TODO Auto-generated method stub
		return null;
	}
}
