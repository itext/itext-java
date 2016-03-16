package com.itextpdf.kernel.pdf.colorspace;

import com.itextpdf.kernel.pdf.PdfName;

abstract public class PdfDeviceCs extends PdfColorSpace<PdfName> {

    private static final long serialVersionUID = 6884911248656287064L;

	@Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return false;
    }


    public PdfDeviceCs(PdfName pdfObject) {
        super(pdfObject);
    }

    static public class Gray extends PdfDeviceCs {

        private static final long serialVersionUID = 2722906212276665191L;

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

        private static final long serialVersionUID = -1605044540582561428L;

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

        private static final long serialVersionUID = 2615036909699704719L;

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
