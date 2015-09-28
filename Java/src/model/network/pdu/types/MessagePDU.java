package model.network.pdu.types;

import java.util.Date;

import model.network.MessageData;
import model.network.pdu.OpCode;
import model.network.pdu.PDU;

public class MessagePDU extends PDU{


    private final int nickLenghtIndex = 2;
    private final int msgLenghtIndex = 4;

    private final int timeEnd = 12;


    private byte[] bytes;
    private MessageData msgData;

    public MessagePDU(byte[] bytes) {
        this.bytes = bytes;
        msgData = parseIn(bytes);
    }

	public MessagePDU(String message, String nickname,Date timestamp) {
	    bytes = parseOut();

	}

	public MessageData parseIn(byte[] bytes) {


	    if(!checkPad(bytes)) {
	        return null;
	    }

	    int nickLength = (int) bytes[nickLenghtIndex];
	    int msgLength = (int) (bytes[msgLenghtIndex]<<8) |
	            ( bytes[msgLenghtIndex+1] & 0xff);

	    int index = 8;

	    String timeStamp  = "";
	    for( ; index < timeEnd; index++ ) {
	        timeStamp += (char) bytes[index];
	    }

	    String msg = "";
	    for(; index < msgLength+timeEnd; index++) {
	        msg += (char) bytes[index];
	    }

	    String nick = "";
	    for(; index < nickLength + msgLength + timeEnd; index++) {
	        nick += bytes[index];
	    }

	    return new MessageData(nick,msg,timeStamp);
	}

	private byte[] parseOut() {
        byte bytes[] = new byte[PDU.pduSize()];
	    bytes[0] = OpCode.MESSAGE.value;




	    return bytes;
	}

	private boolean checkPad(byte[] bytes) {
	    return (bytes[1] != 0 && bytes[6] != 0 && bytes[7] != 0);
	}

	@Override
	public byte[] toByteArray() {
		return bytes;
	}

	@Override
	public int getSize() {
		return 0;
	}

	@Override
	public byte getOpCode() {
		return OpCode.MESSAGE.value;
	}

	public MessageData getMessageData() {
	    return msgData;
	}
}
