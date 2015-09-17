package model.network;

/**
 * <h1> serverData </h1>
 *  Store information of a server.
 *
 */
public class ServerData {

    private String name;
    private String address;
    private int port;
    private int nrClients;

    public ServerData(String name, String address, int port, int nrClients) {
        this.name = name;
        this.address = address;
        this.port = port;
        this.nrClients = nrClients;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public int getNrClients() {
        return nrClients;
    }

}
