package org.spectrumauctions.sats.mechanism.vcg;

import ch.uzh.ifi.ce.domain.BidderAllocation;
import org.junit.Ignore;
import org.junit.Test;
import org.spectrumauctions.sats.core.model.mrvm.MRVMBidder;
import org.spectrumauctions.sats.core.model.mrvm.MultiRegionModel;
import org.spectrumauctions.sats.opt.model.ModelMIP;
import org.spectrumauctions.sats.opt.model.mrvm.MRVM_MIP;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class VCGWithMRVMTest {

    @Test
    @Ignore
    public void testVCGWithStandardMRVM() {
        List<MRVMBidder> bidders = new MultiRegionModel().createNewPopulation(234456867);
        ModelMIP wdp = new MRVM_MIP(bidders);
        ch.uzh.ifi.ce.mechanisms.AuctionMechanism am = new ModelVCGMechanism(wdp);
        ch.uzh.ifi.ce.domain.Payment payment = am.getPayment();
        double totalValue = am.getAllocation().getTotalAllocationValue().doubleValue();
        double sumOfValues = bidders.stream().mapToDouble(b -> am.getAllocation().getTradesMap().getOrDefault(b, BidderAllocation.ZERO_ALLOCATION).getValue().doubleValue()).sum();
        assertEquals(totalValue, sumOfValues, 1e-6);
        assertEquals(3.42515584861134e7, am.getAllocation().getTotalAllocationValue().doubleValue(), 1e-2);
    }
}
