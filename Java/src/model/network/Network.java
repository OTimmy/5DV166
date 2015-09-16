package model.network;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import controller.ErrorManager;

import model.network.pdu.types.GetListPDU;
import model.network.pdu.types.SListPDU;

//YES
// http://stackoverflow.com/questions/9520911/java-sending-and-receiving-file-byte-over-sockets
// https://docs.oracle.com/javase/tutorial/networking/datagrams/
// http://examples.javacodegeeks.com/core-java/net/inetaddress/get-hostname-from-ip-address/
/**
 * @author c12ton
 * @version 2015.09.16
 * Manage all network related stuff.
 *
 */
public class Network extends ErrorManager{


	private final String nameServerAddress = "itchy.cs.umu.se";
	private final int nameServerPort = 1337;
	private final int recieveTimeOut = 400;

	private DatagramSocket udpSocket;

	private PrintWriter msgOut;
	private OutputStream socketOut;
	private InputStream socketIn;

	
	//return false, if connection is not established
	public boolean conncetToNameServer() {		
		try {

			InetAddress address = InetAddress.getByName(nameServerAddress);
			GetListPDU pdu = new GetListPDU();

			DatagramPacket packet = new DatagramPacket(pdu.toByteArray(),
														pdu.getSize(),
														address,nameServerPort);
			//Initating udpSocket
			udpSocket = new DatagramSocket();
			udpSocket.setSoTimeout(recieveTimeOut);

			udpSocket.send(packet);

		} catch(IOException e) {
		    reportError(e.toString());
		    return false;
		}
		return true;
	}

   //getServerAddress

   //getServerClinets

	//Should return arraylist???
	public void getNameServerList() {
	    try {

	        SListPDU pdu = new SListPDU();
	        DatagramPacket packet = new DatagramPacket(pdu.toByteArray(),
	                                                   pdu.getSize());
	        udpSocket.receive(packet);

	        //God no!!!!
	        int nrTrys = 0;
	        for(nrTrys = 0; (pdu.parse(packet.getData()) == false) &&
	                nrTrys < 1; nrTrys++) {

                packet = new DatagramPacket(pdu.toByteArray(), pdu.getSize());
                udpSocket.receive(packet);

	        }
	        
	        reportError("Hola");
	        
	        if(nrTrys < 1) {
	            //pdu.getNameList
	            //pdu.getStuff

	        } else {
	            reportError("Package didn't ge trough");
	        }

	    }catch (IOException e) {
	    	reportError(e.toString());
	    }
	
	}

	public void conncetToClientServer() {

	}


	public void getClientServerData() {

	}

	public void disconnect() {

	}


	/**
	 * @return true if message was successfully transmitted,
	 * else false.
	 */
	public boolean trySendMessage() {
		return false;
	}

	public void readMessage() {

	}


	public void changeNick(String nick) {

	}
	
}