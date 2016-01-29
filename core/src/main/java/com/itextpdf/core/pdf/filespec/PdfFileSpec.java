package com.itextpdf.core.pdf.filespec;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.core.pdf.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


public class PdfFileSpec<T extends PdfObject> extends PdfObjectWrapper<T>  {

    public PdfFileSpec(T pdfObject) {
        super(pdfObject);
    }

    public static PdfFileSpec createExternalFileSpec(PdfDocument doc, String filePath, boolean isUnicodeFileName) {
        PdfDictionary dict = new PdfDictionary();

        dict.put(PdfName.Type, PdfName.Filespec);
        dict.put(PdfName.F, new PdfString(filePath));
        dict.put(PdfName.UF, new PdfString(filePath, isUnicodeFileName ? PdfEncodings.UnicodeBig : PdfEncodings.PdfDocEncoding));

        PdfFileSpec fileSpec = new PdfFileSpec<>(dict).makeIndirect(doc);

        return fileSpec;
    }

    public static PdfFileSpec createEmbeddedFileSpec(PdfDocument doc, byte[] fileStore, String description, String fileDisplay, String mimeType, PdfDictionary fileParameter, PdfName afRelationshipValue, boolean isUnicodeFileName) {
        PdfStream stream = new PdfStream(fileStore).makeIndirect(doc);
        PdfDictionary params = new PdfDictionary();

        if (fileParameter != null) {
            params.mergeDifferent(fileParameter);
        }
        if (!params.containsKey(PdfName.ModDate)) {
            params.put(PdfName.ModDate, new PdfDate().getPdfObject());
        }
        if (fileStore != null) {
            params.put(PdfName.Size, new PdfNumber(stream.getBytes().length));
            stream.put(PdfName.Params, params);
        }
        return createEmbeddedFileSpec(doc, stream, description, fileDisplay, mimeType, afRelationshipValue, isUnicodeFileName);
    }

    public static PdfFileSpec createEmbeddedFileSpec(PdfDocument doc, String filePath, String description, String fileDisplay, String mimeType, PdfName afRelationshipValue, boolean isUnicodeFileName) throws FileNotFoundException {
        PdfStream stream = new PdfStream(doc, new FileInputStream(filePath));
        return createEmbeddedFileSpec(doc, stream, description, fileDisplay, mimeType, afRelationshipValue, isUnicodeFileName);
    }

    public static PdfFileSpec createEmbeddedFileSpec(PdfDocument doc, InputStream is, String description, String fileDisplay, String mimeType, PdfName afRelationshipValue, boolean isUnicodeFileName) {
        PdfStream stream = new PdfStream(doc, is);
        return createEmbeddedFileSpec(doc, stream, description, fileDisplay, mimeType, afRelationshipValue, isUnicodeFileName);
    }

    private static PdfFileSpec createEmbeddedFileSpec(PdfDocument doc, PdfStream stream, String description, String fileDisplay, String mimeType, PdfName afRelationshipValue, boolean isUnicodeFileName) {
        PdfDictionary dict = new PdfDictionary();
        stream.put(PdfName.Type, PdfName.EmbeddedFile);
        if (afRelationshipValue != null) {
            dict.put(PdfName.AFRelationship, afRelationshipValue);
        } else {
            dict.put(PdfName.AFRelationship, PdfName.Unspecified);
        }

        if (mimeType != null) {
            stream.put(PdfName.Subtype, new PdfName(mimeType));
        }

        if (description != null) {
            dict.put(PdfName.Desc, new PdfString(description));
        }
        dict.put(PdfName.Type, PdfName.Filespec);
        dict.put(PdfName.F, new PdfString(fileDisplay));
        dict.put(PdfName.UF, new PdfString(fileDisplay, isUnicodeFileName ? PdfEncodings.UnicodeBig : PdfEncodings.PdfDocEncoding));

        PdfDictionary EF = new PdfDictionary();
        EF.put(PdfName.F, stream);
        EF.put(PdfName.UF, stream);
        dict.put(PdfName.EF, EF);

        return new PdfFileSpec<>(dict).makeIndirect(doc);
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
