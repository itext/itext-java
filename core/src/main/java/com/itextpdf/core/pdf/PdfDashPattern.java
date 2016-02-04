package com.itextpdf.core.pdf;

public class PdfDashPattern {

    /** This is the length of a dash. */
    private float dash = -1;

    /** This is the length of a gap. */
    private float gap = -1;

    /** This is the phase. */
    private float phase = -1;

    public PdfDashPattern(){
    }

    public PdfDashPattern(float dash) {
        this.dash = dash;
    }

    public PdfDashPattern(float dash, float gap) {
        this.dash = dash;
        this.gap = gap;
    }

    public PdfDashPattern(float dash, float gap, float phase) {
        this(dash, gap);
        this.phase = phase;
    }

    public float getDash() {
        return dash;
    }

    public float getGap() {
        return gap;
    }

    public float getPhase() {
        return phase;
    }
}
