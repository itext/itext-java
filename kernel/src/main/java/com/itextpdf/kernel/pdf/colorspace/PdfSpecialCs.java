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
package com.itextpdf.kernel.pdf.colorspace;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.function.PdfFunction;

import java.util.Arrays;
import java.util.List;

public abstract class PdfSpecialCs extends PdfColorSpace {

    private static final long serialVersionUID = -2725455900398492836L;

    protected PdfSpecialCs(PdfArray pdfObject) {
        super(pdfObject);
    }

    /**
     * To manually flush a {@code PdfObject} behind this wrapper, you have to ensure
     * that this object is added to the document, i.e. it has an indirect reference.
     * Basically this means that before flushing you need to explicitly call {@link #makeIndirect(PdfDocument)}.
     * For example: wrapperInstance.makeIndirect(document).flush();
     * Note that not every wrapper require this, only those that have such warning in documentation.
     */
    @Override
    public void flush() {
        super.flush();
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }

    public static class Indexed extends PdfSpecialCs {
        
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

        public PdfColorSpace getBaseCs() {
            return makeColorSpace(((PdfArray)getPdfObject()).get(1));
        }

        private static PdfArray getIndexedCsArray(PdfObject base, int hival, PdfString lookup) {
            PdfArray indexed = new PdfArray();
            indexed.add(PdfName.Indexed);
            indexed.add(base);
            indexed.add(new PdfNumber(hival));
            indexed.add(lookup.setHexWriting(true));
            return indexed;
        }

    }

    public static class Separation extends PdfSpecialCs {
        
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

        public PdfColorSpace getBaseCs() {
            return makeColorSpace(((PdfArray)getPdfObject()).get(2));
        }

        public PdfName getName() {
            return ((PdfArray)getPdfObject()).getAsName(1);
        }

        private static PdfArray getSeparationCsArray(PdfName name, PdfObject alternateSpace, PdfObject tintTransform) {
            PdfArray separation = new PdfArray();
            separation.add(PdfName.Separation);
            separation.add(name);
            separation.add(alternateSpace);
            separation.add(tintTransform);
            return separation;
        }

    }

    public static class DeviceN extends PdfSpecialCs {

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

        public PdfColorSpace getBaseCs() {
            return makeColorSpace(((PdfArray)getPdfObject()).get(2));
        }

        public PdfArray getNames() {
            return ((PdfArray)getPdfObject()).getAsArray(1);
        }

        protected static PdfArray getDeviceNCsArray(PdfArray names, PdfObject alternateSpace, PdfObject tintTransform) {
            PdfArray deviceN = new PdfArray();
            deviceN.add(PdfName.DeviceN);
            deviceN.add(names);
            deviceN.add(alternateSpace);
            deviceN.add(tintTransform);
            return deviceN;
        }

    }

    public static class NChannel extends DeviceN {
        
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

        protected static PdfArray getNChannelCsArray(PdfArray names, PdfObject alternateSpace, PdfObject tintTransform, PdfDictionary attributes) {
            PdfArray nChannel = getDeviceNCsArray(names, alternateSpace, tintTransform);
            nChannel.add(attributes);
            return nChannel;
        }

    }

    public static class Pattern extends PdfColorSpace {

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
    }

    public static class UncoloredTilingPattern extends Pattern {

        private static final long serialVersionUID = -9030226298201261021L;

        /**
         * To manually flush a {@code PdfObject} behind this wrapper, you have to ensure
         * that this object is added to the document, i.e. it has an indirect reference.
         * Basically this means that before flushing you need to explicitly call {@link #makeIndirect(PdfDocument)}.
         * For example: wrapperInstance.makeIndirect(document).flush();
         * Note that not every wrapper require this, only those that have such warning in documentation.
         */
        @Override
        public void flush() {
            super.flush();
        }

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
