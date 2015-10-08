import view.GUI;
import network.Network;
import controller.Controller;

public class main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
        //if(args.length > 3) {
            Network net = new Network();
            GUI gui = new GUI();
            Controller controller = new Controller(net,gui);
        //} else {
        	System.out.println("/program   -ar0 -arg1 ...");
        //}


	}
}
