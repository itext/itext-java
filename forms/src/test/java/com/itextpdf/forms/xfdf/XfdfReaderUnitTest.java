package com.itextpdf.forms.xfdf;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.ByteArrayOutputStream;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(UnitTest.class)
public class XfdfReaderUnitTest extends ExtendedITextTest {

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.XFDF_NO_F_OBJECT_TO_COMPARE))
    public void xfdfSquareAnnotationWithoutFringe(){
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        pdfDocument.addNewPage();
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDocument, true);

        AnnotObject annotObject = new AnnotObject();
        annotObject.setName(XfdfConstants.SQUARE);
        annotObject.addAttribute(new AttributeObject(XfdfConstants.PAGE, "1"));
        annotObject.addAttribute(new AttributeObject(XfdfConstants.RECT, "493.399638,559.179024,571.790235,600.679928"));
        annotObject.addAttribute(new AttributeObject(XfdfConstants.COLOR, "#000000"));
        annotObject.addAttribute(new AttributeObject(XfdfConstants.TITLE, "Guest"));
        annotObject.addAttribute(new AttributeObject(XfdfConstants.FLAGS, "print"));
        annotObject.addAttribute(new AttributeObject(XfdfConstants.DATE, "D:20200123110420-05'00'"));
        annotObject.addAttribute(new AttributeObject(XfdfConstants.NAME, "436b0463-41e6-d3fe-b660-c3764226615b"));
        annotObject.addAttribute(new AttributeObject(XfdfConstants.CREATION_DATE, "D:20200123110418-05'00'"));
        annotObject.addAttribute(new AttributeObject(XfdfConstants.SUBJECT, "Rectangle"));

        AnnotsObject annotsObject = new AnnotsObject();
        annotsObject.addAnnot(annotObject);
        XfdfObject xfdfObject = new XfdfObject();
        xfdfObject.setAnnots(annotsObject);
        XfdfReader xfdfReader = new XfdfReader();

        xfdfReader.mergeXfdfIntoPdf(xfdfObject, pdfDocument, "smth");
        List<PdfAnnotation> annotations = pdfDocument.getPage(1).getAnnotations();

        Assert.assertNotNull(annotations);
        Assert.assertEquals(1, annotations.size());
        Assert.assertEquals(PdfName.Square, annotations.get(0).getSubtype());

        pdfDocument.close();
    }
}
