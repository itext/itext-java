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
package com.itextpdf.kernel.pdf.canvas.parser.listener;

import com.itextpdf.kernel.pdf.canvas.parser.filter.IEventFilter;

/**
 * A text event listener which filters events on the fly before passing them on to the delegate.
 * The only difference from {@link FilteredEventListener} is that this class conveniently implements
 * {@link ITextExtractionStrategy} and can therefore used as a strategy on its own, apart from the inherited
 * function of filtering event appropriately to its delegates.
 */
public class FilteredTextEventListener extends FilteredEventListener implements ITextExtractionStrategy {
    /**
     * Constructs a {@link FilteredTextEventListener} instance with a {@link ITextExtractionStrategy} delegate.
     *
     * @param delegate  a delegate that fill be called when all the corresponding filters for an event pass
     * @param filterSet filters attached to the delegate that will be tested before passing an event on to the delegate
     */
    public FilteredTextEventListener(ITextExtractionStrategy delegate, IEventFilter... filterSet) {
        super(delegate, filterSet);
    }

    /**
     * As an resultant text we use the concatenation of all the resultant text of all the delegates that implement
     * {@link ITextExtractionStrategy}.
     * @return the resulting concatenation of the text extracted from the delegates
     */
    @Override
    public String getResultantText() {
        StringBuilder sb = new StringBuilder();
        for (IEventListener delegate : delegates) {
            if (delegate instanceof ITextExtractionStrategy) {
                sb.append(((ITextExtractionStrategy) delegate).getResultantText());
            }
        }
        return sb.toString();
    }

}
