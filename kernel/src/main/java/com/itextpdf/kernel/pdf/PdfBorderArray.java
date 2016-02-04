package com.itextpdf.kernel.pdf;

public class PdfBorderArray extends PdfObjectWrapper<PdfArray> {

    public PdfBorderArray(float hRadius, float vRadius, float width) {
        this(hRadius, vRadius, width, null);
    }

    public PdfBorderArray(float hRadius, float vRadius, float width, PdfDashPattern dash) {
        super(new PdfArray(new float[]{hRadius, vRadius, width}));
        if (dash != null) {
            PdfArray dashArray = new PdfArray();
            add(dashArray);
            if (dash.getDash() >= 0) {
                dashArray.add(new PdfNumber(dash.getDash()));
            }
            if (dash.getGap() >= 0) {
                dashArray.add(new PdfNumber(dash.getGap()));
            }
            if (dash.getPhase() >= 0) {
                add(new PdfNumber(dash.getPhase()));
            }
        }
    }
}
