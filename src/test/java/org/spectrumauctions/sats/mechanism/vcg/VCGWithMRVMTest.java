package org.spectrumauctions.sats.mechanism.vcg;

import org.junit.Ignore;
import org.junit.Test;
import org.spectrumauctions.sats.core.model.gsvm.GSVMBidder;
import org.spectrumauctions.sats.core.model.gsvm.GSVMLicense;
import org.spectrumauctions.sats.core.model.gsvm.GlobalSynergyValueModel;
import org.spectrumauctions.sats.core.model.lsvm.LSVMBidder;
import org.spectrumauctions.sats.core.model.lsvm.LSVMLicense;
import org.spectrumauctions.sats.core.model.lsvm.LocalSynergyValueModel;
import org.spectrumauctions.sats.core.model.mrvm.MRVMBidder;
import org.spectrumauctions.sats.core.model.mrvm.MRVMLicense;
import org.spectrumauctions.sats.core.model.mrvm.MultiRegionModel;
import org.spectrumauctions.sats.core.model.srvm.SRVMBidder;
import org.spectrumauctions.sats.core.model.srvm.SRVMLicense;
import org.spectrumauctions.sats.core.model.srvm.SingleRegionModel;
import org.spectrumauctions.sats.mechanism.domain.Payment;
import org.spectrumauctions.sats.mechanism.domain.mechanisms.AuctionMechanism;
import org.spectrumauctions.sats.opt.domain.WinnerDeterminator;
import org.spectrumauctions.sats.opt.model.ModelMIP;
import org.spectrumauctions.sats.opt.model.gsvm.GSVMStandardMIP;
import org.spectrumauctions.sats.opt.model.lsvm.LSVMStandardMIP;
import org.spectrumauctions.sats.opt.model.mrvm.MRVM_MIP;
import org.spectrumauctions.sats.opt.model.srvm.SRVM_MIP;

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
        double sumOfValues = bidders.stream().mapToDouble(b -> am.getAllocation().getTradesMap().get(b).getValue().doubleValue()).sum();
        assertEquals(totalValue, sumOfValues, 1e-6);
        assertEquals(3.42515584861134e7, am.getAllocation().getTotalAllocationValue().doubleValue(), 1e-2);
    }
}
