package network.pdu.types;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import network.pdu.ByteSequenceBuilder;
import network.pdu.Checksum;
import network.pdu.DateUtils;
import network.pdu.OpCode;
import network.pdu.PDU;


public class MessagePDU extends PDU{
    private final int ROW_SIZE = 4;
    private final byte PAD = 0;

    private byte[] bytes;
    private boolean validFlag;

    private String msg;
    private String nick;
    private Date date;

    public MessagePDU(InputStream inStream) throws IOException {
        validFlag = parseIn(inStream);

    }

	public MessagePDU(String message) {
	    bytes = parseOut(message);

	}

	public boolean parseIn(InputStream inStream) throws IOException {

	    //Reading rest of header
	    byte[] headerBytes = readExactly(ROW_SIZE -1,inStream); //new byte[ROW_SIZE -1];

	    int pad = headerBytes[0];

	    int nickLength = (int) (headerBytes[1] & 0xff);
	    int checkSum = (int) (headerBytes[2] & 0xff);

	    //Reading message length + padding
	    byte[] tempBytes = readExactly(ROW_SIZE, inStream);

	    int msgLength = (int) (((tempBytes[0] & 0xff) << 8 ) | (tempBytes[1] & 0xff));
	    pad  = (int) (((tempBytes[2] & 0xff) << 8)  | (tempBytes[3] & 0xff)); //Should be zero

	    System.out.println("SIZE OF MSG: "+msgLength);

	    //Reading time stamp
	    byte[] timeBytes = readExactly(ROW_SIZE, inStream);
	    date = DateUtils.getDateByBytes(timeBytes);

	    // Reading message
	    byte[] msgBytes = readExactly(msgLength,inStream);

	    System.out.println("Recieved size: "+msgBytes.length);
        msg = new String(msgBytes, StandardCharsets.UTF_8);

        //Padding of message
        tempBytes = readExactly(padLengths(msgLength), inStream);

        //nick name
        byte[] nickBytes = readExactly(nickLength, inStream);


        //padding of nick
        tempBytes = readExactly(padLengths(nickLength), inStream);

        if(nickBytes.length == 0) {
            nick = "Server";
        } else {
            nick = new String(nickBytes, StandardCharsets.UTF_8);
        }



        return true;
	}

	private byte[] parseOut(String msg) {

		//OP-code,pad,nick length, check sum 0 default.
		ByteSequenceBuilder builder = new ByteSequenceBuilder(OpCode.MESSAGE.value,
									  PAD, PAD,(byte)0);


		byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_8);
		System.out.println("Sending bytes: "+msgBytes.length);
		//msg length to two bytes, then pad.
		builder.appendShort((short) msgBytes.length);
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

	public boolean isValid() {
	    return validFlag;
	}

	public String getMsg() {
	    return msg;
	}

	public String getNick() {
	    return nick;
	}

	public Date getDate() {
	    return date;
	}
}