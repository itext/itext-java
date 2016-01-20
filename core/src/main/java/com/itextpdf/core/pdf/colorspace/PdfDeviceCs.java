package com.itextpdf.core.pdf.colorspace;

import com.itextpdf.core.pdf.PdfName;

abstract public class PdfDeviceCs extends PdfColorSpace<PdfName> {


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
    }

    static public class Rgb extends PdfDeviceCs {

        public Rgb() {
            super(PdfName.DeviceRGB);
        }

        @Override
        public int getNumberOfComponents() {
            return 3;
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
    }


}
