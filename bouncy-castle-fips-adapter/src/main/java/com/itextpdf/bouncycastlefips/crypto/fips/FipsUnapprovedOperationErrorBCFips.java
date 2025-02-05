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
package com.itextpdf.bouncycastlefips.crypto.fips;

import com.itextpdf.commons.bouncycastle.crypto.fips.AbstractFipsUnapprovedOperationError;
import org.bouncycastle.crypto.fips.FipsUnapprovedOperationError;

import java.util.Objects;

/**
 * Wrapper class for {@link FipsUnapprovedOperationError}.
 */
public class FipsUnapprovedOperationErrorBCFips extends AbstractFipsUnapprovedOperationError {
    private final FipsUnapprovedOperationError error;

    /**
     * Creates new wrapper instance for {@link FipsUnapprovedOperationError}.
     *
     * @param error {@link FipsUnapprovedOperationError} to be wrapped
     */
    public FipsUnapprovedOperationErrorBCFips(FipsUnapprovedOperationError error) {
        this.error = error;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link FipsUnapprovedOperationError}.
     */
    public FipsUnapprovedOperationError getError() {
        return error;
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
        FipsUnapprovedOperationErrorBCFips that = (FipsUnapprovedOperationErrorBCFips) o;
        return Objects.equals(error, that.error);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(error);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return error.toString();
    }

    /**
     * Delegates {@code getMessage} method call to the wrapped exception.
     */
    @Override
    public String getMessage() {
        return error.getMessage();
    }
}
