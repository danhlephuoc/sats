package org.spectrumauctions.sats.opt.domain;

import org.spectrumauctions.sats.core.model.SATSGood;

import java.util.List;

public interface NonGenericDemandQueryMIP<S extends SATSGood> extends DemandQueryMIP {
    NonGenericDemandQueryResult<S> getResult();
    List<? extends NonGenericDemandQueryResult<S>> getResultPool(int numberOfResults);
}
