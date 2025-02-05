/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.kernel.pdf.canvas.wmf;

import com.itextpdf.io.image.ImageType;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfXObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Helper class for the WmfImage implementation. Assists in the creation of a {@link com.itextpdf.kernel.pdf.xobject.PdfFormXObject}.
 */
public class WmfImageHelper {

    /** Scales the WMF font size. The default value is 0.86. */
    public static float wmfFontCorrection = 0.86f;


    private WmfImageData wmf;

    private float plainWidth;
    private float plainHeight;

    /**
     * Creates a helper instance.
     *
     * @param wmf the {@link WmfImageData} object
     */
    public WmfImageHelper(ImageData wmf) {
        if (wmf.getOriginalType() != ImageType.WMF)
            throw new IllegalArgumentException("WMF image expected");
        this.wmf = (WmfImageData)wmf;
        processParameters();
    }

    /**
     * This method checks if the image is a valid WMF and processes some parameters.
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
                throw new PdfException(KernelExceptionMessageConstant.NOT_A_VALID_PLACEABLE_WINDOWS_METAFILE, errorID);
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
            throw new PdfException(KernelExceptionMessageConstant.WMF_IMAGE_EXCEPTION);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) { }
            }
        }
    }

    /**
     * Create a PdfXObject based on the WMF image. The PdfXObject will have the dimensions of the
     * WMF image.
     *
     * @param document PdfDocument to add the PdfXObject to
     * @return PdfXObject based on the WMF image
     */
    public PdfXObject createFormXObject(PdfDocument document) {
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
            throw new PdfException(KernelExceptionMessageConstant.WMF_IMAGE_EXCEPTION, e);
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
