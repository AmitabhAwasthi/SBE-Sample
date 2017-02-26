package com.awasthi.amitabh.communication.connection.client;

import com.awasthi.amitabh.price.PriceSnapshot;
import com.awasthi.amitabh.price.cache.QuoteBook;
import com.awasthi.amitabh.price.cache.impl.QuoteBookImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.BlockingQueue;

/**
 * Author: Amitabh Awasthi
 * Description:-
 */
public class AsyncPriceConsumer extends Thread {
    private static final Logger LOG = LogManager.getLogger(AsyncPriceConsumer.class);
    private final QuoteBook quoteBook = new QuoteBookImpl();

    private final BlockingQueue<PriceSnapshot> priceQueue;

    public AsyncPriceConsumer(BlockingQueue<PriceSnapshot> priceQueue) {
        super("Incoming-price-consumer");
        this.priceQueue = priceQueue;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {

            try {
                final PriceSnapshot priceSnapshot = priceQueue.take();
                LOG.info("Consuming price [" + priceSnapshot + "]");
                quoteBook.applyUpdate(priceSnapshot);
            } catch (InterruptedException e) {
                LOG.warn("Thread interrupted while waiting");
            }
        }
    }
}
