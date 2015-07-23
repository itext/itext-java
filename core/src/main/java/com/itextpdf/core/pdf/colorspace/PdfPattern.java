package com.itextpdf.core.pdf.colorspace;

import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.*;

abstract public class PdfPattern<T extends PdfDictionary> extends PdfObjectWrapper<T> {

    public PdfPattern(T pdfObject) {
        super(pdfObject);
    }

    public static PdfPattern getPatternInstance(PdfDictionary pdfObject, PdfDocument document) {
        PdfNumber type = pdfObject.getAsNumber(PdfName.PatternType);
        if (new PdfNumber(1).equals(type) && pdfObject instanceof PdfStream)
            return new Tiling((PdfStream)pdfObject).makeIndirect(document);
        else if (new PdfNumber(2).equals(type))
            return new Shading(pdfObject).makeIndirect(document);
        throw new IllegalArgumentException("pdfObject");
    }

    public PdfArray getMatrix() {
        return getPdfObject().getAsArray(PdfName.Matrix);
    }

    public void setMatrix(PdfArray matrix) {
        getPdfObject().put(PdfName.Matrix, matrix);
        setModified();
    }

    public static class Tiling extends PdfPattern<PdfStream> {

        private PdfResources resources = null;

        public static class PaintType {
            public static final int Colored = 1;
            public static final int Uncolored = 2;
        }

        public static class TilingType {
            public static final int ConstantSpacing = 1;
            public static final int NoDistortion = 2;
            public static final int ConstantSpacingAndFasterTiling = 3;
        }

        public Tiling(PdfStream pdfObject) {
            super(pdfObject);
        }

        public Tiling(float width, float height) {
            this(width, height, true);
        }

        public Tiling(float width, float height, boolean colored) {
            this(new Rectangle(width, height), colored);
        }

        public Tiling(Rectangle bbox) {
            this(bbox, true);
        }

        public Tiling(Rectangle bbox, boolean colored) {
            this(bbox, bbox.getWidth(), bbox.getHeight(), colored);
        }

        public Tiling(float width, float height, float xStep, float yStep) {
            this(width, height, xStep, yStep, true);
        }

        public Tiling(float width, float height, float xStep, float yStep, boolean colored) {
            this(new Rectangle(width, height), xStep, yStep, colored);
        }

        public Tiling(Rectangle bbox, float xStep, float yStep) {
            this(bbox, xStep, yStep, true);
        }

        public Tiling(Rectangle bbox, float xStep, float yStep, boolean colored) {
            super(new PdfStream());
            getPdfObject().put(PdfName.Type, PdfName.Pattern);
            getPdfObject().put(PdfName.PatternType, new PdfNumber(1));
            getPdfObject().put(PdfName.PaintType, new PdfNumber(colored ? PaintType.Colored : PaintType.Uncolored));
            getPdfObject().put(PdfName.TilingType, new PdfNumber(TilingType.ConstantSpacing));
            getPdfObject().put(PdfName.BBox, new PdfArray(bbox));
            getPdfObject().put(PdfName.XStep, new PdfNumber(xStep));
            getPdfObject().put(PdfName.YStep, new PdfNumber(yStep));
            resources = new PdfResources();
            getPdfObject().put(PdfName.Resources, resources.getPdfObject());
        }

        public boolean isColored() {
            return getPdfObject().getAsNumber(PdfName.PaintType).getIntValue() == PaintType.Colored;
        }

        public void setColored(boolean colored) {
            getPdfObject().put(PdfName.PaintType, new PdfNumber(colored ? PaintType.Colored : PaintType.Uncolored));
            setModified();
        }

        public int getTilingType() {
            return getPdfObject().getAsNumber(PdfName.TilingType).getIntValue();
        }

        public void setTilingType(int tilingType) {
            if (tilingType != TilingType.ConstantSpacing && tilingType != TilingType.NoDistortion &&
                    tilingType != TilingType.ConstantSpacingAndFasterTiling)
                throw new IllegalArgumentException("tilingType");
            getPdfObject().put(PdfName.TilingType, new PdfNumber(tilingType));
            setModified();
        }

        public Rectangle getBBox() {
            return getPdfObject().getAsArray(PdfName.BBox).toRectangle();
        }

        public void setBBox(Rectangle bbox) {
            getPdfObject().put(PdfName.BBox, new PdfArray(bbox));
            setModified();
        }

        public float getXStep() {
            return getPdfObject().getAsNumber(PdfName.XStep).getFloatValue();
        }

        public void setXStep(float xStep) {
            getPdfObject().put(PdfName.XStep, new PdfNumber(xStep));
            setModified();
        }

        public float getYStep() {
            return getPdfObject().getAsNumber(PdfName.YStep).getFloatValue();
        }

        public void setYStep(float yStep) {
            getPdfObject().put(PdfName.YStep, new PdfNumber(yStep));
            setModified();
        }

        public PdfResources getResources() {
            if (this.resources == null) {
                PdfDictionary resources = getPdfObject().getAsDictionary(PdfName.Resources);
                if (resources == null) {
                    resources = new PdfDictionary();
                    getPdfObject().put(PdfName.Resources, resources);
                }
                this.resources = new PdfResources(resources);
            }
            return resources;
        }

        @Override
        public void flush() {
            resources = null;
            super.flush();
        }
    }

    public static class Shading extends PdfPattern<PdfDictionary> {

        public Shading(PdfDictionary pdfObject) {
            super(pdfObject);
        }

        public Shading(com.itextpdf.core.pdf.colorspace.PdfShading shading) {
            super(new PdfDictionary());
            getPdfObject().put(PdfName.Type, PdfName.Pattern);
            getPdfObject().put(PdfName.PatternType, new PdfNumber(2));
            getPdfObject().put(PdfName.Shading, shading.getPdfObject());
        }

        public PdfDictionary getShading() {
            return (PdfDictionary) getPdfObject().get(PdfName.Shading);
        }

        public void setShading(com.itextpdf.core.pdf.colorspace.PdfShading shading) {
            getPdfObject().put(PdfName.Shading, shading.getPdfObject());
            setModified();
        }

        public void setShading(PdfDictionary shading) {
            getPdfObject().put(PdfName.Shading, shading);
            setModified();
        }
    }
}