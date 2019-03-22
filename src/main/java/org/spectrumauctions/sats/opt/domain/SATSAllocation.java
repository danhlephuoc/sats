/**
 * Copyright by Michael Weiss, weiss.michael@gmx.ch
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.spectrumauctions.sats.opt.domain;

import org.spectrumauctions.sats.core.model.SATSBidder;
import org.spectrumauctions.sats.core.model.Bundle;
import org.spectrumauctions.sats.core.model.SATSGood;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * @author Michael Weiss
 *
 */
// TODO: Align with mechanism library's Allocation
public interface SATSAllocation<T extends SATSGood> {

    default Collection<SATSBidder<T>> getWinnersOld() {
        throw new NotImplementedException();
    }

    /**
     * Returns information about the goods allocated to a specific bidder
     */
    Bundle<T> getAllocation(SATSBidder<T> bidder);

    BigDecimal getTotalValue();

    BigDecimal getTradeValue(SATSBidder<T> bidder);

    SATSAllocation<T> getAllocationWithTrueValues();
}
