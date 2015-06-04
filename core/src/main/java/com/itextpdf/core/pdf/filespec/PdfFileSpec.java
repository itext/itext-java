package com.itextpdf.core.pdf.filespec;

import com.itextpdf.basics.font.PdfEncodings;
import com.itextpdf.core.pdf.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


public class PdfFileSpec<T extends PdfObject> extends PdfObjectWrapper<T>  {

    public PdfFileSpec(T pdfObject) {
        super(pdfObject);
    }

    public PdfFileSpec(T pdfObject, PdfDocument pdfDocument) {
        super(pdfObject, pdfDocument);
    }

    public static PdfFileSpec createExternalFileSpec(PdfDocument doc, String filePath, boolean isUnicodeFileName) {
        PdfDictionary dict = new PdfDictionary();

        dict.put(PdfName.Type, PdfName.Filespec);
        dict.put(PdfName.F, new PdfString(filePath));
        dict.put(PdfName.UF, new PdfString(filePath, isUnicodeFileName ? PdfEncodings.UnicodeBig : PdfEncodings.PdfDocEncoding));

        PdfFileSpec fileSpec = new PdfFileSpec(dict, doc);

        return fileSpec;
    }

    public static PdfFileSpec createEmbeddedFileSpec(PdfDocument doc, byte[] fileStore, String fileDisplay, boolean isUnicodeFileName) throws FileNotFoundException {

        PdfStream stream = new PdfStream(doc, fileStore);

        return createEmbeddedFileSpec(doc, stream, fileDisplay, isUnicodeFileName);
    }

    public static PdfFileSpec createEmbeddedFileSpec(PdfDocument doc, String filePath, String fileDisplay, boolean isUnicodeFileName) throws FileNotFoundException {

        PdfStream stream = new PdfStream(doc, new FileInputStream(filePath));

        return createEmbeddedFileSpec(doc, stream, fileDisplay, isUnicodeFileName);
    }

    public static PdfFileSpec createEmbeddedFileSpec(PdfDocument doc, InputStream is, String fileDisplay, boolean isUnicodeFileName) throws FileNotFoundException {

        PdfStream stream = new PdfStream(doc, is);

        return createEmbeddedFileSpec(doc, stream, fileDisplay, isUnicodeFileName);
    }

    private static PdfFileSpec createEmbeddedFileSpec(PdfDocument doc, PdfStream stream, String fileDisplay, boolean isUnicodeFileName) throws FileNotFoundException {
        PdfDictionary dict = new PdfDictionary();

        stream.put(PdfName.Type, PdfName.EmbeddedFile);

        dict.put(PdfName.Type, PdfName.Filespec);
        dict.put(PdfName.F, new PdfString(fileDisplay));
        dict.put(PdfName.UF, new PdfString(fileDisplay, isUnicodeFileName ? PdfEncodings.UnicodeBig : PdfEncodings.PdfDocEncoding));

        PdfDictionary EF = new PdfDictionary();
        EF.put(PdfName.F, stream);
        EF.put(PdfName.UF, stream);
        dict.put(PdfName.EF, EF);

        return new PdfFileSpec(dict, doc);
    }

    public PdfFileSpec setFileIdentifier(PdfArray fileIdentifier){
        return put(PdfName.ID, fileIdentifier);
    }

    public PdfArray getFileIdentifier() {
        return ((PdfDictionary)getPdfObject()).getAsArray(PdfName.ID);
    }

    public PdfFileSpec setVolatile(PdfBoolean isVolatile){
        return put(PdfName.Volatile, isVolatile);
    }

    public PdfBoolean isVolatile() {
        return ((PdfDictionary)getPdfObject()).getAsBoolean(PdfName.Volatile);
    }
}
