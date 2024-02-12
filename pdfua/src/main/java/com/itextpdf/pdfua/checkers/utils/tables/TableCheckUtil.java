package com.itextpdf.pdfua.checkers.utils.tables;

import com.itextpdf.layout.element.Table;

/**
 * Class that provides methods for checking PDF/UA compliance of table elements.
 */
public final class TableCheckUtil {

    /**
     * Creates a new {@link TableCheckUtil} instance.
     */
    private TableCheckUtil() {
        // Empty constructor
    }

    /**
     * Checks if the table is pdf/ua compliant.
     *
     * @param table the table to check.
     */
    public static void checkLayoutTable(Table table) {
        new CellResultMatrix(table.getNumberOfColumns(), table).checkValidTableTagging();
    }


}


