package org.spectrumauctions.sats.mechanism.cca.supplementaryround;

import org.spectrumauctions.sats.core.bidlang.xor.XORValue;
import org.spectrumauctions.sats.core.model.SATSBidder;
import org.spectrumauctions.sats.core.model.SATSGood;
import org.spectrumauctions.sats.mechanism.cca.NonGenericCCAMechanism;

import java.util.List;

public interface NonGenericSupplementaryRound<T extends SATSGood> {
    List<XORValue<T>> getSupplementaryBids(NonGenericCCAMechanism<T> cca, SATSBidder<T> bidder);
}
