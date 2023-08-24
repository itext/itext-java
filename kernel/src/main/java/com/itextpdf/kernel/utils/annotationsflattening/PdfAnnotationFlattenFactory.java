/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
