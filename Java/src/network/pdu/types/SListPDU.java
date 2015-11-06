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

    private String error;
    private ArrayList<String> addresses;
    private ArrayList<String> ports;
    private ArrayList<String> clientNumbers;
    private ArrayList<String> serverNames;
    private byte[] bytes;
    private int sequenceNr;

    public SListPDU(InputStream inStream) throws IOException {
        addresses = new ArrayList<String>();
        ports = new ArrayList<String>();
        clientNumbers = new ArrayList<String>();
        serverNames = new ArrayList<String>();
        error = parse(inStream);

    }

    /**
     * Parser and store data in appropiate list.
     * @return false if parsing failed.
     * @throws IOException
     */
    private String parse(InputStream inStream) throws IOException {

        sequenceNr = readExactly(1, inStream)[0] & 0xff;

        //Reading number of servers
        byte[] nrOfServersBytes = readExactly(2, inStream);
        
        int nrOfServers = (int) ((nrOfServersBytes[0] & 0xff ) << 8 
                                | (nrOfServersBytes[1] & 0xff));

        for(int i = 0; i < nrOfServers; i++) {

            String address = "";

            for(int j = 0; j < ROW_SIZE; j++) {
                address +=  readExactly(1, inStream)[0] & 0xff;
                if(j < ROW_SIZE -1) {
                    address += ".";
                }
            }

            //Reading port
            byte[] portBytes = readExactly(2, inStream); 
            
            int port = (int) ((portBytes[0] & 0xff) << 8 | (portBytes[1] & 0xff));

            int nrOfClients =  readExactly(1, inStream)[0] & 0xff;
            int serverNameLength = readExactly(1, inStream)[0] & 0xff;
            
            //read server name
            byte[] serverNameBytes = readExactly(serverNameLength, inStream); 
            String serverName = new String(serverNameBytes,StandardCharsets.UTF_8);

            //read the pads
            byte[] paddedBytes = readExactly(padLengths(serverNameLength), inStream);
            if(!isPaddedBytes(paddedBytes)) {
                return ERROR_PADDING_SERVER_NAME;
            }

            addresses.add(address);
            ports.add(new Integer(port).toString());
            clientNumbers.add(new Integer(nrOfClients).toString());
            serverNames.add(serverName);
        }
        
        return null;
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

    @Override
    public String getError() {
        return error;
    }
}