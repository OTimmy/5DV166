package model.network.pdu.types;

import java.util.ArrayList;

import model.network.ServerData;
import model.network.pdu.*;
/**
 * @author c12ton
 * @version 0.0
 *
 * And parse the returned data.
 */
public class SListPDU extends PDU{
	private ArrayList<ServerData> servers;
	private byte[] bytes;
	private int sequenceNr;

	public SListPDU(byte[] bytes) {
	    this.bytes = bytes;
	    servers = parse(bytes);
	}

	/**
	 * Parser and store data in appropiate list.
	 * @return false if parsing failed.
	 */
	private ArrayList<ServerData> parse(byte[] bytes) {
		
		
		
		ArrayList<ServerData>servers = new ArrayList<ServerData>();
		sequenceNr = (int) ((bytes[1]));
		int nrOfServers = (int) (((bytes[2] & 0xff )<< 8) | (bytes[3] & 0xff));
		int index = 4;

		for(int i = 0; i < nrOfServers; i++) {
            String address = "";
		    for(int j = index; j < index + 4; j++) {
	                address += "" + ((int) bytes[j] & 0xff);
	                if( j < (index + 3)) {
	                    address += ".";
	                }
	        }

            int port        =  (int) ((( bytes[index +4] & 0xff) << 8) |( bytes[index +5] & 0xff ));
	        int nrOfClients =  (int) bytes [index + 6];
	        int nameLength  =  (int) bytes[index + 7];

	        //start index for server name
	        index += 8;
	        // Getting servers name
	        String serverName =  "";
	        for(int j = index; j < (index + nameLength);j++) {
	            serverName += (char) bytes[j];
	        }
	        
	        
	        System.out.println(checkPadding(index + nameLength,nameLength));
	        //index += nameLength +  (4 - nameLength % 4) % 4;
	        index += nameLength + padLengths(nameLength);
	        servers.add(new ServerData(serverName,address,port,nrOfClients));
	    }
		
		return servers;
	}

	public ArrayList<ServerData> getServerData() {
	    return servers;
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

    private boolean checkPadding(int start,int length) {

    	int padded = padLengths(length);

       for(int i = start; i < padded+start; i++) {
    	   
           if( bytes[i] != 0 ) {
               return false;
           }
       }

        return true;
    }
}