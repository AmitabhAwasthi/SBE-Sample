package com.awasthi.amitabh.communication.connection.client;

import com.awasthi.amitabh.communication.connection.Constants;
import com.awasthi.amitabh.communication.serialization.SbeWireProtocol;
import com.awasthi.amitabh.price.PriceSnapshot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Author: Amitabh Awasthi
 * Description:-
 */
public class PriceReceiver extends Thread {
    private static final Logger LOG = LogManager.getLogger(PriceReceiver.class);

    private final BlockingQueue<PriceSnapshot> priceQueue = new LinkedBlockingQueue<>();
    private final SbeWireProtocol sbeWireProtocol = new SbeWireProtocol();

    private final ByteBuffer receiveBuffer;
    private final DatagramChannel multicastReceiver;

    public PriceReceiver(ByteBuffer receiver_buf, DatagramChannel multicastReceiver) {
        super("Channel-Price-Receiver");
        this.receiveBuffer = receiver_buf;
        this.multicastReceiver = multicastReceiver;
    }

    public void start() {
        final AsyncPriceConsumer asyncPriceConsumer = new AsyncPriceConsumer(priceQueue);
        asyncPriceConsumer.start();
        super.start();
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            receiveBuffer.clear();
            try {
                multicastReceiver.receive(receiveBuffer);
                receiveBuffer.flip();
                final PriceSnapshot priceSnapshot = sbeWireProtocol.deserialize(receiveBuffer);
                LOG.info("Received data while listening on group [Data=" + priceSnapshot + "]");
                priceQueue.put(priceSnapshot);
            } catch (Exception e) {
                LOG.warn("Exception encountered during the reading/processing of incoming data", e);
            }
        }
    }


    public static void main(String[] args) throws Exception {
        final NetworkInterface networkInterface = NetworkInterface.getByName(Constants.MULTICAST_IF_1);
        final InetAddress groupAddress = InetAddress.getByName(Constants.MULTICAST_GROUP_ADDRESS);

        final DatagramChannel multicastChannel = DatagramChannel.open(StandardProtocolFamily.INET)
                .setOption(StandardSocketOptions.SO_REUSEADDR, true)
                .bind(new InetSocketAddress(Constants.MULTICAST_PORT))
                .setOption(StandardSocketOptions.IP_MULTICAST_IF, networkInterface);
        final MembershipKey key = multicastChannel.join(groupAddress, networkInterface);
        LOG.info("Joined multicast group at :" + Constants.MULTICAST_GROUP_ADDRESS);
        LOG.info("Membership key for joined group " + key);

        final ByteBuffer receiveBuf = ByteBuffer.allocateDirect(Constants.BUFFER_SIZE);

        final PriceReceiver priceReceiver = new PriceReceiver(receiveBuf, multicastChannel);
        priceReceiver.start();

    }

}
