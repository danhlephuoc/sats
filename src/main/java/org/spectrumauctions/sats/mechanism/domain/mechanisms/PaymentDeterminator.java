package org.spectrumauctions.sats.mechanism.domain.mechanisms;

import org.spectrumauctions.sats.core.model.SATSGood;
import org.spectrumauctions.sats.mechanism.domain.Payment;

public interface PaymentDeterminator<T extends SATSGood> {

    Payment<T> getPayment();

}