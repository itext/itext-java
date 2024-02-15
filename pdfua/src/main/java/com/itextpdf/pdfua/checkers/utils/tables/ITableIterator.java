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
package com.itextpdf.pdfua.checkers.utils.tables;

import com.itextpdf.kernel.pdf.PdfName;

/**
 * Interface that provides methods for iterating over the elements of a table.
 */
interface ITableIterator<T> {

    /**
     * Checks if there is a next element in the iteration.
     *
     * @return {@code true} if there is a next element, {@code false} otherwise.
     */
    boolean hasNext();


    /**
     * Gets the next element in the iteration.
     *
     * @return The next element.
     */
    T next();

    /**
     * Gets the number of rows in the body of the table.
     *
     * @return The number of rows in the body of the table.
     */
    int getAmountOfRowsBody();

    /**
     * Gets the number of rows in the header of the table.
     *
     * @return The number of rows in the header of the table.
     */
    int getAmountOfRowsHeader();

    /**
     * Gets the number of rows in the footer of the table.
     *
     * @return The number of rows in the footer of the table.
     */
    int getAmountOfRowsFooter();

    /**
     * Gets the location of the current element in the table.
     *
     * @return The location of the current element in the table.
     */
    PdfName getLocation();

}
