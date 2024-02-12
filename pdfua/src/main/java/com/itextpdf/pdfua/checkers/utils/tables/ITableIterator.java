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
