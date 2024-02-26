package com.itextpdf.pdfua.checkers.utils.tables;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;

import java.util.ArrayList;
import java.util.List;

/**
 * The result matrix to validate PDF UA1 tables with based on the TagTreeStructure of the document.
 */
class StructTreeResultMatrix extends AbstractResultMatrix<PdfStructElem> {


    /**
     * Creates a new {@link StructTreeResultMatrix} instance.
     */
    public StructTreeResultMatrix(PdfStructElem elem) {
        super(new TableStructElementIterator(elem));
    }


    /**
     * {@inheritDoc}
     */
    @Override
    List<byte[]> getHeaders(PdfStructElem cell) {
        PdfObject object = cell.getAttributes(false);
        PdfArray pdfArr = null;
        if (object instanceof PdfArray) {
            PdfArray array = (PdfArray) object;
            for (PdfObject pdfObject : array) {
                if (pdfObject instanceof PdfDictionary) {
                    pdfArr = ((PdfDictionary) pdfObject).getAsArray(PdfName.Headers);
                }
            }
        } else if (object instanceof PdfDictionary) {
            pdfArr = ((PdfDictionary) object).getAsArray(PdfName.Headers);
        }
        if (pdfArr == null) {
            return null;
        }
        List<byte[]> list = new ArrayList<>();
        for (PdfObject pdfObject : pdfArr) {
            PdfString str = (PdfString) pdfObject;
            list.add(str.getValueBytes());
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    String getScope(PdfStructElem cell) {
        PdfObject object = cell.getAttributes(false);
        if (object instanceof PdfArray) {
            PdfArray array = (PdfArray) object;
            for (PdfObject pdfObject : array) {
                if (pdfObject instanceof PdfDictionary) {
                    PdfName f = ((PdfDictionary) pdfObject).getAsName(PdfName.Scope);
                    if (f != null) {
                        return f.getValue();
                    }
                }
            }
        } else if (object instanceof PdfDictionary) {
            PdfName f = ((PdfDictionary) object).getAsName(PdfName.Scope);
            if (f != null) {
                return f.getValue();
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    byte[] getElementId(PdfStructElem cell) {
        if (cell == null) {
            return null;
        }
        if (cell.getStructureElementId() == null) {
            return null;
        }
        return cell.getStructureElementId().getValueBytes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    String getRole(PdfStructElem cell) {
        PdfName role = cell.getRole();
        if (role != null) {
            return role.getValue();
        }
        return null;
    }

}
