package org.spectrumauctions.sats.opt.domain;

import org.spectrumauctions.sats.core.model.SATSBidder;
import org.spectrumauctions.sats.core.model.SATSGood;

import java.math.BigDecimal;
import java.util.Map;

public interface NonGenericDemandQueryMIPBuilder<T extends SATSGood> {
    NonGenericDemandQueryMIP<T> getDemandQueryMipFor(SATSBidder<T> bidder, Map<T, BigDecimal> prices, double epsilon);
}
