/**
 * Copyright by Michael Weiss, weiss.michael@gmx.ch
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.spectrumauctions.sats.core.model;

import org.spectrumauctions.sats.core.bidlang.BiddingLanguage;
import org.spectrumauctions.sats.core.util.instancehandling.InstanceHandler;
import org.spectrumauctions.sats.core.util.random.JavaUtilRNGSupplier;
import org.spectrumauctions.sats.core.util.random.RNGSupplier;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public abstract class SATSBidder<G extends SATSGood> extends ch.uzh.ifi.ce.domain.Bidder implements Serializable {

    private static final long serialVersionUID = 3424512863538320455L;
    private final String setupType;
    private final long population;
    private final long longId;
    private final long worldId;
    private final BidderSetup setup;

    protected SATSBidder(BidderSetup setup, long population, long longId, long worldId) {
        super(String.valueOf(longId));
        this.setup = setup;
        this.setupType = setup.getSetupName();
        this.longId = longId;
        this.population = population;
        this.worldId = worldId;
    }

    public long getLongId() {
        return longId;
    }


    /**
     * @return the name of the configuration ({@link BidderSetup}) with which the bidder was created.
     */
    public String getSetupType() {
        return setupType;
    }


    /**
     * Returns the value this bidder has for a specific bundle.
     * Attention: May throw RuntimeExceptions if the items in the bundle are not of the correct world.
     *
     * @param bundle the bundle for which the value is asked
     * @return bidder specific value for this bundle
     */
    @Deprecated
    public double getValue(Bundle<G> bundle) {
        if (bundle.getWorld().equals(this.getWorld())) {
            throw new IncompatibleWorldException("Bundle not from the same world as the bidder");
        }
        return calculateValue(bundle).doubleValue();
    }

    /**
     * Returns the value this bidder has for a specific bundle.
     *
     * @param bundle the bundle for which the value is asked
     * @return bidder specific value for this bundle
     */
    public abstract BigDecimal calculateValue(Bundle<G> bundle);

    /**
     * Use this method to get a desired value function representation (bidding language)
     * for this bidder.
     * Note that this method may cause your compiler to throw a warning, as the generics of generic
     * Bidding Languages (such as XOR) is not specified in .class.
     *
     * @param type the type of the value function
     * @return the value function of this bidder
     * @throws UnsupportedBiddingLanguageException
     *             Throws this exception for all bidding languages
     *             which are not supported by the implementing bidder class. This javadoc should be extended by
     *             the implementing class, specifying which value function representation are supported.
     */
    public <T extends BiddingLanguage> T getValueFunction(Class<T> type)
            throws UnsupportedBiddingLanguageException {
        return getValueFunction(type, new Date().getTime());
    }


    /**
     * Use this method to get a desired value function representation (bidding language)
     * for this bidder.
     * Note that this method may cause your compiler to throw a warning, as the generics of generic
     * Bidding Languages (such as XOR) is not specified in .class.
     * generic
     *
     * @param type the type of the value function
     * @return the value function of this bidder
     * @throws UnsupportedBiddingLanguageException
     *             Throws this exception for all bidding languages
     *             which are not supported by the implementing bidder class. This javadoc should be extended by
     *             the implementing class, specifying which value function representation are supported.
     */
    public <T extends BiddingLanguage> T getValueFunction(Class<T> type, long seed)
            throws UnsupportedBiddingLanguageException {
        return getValueFunction(type, new JavaUtilRNGSupplier(seed));
    }

    public abstract <T extends BiddingLanguage> T getValueFunction(Class<T> type, RNGSupplier rngSupplier)
            throws UnsupportedBiddingLanguageException;

    /**
     * @return the population to which this bidder belongs.
     * The population is not meaningful, if for a specific world instance, only one set of Bidders
     * is created. However, if there are multiple sets of bidders (which should not be part of the same simulation)
     * created, they have different population ids.
     */
    public long getPopulation() {
        return population;
    }

    /**
     * @return World to which this bidder belongs.
     * The implementing SATSBidder class, overriding this method,
     * should return a world type corresponding to the specific model.
     */
    public abstract World getWorld();

    protected void store() {
        InstanceHandler.getDefaultHandler().writeBidder(this);
    }


    /**
     * To prevent from creating too many identical world instances, worlds are not serialized and deserialized with the bidder<br>
     * As a temporary solution, the world instance is re-added after deserialization by calling this method.<br><br>
     *
     * This method will be removed in a later version and be done automatically during deserialization.
     */
    public abstract void refreshReference(World world);

    public long getWorldId() {
        return worldId;
    }

    public abstract SATSBidder<G> drawSimilarBidder(RNGSupplier rngSupplier);

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (longId ^ (longId >>> 32));
        result = prime * result + ((setupType == null) ? 0 : setupType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SATSBidder<?> other = (SATSBidder<?>) obj;
        if (longId != other.longId)
            return false;
        if (setupType == null) {
            if (other.setupType != null)
                return false;
        } else if (!setupType.equals(other.setupType))
            return false;
        return true;
    }


    protected BidderSetup getSetup() {
        return setup;
    }
}
