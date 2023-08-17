package com.itextpdf.kernel.utils.annotationsflattening;

import com.itextpdf.kernel.pdf.PdfName;

import java.util.HashMap;
import java.util.function.Supplier;

public class PdfAnnotationFlattenFactory {
    private static final HashMap<PdfName, Supplier<IAnnotationFlattener>> map;
    private static final PdfName UNKNOWN = new PdfName("Unknown");

    static {
        map = new HashMap<>();
        map.put(PdfName.Link, () -> new DefaultAnnotationFlattener());
        map.put(PdfName.Popup, () -> new NotSupportedFlattener());
        map.put(PdfName.Widget, () -> new NotSupportedFlattener());
        map.put(PdfName.Screen, () -> new NotSupportedFlattener());
        map.put(PdfName._3D, () -> new NotSupportedFlattener());
        map.put(PdfName.Highlight, () -> new HighLightTextMarkupAnnotationFlattener());
        map.put(PdfName.Underline, () -> new UnderlineTextMarkupAnnotationFlattener());
        map.put(PdfName.Squiggly, () -> new SquigglyTextMarkupAnnotationFlattener());
        map.put(PdfName.StrikeOut, () -> new StrikeOutTextMarkupAnnotationFlattener());
        map.put(PdfName.Caret, () -> new NotSupportedFlattener());
        map.put(PdfName.Text, () -> new NotSupportedFlattener());
        map.put(PdfName.Sound, () -> new NotSupportedFlattener());
        map.put(PdfName.Stamp, () -> new NotSupportedFlattener());
        map.put(PdfName.FileAttachment, () -> new NotSupportedFlattener());
        map.put(PdfName.Ink, () -> new NotSupportedFlattener());
        map.put(PdfName.PrinterMark, () -> new NotSupportedFlattener());
        map.put(PdfName.TrapNet, () -> new NotSupportedFlattener());
        map.put(PdfName.FreeText, () -> new NotSupportedFlattener());
        map.put(PdfName.Square, () -> new NotSupportedFlattener());
        map.put(PdfName.Circle, () -> new NotSupportedFlattener());
        map.put(PdfName.Line, () -> new NotSupportedFlattener());
        map.put(PdfName.Polygon, () -> new NotSupportedFlattener());
        map.put(PdfName.PolyLine, () -> new NotSupportedFlattener());
        map.put(PdfName.Redact, () -> new NotSupportedFlattener());
        map.put(PdfName.Watermark, () -> new NotSupportedFlattener());
        // To allow for the unknown subtype
        map.put(UNKNOWN, () -> new NotSupportedFlattener());
    }


    /**
     * Creates a new {@link PdfAnnotationFlattenFactory} instance.
     */
    public PdfAnnotationFlattenFactory() {
        // Empty constructor
    }

    /**
     * Gets the annotation flatten worker for the specified annotation subtype.
     *
     * @param name the annotation subtype. If the subtype is unknown, the worker for the null type will be returned.
     *
     * @return the annotation flatten worker
     */
    public IAnnotationFlattener getAnnotationFlattenWorker(PdfName name) {
        Supplier<IAnnotationFlattener> worker = map.get(name);
        if (worker == null) {
            worker = map.get(UNKNOWN);
        }
        return worker.get();
    }
}
