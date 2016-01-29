package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.font.PdfFont;

/**
 * <CODE>PdfTextArray</CODE> defines an array with displacements and <CODE>PdfString</CODE>-objects.
 * <P>
 * A <CODE>PdfTextArray</CODE> is used with the operator <VAR>TJ</VAR> in <CODE>PdfCanvas</CODE>.
 * The first object in this array has to be a <CODE>PdfString</CODE>;
 * see reference manual version 1.3 section 8.7.5, pages 346-347.
 *       OR
 * see reference manual version 1.6 section 5.3.2, pages 378-379.
 * To emit a more efficient array, we consolidate repeated numbers or strings into single array entries.
 * "add( 50 ); add( -50 );" will REMOVE the combined zero from the array.
 */
public class PdfTextArray extends PdfArray {

    private Float lastNumber;
    private StringBuilder lastString;

    @Override
    public boolean add(PdfObject pdfObject) {
        if (pdfObject.isNumber()) {
            add(((PdfNumber)pdfObject).getFloatValue());
            return true;
        } else if (pdfObject instanceof PdfString) {
            add(((PdfString)pdfObject).getValueBytes());
            return true;
        }
        return false;
    }

    public boolean add(float number) {
        // adding zero doesn't modify the TextArray at all
        if (number != 0) {
            if (lastNumber != null) {
                lastNumber = number + lastNumber;
                if (lastNumber != 0) {
                    set(size() - 1, new PdfNumber(lastNumber));
                } else {
                    remove(size() - 1);
                }
            } else {
                lastNumber = number;
                super.add(new PdfNumber(lastNumber));
            }
            lastString = null;
            return true;
        }
        return false;
    }

    public boolean add(String text, PdfFont font) {
        // adding an empty string doesn't modify the TextArray at all
        return add(font.convertToBytes(text));
    }

    public boolean add(byte[] text) {
        return add(new PdfString(text).getValue());
    }

    protected boolean add(String text) {
        if (text.length() > 0) {
            if (lastString != null) {
                lastString.append(text);
                set(size() - 1, new PdfString(lastString.toString()));
            } else {
                lastString = new StringBuilder(text);
                super.add(new PdfString(lastString.toString()));
            }
            lastNumber = null;
            return true;
        }
        return false;
    }

}
