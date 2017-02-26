package com.awasthi.amitabh.communication.serialization;

import com.awasthi.amitabh.communication.connection.server.RandomPricePool;
import com.awasthi.amitabh.domain.Instrument;
import com.awasthi.amitabh.domain.Market;
import com.awasthi.amitabh.domain.Side;
import com.awasthi.amitabh.domain.State;
import com.awasthi.amitabh.price.Price;
import com.awasthi.amitabh.price.PriceSnapshot;
import com.awasthi.amitabh.price.impl.PriceImpl;
import com.awasthi.amitabh.price.impl.PriceSnapshotImpl;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;

/**
 * Author: Amitabh Awasthi
 * Description:-
 */
public class SbeWireProtocolTest {

    private final SbeWireProtocol sbeWireProtocol = new SbeWireProtocol();


    @Test
    public void test_that_a_valid_message_is_serialized_and_deserialized_correctly() {
        final ByteBuffer byteBuffer = ByteBuffer.allocate(64*1024);

        for (int i = 0; i < 100; i++) {
            final PriceSnapshot priceSnapshot = RandomPricePool.getRandomPriceFromPool();
            sbeWireProtocol.serialize(byteBuffer, priceSnapshot);
            byteBuffer.flip();
            final PriceSnapshot recoveredSnapshot = sbeWireProtocol.deserialize(byteBuffer);
            byteBuffer.clear();

            assertTrue(priceSnapshot.getInstrument().equals(recoveredSnapshot.getInstrument()));
            assertTrue(priceSnapshot.getMarket().equals(recoveredSnapshot.getMarket()));
            assertTrue(priceSnapshot.getState().equals(recoveredSnapshot.getState()));
            System.out.println(recoveredSnapshot);
        }
    }
}