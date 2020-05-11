package org.spectrumauctions.sats.core.model.gsvm;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import edu.harvard.econcs.jopt.solver.mip.*;

import org.apache.commons.math3.util.CombinatoricsUtils;
import org.marketdesignresearch.mechlib.core.Allocation;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Good;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.marketdesignresearch.mechlib.instrumentation.MipInstrumentation;
import org.spectrumauctions.sats.core.bidlang.BiddingLanguage;
import org.spectrumauctions.sats.core.bidlang.xor.DecreasingSizeOrderedXOR;
import org.spectrumauctions.sats.core.bidlang.xor.IncreasingSizeOrderedXOR;
import org.spectrumauctions.sats.core.bidlang.xor.SizeBasedUniqueRandomXOR;
import org.spectrumauctions.sats.core.model.SATSBidder;
import org.spectrumauctions.sats.core.model.UnsupportedBiddingLanguageException;
import org.spectrumauctions.sats.core.model.World;
import org.spectrumauctions.sats.core.util.random.RNGSupplier;
import org.spectrumauctions.sats.opt.model.gsvm.GSVMStandardMIP;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Fabio Isler
 */
public final class GSVMBidder extends SATSBidder {

    private static final long serialVersionUID = -7275733600491984673L;
    private final int bidderPosition;
    private final HashMap<Long, BigDecimal> values;
    private transient GSVMWorld world;
    private final String description;
    private final int activityLimit;

    GSVMBidder(GSVMBidderSetup setup, GSVMWorld world, int bidderPosition, long currentId, long population, RNGSupplier rngSupplier) {
        super(setup, population, currentId, world.getId());
        this.world = world;
        this.bidderPosition = bidderPosition % world.getSize();
        this.values = setup.drawValues(rngSupplier, this);
        this.description = setup.getSetupName() + " with interest in licenses "
                + this.world.getLicenses().stream().filter(l -> this.values.containsKey(l.getLongId())).map(GSVMLicense::getName).collect(Collectors.joining(", "))
                + ".";
        this.activityLimit = setup.getActivityLimit();
        store();
    }

    @Override
    public BigDecimal calculateValue(Bundle bundle) {
        List<Double> values = new ArrayList<>();
        int synergyCount = 0;
        for (Good good : bundle.getSingleQuantityGoods()) {
            GSVMLicense license = (GSVMLicense) good;
            if (this.values.containsKey(license.getLongId())) {
                values.add(this.values.get(license.getLongId()).doubleValue());
                synergyCount++;
            } else if (world.isLegacyGSVM()) {
                synergyCount++;
            }
        }
        
        double value = 0;
        if(world.isLegacyGSVM()) {
        	value = values.stream().mapToDouble(Double::doubleValue).sum();
        } else {
        	// Only use 4 highest base values for regional bidders
        	values.sort(Double::compare);
        	Collections.reverse(values);
        	value = values.stream().limit(this.getActivityLimit()).mapToDouble(Double::doubleValue).sum();
        	// Limit synergy to 4 items
        	synergyCount = Math.min(synergyCount, this.getActivityLimit());
        }
        
        double factor = 0;
        if (synergyCount > 0) factor = 0.2 * (synergyCount - 1);
        return BigDecimal.valueOf(value + value * factor);
    }

    public int getBidderPosition() {
        return bidderPosition;
    }

    @Override
    public <T extends BiddingLanguage> T getValueFunction(Class<T> clazz, RNGSupplier rngSupplier) throws UnsupportedBiddingLanguageException {
        if (clazz.isAssignableFrom(SizeBasedUniqueRandomXOR.class)) {
            return clazz.cast(
                    new SizeBasedUniqueRandomXOR(world.getLicenses(), rngSupplier, this));
        } else if (clazz.isAssignableFrom(IncreasingSizeOrderedXOR.class)) {
            return clazz.cast(
                    new IncreasingSizeOrderedXOR(world.getLicenses(), this));
        } else if (clazz.isAssignableFrom(DecreasingSizeOrderedXOR.class)) {
            return clazz.cast(
                    new DecreasingSizeOrderedXOR(world.getLicenses(), this));
        } else {
            throw new UnsupportedBiddingLanguageException();
        }
    }

