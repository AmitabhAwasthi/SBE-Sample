package com.awasthi.amitabh.communication.connection.server;

import com.awasthi.amitabh.communication.connection.Constants;

import java.io.IOException;

/**
 * Author: Amitabh Awasthi
 * Description:-
 */
public class PriceBroadcaster {

    public static void main(String[] args) throws IOException {
        final ServerThread serverThread = new ServerThread(Constants.MULTICAST_PORT);
        serverThread.start();
    }
}
