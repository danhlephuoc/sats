package org.spectrumauctions.sats.mechanism.vcg;

import org.spectrumauctions.sats.core.model.SATSBidder;
import org.spectrumauctions.sats.core.model.SATSGood;
import org.spectrumauctions.sats.mechanism.domain.MechanismResult;
import org.spectrumauctions.sats.mechanism.domain.BidderPayment;
import org.spectrumauctions.sats.mechanism.domain.Payment;
import org.spectrumauctions.sats.mechanism.domain.mechanisms.AuctionMechanism;
import org.spectrumauctions.sats.opt.domain.SATSAllocation;
import org.spectrumauctions.sats.opt.domain.WinnerDeterminator;

import java.util.HashMap;
import java.util.Map;

public class VCGMechanism<T extends SATSGood> implements AuctionMechanism<T> {

    private WinnerDeterminator<T> baseWD;
    private MechanismResult<T> result;


    public VCGMechanism(WinnerDeterminator<T> wdp) {
        this.baseWD = wdp;
    }

    @Override
    public MechanismResult<T> getMechanismResult() {
        if (result == null) {
            result = calculateVCGPayments();
        }
        return result;
    }

    @Override
    public Payment<T> getPayment() {
        return getMechanismResult().getPayment();
    }

    @Override
    public WinnerDeterminator<T> getWdWithoutBidder(SATSBidder<T> bidder) {
        return baseWD.getWdWithoutBidder(bidder);
    }

    @Override
    public SATSAllocation<T> calculateAllocation() {
        return getMechanismResult().getAllocation();
    }

    @Override
    public WinnerDeterminator<T> copyOf() {
        return baseWD.copyOf();
    }

    @Override
    public void adjustPayoffs(Map<SATSBidder<T>, Double> payoffs) {
        baseWD.adjustPayoffs(payoffs);
    }

    private MechanismResult<T> calculateVCGPayments() {
        SATSAllocation<T> baseAllocation = baseWD.calculateAllocation();

        Map<SATSBidder<T>, BidderPayment> payments = new HashMap<>();
        for (SATSBidder<T> bidder : baseAllocation.getWinnersOld()) {

            double baseAllocationTotalValue = baseAllocation.getTotalValue().doubleValue();
            double baseAllocationBidderValue = baseAllocation.getTradeValue(bidder).doubleValue();
            double valueWithoutBidder = baseAllocationTotalValue - baseAllocationBidderValue;

            WinnerDeterminator<T> wdWithoutBidder = baseWD.getWdWithoutBidder(bidder);
            SATSAllocation<T> allocationWithoutBidder = wdWithoutBidder.calculateAllocation();
            double valueWDWithoutBidder = allocationWithoutBidder.getTotalValue().doubleValue();

            double paymentAmount = valueWDWithoutBidder - valueWithoutBidder;
            payments.put(bidder, new BidderPayment(paymentAmount));

        }
        Payment<T> payment = new Payment<>(payments);
        return new MechanismResult<>(payment, baseAllocation);
    }

}
