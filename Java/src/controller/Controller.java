package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import model.network.Network;
import model.network.ServerData;

/**
 * <h1>Listener.java</h1>
 * <p> Manage the flow between the gui and the underlying model</p>
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
	 * Initate network at given addresses and ports.
	 * Start a watch on UDP of the receiving end.
	 */
	private Network initNetworkListener(Network net) {

		net.addListener(new controller.Listener() {

            @Override
            public void addServer(ServerData t) {
                System.out.println("Server: "+t.getName());               
            }

            @Override
            public void updateServer(ServerData t) {
                System.out.println("Update server");
            }

            @Override
            public void reportErr(String error) {
                System.out.println("FUUUU");
            }

            @Override
            public void notificationLeave(String nick) {
            }

            @Override
            public void notificationJoin(String nick) {
            }

            @Override
            public void clearServers() {
            }
		});

		return net;
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
