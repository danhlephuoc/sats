package org.spectrumauctions.sats.opt.domain;

import org.spectrumauctions.sats.core.bidlang.generic.GenericDefinition;
import org.spectrumauctions.sats.core.model.SATSBidder;
import org.spectrumauctions.sats.core.model.SATSGood;

import java.math.BigDecimal;
import java.util.Map;

public interface GenericDemandQueryMIPBuilder<S extends GenericDefinition<T>, T extends SATSGood> {
    GenericDemandQueryMIP<S, T> getDemandQueryMipFor(SATSBidder<T> bidder, Map<S, BigDecimal> prices, double epsilon);
}
