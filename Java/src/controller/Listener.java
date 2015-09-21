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

	//Thread will start this?? YES
	private void refreshAction(/*gui component to print*/) {
	    int cntNrOfServers = 0;
	    int nrOfServers = net.getNrOfServers();

	    while(cntNrOfServers <= nrOfServers) {
	        ArrayList<ServerData> servers = net.getServers();
	        cntNrOfServers = servers.size();

	        for(ServerData server:servers) {
	            //Print server stuff to gui.
	        }
	    }
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
