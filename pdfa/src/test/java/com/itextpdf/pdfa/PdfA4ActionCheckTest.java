/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.pdfa;


import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.forms.form.element.CheckBox;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfAConformance;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfCircleAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLineAnnotation;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.kernel.pdf.filespec.PdfStringFS;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.pdfa.exceptions.PdfAConformanceException;
import com.itextpdf.pdfa.exceptions.PdfaExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.pdfa.VeraPdfValidator; // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class PdfA4ActionCheckTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/pdfa/";
    private static final String CMP_FOLDER = SOURCE_FOLDER + "cmp/PdfA4ActionCheckTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/pdfa/PdfA4ActionCheckTest/";

    @BeforeAll
    public static void beforeClass() throws FileNotFoundException {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void pdfA4ForbiddenActions_LAUNCH_ActionToPage_Test() {
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> generatePdfADocument(PdfAConformance.PDF_A_4, null, (doc) -> {
                    doc.getFirstPage()
                            .setAdditionalAction(PdfName.O, PdfAction.createLaunch(new PdfStringFS("launch.sh")));
                }));
        Assertions.assertEquals(PdfaExceptionMessageConstant.PAGE_AA_DICTIONARY_SHALL_CONTAIN_ONLY_ALLOWED_KEYS,
                e.getMessage());
    }

    @Test
    public void pdfA4ForbiddenActions_SOUND_ActionToPage_Test() {
        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> {
            generatePdfADocument(PdfAConformance.PDF_A_4, null, (doc) -> {
                InputStream is = null;
                try {
                    is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sample.aif");
                } catch (IOException er) {
                    Assertions.fail(er.getMessage());
                }
                PdfStream sound1 = new PdfStream(doc, is);
                sound1.put(PdfName.R, new PdfNumber(32117));
                sound1.put(PdfName.E, PdfName.Signed);
                sound1.put(PdfName.B, new PdfNumber(16));
                sound1.put(PdfName.C, new PdfNumber(1));

                doc.addNewPage().setAdditionalAction(PdfName.O, PdfAction.createSound(sound1));
            });
        });
        Assertions.assertEquals(PdfaExceptionMessageConstant.PAGE_AA_DICTIONARY_SHALL_CONTAIN_ONLY_ALLOWED_KEYS,
                e.getMessage());
    }

    @Test
    public void pdfA4ForbiddenActions_MOVIE_ActionToPage_Test() throws FileNotFoundException {
        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> {
            generatePdfADocument(PdfAConformance.PDF_A_4, null, (doc) -> {
                doc.addNewPage()
                        .setAdditionalAction(PdfName.O, PdfAction.createMovie(null, "Some movie", PdfName.Play));
            });
        });
        Assertions.assertEquals(PdfaExceptionMessageConstant.PAGE_AA_DICTIONARY_SHALL_CONTAIN_ONLY_ALLOWED_KEYS,
                e.getMessage());
    }

    @Test
    public void pdfA4ForbiddenActions_RESETFORM_ActionToPage_Test() throws FileNotFoundException {
        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> {
            generatePdfADocument(PdfAConformance.PDF_A_4, null, (doc) -> {
                CheckBox checkBox = new CheckBox("test");
                checkBox.setChecked(true);
                Document document = new Document(doc);
                document.add(checkBox);
                doc.addNewPage().setAdditionalAction(PdfName.O, PdfAction.createResetForm(new Object[] {"test"}, 0));
            });
        });
        Assertions.assertEquals(PdfaExceptionMessageConstant.PAGE_AA_DICTIONARY_SHALL_CONTAIN_ONLY_ALLOWED_KEYS,
                e.getMessage());
    }

    @Test
    public void pdfA4ForbiddenActions_IMPORTDATA_ActionToPage_Test() {
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> generatePdfADocument(PdfAConformance.PDF_A_4, null, (doc) -> {
                    doc.addNewPage();
                    PdfDictionary openActions = new PdfDictionary();
                    openActions.put(PdfName.S, PdfName.ImportData);
                    doc.addNewPage().setAdditionalAction(PdfName.O, new PdfAction(openActions));
                }));
        Assertions.assertEquals(PdfaExceptionMessageConstant.PAGE_AA_DICTIONARY_SHALL_CONTAIN_ONLY_ALLOWED_KEYS,
                e.getMessage());
    }

    @Test
    public void pdfA4ForbiddenActions_HIDE_ActionToPage_Test() throws FileNotFoundException {
        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> {
            generatePdfADocument(PdfAConformance.PDF_A_4, null, (doc) -> {
                PdfAnnotation[] annotations = new PdfAnnotation[] {
                        new PdfLineAnnotation(new Rectangle(10, 10, 200, 200), new float[] {50, 750, 50, 750}),
                        new PdfLineAnnotation(new Rectangle(200, 200, 200, 200), new float[] {50, 750, 50, 750})};
                doc.addNewPage().setAdditionalAction(PdfName.O, PdfAction.createHide(annotations, true));
            });
        });
        Assertions.assertEquals(PdfaExceptionMessageConstant.PAGE_AA_DICTIONARY_SHALL_CONTAIN_ONLY_ALLOWED_KEYS,
                e.getMessage());
    }

    @Test
    public void pdfA4ForbiddenActions_RENDITION_ActionToPage_Test() throws FileNotFoundException {
        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> {
            generatePdfADocument(PdfAConformance.PDF_A_4, null, doc -> {
                doc.addNewPage().setAdditionalAction(PdfName.O, PdfAction.createRendition("empty",
                        PdfFileSpec.createEmbeddedFileSpec(doc, null, "bing", "bing", new PdfDictionary(),
                                PdfName.AllOn), "something", new PdfCircleAnnotation(new Rectangle(10, 10, 200, 200))));

            });
        });
        Assertions.assertEquals(PdfaExceptionMessageConstant.PAGE_AA_DICTIONARY_SHALL_CONTAIN_ONLY_ALLOWED_KEYS,
                e.getMessage());
    }

    @Test
    public void pdfA4ForbiddenActions_TRANS_ActionToPage_Test() throws FileNotFoundException {
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> generatePdfADocument(PdfAConformance.PDF_A_4, null, doc -> {
                    PdfDictionary openActions = new PdfDictionary();
                    openActions.put(PdfName.S, PdfName.Trans);
                    doc.addNewPage().setAdditionalAction(PdfName.O, new PdfAction(openActions));
                }));
        Assertions.assertEquals(PdfaExceptionMessageConstant.PAGE_AA_DICTIONARY_SHALL_CONTAIN_ONLY_ALLOWED_KEYS,
                e.getMessage());

    }

    @Test
    public void pdfA4ForbiddenActions_SETSTATE_ActionToPage_Test() throws FileNotFoundException {
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> generatePdfADocument(PdfAConformance.PDF_A_4, null, doc -> {
                    PdfDictionary action = new PdfDictionary();
                    action.put(PdfName.S, PdfName.SetState);

                    doc.addNewPage().setAdditionalAction(PdfName.O, new PdfAction(action));
                }));
        Assertions.assertEquals(PdfaExceptionMessageConstant.PAGE_AA_DICTIONARY_SHALL_CONTAIN_ONLY_ALLOWED_KEYS,
                e.getMessage());
    }


    @Test
    public void pdfA4ForbiddenActions_NOOP_ActionToPage_Test() {
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> generatePdfADocument(PdfAConformance.PDF_A_4, null, doc -> {
                    PdfDictionary action = new PdfDictionary();
                    action.put(PdfName.S, PdfName.NoOp);
                    doc.addNewPage().setAdditionalAction(PdfName.O, new PdfAction(action));
                }));
        Assertions.assertEquals(PdfaExceptionMessageConstant.PAGE_AA_DICTIONARY_SHALL_CONTAIN_ONLY_ALLOWED_KEYS,
                e.getMessage());
    }

    @Test
    public void pdfA4ForbiddenActions_SETOCGSTATE_ActionToPage_Test() {
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> generatePdfADocument(PdfAConformance.PDF_A_4, null, doc -> {
                    doc.addNewPage().setAdditionalAction(PdfName.O, PdfAction.createSetOcgState(new ArrayList<>()));
                }));
        Assertions.assertEquals(PdfaExceptionMessageConstant.PAGE_AA_DICTIONARY_SHALL_CONTAIN_ONLY_ALLOWED_KEYS,
                e.getMessage());
    }

    @Test
    public void pdfA4ForbiddenActions_GOTO3DVIEW_ActionToPage_Test() throws FileNotFoundException {
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> generatePdfADocument(PdfAConformance.PDF_A_4, null, doc -> {
                    PdfDictionary action = new PdfDictionary();
                    action.put(PdfName.S, PdfName.GoTo3DView);
                    doc.addNewPage().setAdditionalAction(PdfName.O, new PdfAction(action));
                }));
        Assertions.assertEquals(PdfaExceptionMessageConstant.PAGE_AA_DICTIONARY_SHALL_CONTAIN_ONLY_ALLOWED_KEYS,
                e.getMessage());
    }


    @Test
    public void pdfA4_SETOCGSTATE_InCatalog_Test() {
        Exception pdfa4Exception = Assertions.assertThrows(PdfAConformanceException.class, () -> {
            generatePdfADocument(PdfAConformance.PDF_A_4, null, doc -> {
                doc.getCatalog().setAdditionalAction(PdfName.O, PdfAction.createSetOcgState(new ArrayList<>()));
            });
        });
        String messageFormat = MessageFormatUtil.format(
                PdfaExceptionMessageConstant.CATALOG_AA_DICTIONARY_SHALL_CONTAIN_ONLY_ALLOWED_KEYS,
                PdfName.SetOCGState.getValue());
        Assertions.assertEquals(messageFormat, pdfa4Exception.getMessage());
    }

    @Test
    public void pdfA4_SETOCGSTATE_Annotation_Test() {
        Exception pdfa4Exception = Assertions.assertThrows(PdfAConformanceException.class, () -> {
            generatePdfADocument(PdfAConformance.PDF_A_4, null, doc -> {
                doc.addNewPage().addAnnotation(constructAnnotationWithAction(PdfName.SetOCGState));
            });
        });
        String messageFormat = MessageFormatUtil.format(PdfaExceptionMessageConstant._0_ACTIONS_ARE_NOT_ALLOWED,
                PdfName.SetOCGState.getValue());
        Assertions.assertEquals(messageFormat, pdfa4Exception.getMessage());
    }


    @Test
    public void pdfA4E_SETOCGSTATE_Annotation_Test() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4ESetOCGStateAnnotation.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4ESetOCGStateAnnotation.pdf";
        generatePdfADocument(PdfAConformance.PDF_A_4E, outPdf, doc -> {
            doc.addNewPage().addAnnotation(constructAnnotationWithAction(PdfName.SetOCGState));
        });
        compareResult(outPdf, cmpPdf);
    }


    @Test
    public void pdfA4_SETGOTO3DVIEW_Annotation_Test() {
        Exception pdfa4Exception = Assertions.assertThrows(PdfAConformanceException.class, () -> {
            generatePdfADocument(PdfAConformance.PDF_A_4, null, doc -> {
                doc.addNewPage().addAnnotation(constructAnnotationWithAction(PdfName.GoTo3DView));
            });
        });
        String messageFormat = MessageFormatUtil.format(PdfaExceptionMessageConstant._0_ACTIONS_ARE_NOT_ALLOWED,
                PdfName.GoTo3DView.getValue());
        Assertions.assertEquals(messageFormat, pdfa4Exception.getMessage());
    }

    @Test
    public void pdfA4E_GOTO3DVIEW_Annotation_Test() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4ESetGoto3DViewAnnotation.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4EGoto3DViewAnnotation.pdf";
        generatePdfADocument(PdfAConformance.PDF_A_4E, outPdf, doc -> {
            doc.addNewPage().addAnnotation(constructAnnotationWithAction(PdfName.GoTo3DView));
        });
        compareResult(outPdf, cmpPdf);
    }


    @Test
    public void pdfA4_AllowedNamedActions_Test() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4AllowedNamedActions.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4AllowedNamedActions.pdf";
        List<PdfName> annots = Arrays.asList(PdfName.NextPage, PdfName.PrevPage, PdfName.FirstPage, PdfName.LastPage);
        generatePdfADocument(PdfAConformance.PDF_A_4, outPdf, doc -> {
            PdfPage page = doc.getFirstPage();
            for (PdfName annot : annots) {
                PdfAnnotation annotation = constructAnnotationWithAction(new PdfName(""));
                annotation.getPdfObject().put(PdfName.A, PdfAction.createNamed(annot).getPdfObject());
                page.addAnnotation(annotation);
            }
        });
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void pdfA4_SpecialAllowedAction_Test() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4SpecialAllowedAction.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4SpecialAllowedAction.pdf";
        List<PdfName> annots = Arrays.asList(PdfName.GoToR, PdfName.GoToE, PdfName.URI, PdfName.SubmitForm);
        generatePdfADocument(PdfAConformance.PDF_A_4, outPdf, doc -> {
            PdfPage page = doc.getFirstPage();
            for (PdfName annot : annots) {
                PdfAnnotation annotation = constructAnnotationWithAction(annot);
                page.addAnnotation(annotation);
            }
        });
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void pdfA4F_SETOCGSTATE_InCatalog_Test() {
        Exception pdfa4Exception = Assertions.assertThrows(PdfAConformanceException.class, () -> {
            generatePdfADocument(PdfAConformance.PDF_A_4F, null, doc -> {
                doc.getCatalog().setAdditionalAction(PdfName.O, PdfAction.createSetOcgState(new ArrayList<>()));
            });
        });
        String messageFormat = MessageFormatUtil.format(
                PdfaExceptionMessageConstant.CATALOG_AA_DICTIONARY_SHALL_CONTAIN_ONLY_ALLOWED_KEYS,
                PdfName.SetOCGState.getValue());
        Assertions.assertEquals(messageFormat, pdfa4Exception.getMessage());
    }

    @Test
    public void pdfA4E_SETOCGSTATE_InCatalog_Test() {
        Exception pdfa4Exception = Assertions.assertThrows(PdfAConformanceException.class, () -> {
            generatePdfADocument(PdfAConformance.PDF_A_4E, null, doc -> {
                doc.getCatalog().setAdditionalAction(PdfName.O, PdfAction.createSetOcgState(new ArrayList<>()));
            });
        });
        String messageFormat = MessageFormatUtil.format(
                PdfaExceptionMessageConstant.CATALOG_AA_DICTIONARY_SHALL_CONTAIN_ONLY_ALLOWED_KEYS,
                PdfName.SetOCGState.getValue());
        Assertions.assertEquals(messageFormat, pdfa4Exception.getMessage());
    }

    @Test
    public void pdfA4_GOTO3DVIEW_InCatalog_Test() {
        Exception pdfa4Exception = Assertions.assertThrows(PdfAConformanceException.class, () -> {
            generatePdfADocument(PdfAConformance.PDF_A_4, null, doc -> {
                PdfDictionary action = new PdfDictionary();
                action.put(PdfName.S, PdfName.GoTo3DView);
                doc.getCatalog().setAdditionalAction(PdfName.O, new PdfAction(action));
            });

        });
        String messageFormat = MessageFormatUtil.format(
                PdfaExceptionMessageConstant.CATALOG_AA_DICTIONARY_SHALL_CONTAIN_ONLY_ALLOWED_KEYS,
                PdfName.GoTo3DView.getValue());
        Assertions.assertEquals(messageFormat, pdfa4Exception.getMessage());
    }

    @Test
    public void pdfA4F_GOTO3DView_InCatalog_Test() {
        Exception pdfa4Exception = Assertions.assertThrows(PdfAConformanceException.class, () -> {
            generatePdfADocument(PdfAConformance.PDF_A_4F, null, doc -> {
                PdfDictionary action = new PdfDictionary();
                action.put(PdfName.S, PdfName.GoTo3DView);
                doc.getCatalog().setAdditionalAction(PdfName.O, new PdfAction(action));
            });
        });
        Assertions.assertEquals(pdfa4Exception.getMessage(),
                PdfaExceptionMessageConstant.CATALOG_AA_DICTIONARY_SHALL_CONTAIN_ONLY_ALLOWED_KEYS);
    }

    @Test
    public void pdfA4E_GOTO3DView_InCatalog_Test() {
        Exception e = Assertions.assertThrows(PdfAConformanceException.class,
                () -> generatePdfADocument(PdfAConformance.PDF_A_4E, null, doc -> {
                    PdfDictionary action = new PdfDictionary();
                    action.put(PdfName.S, PdfName.GoTo3DView);
                    doc.getCatalog().setAdditionalAction(PdfName.O, new PdfAction(action));
                }));
        Assertions.assertEquals(PdfaExceptionMessageConstant.CATALOG_AA_DICTIONARY_SHALL_CONTAIN_ONLY_ALLOWED_KEYS,
                e.getMessage());
    }

    @Test
    public void pdfA4AAEntriesAllowedInAADocumentCatalog_Test() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4AAEntriesAllowedInAADocumentCatalog.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4AAEntriesAllowedInAADocumentCatalog.pdf";
        generatePdfADocument(PdfAConformance.PDF_A_4, outPdf, doc -> {
            PdfDictionary allowedAA = new PdfDictionary();
            allowedAA.put(PdfName.E, new PdfName("HELLO"));
            allowedAA.put(PdfName.X, new PdfName("HELLO"));
            allowedAA.put(PdfName.U, new PdfName("HELLO"));
            allowedAA.put(PdfName.D, new PdfName("HELLO"));
            allowedAA.put(PdfName.Fo, new PdfName("HELLO"));
            allowedAA.put(PdfName.Bl, new PdfName("HELLO"));
            doc.getCatalog().getPdfObject().put(PdfName.AA, allowedAA);
        });
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void pdfA4AAEntriesAllowedInAAPage_Test() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4AAEntriesAllowedInAAPage.pdf";
        String cmpPdf = CMP_FOLDER + "cmp_pdfA4AAEntriesAllowedInAAPage.pdf";
        generatePdfADocument(PdfAConformance.PDF_A_4, outPdf, doc -> {
            PdfDictionary allowedAA = new PdfDictionary();
            allowedAA.put(PdfName.E, new PdfName("HELLO"));
            allowedAA.put(PdfName.X, new PdfName("HELLO"));
            allowedAA.put(PdfName.U, new PdfName("HELLO"));
            allowedAA.put(PdfName.D, new PdfName("HELLO"));
            allowedAA.put(PdfName.Fo, new PdfName("HELLO"));
            allowedAA.put(PdfName.Bl, new PdfName("HELLO"));
            doc.getFirstPage().getPdfObject().put(PdfName.AA, allowedAA);
        });
        compareResult(outPdf, cmpPdf);
    }

    private PdfAnnotation constructAnnotationWithAction(PdfName actionType) {
        PdfAnnotation annotation = new PdfCircleAnnotation(new Rectangle(10, 10, 200, 200));
        PdfDictionary action = new PdfDictionary();
        annotation.setFlag(PdfAnnotation.PRINT);
        action.put(PdfName.Type, PdfName.Action);
        action.put(PdfName.S, actionType);
        annotation.setNormalAppearance(new PdfStream());
        annotation.getPdfObject().put(PdfName.A, action);
        return annotation;
    }

    private void compareResult(String outPdf, String cmpPdf) throws IOException, InterruptedException {
        Assertions.assertNull(// Android-Conversion-Skip-Line TODO DEVSIX-7377
                new VeraPdfValidator().validate(outPdf));// Android-Conversion-Skip-Line TODO DEVSIX-7377
        String result = new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_");
        if (result != null) {
            Assertions.fail(result);
        }
    }

    private void generatePdfADocument(PdfAConformance conformance, String outPdf,
            Consumer<PdfDocument> consumer) throws IOException {
        String filename = DESTINATION_FOLDER + UUID.randomUUID().toString() + ".pdf";
        if (outPdf != null) {
            filename = outPdf;
        }
        PdfWriter writer = new PdfWriter(filename, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        PdfADocument doc = new PdfADocument(writer, conformance,
                new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1",
                        FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm")));
        doc.addNewPage();
        consumer.accept(doc);
        doc.close();
    }

}
