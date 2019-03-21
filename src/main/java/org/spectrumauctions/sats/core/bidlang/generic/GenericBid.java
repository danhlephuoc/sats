package org.spectrumauctions.sats.core.bidlang.generic;


import org.spectrumauctions.sats.core.model.SATSBidder;
import org.spectrumauctions.sats.core.model.SATSGood;

import java.util.ArrayList;
import java.util.List;

public class GenericBid<S extends GenericDefinition<T>, T extends SATSGood> {

    private final SATSBidder<T> bidder;
    private List<GenericValue<S, T>> values;

    public GenericBid(SATSBidder<T> bidder, List<GenericValue<S, T>> values) {
        this.bidder = bidder;
        this.values = values;
    }

    public SATSBidder<T> getBidder() {
        return bidder;
    }

    /**
     * @return Returns an unmodifiable list of all (atomic) XORQ values in this bid
     */
    public List<GenericValue<S, T>> getValues() {
        return values;
    }

    public void addValue(GenericValue<S, T> value) {
        values.add(value);
    }

    public void removeValue(GenericValue<S, T> value) {
        values.remove(value);
    }

    public GenericBid<S, T> copyOf() {
        ArrayList<GenericValue<S, T>> newValues = new ArrayList<>(this.values);
        return new GenericBid<>(this.bidder, newValues);
    }
}
