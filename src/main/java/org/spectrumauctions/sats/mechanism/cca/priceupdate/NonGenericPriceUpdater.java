package org.spectrumauctions.sats.mechanism.cca.priceupdate;

import org.spectrumauctions.sats.core.model.SATSGood;

import java.math.BigDecimal;
import java.util.Map;

public interface NonGenericPriceUpdater<T extends SATSGood> {
    Map<T, BigDecimal> updatePrices(Map<T, BigDecimal> oldPrices, Map<T, Integer> demand);
    Map<T, BigDecimal> getLastPrices();
}
