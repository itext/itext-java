package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;

import java.util.Map;

/**
 * Root renderer responsible for applying the initial axis-flipping transform
 */
public class PdfRootSvgNodeRenderer implements ISvgNodeRenderer {

    ISvgNodeRenderer subTreeRoot;

    public PdfRootSvgNodeRenderer(ISvgNodeRenderer subTreeRoot){
        this.subTreeRoot = subTreeRoot;
        subTreeRoot.setParent(this);
    }

    @Override
    public void setParent(ISvgNodeRenderer parent) {

    }

    @Override
    public ISvgNodeRenderer getParent() {
        return null;
    }

    @Override
    public void draw(SvgDrawContext context) {
        //Set viewport and transformation for pdf-context
        context.addViewPort(this.calculateViewPort(context));
        PdfCanvas currentCanvas = context.getCurrentCanvas();
        currentCanvas.concatMatrix(this.calculateTransformation(context));
        currentCanvas.writeLiteral("% svg root\n");

        subTreeRoot.draw(context);

    }

    @Override
    public void setAttributesAndStyles(Map<String, String> attributesAndStyles) {

    }

    @Override
    public String getAttribute(String key) {
        return null;
    }

    @Override
    public void setAttribute(String key, String value) {

    }

    AffineTransform calculateTransformation(SvgDrawContext context){
        Rectangle viewPort = context.getCurrentViewPort();
        float horizontal = viewPort.getX();
        float vertical = viewPort.getY() + viewPort.getHeight();
        // flip coordinate space vertically and translate on the y axis with the viewport height
        AffineTransform transform = AffineTransform.getTranslateInstance(0,0); //Identity-transform
        transform.concatenate(AffineTransform.getTranslateInstance(horizontal,vertical));
                transform.concatenate(new AffineTransform(1,0,0,-1,0,0));

        return transform;
    }

    Rectangle calculateViewPort(SvgDrawContext context){
        float portX = 0f;
        float portY = 0f;
        float portWidth = 0f;
        float portHeight = 0f;

        PdfStream contentStream = context.getCurrentCanvas().getContentStream();

        if ( ! contentStream.containsKey(PdfName.BBox) ) {
            throw new SvgProcessingException(SvgLogMessageConstant.ROOT_SVG_NO_BBOX);
        }

        PdfArray bboxArray = contentStream.getAsArray(PdfName.BBox);

        portX = bboxArray.getAsNumber(0).floatValue();
        portY = bboxArray.getAsNumber(1).floatValue();
        portWidth = bboxArray.getAsNumber(2).floatValue() - portX;
        portHeight = bboxArray.getAsNumber(3).floatValue() - portY;

        return new Rectangle(portX, portY, portWidth, portHeight);
    }

}
