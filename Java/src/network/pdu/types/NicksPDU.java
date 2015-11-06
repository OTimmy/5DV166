package network.pdu.types;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import network.pdu.OpCode;
import network.pdu.PDU;
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


        int nrOfNicks   = readExactly(1, inStream)[0] & 0xff;

        //Reading length of names
        byte[] nicksLengthBytes = readExactly(2, inStream); 
        int nicksLength = ( nicksLengthBytes[0] & 0xff ) << 8 
                          | ( nicksLengthBytes[1] & 0xff);

        for(int i = 0; i < nrOfNicks; i++) {

            byte b;

            while((b = (byte) readExactly(1, inStream) [0]) != 0) {
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
           return ERROR_PADDING_NICK;
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
        return 0;
    }

    @Override
    public String getError() {
        return error;
    }
}