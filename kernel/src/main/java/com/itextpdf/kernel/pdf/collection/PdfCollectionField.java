package com.itextpdf.kernel.pdf.collection;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.*;

public class PdfCollectionField extends PdfObjectWrapper<PdfDictionary> {

    private static final long serialVersionUID = 4766153544105870238L;
	
    /** A possible type of collection field. */
    public final static int TEXT = 0;
    /** A possible type of collection field. */
    public final static int DATE = 1;
    /** A possible type of collection field. */
    public final static int NUMBER = 2;
    /** A possible type of collection field. */
    public final static int FILENAME = 3;
    /** A possible type of collection field. */
    public final static int DESC = 4;
    /** A possible type of collection field. */
    public final static int MODDATE = 5;
    /** A possible type of collection field. */
    public final static int CREATIONDATE = 6;
    /** A possible type of collection field. */
    public final static int SIZE = 7;

    protected int subType;

    protected PdfCollectionField(PdfDictionary pdfObject) {
        super(pdfObject);
        String subType = pdfObject.getAsName(PdfName.Subtype).getValue();
        switch (subType) {
            case "D":
                this.subType = DATE;
                break;
            case "N":
                this.subType = NUMBER;
                break;
            case "F":
                this.subType = FILENAME;
                break;
            case "Desc":
                this.subType = DESC;
                break;
            case "ModDate":
                this.subType = MODDATE;
                break;
            case "CreationDate":
                this.subType = CREATIONDATE;
                break;
            case "Size":
                this.subType = SIZE;
                break;
            default:
                this.subType = TEXT;
                break;
        }
    }

    /**
     * Creates a PdfCollectionField.
     * @param name		the field name
     * @param subType	the field subtype
     */
    public PdfCollectionField(String name, int subType) {
        super(new PdfDictionary());
        put(PdfName.N, new PdfString(name));
        this.subType = subType;
        switch (subType) {
            default:
                put(PdfName.Subtype, PdfName.S);
                break;
            case DATE:
                put(PdfName.Subtype, PdfName.D);
                break;
            case NUMBER:
                put(PdfName.Subtype, PdfName.N);
                break;
            case FILENAME:
                put(PdfName.Subtype, PdfName.F);
                break;
            case DESC:
                put(PdfName.Subtype, PdfName.Desc);
                break;
            case MODDATE:
                put(PdfName.Subtype, PdfName.ModDate);
                break;
            case CREATIONDATE:
                put(PdfName.Subtype, PdfName.CreationDate);
                break;
            case SIZE:
                put(PdfName.Subtype, PdfName.Size);
                break;
        }
    }

    /**
     * The relative order of the field name. Fields are sorted in ascending order.
     * @param order a number indicating the order of the field
     * @return
     */
    public PdfCollectionField setOrder(int order) {
        return put(PdfName.O, new PdfNumber(order));
    }

    public PdfNumber getOrder() {
        return getPdfObject().getAsNumber(PdfName.O);
    }

    /**
     * Sets the initial visibility of the field.
     * @param visible
     * @return
     */
    public PdfCollectionField setVisibility(boolean visible) {
        return put(PdfName.V, new PdfBoolean(visible));
    }

    public PdfBoolean getVisibility() {
        return getPdfObject().getAsBoolean(PdfName.V);
    }

    /**
     * Indication if the field value should be editable in the viewer.
     * @param editable
     * @return
     */
    public PdfCollectionField setEditable(boolean editable) {
        return put(PdfName.E, new PdfBoolean(editable));
    }

    public PdfBoolean getEditable() {
        return getPdfObject().getAsBoolean(PdfName.E);
    }

    public PdfObject getValue(String value) {
        switch(subType) {
            case TEXT:
                return new PdfString(value);
            case DATE:
                return new PdfDate(PdfDate.decode(value)).getPdfObject();
            case NUMBER:
                return new PdfNumber(Double.parseDouble(value.trim()));
        }
        throw new PdfException(PdfException.IsNotAnAcceptableValueForTheField).setMessageParams(value, getPdfObject().getAsName(PdfName.N).getValue());
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return false;
    }
}
