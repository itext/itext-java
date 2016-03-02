package com.itextpdf.kernel.parser;

import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.geom.Path;

public class ClippingPathInfo implements EventData {
    private Path path;
    private Matrix ctm;

    /**
     * @param path The path to be rendered.
     * @param ctm  The path to be rendered.
     */
    public ClippingPathInfo(Path path, Matrix ctm) {
        this.path = path;
        this.ctm = ctm;
    }

    /**
     * @return The {@link Path} which represents current clipping path.
     */
    public Path getClippingPath() {
        return path;
    }

    /**
     * @return Current transformation matrix.
     */
    public Matrix getCtm() {
        return ctm;
    }
}
