/**
 * Copyright by Michael Weiss, weiss.michael@gmx.ch
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.spectrumauctions.sats.opt.model.mrvm;

import ch.uzh.ifi.ce.domain.Bidder;
import com.google.common.base.Preconditions;
import edu.harvard.econcs.jopt.solver.ISolution;
import edu.harvard.econcs.jopt.solver.client.SolverClient;
import edu.harvard.econcs.jopt.solver.mip.Constraint;
import edu.harvard.econcs.jopt.solver.mip.MIP;
import edu.harvard.econcs.jopt.solver.mip.Variable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spectrumauctions.sats.core.bidlang.generic.GenericValue;
import org.spectrumauctions.sats.core.model.mrvm.*;
import org.spectrumauctions.sats.core.model.mrvm.MRVMRegionsMap.Region;
import org.spectrumauctions.sats.opt.model.ModelMIP;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Michael Weiss
 *
 */
public class MRVM_MIP extends ModelMIP {

    private static final Logger logger = LogManager.getLogger(MRVM_MIP.class);

    public static boolean PRINT_SOLVER_RESULT = false;

    private static SolverClient SOLVER = new SolverClient();

    /**
     * If the highest possible value any bidder can have is higher than {@link MIP#MAX_VALUE} - MAXVAL_SAFETYGAP}
     * a non-zero scaling factor for the calculation is chosen.
     */
    private MRVMWorldPartialMip worldPartialMip;
    private Map<MRVMBidder, MRVMBidderPartialMIP> bidderPartialMips;
    private MRVMWorld world;
    private Collection<MRVMBidder> bidders;

    public MRVM_MIP(Collection<MRVMBidder> bidders) {
        Preconditions.checkNotNull(bidders);
        Preconditions.checkArgument(bidders.size() > 0);
        world = bidders.iterator().next().getWorld();
        double scalingFactor = Scalor.scalingFactor(bidders);
        double biggestPossibleValue = Scalor.biggestUnscaledPossibleValue(bidders).doubleValue() / scalingFactor;
        this.bidders = bidders;
        this.worldPartialMip = new MRVMWorldPartialMip(
                bidders,
                biggestPossibleValue);
        worldPartialMip.appendToMip(getMIP());
        bidderPartialMips = new HashMap<>();
        for (MRVMBidder bidder : bidders) {
            MRVMBidderPartialMIP bidderPartialMIP;
            if (bidder instanceof MRVMNationalBidder) {
                MRVMNationalBidder globalBidder = (MRVMNationalBidder) bidder;
                bidderPartialMIP = new MRVMNationalBidderPartialMip(globalBidder, scalingFactor, worldPartialMip);
            } else if (bidder instanceof MRVMLocalBidder) {
                MRVMLocalBidder globalBidder = (MRVMLocalBidder) bidder;
                bidderPartialMIP = new MRVMLocalBidderPartialMip(globalBidder, scalingFactor, worldPartialMip);
            } else {
                MRVMRegionalBidder globalBidder = (MRVMRegionalBidder) bidder;
                bidderPartialMIP = new MRVMRegionalBidderPartialMip(globalBidder, scalingFactor, worldPartialMip);
            }
            bidderPartialMIP.appendToMip(getMIP());
            bidderPartialMips.put(bidder, bidderPartialMIP);
        }
    }



    public void addConstraint(Constraint constraint) {
        getMIP().add(constraint);
    }

    public void addVariable(Variable variable) {
        getMIP().add(variable);
    }

    public void addObjectiveTerm(double coefficient, Variable variable) {
        getMIP().addObjectiveTerm(coefficient, variable);
    }


    @Override
    public ModelMIP getMIPWithout(Bidder bidder) {
        MRVMBidder mrvmBidder = (MRVMBidder) bidder;
        Preconditions.checkArgument(bidders.contains(mrvmBidder));
        return new MRVM_MIP(bidders.stream().filter(b -> !b.equals(mrvmBidder)).collect(Collectors.toSet()));
    }

    /* (non-Javadoc)
     * @see EfficientAllocator#calculateEfficientAllocation()
     */
    @Override
    public MRVMMipResult adaptMIPResult(ISolution solution) {
        if (PRINT_SOLVER_RESULT) {
            logger.info("Result:\n" + solution);
        }
        MRVMMipResult.Builder resultBuilder = new MRVMMipResult.Builder(world, solution);
        for (Map.Entry<MRVMBidder, MRVMBidderPartialMIP> bidder : bidderPartialMips.entrySet()) {
            Variable bidderValueVar = worldPartialMip.getValueVariable(bidder.getKey());
            double mipUtilityResult = solution.getValue(bidderValueVar);
            double svScalingFactor = bidder.getValue().getScalingFactor();
//            if (svScalingFactor != 1) {
//                logger.info("Scaling SV Value with factor " + svScalingFactor);
//            }
            double unscaledValue = mipUtilityResult * svScalingFactor;
            GenericValue.Builder<MRVMGenericDefinition, MRVMLicense> valueBuilder = new GenericValue.Builder<>(BigDecimal.valueOf(unscaledValue));
            for (Region region : world.getRegionsMap().getRegions()) {
                for (MRVMBand band : world.getBands()) {
                    Variable xVar = worldPartialMip.getXVariable(bidder.getKey(), region, band);
                    double doubleQuantity = solution.getValue(xVar);
                    int quantity = (int) Math.round(doubleQuantity);
                    if (quantity > 0) {
                        MRVMGenericDefinition def = new MRVMGenericDefinition(band, region);
                        valueBuilder.putQuantity(def, quantity);
                    }
                }
            }
            GenericValue<MRVMGenericDefinition, MRVMLicense> build = valueBuilder.build();
            if (!build.getQuantities().isEmpty()) {
                resultBuilder.putGenericValue(bidder.getKey(), build);
            }
        }
        return resultBuilder.build();
    }

    @Override
    public ModelMIP copyOf() {
        return new MRVM_MIP(bidders);
    }

    public MRVMWorldPartialMip getWorldPartialMip() {
        return worldPartialMip;
    }

    public Map<MRVMBidder, MRVMBidderPartialMIP> getBidderPartialMips() {
        return bidderPartialMips;
    }


    /*@Override
    public void adjustPayoffs(Map<SATSBidder<MRVMLicense>, Double> payoffs) {
        for (Map.Entry<SATSBidder<MRVMLicense>, Double> entry : payoffs.entrySet()) {
            MRVMBidder bidder = (MRVMBidder) entry.getKey();
            double negativePayoff = -entry.getValue();
            Variable x = new Variable("x_" + bidder.getLongId(), VarType.BOOLEAN, 0, 1);
            addVariable(x);
            addObjectiveTerm(negativePayoff, x);

            Constraint xConstraint = new Constraint(CompareType.GEQ, 0);
            xConstraint.addTerm(-1, x);
            for (Variable var : worldPartialMip.getXVariables(bidder)) {
                xConstraint.addTerm(1, var);
            }
            addConstraint(xConstraint);

            Constraint xConstraint2 = new Constraint(CompareType.LEQ, 0);
            xConstraint2.addTerm(-MAX_VALUE, x);
            for (Variable var : worldPartialMip.getXVariables(bidder)) {
                xConstraint2.addTerm(1, var);
            }
            addConstraint(xConstraint2);
        }
    }*/

    public Collection<Variable> getXVariables() {

        return bidders
                .stream()
                .map(b -> worldPartialMip.getXVariables(b))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
