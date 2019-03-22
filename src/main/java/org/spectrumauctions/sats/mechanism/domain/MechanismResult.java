package org.spectrumauctions.sats.mechanism.domain;

import org.spectrumauctions.sats.core.model.SATSGood;
import org.spectrumauctions.sats.opt.domain.SATSAllocation;

/**
 * This class represents the result of an Auction, consisting of
 * an SATSAllocation and a Payment vector.
 */
// FIXME: Remove this when not used anymore
public class MechanismResult<T extends SATSGood> {
    private final Payment<T> payment;
    private final SATSAllocation<T> allocation;

    public MechanismResult(Payment<T> payment, SATSAllocation<T> allocation) {
        this.payment = payment;
        this.allocation = allocation;
    }

    public Payment<T> getPayment() {
        return payment;
    }

    public SATSAllocation<T> getAllocation() {
        return allocation;
    }

}
