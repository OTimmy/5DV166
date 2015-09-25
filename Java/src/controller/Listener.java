package controller;

import model.network.ServerData;

public interface Listener {
    public void addServer(ServerData t);                 //synchronized

    public void removeAllServers();

    public void updateServer(ServerData t);              //synchronized

    public void reportErr(String error);        //synchronized

    public void notificationLeave(String nick);

    public void notificationJoin(String nick);

}
