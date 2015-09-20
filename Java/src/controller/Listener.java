package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import model.network.Network;

/**
 * <h1>Listener.java</h1>
 * <p> Manage the flow between the gui and the underlying model</p>
 * 
 * @author c12ton
 * @version 2015.09.06
 */
public class Listener implements ActionListener{

	public Listener() {

		initNetwork();
		ErrorListener errListener = new ErrorListener();
		errListener.Startwatch();

	}
	
	private void initNetwork() {
		Network net = new Network("itchy.cs.umu.se",1337);
		
		net.startWatchUDPThread();
		net.getServerData();
	}
	
	//Thread will start this??
	private void refreshAction() {
		//while(nrOfServers < 
		//while( not new data && !dissrupt??)
		 // data = getServerData;
		//while(data == null) {    
			//data = net.getServiceData();
		//}
		//return data;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		// TODO Auto-generated method stub	
	}
	

	/**
	 *
	 */
	public class ErrorListener extends ErrorManager {

		//implements runnable
		 //startWatch();

		private void Startwatch() {
			while(true) {
				for(String msg:getErrorList()) {
					System.out.println("msg:" +msg);
				}
			}

			//gui.printError(message)
		}
		//thead in sleep till notify singla is sent
	}
}
