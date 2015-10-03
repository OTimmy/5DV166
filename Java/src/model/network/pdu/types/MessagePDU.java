package model.network.pdu.types;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import model.network.MessageData;
import model.network.pdu.Checksum;
import model.network.pdu.OpCode;
import model.network.pdu.PDU;

//TODO
public class MessagePDU extends PDU{

    private final int timeEnd = 12;


    private byte[] bytes;
    private MessageData msgData;

    public MessagePDU(byte[] bytes) {
        this.bytes = bytes;
        msgData = parseIn(bytes);
    }

	public MessagePDU(String message) {
	    bytes = parseOut(message);

	}

	public MessageData parseIn(byte[] bytes) {


		if(Checksum.computeChecksum(bytes) != 0  && !checkPad(bytes) ) {
			return null;
		}

	    int nickLength = (int) bytes[2];
	    int msgLength = (int) (bytes[4]<<8) | ( bytes[5] & 0xff);

	    int index = 8;

	    String timeStamp  = "";
	    for( ; index < timeEnd; index++ ) {
	        timeStamp += (char) bytes[index];
	    }

	    byte[] msgBytes = new byte[msgLength];

	    for(int i = 0; index < msgLength+timeEnd; i++,index++) {
	        msgBytes[i] = bytes[index];
	    }
	    String msg = new String(msgBytes, StandardCharsets.UTF_8);;

	    String nick = "";
	    for(; index < (nickLength + msgLength + timeEnd); index++) {
	        nick += bytes[index];
	    }


	    return new MessageData(nick,msg,timeStamp);
	}

	private byte[] parseOut(String msg) {
        byte bytes[] = new byte[PDU.pduSize()];
	    bytes[0] = OpCode.MESSAGE.value;
	    bytes[4] = (byte) (msg.length() & 0xff);
	    bytes[5] = (byte) ((msg.length() >> 8) & 0xff);
//	    bytes[6] = 0;
//	    bytes[7] = 0;

//	    bytes[12] = 0;
//	    bytes[13] = 0;
//	    bytes[14] = 0;
//	    bytes[15] = 0;

	    byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_8);

	    //Write message to byte
	    int msgEnd = msg.length() +  (4 - msg.length() % 4) % 4 + 12;
	    int index = 12;
	    for(int i = 0; index < msgEnd;i++,index++) {
	    	bytes[index] = msgBytes[i];
	    }

	    //bytes[3] = 0;
	    byte sum = Checksum.computeChecksum(bytes);

	    bytes[3] = sum;

	    return bytes;
	}


	/**
	 *
	 *
	 *
	 * @param start of padding, length of message or nicks
	 * @return true if padding is correct otherwise false.
	 */
	private boolean checkMessagePadding() {

	    if((bytes[1] != 0 || bytes[6] != 0 || bytes[7] != 0)) {
	        return false;
	    }

        int msgLength  = (int) (bytes[4]<<8 & 0xff) | ( bytes[5] & 0xff);
        int msgStart   = 12;

	    //message padding
	    if(!checkStringPadding(msgStart,msgLength)) {
	        return false;
	    }

	    int nickLength = (int) bytes[2];
        int nickStart  = msgStart + padLengths(msgLength);

	    if(!checkStringPadding(nickStart,nickLength)) {
	        return false;
	    }

	    return true;
	}

	private boolean checkStringPadding(int start, int length) {
        int end = start + padLengths(length);

        for(;start < end; start++ ) {
            if(bytes[start] != 0) {
                return false;
            }
        }

        return true;
	}

	@Override
	public byte[] toByteArray() {
		return bytes;
	}

	@Override
	public int getSize() {
		return PDU.pduSize();
	}

	@Override
	public byte getOpCode() {
		return OpCode.MESSAGE.value;
	}

	public MessageData getMessageData() {
	    return msgData;
	}
}
