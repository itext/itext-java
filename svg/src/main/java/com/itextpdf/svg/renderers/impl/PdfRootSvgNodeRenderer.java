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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.svg.exceptions.SvgExceptionMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;

import java.util.Map;

/**
 * Root renderer responsible for applying the initial axis-flipping transform
 */
public class PdfRootSvgNodeRenderer implements ISvgNodeRenderer {

    ISvgNodeRenderer subTreeRoot;

    /**
     * Creates a {@link PdfRootSvgNodeRenderer} instance.
     * @param subTreeRoot root of the subtree
     */
    public PdfRootSvgNodeRenderer(ISvgNodeRenderer subTreeRoot){
        this.subTreeRoot = subTreeRoot;
        subTreeRoot.setParent(this);
    }

    @Override
    public void setParent(ISvgNodeRenderer parent) {
        // Do nothing because it is root
    }

    @Override
    public ISvgNodeRenderer getParent() {
        return null;
    }

    @Override
    public void draw(SvgDrawContext context) {
        //Set viewport and transformation for pdf-context
        context.addViewPort(calculateViewPort(context));
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

    @Override
    public Map<String, String> getAttributeMapCopy() {
        return null;
    }

    @Override
    public Rectangle getObjectBoundingBox(SvgDrawContext context) {
        return null;
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

    static Rectangle calculateViewPort(SvgDrawContext context){
        float portX = 0f;
        float portY = 0f;
        float portWidth = 0f;
        float portHeight = 0f;

        PdfStream contentStream = context.getCurrentCanvas().getContentStream();

        if ( ! contentStream.containsKey(PdfName.BBox) ) {
            throw new SvgProcessingException(SvgExceptionMessageConstant.ROOT_SVG_NO_BBOX);
        }

        PdfArray bboxArray = contentStream.getAsArray(PdfName.BBox);

        portX = bboxArray.getAsNumber(0).floatValue();
        portY = bboxArray.getAsNumber(1).floatValue();
        portWidth = bboxArray.getAsNumber(2).floatValue() - portX;
        portHeight = bboxArray.getAsNumber(3).floatValue() - portY;

        return new Rectangle(portX, portY, portWidth, portHeight);
    }

    @Override
    public ISvgNodeRenderer createDeepCopy() {
        PdfRootSvgNodeRenderer copy = new PdfRootSvgNodeRenderer(subTreeRoot.createDeepCopy());
        return copy;
    }

}
