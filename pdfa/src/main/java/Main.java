import com.itextpdf.basics.font.FontConstants;
import com.itextpdf.basics.font.Type1Font;
import com.itextpdf.basics.io.ByteArrayOutputStream;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.core.font.PdfType0Font;
import com.itextpdf.core.font.PdfType1Font;
import com.itextpdf.core.pdf.*;
import com.itextpdf.core.xmp.XMPException;
import com.itextpdf.pdfa.PdfAConformanceLevel;
import com.itextpdf.pdfa.PdfADocument;
import com.itextpdf.pdfa.PdfOutputIntent;


import java.io.*;

/**
 * Created by user on 9/8/2015.
 */
public class Main {
    static final public String sourceFolder = "d:/java/itext6/pdfa/src/test/resources/com/itextpdf/pdfa/";
    public static void main(String args[]) throws IOException, XMPException {

        PdfWriter writer = new PdfWriter(new FileOutputStream("d:/fuck.pdf"));
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2A, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();

        doc.getCatalog().setLang(new PdfString("en-US"));
        doc.setTagged();
        doc.addNewPage();
        //doc.setXmpMetadata(new byte[10]);

        /*font.setSubset(true);
        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());
        canvas.saveState().beginText().moveText(36, 600).
                setFontAndSize(font, 12).showText("Hello World").
                endText().restoreState();
        canvas.release();*/
        doc.close();
    }
}


