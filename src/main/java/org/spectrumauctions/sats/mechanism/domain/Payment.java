package org.spectrumauctions.sats.mechanism.domain;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Maps;
import org.spectrumauctions.sats.core.model.SATSBidder;
import org.spectrumauctions.sats.core.model.SATSGood;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * This class represents the Payment vector after a WinnerDetermination.
 */
public final class Payment<T extends SATSGood> {
    private final Map<SATSBidder<T>, BidderPayment> payments;


    /**
     * @param payments     Map of bidder to payments. One payment per bidder. Payment may
     *                     be 0 and allocation may of payment may be empty
     */
    public Payment(Map<SATSBidder<T>, BidderPayment> payments) {
        this.payments = Collections.unmodifiableMap(payments);
    }

    public double getTotalPayments() {
        double totalPayments = 0;
        for (BidderPayment payment : getPayments()) {
            totalPayments += payment.getAmount();
        }
        return totalPayments;
    }

    public Collection<BidderPayment> getPayments() {
        return payments.values();
    }

    public Map<SATSBidder<T>, BidderPayment> getPaymentMap() {
        return payments;
    }

    public BidderPayment paymentOf(SATSBidder<T> bidder) {
        BidderPayment payment = payments.get(bidder);
        if (payment == null) {
            return new BidderPayment(0);
        }
        return payment;
    }

    @Override
    public String toString() {

        return "Payment[payments=" + payments + "]";
    }

    public Set<SATSBidder<T>> getWinners() {
        return payments.keySet();
    }

    public boolean isWinner(SATSBidder<T> bidder) {
        return payments.containsKey(bidder);
    }


    public static <T extends SATSGood> Payment<T> getZeroPayment(Set<SATSBidder<T>> bidders) {
        BidderPayment zeroBidderPayment = new BidderPayment(0);
        Function<Object, BidderPayment> zeroPaymentFunction = Functions.constant(zeroBidderPayment);
        Map<SATSBidder<T>, BidderPayment> emptyPaymentMap = Maps.asMap(bidders, zeroPaymentFunction);
        return new Payment<T>(emptyPaymentMap);
    }

}
