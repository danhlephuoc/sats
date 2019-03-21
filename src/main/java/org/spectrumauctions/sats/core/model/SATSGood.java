/**
 * Copyright by Michael Weiss, weiss.michael@gmx.ch
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.spectrumauctions.sats.core.model;

import ch.uzh.ifi.ce.domain.Good;
import lombok.Getter;

import java.io.Serializable;
import java.util.Comparator;

@Getter
public abstract class SATSGood implements Good {

    private final long longId;
    protected final long worldId;

    protected SATSGood(long longId, long worldId) {
        this.longId = longId;
        this.worldId = worldId;
    }

    @Override
    public String getId() {
        return String.valueOf(longId);
    }

    public abstract World getWorld();

    public static class IdComparator implements Comparator<SATSGood>, Serializable {

        private static final long serialVersionUID = -251782333802510799L;

        private static Comparator<SATSGood> comparator = Comparator.comparingLong(SATSGood::getLongId);

        @Override
        public int compare(SATSGood arg0, SATSGood arg1) {
            return comparator.compare(arg0, arg1);
        }
    }

}
