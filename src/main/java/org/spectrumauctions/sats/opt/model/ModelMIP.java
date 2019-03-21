package org.spectrumauctions.sats.opt.model;

import ch.uzh.ifi.ce.domain.*;
import ch.uzh.ifi.ce.mechanisms.MetaInfo;
import ch.uzh.ifi.ce.mechanisms.winnerdetermination.WinnerDetermination;
import com.google.common.collect.Sets;
import edu.harvard.econcs.jopt.solver.IMIP;
import edu.harvard.econcs.jopt.solver.ISolution;
import edu.harvard.econcs.jopt.solver.SolveParam;
import edu.harvard.econcs.jopt.solver.mip.MIP;
import edu.harvard.econcs.jopt.solver.mip.Variable;
import org.spectrumauctions.sats.core.model.SATSBidder;
import org.spectrumauctions.sats.core.model.SATSGood;
import org.spectrumauctions.sats.opt.domain.SATSAllocation;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Fabio Isler
 */
public abstract class ModelMIP extends WinnerDetermination {

    public ModelMIP() {
        super(new AuctionInstance(new Bids()));
    }

    @Override
    protected Variable getBidVariable(BundleBid bundleBid) {
        return null;
    }

    @Override
    protected Allocation adaptMIPResult(ISolution mipResult) {

        SATSAllocation allocation = calculateAllocation();
        Map<Bidder, BidderAllocation> allocationMap = new HashMap<>();
        for (Object winner : allocation.getWinners()) {
            SATSBidder bidder = (SATSBidder) winner;
            BigDecimal tradeValue = allocation.getTradeValue(bidder);
            Set<Good> goods = Sets.newHashSet(allocation.getAllocation(bidder));
            BidderAllocation bidderAllocation = new BidderAllocation(tradeValue, goods, Sets.newHashSet());
            allocationMap.put(bidder, bidderAllocation);
        }

        MetaInfo metaInfo = new MetaInfo();
        metaInfo.setNumberOfMIPs(1);
        metaInfo.setMipSolveTime(mipResult.getSolveTime());
        return new Allocation(allocationMap, new Bids(), metaInfo);
    }

    protected abstract SATSAllocation calculateAllocation();
    public abstract ModelMIP getMIPWithout(Bidder bidder);
    public abstract ModelMIP copyOf();

    private IMIP mip = new MIP();

    /**
     * This is mainly used for testing or if you need to access special SolveParams.
     * In most cases the setters from this class are sufficient.
     *
     * @return reference to the JOpt mip
     */
    public IMIP getMIP() {
        return mip;
    }

    /**
     * Defines whether or not the solver output should be displayed.
     * Default is false.
     *
     * @param displayOutput If the output of the solver should be displayed, set this to true.
     */
    public void setDisplayOutput(boolean displayOutput) {
        mip.setSolveParam(SolveParam.DISPLAY_OUTPUT, displayOutput);
    }

    /**
     * Defines the behaviour in case the solver hits the defined timeout.
     *
     * @param acceptSuboptimal true: accept a suboptimal solution at timeout; false: throw an exception at timeout
     */
    public void setAcceptSuboptimal(boolean acceptSuboptimal) {
        mip.setSolveParam(SolveParam.ACCEPT_SUBOPTIMAL, acceptSuboptimal);
    }

    /**
     * Defines the time limit for the solver.
     * What happens after the time limit is defined via {@link #setAcceptSuboptimal(boolean)}.
     *
     * @param timeLimit the time limit in seconds
     */
    public void setTimeLimit(double timeLimit) {
        mip.setSolveParam(SolveParam.TIME_LIMIT, timeLimit);
    }

}
