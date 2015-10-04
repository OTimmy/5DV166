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
import model.network.pdu.types.UJoinPDU;
import model.network.pdu.types.ULeavePDU;
//TODO send the whol god damn pdu trought the listener
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
    	nicks = new ArrayList<String>();
	}

	/**
	 * Initate listeners dedicated for the network.
	 */
	private void initNetworkListener(Network net) {

		net.addServerListener(new controller.Listener<ServerData>() {

			@Override
			public void update(ServerData server) {
			   if(server != null) {
	                gui.addToServerList(server);
			   }
			}

		});

		net.addMessageListener(new controller.Listener<MessageData>() {

			@Override
			public void update(MessageData msg) {
				gui.printOnMessageBoard(msg.getNickname()+":"+msg.getMsg());
			}

		});

		net.addTCPErrorListener(new Listener<String>() {

            @Override
            public void update(String t) {
                System.out.println(t);
                gui.setConnectServerButton("Connect");
                nicks = new ArrayList<String>();
                nicks.clear();
            }
        });

		net.addUDPErrorListener(new Listener<String>() {

            @Override
            public void update(String t) {
                System.out.println(t);
                gui.setConnectNameServerButton("Connect");
                //gui.printErrorBrowser(t);
            }
        });

		net.addNicksListener(new Listener<String>() {
//
            @Override
            public void update(String t) {
                System.out.println("Nick: "+ t);
                gui.addNick(t);
                nicks.add(t);
            }

		});

		net.addUserJoinListener(new Listener<UJoinPDU>() {

			@Override
			public void update(UJoinPDU t) {
				// TODO Auto-generated method stub
				
			}

		});

		net.addUserLeaveListener(new Listener<ULeavePDU>() {

			@Override
			public void update(ULeavePDU t) {
				// TODO Auto-generated method stub
				
			}

		});



		//net.addUserJ
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
			        net.disconnectServer();
			    }

			    if(!serverConnected) {
			        gui.setConnectServerButton("Connect");
			    } else {
			        gui.setConnectServerButton("Disconnect");
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