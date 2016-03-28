package com.itextpdf.kernel.pdf.colorspace;

import com.itextpdf.kernel.pdf.PdfName;

abstract public class PdfDeviceCs extends PdfColorSpace<PdfName> {

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return false;
    }


    public PdfDeviceCs(PdfName pdfObject) {
        super(pdfObject);
    }

    static public class Gray extends PdfDeviceCs {

        public Gray() {
            super(PdfName.DeviceGray);
        }

        @Override
        public int getNumberOfComponents() {
            return 1;
        }

        @Override
        public float[] getDefaultColorants() {
            return new float[getNumberOfComponents()];
        }
    }

    static public class Rgb extends PdfDeviceCs {

        public Rgb() {
            super(PdfName.DeviceRGB);
        }

        @Override
        public int getNumberOfComponents() {
            return 3;
        }

        @Override
        public float[] getDefaultColorants() {
            return new float[getNumberOfComponents()];
        }
    }

    static public class Cmyk extends PdfDeviceCs {

        public Cmyk() {
            super(PdfName.DeviceCMYK);
        }

        @Override
        public int getNumberOfComponents() {
            return 4;
        }

        @Override
        public float[] getDefaultColorants() {
            return new float[]{0, 0, 0, 1};
        }
    }


}