    @Override
    public GSVMWorld getWorld() {
        return this.world;
    }

    @Override
    public void refreshReference(World world) {
        Preconditions.checkArgument(world.getId() == getWorldId());
        if (world instanceof GSVMWorld) {
            this.world = (GSVMWorld) world;
        } else {
            throw new IllegalArgumentException("World is not of correct type");
        }
    }

    @Override
    public SATSBidder drawSimilarBidder(RNGSupplier rngSupplier) {
        return new GSVMBidder((GSVMBidderSetup) getSetup(), getWorld(), getBidderPosition(), getLongId(), getPopulation(), rngSupplier);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GSVMBidder that = (GSVMBidder) o;
        return getBidderPosition() == that.getBidderPosition() &&
                Objects.equals(values, that.values) &&
                Objects.equals(getWorld(), that.getWorld());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getBidderPosition(), values, getWorld());
    }

    public Map<Long, BigDecimal> getBaseValues() {
        return Collections.unmodifiableMap(values);
    }

    @Override
    public LinkedHashSet<Bundle> getBestBundles(Prices prices, int maxNumberOfBundles, boolean allowNegative) {
        GSVMStandardMIP mip = new GSVMStandardMIP(world, Lists.newArrayList(this));
        mip.setMipInstrumentation(getMipInstrumentation());
        mip.setPurpose(MipInstrumentation.MipPurpose.DEMAND_QUERY);
        Variable priceVar = new Variable("p", VarType.DOUBLE, 0, MIP.MAX_VALUE);
        mip.getMIP().add(priceVar);
        mip.getMIP().addObjectiveTerm(-1, priceVar);
        Constraint price = new Constraint(CompareType.EQ, 0);
        price.addTerm(-1, priceVar);
        for (GSVMLicense license : world.getLicenses()) {
            Map<Integer, Variable> xVariables = mip.getXVariables(this, license);
            for (Variable xVariable : xVariables.values()) {
                price.addTerm(prices.getPrice(Bundle.of(license)).getAmount().doubleValue(), xVariable);
            }
        }
        mip.getMIP().add(price);
        
        mip.setEpsilon(DEFAULT_DEMAND_QUERY_EPSILON);
        mip.setTimeLimit(DEFAULT_DEMAND_QUERY_TIME_LIMIT);
        
        // Limit max number of bundles to the feasible ones in non legacy worlds
        if(!world.isLegacyGSVM()) {
        	int maxNumberOfBundlesInterestedIn = 0;
        	for(int i = 0; i <= this.getActivityLimit(); i++) {
        		maxNumberOfBundlesInterestedIn += CombinatoricsUtils.binomialCoefficient(this.getBaseValues().size(), i);
        	}
        	maxNumberOfBundles = Math.min(maxNumberOfBundles, maxNumberOfBundlesInterestedIn);
        } else {
        	maxNumberOfBundles = Math.min(maxNumberOfBundles, (int)Math.pow(2, this.getWorld().getNumberOfGoods()));
        }
        
        List<Allocation> optimalAllocations = mip.getBestAllocations(maxNumberOfBundles, allowNegative);

        LinkedHashSet<Bundle> result = optimalAllocations.stream()
                .map(allocation -> allocation.allocationOf(this).getBundle())
                .filter(bundle -> allowNegative || getUtility(bundle, prices).signum() > -1)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (result.isEmpty()) result.add(Bundle.EMPTY);
        return result;
    }
    
    public int getActivityLimit() {
    	return this.activityLimit;
    }

    @Override
    public String getDescription() {
        return description;
    }

}
