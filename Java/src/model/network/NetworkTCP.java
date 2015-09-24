package model.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import controller.Listener;

public class NetworkTCP {

    private Socket socket;
    private Listener<String> errListener;
    private OutputStream out;
    private InputStream in;


    public void connect(String address,int port) {
        try {
            socket = new Socket(address,port);
            out = socket.getOutputStream();
            in = socket.getInputStream();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            errListener.update(e.getMessage());
        }
    }



    public void sendPDU() {

    }

    public void disconnect() {
        try {
            //inStream.close();
            //outStream.close();
            socket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            errListener.update(e.getMessage());
        }
    }



    public void addListener(Listener<String> errListener) {
        this.errListener = errListener;
    }






    /*Connect to tcp servers*/

    /*Download pdu from tcp*/

    /*getPDU*/


}
