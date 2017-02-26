package com.awasthi.amitabh.communication.serialization;

import com.awasthi.amitabh.communication.connection.Constants;
import com.awasthi.amitabh.price.Price;
import com.awasthi.amitabh.price.PriceSnapshot;
import com.awasthi.amitabh.price.impl.PriceImpl;
import com.awasthi.amitabh.price.impl.PriceSnapshotImpl;
import com.awasthi.amitabh.sbe.*;
import org.agrona.concurrent.AtomicBuffer;
import org.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;

/**
 * Author: Amitabh Awasthi
 * Description:-
 */
public final class SbeWireProtocol {

    private final MessageHeaderEncoder MESSAGE_HEADER_ENCODER = new MessageHeaderEncoder();
    private final MessageHeaderDecoder MESSAGE_HEADER_DECODER = new MessageHeaderDecoder();

    private final PriceSnapshotEncoder PRICE_SNAPSHOT_ENCODER = new PriceSnapshotEncoder();
    private final PriceSnapshotDecoder PRICE_SNAPSHOT_DECODER = new PriceSnapshotDecoder();

    private final ByteBuffer INT_READ_BUF = ByteBuffer.allocateDirect(Constants.BUFFER_SIZE);
    private final ByteBuffer INT_WRITE_BUF = ByteBuffer.allocateDirect(Constants.BUFFER_SIZE);

    private final AtomicBuffer READ_BUFFER = new UnsafeBuffer(INT_READ_BUF);
    private final AtomicBuffer WRITE_BUFFER = new UnsafeBuffer(INT_WRITE_BUF);


    /**
     * Call flip on this byteBuffer after this method returns
     *
     * @param byteBuffer
     * @param o
     */
    public void serialize(ByteBuffer byteBuffer, PriceSnapshot o) {
        try {
            int bufferOffset = 0;
            int encodingLength = bufferOffset;

            MESSAGE_HEADER_ENCODER.wrap(WRITE_BUFFER, bufferOffset)
                    .blockLength(PRICE_SNAPSHOT_ENCODER.sbeBlockLength())
                    .templateId(PRICE_SNAPSHOT_ENCODER.sbeTemplateId())
                    .schemaId(PRICE_SNAPSHOT_ENCODER.sbeSchemaId())
                    .version(PRICE_SNAPSHOT_ENCODER.sbeSchemaVersion());

            bufferOffset += MESSAGE_HEADER_ENCODER.encodedLength();
            encodingLength += MESSAGE_HEADER_ENCODER.encodedLength();

            PRICE_SNAPSHOT_ENCODER.wrap(WRITE_BUFFER, bufferOffset)
                    .instrument(Instrument.valueOf(o.getInstrument().name()))
                    .market(Market.valueOf(o.getMarket().name()))
                    .state(State.valueOf(o.getState().name()));

            final PriceSnapshotEncoder.PricesEncoder pricesEncoder = PRICE_SNAPSHOT_ENCODER.pricesCount(o.getPrices().length);

            for (Price price : o.getPrices()) {
                pricesEncoder.next().
                        price()
                        .side(Side.valueOf(price.getSide().name()))
                        .amount(price.getAmount())
                        .price(price.getPrice());

            }
            encodingLength += PRICE_SNAPSHOT_ENCODER.encodedLength();
            byteBuffer.put(INT_WRITE_BUF);
        } finally {
            INT_WRITE_BUF.clear();
        }
    }

    /**
     * Call clear on this byteBuffer after this method returns
     *
     * @param byteBuffer
     * @return
     */
    public PriceSnapshot deserialize(ByteBuffer byteBuffer) {
        try {
            READ_BUFFER.putBytes(0, byteBuffer, 0, byteBuffer.limit());
            int bufferOffset = 0;
            MESSAGE_HEADER_DECODER.wrap(READ_BUFFER, bufferOffset);

        /*
         * Notice that here we can easily fetch header fields in any order
         * But we should avoid in-order access of fields as noted in different SBE articles
         */
            final int blockLength = MESSAGE_HEADER_DECODER.blockLength();
            final int templateId = MESSAGE_HEADER_DECODER.templateId();
            final int schemaId = MESSAGE_HEADER_DECODER.schemaId();
            final int version = MESSAGE_HEADER_DECODER.version();

            bufferOffset += MESSAGE_HEADER_DECODER.encodedLength();

            PRICE_SNAPSHOT_DECODER.wrap(READ_BUFFER, bufferOffset, blockLength, version);
            final com.awasthi.amitabh.domain.Instrument instrument =
                    com.awasthi.amitabh.domain.Instrument.valueOf(PRICE_SNAPSHOT_DECODER.instrument().name());
            final com.awasthi.amitabh.domain.Market market = com.awasthi.amitabh.domain.Market.valueOf(PRICE_SNAPSHOT_DECODER.market().name());

            final com.awasthi.amitabh.domain.State state = com.awasthi.amitabh.domain.State.valueOf(PRICE_SNAPSHOT_DECODER.state().name());

            final PriceSnapshotDecoder.PricesDecoder pricesDecoder = PRICE_SNAPSHOT_DECODER.prices();
            final int pricesCount = pricesDecoder.count();

            final Price[] prices = new Price[pricesCount];

            for (int i = 0; i < pricesCount; i++) {
                final PriceDecoder priceDecoder = pricesDecoder.next().price();
                final com.awasthi.amitabh.domain.Side side = com.awasthi.amitabh.domain.Side.valueOf(priceDecoder.side().name());
                final double amount = priceDecoder.amount();
                final double price = priceDecoder.price();
                prices[i] = new PriceImpl(side, amount, price);
            }
            return new PriceSnapshotImpl(instrument, market, state, prices);
        } finally {
            INT_READ_BUF.clear();
        }
    }
}
