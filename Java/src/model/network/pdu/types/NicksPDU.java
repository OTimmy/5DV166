package model.network.pdu.types;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import model.network.pdu.OpCode;
import model.network.pdu.PDU;
//TODO Implement proper check of padding
public class NicksPDU extends PDU{

    private final int HEADER_SIZE       = 4;

    private ArrayList<String> nicks;
    private static boolean validFlag;
    private ArrayList<Byte> bytes;

    public NicksPDU(InputStream inStream) throws IOException {
        bytes = new ArrayList<Byte>();

        validFlag = true;
        nicks = parse(inStream);

    }

    private ArrayList<String> parse(InputStream inStream) throws IOException {
        ArrayList<String> nicks = new ArrayList<String>();


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
       tempBytes = new byte[padLengths(nicksLength)];
       inStream.read(tempBytes, 0, tempBytes.length);

        return nicks;
    }

    @Override
    public byte[] toByteArray() {
        byte[] result = new byte[bytes.size()];
        for(int i = 0; i < bytes.size(); i++) {
            result[i] = bytes.get(i);
        }

        return result;
    }

    @Override
    public byte getOpCode() {
        return OpCode.NICKS.value;
    }

    public ArrayList<String> getNicks() {
        return nicks;
    }

    /**
     * @param start of padding, and length of nick.
     */
    private boolean checkPadding(byte[] bytes, int length) {

        int padded = length + padLengths(length);
        if(bytes.length < padded) {
            return false;
        }
//        System.out.println("Length: "+length);
//        System.out.println("Padded "+padded);
//        System.out.println("val at 12 "+bytes[12]);
        for(int i = length; i < bytes.length; i++) {
            if(bytes[i] != 0) {
               return false;
            }
        }

    	return true;
    }

    public boolean isValid() {
        return validFlag;
    }

    @Override
    public int getSize() {
        // TODO Auto-generated method stub
        return 0;
    }
}