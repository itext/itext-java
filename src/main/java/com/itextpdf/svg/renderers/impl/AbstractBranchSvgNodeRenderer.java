package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfXObject;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;

/**
 * Abstract class that will be the superclass for any element that can function
 * as a parent.
 */
public abstract class AbstractBranchSvgNodeRenderer extends AbstractSvgNodeRenderer {

    /**
     * Method that will set properties to be inherited by this branch renderer's
     * children and will iterate over all children in order to draw them.
     *
     * @param context the object that knows the place to draw this element and
     * maintains its state
     */
    @Override
    public void draw(SvgDrawContext context) {
        PdfStream stream = new PdfStream();
        stream.put(PdfName.Type, PdfName.XObject);
        stream.put(PdfName.Subtype, PdfName.Form);
        stream.put(PdfName.BBox, new PdfArray(new Rectangle(1, 1, 1, 1))); // required
        PdfFormXObject xObject = (PdfFormXObject) PdfXObject.makeXObject(stream);

        PdfCanvas newCanvas = new PdfCanvas(xObject, context.getCurrentCanvas().getDocument());
        context.pushCanvas(newCanvas);
        for (ISvgNodeRenderer child : getChildren()) {
            child.draw(context);
        }
        context.popCanvas();
        context.getCurrentCanvas().addXObject(xObject, 1, 0, 0, 1, 0, 0);
    }
}
