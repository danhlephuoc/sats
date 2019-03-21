package org.spectrumauctions.sats.mechanism.vcg;

import ch.uzh.ifi.ce.domain.Bidder;
import ch.uzh.ifi.ce.mechanisms.vcg.VCGAuction;
import ch.uzh.ifi.ce.mechanisms.winnerdetermination.WinnerDetermination;
import org.spectrumauctions.sats.opt.model.ModelMIP;

public class ModelVCGMechanism extends VCGAuction {

    private ModelMIP winnerDetermination;

    public ModelVCGMechanism(ModelMIP winnerDetermination)  {
        super(winnerDetermination);
        this.winnerDetermination = winnerDetermination;
    }

    @Override
    protected WinnerDetermination getWinnerDetermination() {
        return winnerDetermination;
    }

    @Override
    protected WinnerDetermination getWinnerDeterminationWithout(Bidder bidder) {
        return winnerDetermination.getMIPWithout(bidder);
    }
}
