package com.itextpdf.core.pdf.colorspace;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.function.PdfFunction;

import java.util.List;

abstract public class PdfSpecialCs extends PdfColorSpace<PdfArray> {

    public PdfSpecialCs(PdfArray pdfObject, PdfDocument document) throws PdfException {
        super(pdfObject, document);
    }

    static public class Indexed extends PdfSpecialCs {
        public Indexed(PdfArray pdfObject, PdfDocument document) throws PdfException {
            super(pdfObject, document);
        }

        public Indexed(PdfDocument document, PdfObject base, int hival, PdfString lookup) throws PdfException {
            this(getIndexedCsArray(base, hival, lookup), document);
        }

        @Override
        public int getNumOfComponents() throws PdfException {
            return 1;
        }

        public PdfColorSpace getBaseCs() throws PdfException {
            return makeColorSpace(((PdfArray) getPdfObject()).get(1), getDocument());
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
        public Separation(PdfArray pdfObject, PdfDocument document) throws PdfException {
            super(pdfObject, document);
        }

        public Separation(PdfDocument document, PdfName name, PdfObject alternateSpace, PdfObject tintTransform) throws PdfException {
            this(getSeparationCsArray(name, alternateSpace, tintTransform), document);
        }

        public Separation(PdfDocument document, String name, PdfColorSpace alternateSpace, PdfFunction tintTransform) throws PdfException {
            this(document, new PdfName(name), alternateSpace.getPdfObject(), tintTransform.getPdfObject());
            if (tintTransform.getInputSize() != 1 || tintTransform.getOutputSize() != alternateSpace.getNumOfComponents()) {
                throw new PdfException(PdfException.FunctionIsNotCompatibleWitColorSpace, this);
            }
        }

        @Override
        public int getNumOfComponents() throws PdfException {
            return 1;
        }

        public PdfColorSpace getBaseCs() throws PdfException {
            return makeColorSpace(((PdfArray) getPdfObject()).get(2), getDocument());
        }

        public PdfName getName() throws PdfException {
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

        public DeviceN(PdfArray pdfObject, PdfDocument document) throws PdfException {
            super(pdfObject, document);
            numOfComponents = pdfObject.getAsArray(1).size();
        }

        public DeviceN(PdfDocument document, PdfArray names, PdfObject alternateSpace, PdfObject tintTransform) throws PdfException {
            this(getDeviceNCsArray(names, alternateSpace, tintTransform), document);
        }

        public DeviceN(PdfDocument document, List<String> names, PdfColorSpace alternateSpace, PdfFunction tintTransform) throws PdfException {
            this(document, new PdfArray(names, true), alternateSpace.getPdfObject(), tintTransform.getPdfObject());
            if (tintTransform.getInputSize() != getNumOfComponents() || tintTransform.getOutputSize() != alternateSpace.getNumOfComponents()) {
                throw new PdfException(PdfException.FunctionIsNotCompatibleWitColorSpace, this);
            }
        }

        @Override
        public int getNumOfComponents() throws PdfException {
            return numOfComponents;
        }

        public PdfColorSpace getBaseCs() throws PdfException {
            return makeColorSpace(((PdfArray) getPdfObject()).get(2), getDocument());
        }

        public PdfArray getNames() throws PdfException {
            return ((PdfArray) getPdfObject()).getAsArray(1);
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
        public NChannel(PdfArray pdfObject, PdfDocument document) throws PdfException {
            super(pdfObject, document);
        }

        public NChannel(PdfDocument document, PdfArray names, PdfObject alternateSpace, PdfObject tintTransform, PdfDictionary attributes) throws PdfException {
            this(getNChannelCsArray(names, alternateSpace, tintTransform, attributes), document);
        }

        public NChannel(PdfDocument document, List<String> names, PdfColorSpace alternateSpace, PdfFunction tintTransform, PdfDictionary attributes) throws PdfException {
            this(document, new PdfArray(names, true), alternateSpace.getPdfObject(), tintTransform.getPdfObject(), attributes);
            if (tintTransform.getInputSize() != 1 || tintTransform.getOutputSize() != alternateSpace.getNumOfComponents()) {
                throw new PdfException(PdfException.FunctionIsNotCompatibleWitColorSpace, this);
            }
        }

        static protected PdfArray getNChannelCsArray(PdfArray names, PdfObject alternateSpace, PdfObject tintTransform, PdfDictionary attributes) {
            PdfArray nChannel = getDeviceNCsArray(names, alternateSpace, tintTransform);
            nChannel.add(attributes);
            return nChannel;
        }

    }


}
