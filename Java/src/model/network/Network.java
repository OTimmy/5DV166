package model.network;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import controller.ErrorManager;

import model.network.pdu.types.GetListPDU;
import model.network.pdu.types.SListPDU;


/**
 * @author c12ton
 * @version 2015.09.16
 * Manage all network related stuff.
 *
 */
public class Network extends ErrorManager{


	private final String nameServerAddress = "itchy.cs.umu.se";
	private final int nameServerPort = 1337;
	private final int recieveTimeOut = 400;  //in ms

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
	///getNameList, should be looped by a thread
	public void watchUDPSocket() {
	    try {

	        SListPDU pdu = new SListPDU();
	        DatagramPacket packet = new DatagramPacket(pdu.toByteArray(),
	                                                   pdu.getSize());
	        udpSocket.receive(packet);


	    }catch (IOException e) {
	    	reportError(e.toString());
	    }

	}

	private void sendGetList() {

	}




	//public void get































	public void conncetToClientServer(String address, int port) {

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