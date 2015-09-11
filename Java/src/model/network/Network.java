package model.network;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import network.pdu.types.GetListPDU;
import network.pdu.types.SListPDU;


// http://stackoverflow.com/questions/9520911/java-sending-and-receiving-file-byte-over-sockets
// https://docs.oracle.com/javase/tutorial/networking/datagrams/
/**
 * @author c12ton
 * @version 2015.09.16
 * Manage all network related stuff.
 *
 */
public class Network {


	private final String nameServerAddress = "itchy.cs.umu.se";
	private final int nameServerPort = 1337;

	private DatagramSocket udpSocket;
	
	private PrintWriter msgOut;
	private OutputStream socketOut;
	private InputStream socketIn;

	//Should throw exceptions, and it should be handled in Listener.
	public void connect(String address, int port, String nickName) { //Change address type to InetAddress??
		try {

			socket = new DatagramSocket();

			//TEST Send pdu
			byte[] pduGETLIST = new byte[2];
			pduGETLIST[0] = 3;
			pduGETLIST[1] = 0;

			//SENDING packet
			InetAddress inetAddress = InetAddress.getByName(address);
			packet = new DatagramPacket(pduGETLIST,pduGETLIST.length,inetAddress,port);
			socket.send(packet);

			//RECEIVING packet
			byte[] pduSLIST = new byte[16];
			packet = new DatagramPacket(pduSLIST,pduSLIST.length);
			  //method blocks untill it receive packet.
			socket.receive(packet);
			socket.


			System.out.println(pduSLIST[0]);

		} catch (IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //catch (ACKException e)
	}

	//return false, if connection is not established
	public boolean conncetToNameServer(){
		//settings
		InetAddress address = InetAddress.getByName(nameServerAddress);
		GetListPDU pdu = new GetListPDU();

		DatagramPacket packet = new DatagramPacket(pdu.toByteArrat(),
													pdu.toByteArrat().length,
													address,nameServerPort);

		DatagramSocket datagramSocket = new DatagramSocket();
		
		datagramSocket.send(packet);
		 udpSocket.r

		return datagramSocket.isConnected();
	}


	public void getNameServerList() {
		
		SListPDU pdu = new SListPDU();
		DatagramPacket packet = new DatagramPacket(pdu.,pduSLIST.length); 
		udpSocket.receive(pakcet);
		//while loop till end of data
		 // collect data
		//done
		//return data
		datagramSocket.receive(arg0);
		packet.re
		
		
		datagramSocket.disconnect();

		return;
	}
	
	
	public void conncetToClientServer() {

	}


	public void getClientServerData() {

	}

	public void

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

