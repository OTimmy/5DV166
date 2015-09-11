package model.network;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import model.network.pdu.types.GetListPDU;
import model.network.pdu.types.SListPDU;

//YES
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
	private final int recieveTimeOut = 400;

	private DatagramSocket udpSocket;

	private PrintWriter msgOut;
	private OutputStream socketOut;
	private InputStream socketIn;

	//Should throw exceptions, and it should be handled in Listener.
//	public void connect(String address, int port, String nickName) { //Change address type to InetAddress??
//		try {
////
////			socket = new DatagramSocket();
////
////			//TEST Send pdu
////			byte[] pduGETLIST = new byte[2];
////			pduGETLIST[0] = 3;
////			pduGETLIST[1] = 0;
////
////			//SENDING packet
////			InetAddress inetAddress = InetAddress.getByName(address);
////			packet = new DatagramPacket(pduGETLIST,pduGETLIST.length,inetAddress,port);
////			socket.send(packet);
////
////			//RECEIVING packet
////			byte[] pduSLIST = new byte[16];
////			packet = new DatagramPacket(pduSLIST,pduSLIST.length);
////			  //method blocks untill it receive packet.
////			socket.receive(packet);
////			socket.
////
////
////			System.out.println(pduSLIST[0]);
//
//		} catch (IOException e){
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} //catch (ACKException e)
//	}

	//return false, if connection is not established
	public boolean conncetToNameServer() {
		//settings
		try {

			InetAddress address = InetAddress.getByName(nameServerAddress);
			GetListPDU pdu = new GetListPDU();

			DatagramPacket packet = new DatagramPacket(pdu.toByteArray(4),
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



	//Problem with address, becuase its signed bit, change dat to unsigned
	//Should return arraylist???
	public void getNameServerList() throws IOException {

	    try {


	        SListPDU pdu = new SListPDU();

	        DatagramPacket packet = new DatagramPacket(pdu.toByteArray(100),
													pdu.getSize());

	        udpSocket.receive(packet);

	        System.out.println("OP-code: "+packet.getData()[0]);


//	        packet = new DatagramPacket(pdu.toByteArray(1337),
//                       pdu.getLength());
//	        udpSocket.receive(packet);

	        System.out.println("Sekvensnr: "+ packet.getData()[1]);

	        //udpSocket.getS

          packet = new DatagramPacket(pdu.toByteArray(1337),
                       pdu.getSize());
          udpSocket.receive(packet);

          System.out.println("Sekvensnr: "+(char)packet.getData()[1]);


	    }catch(SocketTimeoutException e) {
	        System.out.println("Could not download list, timed out. (MSG)");

	        return;
	    }catch (IOException e) {

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

