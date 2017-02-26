package com.awasthi.amitabh.communication.connection.server;

import com.awasthi.amitabh.communication.connection.Constants;
import com.awasthi.amitabh.communication.serialization.SbeWireProtocol;
import com.awasthi.amitabh.price.PriceSnapshot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * Author: Amitabh Awasthi
 * Description:-
 */
public class ServerThread extends Thread {
    private static final Logger LOG = LogManager.getLogger("Price-Multicast-Broadcaster");

    private final InetSocketAddress groupAddress;
    private final DatagramChannel multicastChannel;
    private final ByteBuffer senderBuffer = ByteBuffer.allocateDirect(Constants.BUFFER_SIZE);

    private final SbeWireProtocol sbeWireProtocol = new SbeWireProtocol();

    private boolean isStopped = false;

    public ServerThread(int port) throws IOException {
        this("Pricing-Publisher-Thread", port);
    }

    public ServerThread(String quoteServer, int port) throws IOException {
        super(quoteServer);
        this.groupAddress = new InetSocketAddress(Constants.MULTICAST_GROUP_ADDRESS, port); // group to send data to
        // server does NOT need to bind to the multicast group unless it also wants to listen to it
        final NetworkInterface ni = NetworkInterface.getByName(Constants.MULTICAST_IF_1); // interface used by my IP --> will be the interface used to listen to the group
        this.multicastChannel = DatagramChannel.open(StandardProtocolFamily.INET)
                .setOption(StandardSocketOptions.SO_REUSEADDR, true)
                .bind(new InetSocketAddress(port))
                .setOption(StandardSocketOptions.IP_MULTICAST_IF, ni);
        this.multicastChannel.configureBlocking(false); // join to listen group on that interface --> do note that any datagram sent directly to my IP (used to fetch NetworkInterface) will also be received
        // if you want to not receive any other unicast / udp message, then use mutlicastChannel.join(group, ni, address) method instead
    }

    @Override
    public void run() {
        try {
            while (!isStopped) {
                this.senderBuffer.clear();
                final PriceSnapshot priceSnapshot = RandomPricePool.getRandomPriceFromPool();
                LOG.info("Sending message via multicast [data=" + priceSnapshot + "]");
                sbeWireProtocol.serialize(senderBuffer, priceSnapshot);

                this.senderBuffer.flip();
                multicastChannel.send(senderBuffer, groupAddress);
                Thread.sleep(1000l);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
