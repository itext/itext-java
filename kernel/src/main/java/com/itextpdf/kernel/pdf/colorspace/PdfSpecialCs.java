package com.itextpdf.kernel.pdf.colorspace;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.function.PdfFunction;

import java.util.Arrays;
import java.util.List;

abstract public class PdfSpecialCs extends PdfColorSpace<PdfArray> {

    private static final long serialVersionUID = -2725455900398492836L;

	public PdfSpecialCs(PdfArray pdfObject) {
        super(pdfObject);
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }

    static public class Indexed extends PdfSpecialCs {
        
    	private static final long serialVersionUID = -1155418938167317916L;

		public Indexed(PdfArray pdfObject) {
            super(pdfObject);
        }

        public Indexed(PdfObject base, int hival, PdfString lookup) {
            this(getIndexedCsArray(base, hival, lookup));
        }

        @Override
        public int getNumberOfComponents() {
            return 1;
        }

        @Override
        public float[] getDefaultColorants() {
            return new float[getNumberOfComponents()];
        }

        public PdfColorSpace getBaseCs() {
            return makeColorSpace(getPdfObject().get(1));
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
        
		private static final long serialVersionUID = 4259327393838350842L;

		public Separation(PdfArray pdfObject) {
            super(pdfObject);
        }

        public Separation(PdfName name, PdfObject alternateSpace, PdfObject tintTransform) {
            this(getSeparationCsArray(name, alternateSpace, tintTransform));
        }

        public Separation(String name, PdfColorSpace alternateSpace, PdfFunction tintTransform) {
            this(new PdfName(name), alternateSpace.getPdfObject(), tintTransform.getPdfObject());
            if (!tintTransform.checkCompatibilityWithColorSpace(alternateSpace)) {
                throw new PdfException(PdfException.FunctionIsNotCompatibleWitColorSpace, this);
            }
        }

        @Override
        public int getNumberOfComponents() {
            return 1;
        }

        @Override
        public float[] getDefaultColorants() {
            return new float[]{1f};
        }

        public PdfColorSpace getBaseCs() {
            return makeColorSpace((getPdfObject()).get(2));
        }

        public PdfName getName() {
            return (getPdfObject()).getAsName(1);
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

        private static final long serialVersionUID = 4051693146595260270L;
		
        protected int numOfComponents = 0;

        public DeviceN(PdfArray pdfObject) {
            super(pdfObject);
            numOfComponents = pdfObject.getAsArray(1).size();
        }

        public DeviceN(PdfArray names, PdfObject alternateSpace, PdfObject tintTransform) {
            this(getDeviceNCsArray(names, alternateSpace, tintTransform));
        }

        public DeviceN(List<String> names, PdfColorSpace alternateSpace, PdfFunction tintTransform) {
            this(new PdfArray(names, true), alternateSpace.getPdfObject(), tintTransform.getPdfObject());
            if (tintTransform.getInputSize() != getNumberOfComponents() || tintTransform.getOutputSize() != alternateSpace.getNumberOfComponents()) {
                throw new PdfException(PdfException.FunctionIsNotCompatibleWitColorSpace, this);
            }
        }

        @Override
        public int getNumberOfComponents() {
            return numOfComponents;
        }

        @Override
        public float[] getDefaultColorants() {
            float[] defaultColorants = new float[getNumberOfComponents()];
            for (int i = 0; i < getNumberOfComponents(); i++) {
                defaultColorants[i] = 1;
            }
            return defaultColorants;
        }

        public PdfColorSpace getBaseCs() {
            return makeColorSpace(getPdfObject().get(2));
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
        
    	private static final long serialVersionUID = 5352964946869757972L;

		public NChannel(PdfArray pdfObject) {
            super(pdfObject);
        }

        public NChannel(PdfArray names, PdfObject alternateSpace, PdfObject tintTransform, PdfDictionary attributes) {
            this(getNChannelCsArray(names, alternateSpace, tintTransform, attributes));
        }

        public NChannel(List<String> names, PdfColorSpace alternateSpace, PdfFunction tintTransform, PdfDictionary attributes) {
            this(new PdfArray(names, true), alternateSpace.getPdfObject(), tintTransform.getPdfObject(), attributes);
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

        private static final long serialVersionUID = 8057478102447278706L;

		@Override
        protected boolean isWrappedObjectMustBeIndirect() {
            return false;
        }

        public Pattern() {
            super(PdfName.Pattern);
        }

        protected Pattern(PdfObject pdfObj) {
            super(pdfObj);
        }

        @Override
        public int getNumberOfComponents() {
            return 0;
        }

        @Override
        public float[] getDefaultColorants() {
            return new float[getNumberOfComponents()];
        }
    }

    static public class UncoloredTilingPattern extends Pattern {

        private static final long serialVersionUID = -9030226298201261021L;

		@Override
        protected boolean isWrappedObjectMustBeIndirect() {
            return true;
        }

        public UncoloredTilingPattern(PdfArray pdfObject) {
            super(pdfObject);
        }

        public UncoloredTilingPattern(PdfColorSpace underlyingColorSpace) {
            super(new PdfArray(Arrays.asList(PdfName.Pattern, underlyingColorSpace.getPdfObject())));
        }

        @Override
        public int getNumberOfComponents() {
            return PdfColorSpace.makeColorSpace(((PdfArray) getPdfObject()).get(1)).getNumberOfComponents();
        }

        public PdfColorSpace getUnderlyingColorSpace() {
            return PdfColorSpace.makeColorSpace(((PdfArray) getPdfObject()).get(1));
        }
    }

}
