import view.GUI;
import model.network.Network;
import controller.Controller;


public class main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	  //  MessagePDU pdu = new MessagePDU("Hej på er!");
        //if(args.length > 3) {
            Network net = new Network();
            GUI gui = new GUI();
            Controller controller = new Controller(net,gui);
        //} else {
        	System.out.println("/program   -ar0 -arg1 ...");
        //}


	}
}
