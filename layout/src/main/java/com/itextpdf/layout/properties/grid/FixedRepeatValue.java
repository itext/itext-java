/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.layout.properties.grid;

import java.util.List;

/**
 * This class represents an fixed-repeat template value.
 * This value is preprocessed before grid sizing algorithm so its only exists at template level.
 */
public class FixedRepeatValue extends TemplateValue {
    private final List<GridValue> values;
    private final int repeatCount;

    /**
     * Create a new fixed-repeat value
     *
     * @param repeatCount number of repetitions
     * @param values template values to repeat
     */
    public FixedRepeatValue(int repeatCount, List<GridValue> values) {
        super(ValueType.FIXED_REPEAT);
        this.values = values;
        this.repeatCount = repeatCount;
    }

    /**
     * Gets template values which should be repeated.
     *
     * @return template values list
     */
    public List<GridValue> getValues() {
        return values;
    }

    /**
     * Gets number of template values repetitions.
     *
     * @return number of template values repetitions
     */
    public int getRepeatCount() {
        return repeatCount;
    }
}
