/**
 * Copyright by Michael Weiss, weiss.michael@gmx.ch
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.spectrumauctions.sats.opt.domain;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spectrumauctions.sats.core.bidlang.generic.GenericDefinition;
import org.spectrumauctions.sats.core.bidlang.generic.GenericValue;
import org.spectrumauctions.sats.core.model.SATSBidder;
import org.spectrumauctions.sats.core.model.Bundle;
import org.spectrumauctions.sats.core.model.SATSGood;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Michael Weiss
 *
 */
public class GenericSATSAllocation<T extends GenericDefinition<S>, S extends SATSGood> implements SATSAllocation<S> {

    private static final Logger logger = LogManager.getLogger(GenericSATSAllocation.class);

    protected final ImmutableMap<SATSBidder<S>, GenericValue<T, S>> values;

    public GenericSATSAllocation(Builder<T, S> builder) {
        this.values = ImmutableMap.copyOf(builder.storedValues);
    }

    public ImmutableMap<T, Integer> getQuantities(SATSBidder<S> bidder) {
        return values.get(bidder).getQuantities();
    }

    /* (non-Javadoc)
     * @see SATSAllocation#getAllocatedItems(org.spectrumauctions.sats.core.model.SATSBidder)
     */
    @Override
    public Bundle<S> getAllocation(SATSBidder<S> bidder) {
        return values.get(bidder).anyConsistentBundle();
    }

    public GenericValue<T, S> getGenericAllocation(SATSBidder<S> bidder) {
        return values.get(bidder);
    }

    /* (non-Javadoc)
     * @see SATSAllocation#getWinners()
     */
    @Override
    public Collection<SATSBidder<S>> getWinners() {
        return values.keySet();
    }

    @Override
    public BigDecimal getTotalValue() {
        BigDecimal sum = BigDecimal.ZERO;
        for (GenericValue<T, S> genVal : values.values()) {
            sum = sum.add(genVal.getValue());
        }
        return sum;
    }

    @Override
    public BigDecimal getTradeValue(SATSBidder<S> bidder) {
        if (bidder == null || !values.containsKey(bidder)) return BigDecimal.ZERO;
        return values.get(bidder).getValue();
    }

    @Override
    public SATSAllocation<S> getAllocationWithTrueValues() {
        GenericSATSAllocation.Builder<T, S> builder = new GenericSATSAllocation.Builder<>();
        for (Map.Entry<SATSBidder<S>, GenericValue<T, S>> bidderEntry : values.entrySet()) {
            BigDecimal trueValue = bidderEntry.getKey().calculateValue(bidderEntry.getValue().anyConsistentBundle());
            GenericValue.Builder<T, S> trueEntryBuilder = new GenericValue.Builder<>(trueValue);
            for (Map.Entry<T, Integer> quantities : bidderEntry.getValue().getQuantities().entrySet()) {
                trueEntryBuilder.putQuantity(quantities.getKey(), quantities.getValue());
            }
            builder.putGenericValue(bidderEntry.getKey(), trueEntryBuilder.build());
        }
        SATSAllocation<S> allocationWithTrueValues = new GenericSATSAllocation<>(builder);
        if (this.equals(allocationWithTrueValues)) {
            logger.warn("Requested allocation with true values when initial allocation already included true values.");
        }
        return allocationWithTrueValues;
    }

    public static class Builder<G extends GenericDefinition<T>, T extends SATSGood> {

        private Map<SATSBidder<T>, GenericValue<G, T>> storedValues;

        public Builder() {
            this.storedValues = new HashMap<>();
        }

        public void putGenericValue(SATSBidder<T> bidder, GenericValue<G, T> value) {
            Preconditions.checkNotNull(bidder);
            Preconditions.checkNotNull(value);
            storedValues.put(bidder, value);
        }
    }

}
