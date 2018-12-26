/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.pdfa;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.ParagraphRenderer;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

@Category(IntegrationTest.class)
public class PdfAFormFieldTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";
    public static final String destinationFolder = "./target/test/com/itextpdf/pdfa/PdfAFormFieldTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void pdfAButtonFieldTest() throws Exception {
        PdfDocument pdf;
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        String file = "pdfAButtonField.pdf";
        String filename = destinationFolder + file;
        pdf = new PdfADocument(
                new PdfWriter(new FileOutputStream(filename)),
                PdfAConformanceLevel.PDF_A_1B,
                new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB ICC preference", is));

        PageSize pageSize = PageSize.LETTER;
        Document doc = new Document(pdf, pageSize);
        PdfFontFactory.register(sourceFolder + "FreeSans.ttf",sourceFolder + "FreeSans.ttf");
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf", true);

        PdfButtonFormField group = PdfFormField.createRadioGroup(pdf, "group", "", PdfAConformanceLevel.PDF_A_1B);
        group.setReadOnly(true);

        Paragraph p = new Paragraph();
        Text t = new Text("supported");

        t.setFont(font);

        p.add(t);

        Image ph = new Image(new PdfFormXObject(new Rectangle(10, 10)));
        Paragraph pc = new Paragraph().add(ph);
        PdfAButtonFieldTestRenderer r = new PdfAButtonFieldTestRenderer(pc, group, "v1");

        pc.setNextRenderer(r);

        p.add(pc);

        Paragraph pc1 = new Paragraph().add(ph);
        PdfAButtonFieldTestRenderer r1 = new PdfAButtonFieldTestRenderer(pc, group, "v2");
        pc1.setNextRenderer(r1);

        Paragraph p2 = new Paragraph();
        Text t2 = new Text("supported 2");
        t2.setFont(font);

        p2.add(t2).add(pc1);

        doc.add(p);
        doc.add(p2);
        group.setValue("v1");
        PdfAcroForm.getAcroForm(pdf, true).addField(group);

        pdf.close();
        Assert.assertNull(new CompareTool().compareByContent(filename, sourceFolder + "cmp_" + file, destinationFolder, "diff_"));
    }

    static class PdfAButtonFieldTestRenderer extends ParagraphRenderer {
        private PdfButtonFormField _group;
        private String _value;

        public PdfAButtonFieldTestRenderer(Paragraph para, PdfButtonFormField group, String value)
        {
            super(para);
            _group = group;
            _value = value;
        }

        @Override
        public void draw(DrawContext context)
        {
            int pageNumber = getOccupiedArea().getPageNumber();
            Rectangle bbox = getInnerAreaBBox();
            PdfDocument pdf = context.getDocument();
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);
            PdfFormField chk = PdfFormField.createRadioButton(pdf, bbox, _group, _value, PdfAConformanceLevel.PDF_A_1B);
            chk.setPage(pageNumber);

            chk.setVisibility(PdfFormField.VISIBLE);
            chk.setBorderColor(ColorConstants.BLACK);
            chk.setBackgroundColor(ColorConstants.WHITE);
            chk.setReadOnly(true);

            PdfFormXObject appearance = new PdfFormXObject(bbox);
            PdfCanvas canvas = new PdfCanvas(appearance, pdf);

            canvas.saveState()
                    .moveTo(bbox.getLeft(), bbox.getBottom())
                    .lineTo(bbox.getRight(), bbox.getBottom())
                    .lineTo(bbox.getRight(), bbox.getTop())
                    .lineTo(bbox.getLeft(), bbox.getTop())
                    .lineTo(bbox.getLeft(), bbox.getBottom())
                    .setLineWidth(1f)
                    .stroke()
                    .restoreState();

            form.addFieldAppearanceToPage(chk, pdf.getPage(pageNumber));
            //appearance stream was set, while AS has kept as is, i.e. in Off state.
            chk.setAppearance(PdfName.N,  "v1".equals(_value) ? _value : "Off", appearance.getPdfObject());
        }
    }
}


