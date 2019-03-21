package org.spectrumauctions.sats.mechanism.cca.supplementaryround;

import org.spectrumauctions.sats.core.bidlang.generic.GenericDefinition;
import org.spectrumauctions.sats.core.bidlang.generic.GenericValue;
import org.spectrumauctions.sats.core.model.SATSBidder;
import org.spectrumauctions.sats.core.model.SATSGood;
import org.spectrumauctions.sats.mechanism.cca.GenericCCAMechanism;

import java.util.List;

public interface GenericSupplementaryRound<G extends GenericDefinition<T>, T extends SATSGood> {
    List<GenericValue<G, T>> getSupplementaryBids(GenericCCAMechanism<G, T> cca, SATSBidder<T> bidder);
}
