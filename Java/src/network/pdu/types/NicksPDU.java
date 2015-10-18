package network.pdu.types;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import network.pdu.OpCode;
import network.pdu.PDU;
//TODO Implement proper check of padding
public class NicksPDU extends PDU{

	private String error;
    private ArrayList<String> nicks;
    private ArrayList<Byte> bytes;

    public NicksPDU(InputStream inStream) throws IOException {
        bytes = new ArrayList<Byte>();
        
        error = parse(inStream);
    }

    private String parse(InputStream inStream) throws IOException {
        nicks = new ArrayList<String>();


        int nrOfNicks   = inStream.read();

        //Reading length of names
        byte[] tempBytes = new byte[2];
        inStream.read(tempBytes, 0, 2);
        int nicksLength = ( tempBytes[0] & 0xff ) << 8 | ( tempBytes[1] & 0xff);


        for(int i = 0; i < nrOfNicks; i++) {

            byte b;

            while((b = (byte) inStream.read()) != 0) {
            	bytes.add(b);
            }


            byte[] nickBytes = new byte[bytes.size()];

            for(int j = 0; j < bytes.size(); j++) {
            	nickBytes[j] = bytes.get(j);
            }

            bytes = new ArrayList<Byte>();


            String nick = new String(nickBytes,0,
            						nickBytes.length,StandardCharsets.UTF_8);

            nicks.add(nick);
       }

       //Reading the remaining padding
       byte[] padBytes = readExactly(padLengths(nicksLength), inStream); 

       if(!isPaddedBytes(padBytes)) {
    	   return "Incorrect padding of nicks";
       }
       
        return null;
    }

    @Override
    public byte[] toByteArray() {
        return null;
    }

    @Override
    public byte getOpCode() {
        return OpCode.NICKS.value;
    }

    public ArrayList<String> getNicks() {
        return nicks;
    }

    @Override
    public int getSize() {
        // TODO Auto-generated method stub
        return 0;
    }

	@Override
	public String getError() {
		return error;
	}
}