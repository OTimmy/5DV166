package network.pdu.types;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import network.pdu.*;
/**
 * @author c12ton
 * @version 0.0
 *
 * And parse the returned data.
 */
public class SListPDU extends PDU{
    private final int ROW_SIZE = 4;

	private ArrayList<String> addresses;
	private ArrayList<String> ports;
	private ArrayList<String> clientNumbers;
	private ArrayList<String> serverNames;
	private byte[] bytes;
	private int sequenceNr;
	private boolean validFlag;

	public SListPDU(InputStream inStream) throws IOException {
	    addresses = new ArrayList<String>();
	    ports = new ArrayList<String>();
	    clientNumbers = new ArrayList<String>();
	    serverNames = new ArrayList<String>();

	    validFlag = true;

	    parse(inStream);

	}

	/**
	 * Parser and store data in appropiate list.
	 * @return false if parsing failed.
	 * @throws IOException
	 */
	private void parse(InputStream inStream) throws IOException {

	    sequenceNr = inStream.read();

	    //Reading number of servers
	    byte[] tempBytes = new byte[2];
	    inStream.read(tempBytes, 0, tempBytes.length);
		int nrOfServers = (int) ((tempBytes[0] & 0xff ) << 8 | (tempBytes[1] & 0xff));

		for(int i = 0; i < nrOfServers; i++) {

		    String address = "";

            for(int j = 0; j < ROW_SIZE; j++) {
                address +=  inStream.read();
                if(j < ROW_SIZE -1) {
                    address += ".";
                }
            }

            //Reading port
            tempBytes = new byte[2];
            inStream.read(tempBytes, 0, tempBytes.length);
            int port = (int) ((tempBytes[0] & 0xff) << 8 | (tempBytes[1] & 0xff));

	        int nrOfClients =  inStream.read();
	        int nameLength = inStream.read();
	        //int nameLength  =  (int) bytes[index + 7] & 0xff;

	        //read server name
	        byte[] nameBytes = new byte[nameLength];
	        inStream.read(nameBytes, 0, nameBytes.length);
	        String serverName = new String(nameBytes,StandardCharsets.UTF_8);

	        //read the pads
	        int padded = padLengths(nameLength);
	        tempBytes = new byte[padded];
	        inStream.read(tempBytes, 0, tempBytes.length);

	        //Checking the pads
//	        for(byte b:tempBytes) {
//	            if(b != 0) {
//	                validFlag = false;
//	                return;
//	            }
//	        }


	        addresses.add(address);
	        ports.add(new Integer(port).toString());
	        clientNumbers.add(new Integer(nrOfClients).toString());
	        serverNames.add(serverName);

	    }
	}


	public int getSize() {
	    return bytes.length;
	}

	public int getSequenceNr() {
	    return sequenceNr;
	}

    @Override
    public byte[] toByteArray() {
        return bytes;
    }

	@Override
	public byte getOpCode() {
		return OpCode.SLIST.value;
	}

    public boolean isValid() {
        return validFlag;
    }

    public ArrayList getServerNames() {
        return serverNames;
    }

    public ArrayList getAddresses() {
        return addresses;
    }

    public ArrayList getPorts() {
        return ports;
    }

    public ArrayList getClientNumberss() {
        return clientNumbers;
    }
}