package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import model.network.MessageData;
import model.network.Network;
import model.network.ServerData;

/**
 * <h1>Listener.java</h1>
 *  Manage the flow between the gui and the underlying model
 *
 * @author c12ton
 * @version 0.0
 */
public class Controller implements ActionListener{

    private Network net;
    public Controller(Network net) {
    	//Model
    	this.net = net;
		initNetworkListener(net);
		//temp
		net.connectToNameServer("itchy.cs.umu.se", 1337);
		//net.ConnectToServer("for.cs.umu.se", 51515);
		//View
	}

	/**
	 * Initate listeners dedicated for the network.
	 */
	private void initNetworkListener(Network net) {

		net.addErrorListener(new controller.Listener<String>() {

			@Override
			public void update(String t) {
				System.out.println("Error");
			}
		});

		net.addServerListener(new controller.Listener<ServerData>() {

			@Override
			public void update(ServerData t) {
			   System.out.println("Server name: " + t.getName());
			}

		});

		net.addMessageListener(new controller.Listener<MessageData>() {

			@Override
			public void update(MessageData t) {
				System.out.println("Message received: "+t.getMsg());
			}

		});

	}


    private void connectToNameServer(String address, int port) {
 	  net.connectToNameServer(address, port);
    }

    private void disconnectNameServer() {
    	net.disconnectNameServer();
    }

    private void refreshServerList() {
    	net.refreshServers();
    }

   private void connectServer(String address,int port) {
       net.ConnectToServer("scratchy.cs.umu.se", 1234);
   }

    @Override
    public void actionPerformed(ActionEvent e) {
    	//loads of else if statements
    }
}
