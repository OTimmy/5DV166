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
	//TODO currentsequence

	public SListPDU(byte[] bytes) {
	    this.bytes = bytes;
	    servers = new ArrayList<ServerData>();
        parse(bytes);
	}

	/**
	 * Parser and store data in appropiate list.
	 * @return false if parsing failed.
	 */
	private void parse(byte[] bytes) {

		int index = 4;
		//currentSequence = (int) bytes[1];
		int nrOfServers = (int) ((bytes[2] << 8)+ bytes[3]);

		for(int i = 0; i < nrOfServers; i++) {
            String address = "";
		    for(int j = index; j < index + 4; j++) {
	                address += "" + ((int) bytes[j] & 0xff);
	                if( j < (index + 3)) {
	                    address += ".";
	                }
	        }

            int port        =  (int) (( bytes[index +4] << 8) |( bytes[index +5] & 0xff ));
	        int nrOfClients = (int) bytes [index + 6];
	        int nameLength  = (int) bytes[index + 7];
	        System.out.println("Port:" +port);
	        //start index for server name
	        index += 8;
	        // Getting servers name
	        String serverName =  "";
	        for(int j = index; j < (index + nameLength);j++) {
	            serverName += (char) bytes[j];
	        }


	        index += nameLength +  (4 - nameLength % 4) % 4;

	        servers.add(new ServerData(serverName,address,port,nrOfClients));
	    }
	}

	public ArrayList<ServerData> getServerData() {
	    return servers;
	}

	public int getSize() {
	    return bytes.length;
	}

    @Override
    public byte[] toByteArray() {
        return bytes;
    }
}