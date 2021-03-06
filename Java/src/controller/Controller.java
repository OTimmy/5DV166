package controller;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import view.GUI;

import network.Network;
import network.pdu.DateUtils;
import network.pdu.types.UCNickPDU;
import network.pdu.types.UJoinPDU;
import network.pdu.types.ULeavePDU;
import network.pdu.types.SListPDU;
import network.pdu.types.MessagePDU;
//Refresh button should open browser.
//
/**
 * <h1>Listener.java</h1>
 *  Manage the flow between the gui and the underlying model
 *
 * @author c12ton
 * @version 0.0
 */

public class Controller {

    private final int KEY_ENTER = 10;
    private final int TAB_BROWS = 0;
    private final int TAB_CHAT  = 1;

    private final String ERROR_TCP_CONNECT = "Couldn't connect to server";

    private Network net;
    private GUI gui;
    private ArrayList<String> nicks;
    private String nick;

    public Controller(Network net, GUI gui) {

    	this.net = net;
		this.gui = gui;
    	initNetworkListener();
    	initGUIActionListener();
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

			        gui.addToTable(address,port,nrClients,name);
			    }

			}

		});

		net.addMessageListener(new controller.Listener<MessagePDU>() {

			@Override
			public void update(MessagePDU t) {
			    String date = DateUtils.format(t.getDate());
			    String nick;
			    if(t.getNick().length() == 0) {
			        nick = " **Server** ";
			    } else {
			        nick = " <" + t.getNick() +"> ";
			    }

                gui.printOnMessageBoard(date +nick
                        +t.getMsg());

			}
		});


		net.addTCPErrorListener(new Listener<String>() {

            @Override
            public void update(String t) {

                if(t.compareTo(ERROR_TCP_CONNECT) == 0) {
                    gui.printErrorBrowser(ERROR_TCP_CONNECT);
                } else {
                    gui.printOnMessageBoard("Error:"+t);
                }

                net.disconnectServer();
                gui.setConnectServerButton("Connect");
                clearNicks();
            }
        });

		net.addUDPErrorListener(new Listener<String>() {

            @Override
            public void update(String t) {
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

	/**
	 * Adding action listeners for the gui buttons.
	 */
	private void initGUIActionListener() {

	    gui.addRefreshButtonListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            gui.clearTable();

	            if(gui.getNameServerAddress().length() != 0
	                    && gui.getNameServerPort().matches("[0-9]+")) {

	                String address = gui.getNameServerAddress();

	                int port = new Integer(gui.getNameServerPort());

	                net.refreshServers(address,port);
	                gui.openTab(TAB_BROWS);
	            }
	        }
        });


        gui.addConnectSeverButtonListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                gui.printErrorBrowser(""); // reset
                if(!net.isConnectedToServer()) {

                    String address = gui.getServerAddress();

                    if(address.length() != 0
                            && gui.getServerPort().matches("[0-9]+") ) {
                        gui.clearMessageBoard();
                        int port = new Integer(gui.getServerPort());
                        String nick = gui.getNick();
                        net.ConnectToServer(address, port, nick);
                    }

                } else {
                    net.disconnectServer();
                    clearNicks();
                }


                if(!net.isConnectedToServer()) {
                    gui.setConnectServerButton("Connect");
                    clearNicks();
                    gui.setChatTabTitle("Chat");
                    gui.openTab(TAB_BROWS);
                } else {
                    gui.setConnectServerButton("Disconnect");
                    gui.setChatTabTitle(gui.getServerTopic());
                    gui.openTab(TAB_CHAT);
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


        gui.addSendButtonListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = gui.getSendTextArea();
                net.SendMessage(msg,nick);
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
                nicks.add(nickName);
                gui.addNick(nickName);
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