package org.spectrumauctions.sats.opt.domain;

import org.spectrumauctions.sats.core.bidlang.generic.GenericDefinition;
import org.spectrumauctions.sats.core.model.SATSGood;

import java.util.List;

public interface GenericDemandQueryMIP<T extends GenericDefinition<S>, S extends SATSGood> extends DemandQueryMIP {
    GenericDemandQueryResult<T, S> getResult();
    List<? extends GenericDemandQueryResult<T, S>> getResultPool(int numberOfResults);
}
