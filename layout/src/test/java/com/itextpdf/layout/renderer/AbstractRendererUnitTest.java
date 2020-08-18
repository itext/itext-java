/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
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
package com.itextpdf.layout.renderer;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.gradients.AbstractLinearGradientBuilder;
import com.itextpdf.kernel.colors.gradients.GradientColorStop;
import com.itextpdf.kernel.colors.gradients.StrategyBasedLinearGradientBuilder;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.pdf.xobject.PdfXObject;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.property.BackgroundImage;
import com.itextpdf.layout.property.BackgroundRepeat;
import com.itextpdf.layout.property.Property;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Arrays;
import java.util.List;

@Category(UnitTest.class)
public class AbstractRendererUnitTest extends ExtendedITextTest {

    @Test
    public void createXObjectTest() {
        AbstractLinearGradientBuilder gradientBuilder = new StrategyBasedLinearGradientBuilder()
                .setGradientDirectionAsStrategy(StrategyBasedLinearGradientBuilder.GradientStrategy.TO_BOTTOM_LEFT)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, GradientColorStop.OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, GradientColorStop.OffsetType.RELATIVE));

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfXObject pdfXObject = AbstractRenderer.createXObject(gradientBuilder, new Rectangle(0, 0, 20, 20), pdfDocument);
        Assert.assertNotNull(pdfXObject.getPdfObject().get(PdfName.Resources));
    }

    @Test
    public void createXObjectWithNullLinearGradientTest() {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfXObject pdfXObject = AbstractRenderer.createXObject(null, new Rectangle(0, 0, 20, 20), pdfDocument);
        Assert.assertNull(pdfXObject.getPdfObject().get(PdfName.Resources));
    }

    @Test
    public void createXObjectWithInvalidColorTest() {
        AbstractLinearGradientBuilder gradientBuilder = new StrategyBasedLinearGradientBuilder();

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfXObject pdfXObject = AbstractRenderer.createXObject(gradientBuilder, new Rectangle(0, 0, 20, 20), pdfDocument);
        Assert.assertNull(pdfXObject.getPdfObject().get(PdfName.Resources));
    }

    @Test
    public void drawBackgroundImageTest() {
        final AbstractRenderer renderer = new DivRenderer(new Div()) {
            @Override
            public Rectangle getOccupiedAreaBBox() {
                return new Rectangle(100.0f, 50.0f);
            }
        };
        final byte[] bytes = new byte[]{54, 25, 47, 15, 2, 2, 2, 44, 55, 77, 86, 24};
        final int[] counter = new int[]{0};
        PdfDocument document = new PdfDocument(new PdfWriter(new java.io.ByteArrayOutputStream()));
        document.addNewPage();
        DrawContext context = new DrawContext(document, new PdfCanvas(document, 1) {
            PdfXObject object = null;

            @Override
            public PdfCanvas addXObject(PdfXObject xObject, Rectangle rect) {
                ++counter[0];
                if (object == xObject) {
                    return null;
                }
                object = xObject;
                Assert.assertTrue(xObject instanceof PdfImageXObject);
                Assert.assertEquals(Arrays.toString(((PdfImageXObject) xObject).getImageBytes(false)),
                        Arrays.toString(bytes));
                return null;
            }
        });
        renderer.setProperty(Property.BACKGROUND_IMAGE, new BackgroundImage(new PdfImageXObject(
                ImageDataFactory.createRawImage(bytes))) {
            @Override
            public float getWidth() {
                return 10.0f;
            }

            @Override
            public float getHeight() {
                return 10.0f;
            }
        });
        renderer.drawBackground(context);
        Assert.assertEquals(66, counter[0]);
    }

    @Test
    public void drawBackgroundImageWithNoRepeatXTest() {
        final AbstractRenderer renderer = new DivRenderer(new Div()) {
            @Override
            public Rectangle getOccupiedAreaBBox() {
                return new Rectangle(100.0f, 50.0f);
            }
        };
        final byte[] bytes = new byte[]{54, 25, 47, 15, 2, 2, 2, 44, 55, 77, 86, 24};
        final int[] counter = new int[]{0};
        PdfDocument document = new PdfDocument(new PdfWriter(new java.io.ByteArrayOutputStream()));
        document.addNewPage();
        DrawContext context = new DrawContext(document, new PdfCanvas(document, 1) {
            PdfXObject object = null;

            @Override
            public PdfCanvas addXObject(PdfXObject xObject, Rectangle rect) {
                ++counter[0];
                if (object == xObject) {
                    return null;
                }
                object = xObject;
                Assert.assertTrue(xObject instanceof PdfImageXObject);
                Assert.assertEquals(Arrays.toString(((PdfImageXObject) xObject).getImageBytes(false)),
                        Arrays.toString(bytes));
                return null;
            }
        });
        renderer.setProperty(Property.BACKGROUND_IMAGE, new BackgroundImage(new PdfImageXObject(
                ImageDataFactory.createRawImage(bytes)), new BackgroundRepeat(false, true)) {
            @Override
            public float getWidth() {
                return 10.0f;
            }

            @Override
            public float getHeight() {
                return 10.0f;
            }
        });
        renderer.drawBackground(context);
        Assert.assertEquals(6, counter[0]);
    }

    @Test
    public void drawBackgroundImageWithNoRepeatYTest() {
        final AbstractRenderer renderer = new DivRenderer(new Div()) {
            @Override
            public Rectangle getOccupiedAreaBBox() {
                return new Rectangle(100.0f, 50.0f);
            }
        };
        final byte[] bytes = new byte[]{54, 25, 47, 15, 2, 2, 2, 44, 55, 77, 86, 24};
        final int[] counter = new int[]{0};
        PdfDocument document = new PdfDocument(new PdfWriter(new java.io.ByteArrayOutputStream()));
        document.addNewPage();
        DrawContext context = new DrawContext(document, new PdfCanvas(document, 1) {
            PdfXObject object = null;

            @Override
            public PdfCanvas addXObject(PdfXObject xObject, Rectangle rect) {
                ++counter[0];
                if (object == xObject) {
                    return null;
                }
                object = xObject;
                Assert.assertTrue(xObject instanceof PdfImageXObject);
                Assert.assertEquals(Arrays.toString(((PdfImageXObject) xObject).getImageBytes(false)),
                        Arrays.toString(bytes));
                return null;
            }
        });
        renderer.setProperty(Property.BACKGROUND_IMAGE, new BackgroundImage(new PdfImageXObject(
                ImageDataFactory.createRawImage(bytes)), new BackgroundRepeat(true, false)) {
            @Override
            public float getWidth() {
                return 10.0f;
            }

            @Override
            public float getHeight() {
                return 10.0f;
            }
        });
        renderer.drawBackground(context);
        Assert.assertEquals(11, counter[0]);
    }

    @Test
    public void drawBackgroundImageWithNoRepeatTest() {
        final AbstractRenderer renderer = new DivRenderer(new Div()) {
            @Override
            public Rectangle getOccupiedAreaBBox() {
                return new Rectangle(100.0f, 50.0f);
            }
        };
        final byte[] bytes = new byte[]{54, 25, 47, 15, 2, 2, 2, 44, 55, 77, 86, 24};
        final int[] counter = new int[]{0};
        PdfDocument document = new PdfDocument(new PdfWriter(new java.io.ByteArrayOutputStream()));
        document.addNewPage();
        DrawContext context = new DrawContext(document, new PdfCanvas(document, 1) {
            PdfXObject object = null;

            @Override
            public PdfCanvas addXObject(PdfXObject xObject, Rectangle rect) {
                ++counter[0];
                if (object == xObject) {
                    return null;
                }
                object = xObject;
                Assert.assertTrue(xObject instanceof PdfImageXObject);
                Assert.assertEquals(Arrays.toString(((PdfImageXObject) xObject).getImageBytes(false)),
                        Arrays.toString(bytes));
                return null;
            }
        });
        renderer.setProperty(Property.BACKGROUND_IMAGE, new BackgroundImage(new PdfImageXObject(
                ImageDataFactory.createRawImage(bytes)), new BackgroundRepeat(false, false)) {
            @Override
            public float getWidth() {
                return 10.0f;
            }

            @Override
            public float getHeight() {
                return 10.0f;
            }
        });
        renderer.drawBackground(context);
        Assert.assertEquals(1, counter[0]);
    }

    @Test
    public void drawBackgroundImagesTest() {
        final AbstractRenderer renderer = new DivRenderer(new Div()) {
            @Override
            public Rectangle getOccupiedAreaBBox() {
                return new Rectangle(100.0f, 50.0f);
            }
        };
        final List<byte[]> listBytes = Arrays.asList(
                new byte[]{54, 25, 47, 15, 2, 2, 2, 44, 55, 77, 86, 24},
                new byte[]{4, 15, 41, 23, 3, 2, 7, 14, 55, 27, 46, 12, 14, 14, 7, 7, 24, 25});
        final int[] counter = new int[]{0};
        PdfDocument document = new PdfDocument(new PdfWriter(new java.io.ByteArrayOutputStream()));
        document.addNewPage();
        DrawContext context = new DrawContext(document, new PdfCanvas(document, 1) {
            PdfXObject object = null;

            @Override
            public PdfCanvas addXObject(PdfXObject xObject, Rectangle rect) {
                if (object == xObject) {
                    return null;
                }
                object = xObject;
                Assert.assertTrue(xObject instanceof PdfImageXObject);
                Assert.assertEquals(Arrays.toString(((PdfImageXObject) xObject).getImageBytes(false)),
                        Arrays.toString(listBytes.get(counter[0]++)));
                return null;
            }
        });
        renderer.setProperty(Property.BACKGROUND_IMAGE, Arrays.asList((BackgroundImage)
                new BackgroundImage(new PdfImageXObject(ImageDataFactory.createRawImage(listBytes.get(1)))) {
                    @Override
                    public float getWidth() {
                        return 10.0f;
                    }

                    @Override
                    public float getHeight() {
                        return 10.0f;
                    }
                }, (BackgroundImage)
                new BackgroundImage(new PdfImageXObject(ImageDataFactory.createRawImage(listBytes.get(0)))) {
                    @Override
                    public float getWidth() {
                        return 10.0f;
                    }

                    @Override
                    public float getHeight() {
                        return 10.0f;
                    }
                }));
        renderer.drawBackground(context);
        Assert.assertEquals(listBytes.size(), counter[0]);
    }
}
