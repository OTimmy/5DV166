package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import model.network.Network;
import model.network.ServerData;

/**
 * <h1>Listener.java</h1>
 * <p> Manage the flow between the gui and the underlying model</p>
 *
 * @author c12ton
 * @version 0.0
 */
public class Listener implements ActionListener{

    private Network net;
    private ErrorListener errListener;
	public Listener() {

	    errListener = new ErrorListener();
		net = initNetwork("itchy.cs.umu.se",1337);

		refreshAction();
		refreshAction();

	}

	/**
	 * Initate network at given addresses and ports.
	 * Start a watch on UDP of the receiving end.
	 */
	private Network initNetwork(String address,int port) {
		Network net = new Network(address,port);
		net.startWatchUDPThread();

		return net;
	}


	private void refreshAction(/*gui component to print*/) {
	    ArrayList<ServerData> servers = net.getServers();

	    int nrOfServers = net.getNrOfServers();
	    int cntNrOfServers = 0;
	    
	    System.out.println("Nr of server:"+ nrOfServers);
	    
	    while(cntNrOfServers < nrOfServers) {
	    	
	        for(ServerData server:servers) {
	            System.out.println("Name:"+ server.getName());
	            //Print server stuff to gui.
	        }
	        servers = net.getServers();
	        cntNrOfServers = servers.size();

	    }
	    System.out.println("refreshAction done!!");
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		// TODO Auto-generated method stub
	}


	/**
	 *
	 */
	public class ErrorListener extends ErrorManager {

		private void Startwatch() {
			while(true) {
				for(String msg:getErrorList()) {
					System.out.println("msg:" +msg);
				}
			}
		}
	}
}
