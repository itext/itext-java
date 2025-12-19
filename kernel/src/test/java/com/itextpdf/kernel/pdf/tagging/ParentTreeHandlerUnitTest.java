package com.itextpdf.kernel.pdf.tagging;

import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayOutputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class ParentTreeHandlerUnitTest extends ExtendedITextTest {

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate =
                    KernelLogMessageConstant.DUPLICATE_STRUCT_PARENT_INDEX_IN_TAGGED_OBJECT_REFERENCES, count = 1)
    })
    public void duplicateStructParentIndexFromForeignStructTreeIsIgnoredTest() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            pdfDoc.setTagged();
            PdfPage page = pdfDoc.addNewPage();

            PdfStructTreeRoot structTreeRoot = pdfDoc.getStructTreeRoot();
            ParentTreeHandler handler = structTreeRoot.getParentTreeHandler();

            int structParentIndex = 10;

            PdfStructElem validParent = new PdfStructElem(pdfDoc, PdfName.Span, page);
            structTreeRoot.addKid(validParent);

            PdfDictionary validObj = new PdfDictionary();
            validObj.put(PdfName.StructParent, new PdfNumber(structParentIndex));

            PdfDictionary validObjRefDict = new PdfDictionary();
            validObjRefDict.put(PdfName.Pg, page.getPdfObject());
            validObjRefDict.put(PdfName.Obj, validObj);

            PdfObjRef validObjRef = new PdfObjRef(validObjRefDict, validParent);

            PdfDictionary foreignStructTreeRoot = new PdfDictionary();
            PdfDictionary foreignParentDict = new PdfDictionary();
            foreignParentDict.put(PdfName.P, foreignStructTreeRoot);
            PdfStructElem foreignParent = new PdfStructElem(foreignParentDict);

            PdfDictionary foreignObj = new PdfDictionary();
            foreignObj.put(PdfName.StructParent, new PdfNumber(structParentIndex));

            PdfDictionary foreignObjRefDict = new PdfDictionary();
            foreignObjRefDict.put(PdfName.Pg, page.getPdfObject());
            foreignObjRefDict.put(PdfName.Obj, foreignObj);

            PdfObjRef foreignObjRef = new PdfObjRef(foreignObjRefDict, foreignParent);

            handler.registerMcr(validObjRef);
            handler.registerMcr(foreignObjRef);

            PdfObjRef resolved = handler.findObjRefByStructParentIndex(page.getPdfObject(), structParentIndex);
            Assertions.assertNotNull(resolved);
            Assertions.assertSame(validObjRef.getPdfObject(), resolved.getPdfObject());
        }
    }
}
