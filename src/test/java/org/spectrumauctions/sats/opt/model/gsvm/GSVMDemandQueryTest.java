package org.spectrumauctions.sats.opt.model.gsvm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.marketdesignresearch.mechlib.core.Bundle;
import org.marketdesignresearch.mechlib.core.Domain;
import org.marketdesignresearch.mechlib.core.price.Price;
import org.marketdesignresearch.mechlib.core.price.Prices;
import org.spectrumauctions.sats.core.model.gsvm.GSVMBidder;
import org.spectrumauctions.sats.core.model.gsvm.GlobalSynergyValueModel;
import org.spectrumauctions.sats.core.util.random.JavaUtilRNGSupplier;
import org.spectrumauctions.sats.mechanism.domains.GSVMDomain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;

/**
 * @author Fabio Isler
 */
public class GSVMDemandQueryTest {

    private static final Logger logger = LogManager.getLogger(GSVMDemandQueryTest.class);

    @Test
    public void testAllBiddersInGSVM() {
        List<GSVMBidder> bidders = new GlobalSynergyValueModel().createNewPopulation(new JavaUtilRNGSupplier(73246104));

        for (GSVMBidder bidder : bidders) {
            Bundle bundle = bidder.getBestBundle(Prices.NONE);
            BigDecimal value = bidder.calculateValue(bundle);
            Price price = Prices.NONE.getPrice(bundle);
            BigDecimal utility = bidder.getUtility(bundle, Prices.NONE);
            Assert.assertTrue(utility.compareTo(BigDecimal.ZERO) > 0);
            logger.info("Bidder {} chooses bundle [{}].\tValue: {}\tPrice: {}\tUtility: {})",
                    bidder.getName(),
                    bundle,
                    value.setScale(2, RoundingMode.HALF_UP),
                    price.getAmount().setScale(2, RoundingMode.HALF_UP),
                    utility.setScale(2, RoundingMode.HALF_UP));
        }
    }

    @Test
    public void testMultipleBundles() {
        List<GSVMBidder> bidders = new GlobalSynergyValueModel().createNewPopulation(new JavaUtilRNGSupplier(73246104));
        Domain domain = new GSVMDomain(bidders);
        GSVMBidder bidder = bidders.get(0);
        Prices prices = domain.proposeStartingPrices();

        Set<Bundle> bundles = bidder.getBestBundles(prices, 10);
        for (Bundle bundle : bundles) {
            BigDecimal value = bidder.calculateValue(bundle);
            Price price = prices.getPrice(bundle);
            BigDecimal utility = bidder.getUtility(bundle, prices);
            Assert.assertTrue(utility.compareTo(BigDecimal.ZERO) > 0);
            logger.info("Bidder {} chooses bundle [{}].\tValue: {}\tPrice: {}\tUtility: {})",
                    bidder.getName(),
                    bundle,
                    value.setScale(2, RoundingMode.HALF_UP),
                    price.getAmount().setScale(2, RoundingMode.HALF_UP),
                    utility.setScale(2, RoundingMode.HALF_UP));
        }

    }
}
