/**
 * Copyright by Michael Weiss, weiss.michael@gmx.ch
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.spectrumauctions.sats.opt.domain;

import org.spectrumauctions.sats.core.model.SATSBidder;
import org.spectrumauctions.sats.core.model.SATSGood;

import java.util.Map;

/**
 * @author Michael Weiss
 *
 */
// TODO: Remove such that the mechanism library's WinnerDetermination / the ModelMIP class is used
public interface WinnerDeterminator<T extends SATSGood> {

        WinnerDeterminator<T> getWdWithoutBidder(SATSBidder<T> bidder);

        SATSAllocation<T> calculateAllocation();

        WinnerDeterminator<T> copyOf();

        void adjustPayoffs(Map<SATSBidder<T>, Double> payoffs);

}
