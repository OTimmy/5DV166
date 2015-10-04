package model.network.pdu.types;

import java.nio.charset.StandardCharsets;

import model.network.MessageData;
import model.network.pdu.ByteSequenceBuilder;
import model.network.pdu.Checksum;
import model.network.pdu.OpCode;
import model.network.pdu.PDU;


public class MessagePDU extends PDU{

    private final int TIME_END = 12;
    private final byte PAD = 0;

    private byte[] bytes;
    private MessageData msgData;
    private boolean validFlag;

    public MessagePDU(byte[] bytes) {
        this.bytes = bytes;
        validFlag = true;
        msgData = parseIn(bytes);
        //Always true
        validFlag = true;

    }

	public MessagePDU(String message) {
	    bytes = parseOut(message);

	}

	public MessageData parseIn(byte[] bytes) {

		if(Checksum.computeChecksum(bytes) != 0  && !checkPadding(bytes) ) {
		    validFlag = false;
			//return null;
		}

	    int nickLength = (int) bytes[2];
	    int msgLength = (int) (((bytes[4] & 0xff)<<8) | ( bytes[5] & 0xff) );
	    int index = 8;
	    int end = TIME_END;

	    //Time stamp
	    String timeStamp  = "";
	    for( ; index < TIME_END; index++ ) {
	        timeStamp += (char) bytes[index];
	    }

	    //Message
	    end = msgLength + TIME_END;
	    byte[] msgBytes = new byte[msgLength];
	    for(int i = 0; index < end; i++,index++) {
	        msgBytes[i] = bytes[index];
	    }

	    String msg = new String(msgBytes, StandardCharsets.UTF_8);
	    index+= padLengths(msgBytes.length);


	    byte[] nickBytes = new byte[nickLength];
	    for(int i = 0, j = index; j < (nickLength + index); i++,j++) {
	        nickBytes[i] = bytes[j];
	    }
	    String nick = new String(nickBytes, StandardCharsets.UTF_8);

	    return new MessageData(nick,msg,timeStamp);
	}

	private byte[] parseOut(String msg) {

		//OP-code,pad,nick length, check sum 0 default.
		ByteSequenceBuilder builder = new ByteSequenceBuilder(OpCode.MESSAGE.value,
									  PAD, PAD,(byte)0);

		byte[] msgBytes = msg.getBytes();
		//msg length to two bytes, then pad.
		builder.appendShort((byte) msgBytes.length);
		//pad remaining 2 bytes
		builder.pad();

		//time stamp zero (over 4 bytes)
		builder.appendInt(0);

		//message
		builder.append(msgBytes);

		//padding
		builder.pad();


		//Done
		byte[] bytes = builder.toByteArray();


		bytes[3] = Checksum.computeChecksum(bytes);


	    return bytes;
	}


	/**
	 * @param start of padding, length of message or nicks
	 * @return true if padding is correct otherwise false.
	 */
	public boolean checkPadding(byte[] bytes) {

	    if((bytes[1] != 0 || bytes[6] != 0 || bytes[7] != 0)) {
	        return false;
	    }

        int msgLength  = (int) (bytes[4]<<8 & 0xff) | ( bytes[5] & 0xff);
        int msgStart   = 12;

	    //message padding
	    if(!checkStringPadding(bytes, msgStart,msgLength)) {
	        return false;
	    }

	    int nickLength = (int) bytes[2];
        int nickStart  = msgStart + padLengths(msgLength);

	    if(!checkStringPadding(bytes, nickStart,nickLength)) {
	        return false;
	    }

	    return true;
	}

	private boolean checkStringPadding(byte[] bytes, int start, int length) {
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
		return bytes.length;
	}

	@Override
	public byte getOpCode() {
		return OpCode.MESSAGE.value;
	}

	public MessageData getMessageData() {
	    return msgData;
	}

	public boolean isValid() {
	    return validFlag;
	}
}
