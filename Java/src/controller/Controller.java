package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;

import view.GUI;

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
public class Controller {

    private Network net;
    private GUI gui;
    private ArrayList<String> nicks;
    private boolean serverConnected;
    private boolean nameServerConnected;

    public Controller(Network net, GUI gui) {

    	this.net = net;
		this.gui = gui;
    	initNetworkListener(net);
    	initGUIActionListener();
    	//net.connectToNameServer("itchy.cs.umu.se", 1337);
		nameServerConnected = false;
		serverConnected = false;
		net.ConnectToServer("nightcrawler.cs.umu.se", 51515, "FUCKU");
	}

	/**
	 * Initate listeners dedicated for the network.
	 */
	private void initNetworkListener(Network net) {

		net.addErrorListener(new controller.Listener<String>() {

			@Override
			public void update(String t) {
				System.out.println("Error: "+t);
			}
		});

		net.addServerListener(new controller.Listener<ServerData>() {

			@Override
			public void update(ServerData t) {
			   if(t != null) {
	                System.out.println("Server name: " + t.getName());
			   } else {
			       System.out.println("Reset servers");
			   }
			}

		});

		net.addMessageListener(new controller.Listener<MessageData>() {

			@Override
			public void update(MessageData t) {
				System.out.println("Message received: "+t.getMsg());
			}

		});

//		net.addNicksListener(new controller.Listener<String>() {
//
//            @Override
//            public void update(String nicks) {
//
//            }
//
//		});
	}

	private void initGUIActionListener() {
		gui.addConnectNameServerButtonListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

			    if(nameServerConnected == false) {
			        String address = gui.getNameServerAddress();
			        int port = new Integer(gui.getNameServerPort());

			        nameServerConnected = net.connectToNameServer(address, port);

			    } else {
			        net.disconnectNameServer();
			        nameServerConnected = false;
			    }

			    JButton button = (JButton) arg0.getSource();

			    if(nameServerConnected == false) {
			        button.setText("Connect");
			    } else {
			        button.setText("Disconnect");
			    }
			}
		});

		gui.addConnectSeverButtonListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

			    if(!serverConnected) {
			        String address = gui.getServerAddress();
			        int port = new Integer(gui.getServerPort());
			        String nick = gui.getNick();
			        serverConnected = net.ConnectToServer(address, port, nick);
			    } else {
			        serverConnected = false;
			    }

	            JButton button = (JButton) e.getSource();

			    if(!serverConnected) {
			        button.setText("Connect");
			    } else {
			        button.setText("Disconnect");
			    }

			}

		});

		gui.addOkButtonListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			    String nick = gui.getNick();
				net.changeNick(nick);
			}
		});

		gui.addRefreshButtonListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			    if(nameServerConnected) {
	                net.refreshServers();
			    }
			}
		});

		gui.addSendButtonListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = gui.getSendTextArea();
				net.SendMessage(msg);
			}
		});
	}
}