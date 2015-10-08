package controller;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JTable;


import view.GUI;

import model.network.Network;
import model.network.ServerData;
import model.network.pdu.DateUtils;
import model.network.pdu.types.UCNickPDU;
import model.network.pdu.types.UJoinPDU;
import model.network.pdu.types.ULeavePDU;
import model.network.pdu.types.SListPDU;
import model.network.pdu.types.MessagePDU;

/**
 * <h1>Listener.java</h1>
 *  Manage the flow between the gui and the underlying model
 *
 * @author c12ton
 * @version 0.0
 */
//TODOUae hashmap for users, and and let index be there key?
public class Controller {

    private final int KEY_ENTER = 10;

    private Network net;
    private GUI gui;
    private ArrayList<String> nicks;
    private boolean nameServerConnected;
    private String nick;

    public Controller(Network net, GUI gui) {

    	this.net = net;
		this.gui = gui;
    	initNetworkListener();
    	initGUIActionListener();
		nameServerConnected = false;
    	nicks = new ArrayList<String>();
	}

	/**
	 * Initate listeners dedicated for the network.
	 */
	private void initNetworkListener() {

		net.addServerListener(new controller.Listener<SListPDU>() {

			@Override
			public void update(SListPDU t) {

			    for(int i = 0; i < t.getServerNames().size(); i++) {
			        String address = (String) t.getAddresses().get(i);
			        String port    = (String) t.getPorts().get(i);
			        String nrClients = (String) t.getClientNumberss().get(i);
			        String name    = (String) t.getServerNames().get(i);

			        gui.addToServerList(address,port,nrClients,name);
			    }

			}

		});

		net.addMessageListener(new controller.Listener<MessagePDU>() {

			@Override
			public void update(MessagePDU t) {
			    String date = DateUtils.format(t.getDate());

			    gui.printOnMessageBoard(date +"<"+t.getNick()+"> "
			                            +t.getMsg());
			}

		});

		net.addTCPErrorListener(new Listener<String>() {

            @Override
            public void update(String t) {
                gui.clearMessageBoard();
                gui.printOnMessageBoard("Error:"+t);
                gui.setConnectServerButton("Connect");
                clearNicks();
            }
        });

		net.addUDPErrorListener(new Listener<String>() {

            @Override
            public void update(String t) {
                System.out.println(t);
                gui.setConnectNameServerButton("Connect");
                gui.printErrorBrowser(t);
            }
        });

		net.addNicksListener(new Listener<String>() {

			@Override
			public void update(String t) {
			    addNickToList(t);
			}

		});

		net.addUJoinListener(new Listener<UJoinPDU>() {

			@Override
			public void update(UJoinPDU t) {
			    String date = DateUtils.format(t.getDate());
			    gui.printOnMessageBoard(date+" "+t.getNick() + " has joined");

			    addNickToList(t.getNick());
			}

		});

		net.addULeaveListener(new Listener<ULeavePDU>() {

			@Override
			public void update(ULeavePDU t) {
			    String date = DateUtils.format(t.getDate());
			    gui.printOnMessageBoard(date+" "+t.getNick()+" has left");
			    removeNickFromList(t.getNick());
			}

		});

		net.addUCNickListener(new Listener<UCNickPDU>() {

            @Override
            public void update(UCNickPDU t) {
                String date = DateUtils.format(t.getDate());
                gui.printOnMessageBoard(date + " "+ t.getOldNick()
                                       +" has changed to " + t.getNewNick() );

                changeNickFromList(t.getOldNick(), t.getNewNick());
            }

		});
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
			    System.out.println("connection: "+net.isConnectedToServer());
			    if(!net.isConnectedToServer()) {
			        gui.clearMessageBoard();
			        String address = gui.getServerAddress();
			        int port = new Integer(gui.getServerPort());
			        String nick = gui.getNick();

			        net.ConnectToServer(address, port, nick);

			    } else {
			        net.disconnectServer();
			        clearNicks();
			    }

			    if(!net.isConnectedToServer()) {
			        gui.setConnectServerButton("Connect");
			        clearNicks();
			    } else {
			        gui.setConnectServerButton("Disconnect");
			    }

			}

		});

		gui.addOkButtonListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			    nick = gui.getNick();
			    System.out.println("Nick: "+nick);
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

                JTable table = (JTable) e.getSource();
                int row = table.getSelectedRow();

                String[] server = gui.getServerAtRow(row);
                gui.setServerField(server[0],server[1]);
            }
        });

		gui.addSendTextAreaListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {

                if(KEY_ENTER == e.getKeyCode()) {
                    e.consume();
                    String msg = gui.getSendTextArea();
                    net.SendMessage(msg,nick);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}

		});
	}

	private void addNickToList(String nickName) {
	    synchronized(nicks) {
	       // if(!nicks.contains(nick)) {
	            nicks.add(nickName);
	            gui.addNick(nickName);
	       // }
	    }
	}

	private void changeNickFromList(String oldNickName, String newNickName) {
	    synchronized(nicks) {
	        //local
	        int index = nicks.indexOf(oldNickName);
	        if(index > -1) {
		        nicks.set(index, newNickName);

		        //gui
		        gui.clearNicks();
		        for(String nick:nicks) {
		            gui.addNick(nick);
		        }
	        }
	    }
	}

	private void removeNickFromList(String nickName) {
	    synchronized(nicks) {
	        nicks.remove(nickName);
	        gui.clearNicks();
	        for(String nick: nicks) {
	            gui.addNick(nick);
	        }
	    }
	}

	private void clearNicks() {
	    synchronized(nicks) {
	        nicks.clear();
	        gui.clearNicks();
	    }
	}
}