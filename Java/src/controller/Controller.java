package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

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
    
    public Controller(Network net, GUI gui) {
    	//Model
    	this.net = net;
		this.gui = gui;
    	initNetworkListener(net);
		
		//temp
		net.connectToNameServer("itchy.cs.umu.se", 1337);
		//net.ConnectToServer("proton.cs.umu.se", 515);
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

		net.addNicksListener(new controller.Listener<ArrayList<String>>() {

            @Override
            public void update(ArrayList<String> nicks) {
                for(String nick:nicks) {

                }
            }

		});
	}

	public void initGUIActionListener() {
		gui.addConnectNameServerButtonListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		gui.addConnectSeverButtonListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(net.ConnectToServer(address, port) {
					//set button to disconnect
				}
			}
			
		});
		
		gui.addOkButtonListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				gui.getNick();
				net.changeNick()
			}
		});
		
		gui.addRefreshButtonListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				net.refreshServers();
				
			}
		});
		
		gui.addSendButtonListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = gui.getSendTxtArea();
				net.SendMessage(msg);
			}
		});
		
	}
}