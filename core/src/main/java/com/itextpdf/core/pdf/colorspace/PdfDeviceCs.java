package com.itextpdf.core.pdf.colorspace;

import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;

abstract public class PdfDeviceCs extends PdfColorSpace<PdfName> {


    public PdfDeviceCs(PdfName pdfObject) {
        super(pdfObject);
    }

    public PdfDeviceCs(PdfName pdfObject, PdfDocument pdfDocument) {
        super(pdfObject, pdfDocument);
    }

    static public class Gray extends PdfDeviceCs {

        public Gray() {
            super(PdfName.DeviceGray);
        }

        public Gray(PdfDocument pdfDocument) {
            super(PdfName.DeviceGray, pdfDocument);
        }

        @Override
        public int getNumOfComponents() {
            return 1;
        }
    }

    static public class Rgb extends PdfDeviceCs {

        public Rgb() {
            super(PdfName.DeviceRGB);
        }

        public Rgb(PdfDocument pdfDocument) {
            super(PdfName.DeviceRGB, pdfDocument);
        }

        @Override
        public int getNumOfComponents() {
            return 3;
        }
    }

    static public class Cmyk extends PdfDeviceCs {

        public Cmyk() {
            super(PdfName.DeviceCMYK);
        }

        public Cmyk(PdfDocument pdfDocument) {
            super(PdfName.DeviceCMYK, pdfDocument);
        }

        @Override
        public int getNumOfComponents() {
            return 4;
        }
    }


}
