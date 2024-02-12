package com.itextpdf.pdfua.checkers.utils.tables;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.tagging.PdfStructureAttributes;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Class that has the result of the algorithm that checks the table for PDF/UA compliance.
 */
final class CellResultMatrix extends AbstractResultMatrix<Cell> {

    /**
     * Creates a new {@link CellResultMatrix} instance.
     *
     * @param cols  The number of columns in the table.
     * @param table The table that needs to be checked.
     */
    public CellResultMatrix(int cols, Table table) {
        super(cols, new TableCellIterator(table));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    int getCol(Cell cell) {
        return cell.getCol();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    int getRow(Cell cell) {
        PdfName location = this.iterator.getLocation();
        int row = cell.getRow();
        if (location == PdfName.TBody) {
            row += this.iterator.getAmountOfRowsHeader();
        }
        if (location == PdfName.TFoot) {
            row += this.iterator.getAmountOfRowsHeader();
            row += this.iterator.getAmountOfRowsBody();
        }

        return row;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRowspan(Cell data) {
        return data.getRowspan();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getColspan(Cell data) {
        return data.getColspan();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    List<byte[]> getHeaders(Cell cell) {
        PdfObject headerArray = getAttribute(cell.getAccessibilityProperties(), PdfName.Headers);
        if (headerArray == null) {
            return null;
        }
        //If it's not an array, we return an empty list to trigger failure.
        if (!headerArray.isArray()) {
            return new ArrayList<>();
        }
        PdfArray array = (PdfArray) headerArray;
        List<byte[]> result = new ArrayList<>();
        for (PdfObject pdfObject : array) {
            result.add(((PdfString) pdfObject).getValueBytes());
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    String getScope(Cell cell) {
        PdfName pdfStr = (PdfName) getAttribute(cell.getAccessibilityProperties(), PdfName.Scope);
        return pdfStr == null ? null : pdfStr.getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    byte[] getElementId(Cell cell) {
        return cell.getAccessibilityProperties().getStructureElementId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    String getRole(Cell cell) {
        return cell.getAccessibilityProperties().getRole();
    }

    private static PdfObject getAttribute(AccessibilityProperties props, PdfName name) {
        for (PdfStructureAttributes attributes : props.getAttributesList()) {
            PdfObject obj = attributes.getPdfObject().get(name);
            if (obj != null) {
                return obj;
            }
        }
        return null;
    }
}
