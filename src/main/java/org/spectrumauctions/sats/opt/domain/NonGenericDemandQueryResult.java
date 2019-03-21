package org.spectrumauctions.sats.opt.domain;

import org.spectrumauctions.sats.core.bidlang.xor.XORValue;
import org.spectrumauctions.sats.core.model.SATSGood;

public interface NonGenericDemandQueryResult<T extends SATSGood> {
    XORValue<T> getResultingBundle();
}
