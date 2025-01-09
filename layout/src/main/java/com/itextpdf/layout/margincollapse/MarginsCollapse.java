/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.layout.margincollapse;

class MarginsCollapse implements Cloneable {
    private float maxPositiveMargin = 0;
    private float minNegativeMargin = 0;

    void joinMargin(float margin) {
        if (maxPositiveMargin < margin) {
            maxPositiveMargin = margin;
        } else if (minNegativeMargin > margin) {
            minNegativeMargin = margin;
        }
    }

    void joinMargin(MarginsCollapse marginsCollapse) {
        joinMargin(marginsCollapse.maxPositiveMargin);
        joinMargin(marginsCollapse.minNegativeMargin);
    }

    float getCollapsedMarginsSize() {
        return maxPositiveMargin + minNegativeMargin;
    }

    /**
     * Creates a "deep copy" of this MarginsCollapse, meaning the object returned by this method will be independent
     * of the object being cloned.
     *
     * @return the copied MarginsCollapse.
     */
    @Override
    public MarginsCollapse clone() {
        try {
            // super.clone is safe to return since all of the MarginsCollapse's fields are primitive.
            return (MarginsCollapse) super.clone();
        } catch (CloneNotSupportedException e) {
            // should never happen since Cloneable is implemented
            return null;
        }
    }
}
