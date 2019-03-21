package org.spectrumauctions.sats.mechanism.domain;

import org.spectrumauctions.sats.core.model.SATSBidder;

/**
 * This class represents the Payment made by a single {@link SATSBidder}.
 */
public class BidderPayment {
    private final double amount;

    public BidderPayment(double amount) {
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        String returnString = "Payment: " + getAmount() + "\n";
        return returnString;
    }
}
