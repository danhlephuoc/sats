package org.spectrumauctions.sats.mechanism.vcg;

import ch.uzh.ifi.ce.domain.*;
import ch.uzh.ifi.ce.mechanisms.vcg.ORVCGAuction;
import ch.uzh.ifi.ce.mechanisms.vcg.XORVCGAuction;
import com.google.common.collect.Sets;
import org.assertj.core.data.Offset;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.spectrumauctions.sats.core.bidlang.generic.GenericBid;
import org.spectrumauctions.sats.core.bidlang.generic.GenericDefinition;
import org.spectrumauctions.sats.core.bidlang.xor.XORBid;
import org.spectrumauctions.sats.core.model.Bundle;
import org.spectrumauctions.sats.mechanism.MockWorld.MockGood;
import org.spectrumauctions.sats.mechanism.MockWorld.MockBand;
import org.spectrumauctions.sats.mechanism.MockWorld.MockBidder;
import org.spectrumauctions.sats.mechanism.MockWorld;
import org.spectrumauctions.sats.mechanism.domain.Payment;
import org.spectrumauctions.sats.mechanism.domain.mechanisms.AuctionMechanism;
import org.spectrumauctions.sats.opt.domain.WinnerDeterminator;
import org.spectrumauctions.sats.opt.xorq.XORQWinnerDetermination;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.assertj.core.api.Assertions.*;


public class VCGWithMockWorldTest {

    private MockGood A;
    private MockGood B;
    private MockGood C;
    private MockGood D;
    private MockGood E;

    private MockBand B0;
    private MockBand B1;
    private MockBand B2;
    private Map<Integer, MockBidder> bidders;

    @Before
    public void setUp() {
        A = MockWorld.getInstance().createNewGood();
        B = MockWorld.getInstance().createNewGood();
        B0 = MockWorld.getInstance().createNewBand(Lists.newArrayList(A, B));
        C = MockWorld.getInstance().createNewGood();
        D = MockWorld.getInstance().createNewGood();
        B1 = MockWorld.getInstance().createNewBand(Lists.newArrayList(C, D));
        E = MockWorld.getInstance().createNewGood();
        B2 = MockWorld.getInstance().createNewBand(Lists.newArrayList(E));
        bidders = new HashMap<>();
        MockWorld.getInstance().reset();
    }


    private MockWorld.MockBidder bidder(int id) {
        MockWorld.MockBidder fromMap = bidders.get(id);
        if (fromMap == null) {
            MockWorld.MockBidder bidder = MockWorld.getInstance().createNewBidder();
            bidders.put((int) bidder.getLongId(), bidder);
            return bidder(id);
        }
        return fromMap;
    }

    @Test
    public void testXORVCG() {
        bidder(1).addBid(new Bundle<>(A), 2);
        bidder(2).addBid(new Bundle<>(A, B, D), 3);
        bidder(3).addBid(new Bundle<>(B, C), 2);
        bidder(4).addBid(new Bundle<>(C, D), 1);

        Bids bids = new Bids();
        bids.setBid(bidder(1), bidder(1).getBids());
        bids.setBid(bidder(2), bidder(2).getBids());
        bids.setBid(bidder(3), bidder(3).getBids());
        bids.setBid(bidder(4), bidder(4).getBids());

        AuctionInstance auctionInstance = new AuctionInstance(bids);
        ch.uzh.ifi.ce.mechanisms.AuctionMechanism am = new XORVCGAuction(auctionInstance);
        ch.uzh.ifi.ce.domain.Payment payment = am.getPayment();
        assertThat(am.getAllocation().getTotalAllocationValue().doubleValue()).isEqualTo(4);
        assertThat(payment.paymentOf(bidder(1)).getAmount().doubleValue()).isEqualTo(1);
        assertThat(payment.paymentOf(bidder(2)).getAmount()).isZero();
        assertThat(payment.paymentOf(bidder(3)).getAmount().doubleValue()).isEqualTo(1);
        assertThat(payment.paymentOf(bidder(4)).getAmount()).isZero();

    }

    @Test
    public void testXORQVCG() {
        Map<MockBand, Integer> bid1 = new HashMap<>();
        bid1.put(B0, 1);
        bid1.put(B1, 1);
        bidder(1).addGenericBid(bid1, 2);

        Map<MockBand, Integer> bid2 = new HashMap<>();
        bid2.put(B0, 1);
        bid2.put(B1, 1);
        bid2.put(B2, 1);
        bidder(2).addGenericBid(bid2, 3);

        Map<MockBand, Integer> bid3 = new HashMap<>();
        bid3.put(B1, 1);
        bidder(3).addGenericBid(bid3, 1.5);

        Map<MockBand, Integer> bid4 = new HashMap<>();
        bid4.put(B0, 2);
        bid4.put(B1, 2);
        bid4.put(B2, 1);
        bidder(4).addGenericBid(bid4, 4);

        Bids bids = new Bids();
        bids.setBid(bidder(1), bidder(1).getGenericBids());
        bids.setBid(bidder(2), bidder(2).getGenericBids());
        bids.setBid(bidder(3), bidder(3).getGenericBids());
        bids.setBid(bidder(4), bidder(4).getGenericBids());

        AuctionInstance auctionInstance = new AuctionInstance(bids);
        ch.uzh.ifi.ce.mechanisms.AuctionMechanism am = new XORVCGAuction(auctionInstance);
        ch.uzh.ifi.ce.domain.Payment payment = am.getPayment();
        assertThat(payment.getTotalPayments().doubleValue()).isEqualTo(3.5);
        assertThat(payment.paymentOf(bidder(1)).getAmount().doubleValue()).isEqualTo(1.5);
        assertThat(payment.paymentOf(bidder(2)).getAmount().doubleValue()).isEqualTo(2);
        assertThat(payment.paymentOf(bidder(3)).getAmount()).isZero();
        assertThat(payment.paymentOf(bidder(4)).getAmount()).isZero();

    }

}
