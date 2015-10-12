package network.pdu.types;

import network.pdu.ByteSequenceBuilder;
import network.pdu.OpCode;
import network.pdu.PDU;

public class ChNickPDU extends PDU{

    private byte bytes[];
    public ChNickPDU(String nickname) {
        bytes = parse(nickname);
    }

    private byte[] parse(String name) {
        byte[] nickBytes = name.getBytes();
        ByteSequenceBuilder builder = new ByteSequenceBuilder(OpCode.CHNICK.value,
                                                       (byte) nickBytes.length);
        builder.pad();
        builder.append(nickBytes);
        builder.pad();

        return builder.toByteArray();
    }

    @Override
    public byte[] toByteArray() {
        return bytes;
    }

    @Override
    public int getSize() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public byte getOpCode() {
        return OpCode.CHNICK.value;
    }

	@Override
	public boolean isValid() {
		return false;
	}
}
