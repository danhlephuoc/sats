package org.spectrumauctions.sats.opt.model.mrvm;

import ch.uzh.ifi.ce.mechanisms.winnerdetermination.WinnerDetermination;
import org.junit.Assert;
import org.junit.Test;
import org.spectrumauctions.sats.core.model.mrvm.MRVMBidder;
import org.spectrumauctions.sats.core.model.mrvm.MRVMWorld;
import org.spectrumauctions.sats.core.model.mrvm.MultiRegionModel;

import java.util.List;

public class MRVMSATSAllocationTest {

    @Test
    public void testEqualAllocationWhenSolvingTwice() {
        MultiRegionModel model = new MultiRegionModel();
        MRVMWorld world =  model.createWorld(1234567);
        List<MRVMBidder> biddersList = model.createPopulation(world, 1234567);
        WinnerDetermination mip = new MRVM_MIP(biddersList);
        // mip.setDisplayOutput(true); FIXME
        MRVMMipResult result1 = (MRVMMipResult) mip.getAllocation();
        MRVMMipResult result2 = (MRVMMipResult) mip.getAllocation();
        for (MRVMBidder bidder : biddersList) {
            Assert.assertEquals(result1.getGenericAllocation(bidder).getTotalQuantity(), result2.getGenericAllocation(bidder).getTotalQuantity());
        }
        Assert.assertEquals(result1.getTotalAllocationValue(), result2.getTotalAllocationValue());
    }

    @Test
    public void testEqualAllocationWhenCreatingMIPTwice() {
        MultiRegionModel model = new MultiRegionModel();
        MRVMWorld world =  model.createWorld(1234567);
        List<MRVMBidder> biddersList = model.createPopulation(world, 1234567);
        WinnerDetermination mip1 = new MRVM_MIP(biddersList);
        // mip1.setDisplayOutput(true); FIXME
        WinnerDetermination mip2 = new MRVM_MIP(biddersList);
        // mip2.setDisplayOutput(true); FIXME
        MRVMMipResult result1 = (MRVMMipResult) mip1.getAllocation(); // FIXME
        MRVMMipResult result2 = (MRVMMipResult) mip2.getAllocation(); // FIXME
        for (MRVMBidder bidder : biddersList) {
            Assert.assertEquals(result1.getGenericAllocation(bidder).getTotalQuantity(), result2.getGenericAllocation(bidder).getTotalQuantity());
        }
    }

}
