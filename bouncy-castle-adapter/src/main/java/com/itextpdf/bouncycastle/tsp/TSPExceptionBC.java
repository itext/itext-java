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
package com.itextpdf.bouncycastle.tsp;

import com.itextpdf.commons.bouncycastle.tsp.AbstractTSPException;

import java.util.Objects;
import org.bouncycastle.tsp.TSPException;

/**
 * Wrapper class for {@link org.bouncycastle.tsp.TSPException}.
 */
public class TSPExceptionBC extends AbstractTSPException {
    private final TSPException tspException;

    /**
     * Creates new wrapper instance for {@link TSPException}.
     *
     * @param tspException {@link TSPException} to be wrapped
     */
    public TSPExceptionBC(TSPException tspException) {
        super();
        this.tspException = tspException;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link TSPException}.
     */
    public TSPException getTSPException() {
        return tspException;
    }

    /**
     * Indicates whether some other object is "equal to" this one. Compares wrapped objects.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TSPExceptionBC that = (TSPExceptionBC) o;
        return Objects.equals(tspException, that.tspException);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(tspException);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return tspException.toString();
    }

    /**
     * Delegates {@code getMessage} method call to the wrapped exception.
     */
    @Override
    public String getMessage() {
        return tspException.getMessage();
    }
}
