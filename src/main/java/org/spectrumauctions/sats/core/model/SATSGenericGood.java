/**
 * Copyright by Michael Weiss, weiss.michael@gmx.ch
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.spectrumauctions.sats.core.model;

import ch.uzh.ifi.ce.domain.Good;
import lombok.Getter;

import java.util.List;

@Getter
public abstract class SATSGenericGood implements Good {

    private final long longId;
    protected final long worldId;
    private List<SATSGood> goods;

    protected SATSGenericGood(long longId, long worldId, List<SATSGood> goods) {
        this.longId = longId;
        this.worldId = worldId;
        this.goods = goods;
    }

    /*public boolean isPartOf(SATSGood good) {
        return goods.contains(good);
    }

    public List<Good> allLicenses() {
        return Lists.newArrayList(goods);
    }*/

    @Override
    public int available() {
        return goods.size();
    }


    @Override
    public String getId() {
        return String.valueOf(longId);
    }

    public abstract World getWorld();

}
