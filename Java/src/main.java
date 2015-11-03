import view.GUI;
import network.Network;
import controller.Controller;

public class main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
        String nameServeraddress;
        String nameServerport;
        String serverAddress;
        String serverPort;
        
        GUI gui;
        if(args.length == 4) {
            nameServeraddress = args[0];
            nameServerport    = args[1];
            serverAddress     = args[2];
            serverPort        = args[3];
            
            //Controller controller = new controller()
            gui = new GUI(nameServeraddress,nameServerport,
                              serverAddress,serverPort);
        } else {
            System.out.println("Client arg: nameAddress namePort address port");
            System.out.println("Default name server: itchy.cs.umu.se, 1337");
           
            gui = new GUI("itchy.cs.umu.se","1337");
        }

        Network net = new Network();
                 
        new Controller(net,gui);
	}
}
