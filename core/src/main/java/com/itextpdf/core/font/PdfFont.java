package com.itextpdf.core.font;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.font.FontConstants;
import com.itextpdf.basics.font.PdfEncodings;
import com.itextpdf.basics.font.Type1Font;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfObjectWrapper;

import java.io.IOException;

/**
 * Nothing here...
 * We do not yet know how the font class should look like.
 */
public class PdfFont extends PdfObjectWrapper<PdfDictionary> {

    public PdfFont(PdfDictionary pdfObject, PdfDocument pdfDocument) throws PdfException {
        super(pdfObject, pdfDocument);
        getPdfObject().put(PdfName.Type, PdfName.Font);
    }

    protected PdfFont(PdfDocument pdfDocument) throws PdfException {
        this(new PdfDictionary(), pdfDocument);
        getPdfObject().put(PdfName.Type, PdfName.Font);
    }

    public static PdfFont getDefaultFont(PdfDocument pdfDocument) throws PdfException, IOException {
        return new PdfType1Font(pdfDocument, new Type1Font(FontConstants.HELVETICA, ""));
    }

    /**
     * Converts the text into bytes to be placed in the document.
     * The conversion is done according to the font and the encoding and the characters
     * used are stored.
     * @param text the text to convert
     * @return the conversion
     */
    public byte[] convertToBytes(String text) {
        //TODO when implement document fonts, throw exception
        //throw new IllegalStateException();
        return PdfEncodings.convertToBytes(text, "");
    }

    /**
     * Returns the width of a certain character of this font.
     *
     * @param ch	a certain character.
     * @return a width in Text Space.
     */
    public float getWidth(int ch) {
        throw new IllegalStateException();
    }

    /**
     * Returns the width of a string of this font.
     *
     * @param s	a string content.
     * @return a width of string in Text Space.
     */
    public float getWidth(String s) {
        throw new IllegalStateException();
    }

    @Override
    public PdfFont copy(PdfDocument document) throws PdfException {
        return new PdfFont((PdfDictionary)getPdfObject().copy(document), document);
    }
}
