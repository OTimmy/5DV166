package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JTable;


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
    private String nick;

    public Controller(Network net, GUI gui) {

    	this.net = net;
		this.gui = gui;
    	initNetworkListener(net);
    	initGUIActionListener();
		nameServerConnected = false;
    	serverConnected = false;
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
			public void update(ServerData server) {
			   if(server != null) {
	                System.out.println("Server name: " + server.getName());
	                gui.addToServerList(server);
			   } else {
			       System.out.println("Reset servers");
			   }
			}

		});

		net.addMessageListener(new controller.Listener<MessageData>() {

			@Override
			public void update(MessageData msg) {
				System.out.println("Message received: "+msg.getMsg());
				gui.printOnMessageBoard(msg.getMsg());
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
			        gui.clearTable();
			        String address = gui.getNameServerAddress();
			        int port = new Integer(gui.getNameServerPort());

			        nameServerConnected = net.connectToNameServer(address, port);

			    } else {
			        gui.clearTable();
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
			    nick = gui.getNick();
			    net.changeNick(nick);
			}
		});

		gui.addRefreshButtonListener(new ActionListener() {
			//IF refresh is in progress, user should be able to cancle the refresh, by clicking again on refresh(??MABY NOT))
			@Override
			public void actionPerformed(ActionEvent e) {
			    if(nameServerConnected) {
	                gui.clearTable();
			        net.refreshServers();
			    }
			}
		});

		gui.addSendButtonListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = gui.getSendTextArea();
				net.SendMessage(msg,nick);
			}
		});

		gui.addTableListener(new MouseListener() {

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseClicked(MouseEvent e) {
                // TODO Auto-generated method stub
                JTable table = (JTable) e.getSource();
                int row = table.getSelectedRow();

                String[] server = gui.getServerAtRow(row);
                gui.setServerField(server[0],server[1]);
            }
        });
	}
}