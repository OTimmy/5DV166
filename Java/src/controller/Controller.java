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
    public Controller() {

		net = initNetwork("itchy.cs.umu.se",1337);
		initErrorHandler();
		startNetUDPThread();
		refreshAction();
	}

	/**
	 * Initate network at given addresses and ports.
	 * Start a watch on UDP of the receiving end.
	 */
	private Network initNetwork(String address,int port) {
		Network net = new Network(address,port);

		net.addUDPListener(new controller.Listener<ServerData>() {
		       public void update(ServerData t) {
		           System.out.println(t.getName());
		           /*gui component to print*/
		       }
		});

		return net;
	}

    private void startNetUDPThread() {
        Thread t = new Thread() {
             public void run() {
                 net.udpUpdateServers();
             }
          };
          t.start();
     }

	private void refreshAction() {
	    if(!net.udpRequestServers()) {
	        System.out.println("Failed to request servers");
	    }
	}
	
    
   private void connectServer(String address,int port) {
	   net.connectToServer(address, port);
   }
   
   
    private void initErrorHandler() {
        net.addErrListener(new controller.Listener<String>() {
            @Override
            public void update(String t) {
                System.out.println("Error:"+t);
            }
        });
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
    }
}
