package com.itextpdf.canvas.image;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.basics.image.Image;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.xobject.PdfFormXObject;
import com.itextpdf.core.pdf.xobject.PdfXObject;

import java.io.IOException;
import java.io.InputStream;

public class WmfImageHelper {

    /** Scales the WMF font size. The default value is 0.86. */
    public static float wmfFontCorrection = 0.86f;


    private WmfImage wmf;

    private float plainWidth;
    private float plainHeight;


    public WmfImageHelper(Image wmf) {
        if (wmf.getOriginalType() != Image.WMF)
            throw new IllegalArgumentException("WMF image expected");
        this.wmf = (WmfImage)wmf;
        processParameters();
    }

    /**
     * This method checks if the image is a valid WMF and processes some parameters.
     * @throws PdfException
     */
    private void processParameters() {
        InputStream is = null;
        try {
            String errorID;
            if (wmf.getData() == null){
                is = wmf.getUrl().openStream();
                errorID = wmf.getUrl().toString();
            }
            else{
                is = new java.io.ByteArrayInputStream(wmf.getData());
                errorID = "Byte array";
            }
            InputMeta in = new InputMeta(is);
            if (in.readInt() != 0x9AC6CDD7)	{
                throw new PdfException("1.is.not.a.valid.placeable.windows.metafile", errorID);
            }
            in.readWord();
            int left = in.readShort();
            int top = in.readShort();
            int right = in.readShort();
            int bottom = in.readShort();
            int inch = in.readWord();
            wmf.setDpi(72, 72);
            wmf.setHeight((float) (bottom - top) / inch * 72f);
            wmf.setWidth((float) (right - left) / inch * 72f);
        } catch (IOException e) {
            throw new PdfException(PdfException.WmfImageException);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) { }
            }
        }
    }

    public PdfXObject createPdfForm(PdfDocument document) {
        PdfFormXObject pdfForm = new PdfFormXObject(new Rectangle(0, 0, wmf.getWidth(), wmf.getHeight()));
        PdfCanvas canvas = new PdfCanvas(pdfForm, document);

        InputStream is = null;
        try {
            if (wmf.getData() == null){
                is = wmf.getUrl().openStream();
            }
            else{
                is = new java.io.ByteArrayInputStream(wmf.getData());
            }
            MetaDo meta = new MetaDo(is, canvas);
            meta.readAll();
        } catch (IOException e) {
            throw new PdfException(PdfException.WmfImageException, e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) { }
            }
        }
        return pdfForm;
    }
}
