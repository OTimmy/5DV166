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
public class Network {


	private final String nameServerAddress = "itchy.cs.umu.se";
	private final int nameServerPort = 1337;
	private final int recieveTimeOut = 400;

	private DatagramSocket udpSocket;

	private PrintWriter msgOut;
	private OutputStream socketOut;
	private InputStream socketIn;

	//return false, if connection is not established
	public boolean conncetToNameServer() {
		//settings
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
		    System.out.println("Could not connect");
		    return false;
		}
		return true;
	}

   //getServerAddress

   //getServerClinets

   //
	//Problem with address, becuase its signed bit, change dat to unsigned
	//Should return arraylist???
	public void getNameServerList() {
	    try {

	        SListPDU pdu = new SListPDU();
	        DatagramPacket packet = new DatagramPacket(pdu.toByteArray(),
	                                                   pdu.getSize());
	        udpSocket.receive(packet);

	        System.out.println("OP-code: "+packet.getData()[0]);
	        System.out.println("Sekvensnr: "+ packet.getData()[1]);
	        int value = (packet.getData()[3]<<8 | packet.getData()[4]);
	        System.out.println("Antalet:" + value);
	        //int ip = (int) packet.getData()[6] & 0xff;
	        //String ip = "";//s((int) packet.getData()[4] & 0xff) + ".";

	        pdu.parser(packet.getData());
	        
	        String ip = "";

	        for(int i = 4; i < 8; i++) {
	            ip += ((int) packet.getData()[i] & 0xff);

	            if(i < 7) {
	                ip += ".";
	            }
	        }
	        InetAddress inetAddr = InetAddress.getByName(ip);
	        //InetAddress inetAddr = InetAddress.getByName("130.239.42");

	        String host = inetAddr.getHostName();

	        System.out.println("Host:" + host);

	    }catch(SocketTimeoutException e) {
	        System.out.println("Could not download list, timed out. (MSG)");

	        return;
	    }catch (IOException e) {
	        e.printStackTrace();
	        System.out.println("Unknown host stuff");
	        return;
	    }
		return;
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

