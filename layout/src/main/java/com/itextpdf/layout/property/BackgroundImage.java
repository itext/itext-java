package com.itextpdf.layout.property;

import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;

public class BackgroundImage {
    protected PdfImageXObject image;
    protected boolean repeatX;
    protected boolean repeatY;

    public BackgroundImage(PdfImageXObject image) {
        this(image, true, true);
    }

    public BackgroundImage(PdfImageXObject image, boolean repeatX, boolean repeatY) {
        this.image = image;
        this.repeatX = repeatX;
        this.repeatY = repeatY;
    }

    public PdfImageXObject getImage() {
        return image;
    }

    public boolean isRepeatX() {
        return repeatX;
    }

    public boolean isRepeatY() {
        return repeatY;
    }
}
