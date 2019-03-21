package org.spectrumauctions.sats.mechanism;

import ch.uzh.ifi.ce.domain.Bid;
import ch.uzh.ifi.ce.domain.BundleBid;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.assertj.core.util.Lists;
import org.mockito.Mockito;
import org.spectrumauctions.sats.core.bidlang.BiddingLanguage;
import org.spectrumauctions.sats.core.model.*;
import org.spectrumauctions.sats.core.util.random.RNGSupplier;

import java.math.BigDecimal;
import java.util.*;
import java.util.List;

public class MockWorld extends World {

    private int numberOfGoods;
    private int numberOfBands;
    private int numberOfBidders;

    private static BidderSetup setup = Mockito.mock(BidderSetup.class);

    private static MockWorld INSTANCE = null;

    public static MockWorld getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MockWorld();
        }
        return INSTANCE;
    }

    public void reset() {
        numberOfGoods = 0;
        numberOfBands = 0;
        numberOfBidders = 0;
    }

    private MockWorld() {
        super("mockWorld");
        Mockito.when(setup.getSetupName()).thenReturn("Mock");
        reset();
    }

    @Override
    public int getNumberOfGoods() {
        return numberOfGoods;
    }

    public MockGood createNewGood() {
        return new MockGood(numberOfGoods++, this.getId());
    }

    public MockBand createNewBand(List<MockGood> goods) {
        numberOfBands++;
        return new MockBand(goods);
    }

    public MockBidder createNewBidder() {
        return new MockBidder(0, numberOfBidders++, this.getId());
    }

    @Override
    public Set<? extends SATSGood> getLicenses() {
        throw new UnsupportedOperationException("Not implemented for MockWorld");
    }

    @Override
    public Collection<? extends SATSBidder<?>> restorePopulation(long populationId) {
        throw new UnsupportedOperationException("Not implemented for MockWorld");
    }

    @Override
    public void refreshFieldBackReferences() {
    }

    public class MockGood extends SATSGood {
        protected MockGood(long id, long worldId) {
            super(id, worldId);
        }

        @Override
        public World getWorld() {
            return MockWorld.this;
        }
    }

    public class MockBand extends SATSGenericGood {
        protected MockBand(List<MockGood> goods) {
            super(id, MockWorld.getInstance().getId(), Lists.newArrayList(goods));
        }

        @Override
        public World getWorld() {
            return MockWorld.this;
        }

    }


    public class MockBidder extends SATSBidder<MockGood> {

        private long idCount = 0;

        Set<BundleBid> bids = new HashSet<>();
        Set<BundleBid> genericBids = new HashSet<>();

        public void addBid(Bundle<MockGood> bundle, double value) {
            bids.add(new BundleBid(BigDecimal.valueOf(value), Sets.newHashSet(bundle), getId() + "_" + idCount++));
        }
        public void addGenericBid(Map<MockBand, Integer> quantities, double value) {
            BundleBid bid = new BundleBid(BigDecimal.valueOf(value), Maps.newHashMap(quantities), getId() + "_generic_" + idCount++);
            genericBids.add(bid);
        }

        public Bid getBids() {
            return new Bid(bids);
        }

        public Bid getGenericBids() {
            return new Bid(genericBids);
        }

        protected MockBidder(long population, long id, long worldId) {
            super(setup, population, id, worldId);
        }

        @Override
        public BigDecimal calculateValue(Bundle<MockGood> bundle) {
            Optional<BundleBid> value = bids.stream().filter(bid -> bid.getBundle().equals(bundle)).findFirst();
            if (value.isPresent()) return value.get().getAmount();
            else return BigDecimal.ZERO;
        }

        @Override
        public <T extends BiddingLanguage> T getValueFunction(Class<T> type, RNGSupplier rngSupplier) {
            throw new UnsupportedOperationException("Not supported in mock");
        }

        @Override
        public World getWorld() {
            return MockWorld.this;
        }

        @Override
        public void refreshReference(World world) {
            throw new UnsupportedOperationException("Not supported in mock");
        }

        @Override
        public SATSBidder<MockGood> drawSimilarBidder(RNGSupplier rngSupplier) {
            throw new UnsupportedOperationException("Not supported in mock");
        }

    }


}
