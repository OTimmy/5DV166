import java.io.IOException;
import java.net.InetAddress;

import controller.Listener;

import model.network.Network;



public class main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		
		Network network = new Network();
			//network.conncetToNameServer();
			System.out.println(network.conncetToNameServer());
			
                network.getNameServerList();
                Listener listener = new Listener();
			// TODO Auto-generated catch block
		//network.connect(("itchy.cs.umu.se"), 1337, "Name");

	}
}
