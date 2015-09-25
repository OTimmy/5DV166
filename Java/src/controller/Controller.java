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
		startNetUDPThread();
		connectServer("",1);
		startNetTCPThread();
		refreshAction();
	}

	/**
	 * Initate network at given addresses and ports.
	 * Start a watch on UDP of the receiving end.
	 */
	private Network initNetwork(String address,int port) {
		Network net = new Network(address,port);

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
                // TODO Auto-generated method stub
                System.out.println("FUUUU");
            }

            @Override
            public void notificationLeave(String nick) {
                // TODO Auto-generated method stub

            }

            @Override
            public void notificationJoin(String nick) {
                // TODO Auto-generated method stub

            }

            @Override
            public void removeAllServers() {
                // TODO Auto-generated method stub

            }
		});

		return net;
	}

    private void startNetUDPThread() {
        Thread t = new Thread() {
             public void run() {
                 net.watchServers();
             }
          };
          t.start();
     }

    private void startNetTCPThread() {
        Thread t = new Thread() {
            public void run() {
                net.watchServer();
            }
        };
        t.start();
    }

	private void refreshAction() {
	    if(!net.requestServers()) {
	        System.out.println("Failed to request servers");
	    }
	}


   private void connectServer(String address,int port) {
	  // if(net.connectToServer(address, port) == true) {
       net.ConnectToServer("scratchy.cs.umu.se", 1234);
	  // }
   }



    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
    }
}
