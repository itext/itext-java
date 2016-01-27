package com.itextpdf.core.pdf.colorspace;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfNumber;
import com.itextpdf.core.pdf.PdfObject;
import com.itextpdf.core.pdf.PdfString;
import com.itextpdf.core.pdf.function.PdfFunction;

import java.util.Arrays;
import java.util.List;

abstract public class PdfSpecialCs extends PdfColorSpace<PdfArray> {

    public PdfSpecialCs(PdfArray pdfObject, PdfDocument document) {
        super(pdfObject);
        makeIndirect(document);
    }

    static public class Indexed extends PdfSpecialCs {
        public Indexed(PdfArray pdfObject, PdfDocument document) {
            super(pdfObject, document);
        }

        public Indexed(PdfDocument document, PdfObject base, int hival, PdfString lookup) {
            this(getIndexedCsArray(base, hival, lookup), document);
        }

        @Override
        public int getNumberOfComponents() {
            return 1;
        }

        public PdfColorSpace getBaseCs() {
            return makeColorSpace(getPdfObject().get(1), getDocument());
        }

        static private PdfArray getIndexedCsArray(PdfObject base, int hival, PdfString lookup) {
            PdfArray indexed = new PdfArray();
            indexed.add(PdfName.Indexed);
            indexed.add(base);
            indexed.add(new PdfNumber(hival));
            indexed.add(lookup.setHexWriting(true));
            return indexed;
        }

    }

    static public class Separation extends PdfSpecialCs {
        public Separation(PdfArray pdfObject, PdfDocument document) {
            super(pdfObject, document);
        }

        public Separation(PdfDocument document, PdfName name, PdfObject alternateSpace, PdfObject tintTransform) {
            this(getSeparationCsArray(name, alternateSpace, tintTransform), document);
        }

        public Separation(PdfDocument document, String name, PdfColorSpace alternateSpace, PdfFunction tintTransform) {
            this(document, new PdfName(name), alternateSpace.getPdfObject(), tintTransform.getPdfObject());
            if (!tintTransform.checkCompatibilityWithColorSpace(alternateSpace)) {
                throw new PdfException(PdfException.FunctionIsNotCompatibleWitColorSpace, this);
            }
        }

        @Override
        public int getNumberOfComponents() {
            return 1;
        }

        public PdfColorSpace getBaseCs() {
            return makeColorSpace(((PdfArray) getPdfObject()).get(2), getDocument());
        }

        public PdfName getName() {
            return ((PdfArray) getPdfObject()).getAsName(1);
        }

        static private PdfArray getSeparationCsArray(PdfName name, PdfObject alternateSpace, PdfObject tintTransform) {
            PdfArray separation = new PdfArray();
            separation.add(PdfName.Separation);
            separation.add(name);
            separation.add(alternateSpace);
            separation.add(tintTransform);
            return separation;
        }

    }

    static public class DeviceN extends PdfSpecialCs {

        protected int numOfComponents = 0;

        public DeviceN(PdfArray pdfObject, PdfDocument document) {
            super(pdfObject, document);
            numOfComponents = pdfObject.getAsArray(1).size();
        }

        public DeviceN(PdfDocument document, PdfArray names, PdfObject alternateSpace, PdfObject tintTransform) {
            this(getDeviceNCsArray(names, alternateSpace, tintTransform), document);
        }

        public DeviceN(PdfDocument document, List<String> names, PdfColorSpace alternateSpace, PdfFunction tintTransform) {
            this(document, new PdfArray(names, true), alternateSpace.getPdfObject(), tintTransform.getPdfObject());
            if (tintTransform.getInputSize() != getNumberOfComponents() || tintTransform.getOutputSize() != alternateSpace.getNumberOfComponents()) {
                throw new PdfException(PdfException.FunctionIsNotCompatibleWitColorSpace, this);
            }
        }

        @Override
        public int getNumberOfComponents() {
            return numOfComponents;
        }

        public PdfColorSpace getBaseCs() {
            return makeColorSpace(getPdfObject().get(2), getDocument());
        }

        public PdfArray getNames() {
            return getPdfObject().getAsArray(1);
        }

        static protected PdfArray getDeviceNCsArray(PdfArray names, PdfObject alternateSpace, PdfObject tintTransform) {
            PdfArray deviceN = new PdfArray();
            deviceN.add(PdfName.DeviceN);
            deviceN.add(names);
            deviceN.add(alternateSpace);
            deviceN.add(tintTransform);
            return deviceN;
        }

    }

    static public class NChannel extends DeviceN {
        public NChannel(PdfArray pdfObject, PdfDocument document) {
            super(pdfObject, document);
        }

        public NChannel(PdfDocument document, PdfArray names, PdfObject alternateSpace, PdfObject tintTransform, PdfDictionary attributes) {
            this(getNChannelCsArray(names, alternateSpace, tintTransform, attributes), document);
        }

        public NChannel(PdfDocument document, List<String> names, PdfColorSpace alternateSpace, PdfFunction tintTransform, PdfDictionary attributes) {
            this(document, new PdfArray(names, true), alternateSpace.getPdfObject(), tintTransform.getPdfObject(), attributes);
            if (tintTransform.getInputSize() != 1 || tintTransform.getOutputSize() != alternateSpace.getNumberOfComponents()) {
                throw new PdfException(PdfException.FunctionIsNotCompatibleWitColorSpace, this);
            }
        }

        static protected PdfArray getNChannelCsArray(PdfArray names, PdfObject alternateSpace, PdfObject tintTransform, PdfDictionary attributes) {
            PdfArray nChannel = getDeviceNCsArray(names, alternateSpace, tintTransform);
            nChannel.add(attributes);
            return nChannel;
        }

    }

    static public class Pattern extends PdfColorSpace<PdfObject> {
        public Pattern() {
            super(PdfName.Pattern);
        }

        protected Pattern(PdfObject pdfObj, PdfDocument pdfDoc) {
            super(pdfObj);
            makeIndirect(pdfDoc);
        }

        @Override
        public int getNumberOfComponents() {
            return 0;
        }
    }

    static public class UncoloredTilingPattern extends Pattern {

        public UncoloredTilingPattern(PdfArray pdfObject, PdfDocument pdfDocument) {
            super(pdfObject, pdfDocument);
        }

        public UncoloredTilingPattern(PdfDocument pdfDocument, PdfColorSpace underlyingColorSpace) {
            super(new PdfArray(Arrays.asList(PdfName.Pattern, underlyingColorSpace.getPdfObject())), pdfDocument);
        }

        @Override
        public int getNumberOfComponents() {
            return PdfColorSpace.makeColorSpace(((PdfArray) getPdfObject()).get(1), getDocument()).getNumberOfComponents();
        }

        public PdfColorSpace getUnderlyingColorSpace() {
            return PdfColorSpace.makeColorSpace(((PdfArray) getPdfObject()).get(1), getDocument());
        }
    }

}
