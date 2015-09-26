import model.network.Network;
import controller.Controller;


public class main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Network net = new Network();
		//GUI gui = new GUI();
		Controller listener = new Controller(net/*, gui*/);
	}
}
