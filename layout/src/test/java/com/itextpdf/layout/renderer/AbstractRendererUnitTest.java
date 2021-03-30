/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
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

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.colors.gradients.AbstractLinearGradientBuilder;
import com.itextpdf.kernel.colors.gradients.GradientColorStop;
import com.itextpdf.kernel.colors.gradients.GradientColorStop.OffsetType;
import com.itextpdf.kernel.colors.gradients.GradientSpreadMethod;
import com.itextpdf.kernel.colors.gradients.LinearGradientBuilder;
import com.itextpdf.kernel.colors.gradients.StrategyBasedLinearGradientBuilder;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.pdf.xobject.PdfXObject;
import com.itextpdf.layout.borders.DashedBorder;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.property.Background;
import com.itextpdf.layout.property.BackgroundBox;
import com.itextpdf.layout.property.BackgroundImage;
import com.itextpdf.layout.property.BackgroundImage.Builder;
import com.itextpdf.layout.property.BackgroundPosition;
import com.itextpdf.layout.property.BackgroundRepeat;
import com.itextpdf.layout.property.BackgroundRepeat.BackgroundRepeatValue;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

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
                return new Rectangle(99.0f, 49.0f);
            }
        };
        final byte[] bytes = new byte[]{54, 25, 47, 15, 2, 2, 2, 44, 55, 77, 86, 24};
        final int[] counter = new int[]{0};
        PdfDocument document = new PdfDocument(new PdfWriter(new java.io.ByteArrayOutputStream()));
        document.addNewPage();
        DrawContext context = new DrawContext(document, new PdfCanvas(document, 1) {
            PdfXObject object = null;

            @Override
            public PdfCanvas addXObjectFittedIntoRectangle(PdfXObject xObject, Rectangle rect) {
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
        renderer.setProperty(Property.BACKGROUND_IMAGE, new BackgroundImage.Builder().setImage(
                new PdfImageXObject(ImageDataFactory.createRawImage(bytes)) {
                    @Override
                    public float getWidth() {
                        return 10.0f;
                    }

                    @Override
                    public float getHeight() {
                        return 10.0f;
                    }
                }).build());
        renderer.drawBackground(context);
        Assert.assertEquals(50, counter[0]);
    }

    @Test
    public void drawBackgroundImageWithNoRepeatXTest() {
        final AbstractRenderer renderer = new DivRenderer(new Div()) {
            @Override
            public Rectangle getOccupiedAreaBBox() {
                return new Rectangle(100.0f, 49.0f);
            }
        };
        final byte[] bytes = new byte[]{54, 25, 47, 15, 2, 2, 2, 44, 55, 77, 86, 24};
        final int[] counter = new int[]{0};
        PdfDocument document = new PdfDocument(new PdfWriter(new java.io.ByteArrayOutputStream()));
        document.addNewPage();
        DrawContext context = new DrawContext(document, new PdfCanvas(document, 1) {
            PdfXObject object = null;

            @Override
            public PdfCanvas addXObjectFittedIntoRectangle(PdfXObject xObject, Rectangle rect) {
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
        renderer.setProperty(Property.BACKGROUND_IMAGE,
                new BackgroundImage.Builder().setImage(new PdfImageXObject(ImageDataFactory.createRawImage(bytes)) {
                    @Override
                    public float getWidth() {
                        return 10.0f;
                    }

                    @Override
                    public float getHeight() {
                        return 10.0f;
                    }
                }).setBackgroundRepeat(new BackgroundRepeat(BackgroundRepeatValue.NO_REPEAT, BackgroundRepeatValue.REPEAT)).build());
        renderer.drawBackground(context);
        Assert.assertEquals(5, counter[0]);
    }

    @Test
    public void drawBackgroundImageWithNoRepeatYTest() {
        final AbstractRenderer renderer = new DivRenderer(new Div()) {
            @Override
            public Rectangle getOccupiedAreaBBox() {
                return new Rectangle(99.0f, 50.0f);
            }
        };
        final byte[] bytes = new byte[]{54, 25, 47, 15, 2, 2, 2, 44, 55, 77, 86, 24};
        final int[] counter = new int[]{0};
        PdfDocument document = new PdfDocument(new PdfWriter(new java.io.ByteArrayOutputStream()));
        document.addNewPage();
        DrawContext context = new DrawContext(document, new PdfCanvas(document, 1) {
            PdfXObject object = null;

            @Override
            public PdfCanvas addXObjectFittedIntoRectangle(PdfXObject xObject, Rectangle rect) {
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
        renderer.setProperty(Property.BACKGROUND_IMAGE, new BackgroundImage.Builder().setImage(
                new PdfImageXObject(ImageDataFactory.createRawImage(bytes)) {
                    @Override
                    public float getWidth() {
                        return 10.0f;
                    }

                    @Override
                    public float getHeight() {
                        return 10.0f;
                    }
                }).setBackgroundRepeat(new BackgroundRepeat(BackgroundRepeatValue.REPEAT, BackgroundRepeatValue.NO_REPEAT)).build());
        renderer.drawBackground(context);
        Assert.assertEquals(10, counter[0]);
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
            public PdfCanvas addXObjectFittedIntoRectangle(PdfXObject xObject, Rectangle rect) {
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
        renderer.setProperty(Property.BACKGROUND_IMAGE, new BackgroundImage.Builder().setImage(
                new PdfImageXObject(ImageDataFactory.createRawImage(bytes)) {
                    @Override
                    public float getWidth() {
                        return 10.0f;
                    }

                    @Override
                    public float getHeight() {
                        return 10.0f;
                    }
                }).setBackgroundRepeat(
                new BackgroundRepeat(BackgroundRepeatValue.NO_REPEAT)).build());
        renderer.drawBackground(context);
        Assert.assertEquals(1, counter[0]);
    }

    @Test
    public void drawBackgroundImageWithPositionTest() {
        final AbstractRenderer renderer = new DivRenderer(new Div()) {
            @Override
            public Rectangle getOccupiedAreaBBox() {
                return new Rectangle(100.0f, 50.0f);
            }
        };
        final byte[] bytes = new byte[]{54, 25, 47, 15, 2, 2, 2, 44, 55, 77, 86, 24};
        PdfDocument document = new PdfDocument(new PdfWriter(new java.io.ByteArrayOutputStream()));
        document.addNewPage();
        DrawContext context = new DrawContext(document, new PdfCanvas(document, 1) {
            PdfXObject object = null;

            @Override
            public PdfCanvas addXObjectFittedIntoRectangle(PdfXObject xObject, Rectangle rect) {
                if (object == xObject) {
                    return null;
                }
                object = xObject;
                Assert.assertTrue(xObject instanceof PdfImageXObject);
                Assert.assertEquals(Arrays.toString(((PdfImageXObject) xObject).getImageBytes(false)),
                        Arrays.toString(bytes));
                Assert.assertEquals(27, (int) rect.getX());
                Assert.assertEquals(40, (int) rect.getY());
                return null;
            }
        });
        renderer.setProperty(Property.BACKGROUND_IMAGE, new BackgroundImage.Builder().setImage(
                new PdfImageXObject(ImageDataFactory.createRawImage(bytes)) {
                    @Override
                    public float getWidth() {
                        return 10.0f;
                    }

                    @Override
                    public float getHeight() {
                        return 10.0f;
                    }
                }).setBackgroundPosition(new BackgroundPosition().setXShift(new UnitValue(UnitValue.PERCENT, 30))).build());
        renderer.drawBackground(context);
    }

    @Test
    public void drawGradientWithPositionTest() {
        final AbstractRenderer renderer = new DivRenderer(new Div()) {
            @Override
            public Rectangle getOccupiedAreaBBox() {
                return new Rectangle(100.0f, 50.0f);
            }
        };
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
                Assert.assertTrue(xObject instanceof PdfFormXObject);
                Assert.assertEquals(-30, (int) rect.getX());
                Assert.assertEquals(100, (int) rect.getY());
                return null;
            }
        });
        renderer.setProperty(Property.BACKGROUND_IMAGE, new BackgroundImage.Builder().setLinearGradientBuilder(new StrategyBasedLinearGradientBuilder()
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue())).addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue())))
                .setBackgroundPosition(new BackgroundPosition().setPositionX(BackgroundPosition.PositionX.RIGHT).setPositionY(BackgroundPosition.PositionY.BOTTOM)
                        .setYShift(UnitValue.createPointValue(100)).setXShift(UnitValue.createPointValue(30))).build());
        renderer.drawBackground(context);
    }

    @Test
    public void drawGradientWithPercentagePositionTest() {
        final AbstractRenderer renderer = new DivRenderer(new Div()) {
            @Override
            public Rectangle getOccupiedAreaBBox() {
                return new Rectangle(100.0f, 50.0f);
            }
        };
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
                Assert.assertTrue(xObject instanceof PdfFormXObject);
                Assert.assertEquals(0, (int) rect.getX());
                Assert.assertEquals(0, (int) rect.getY());
                return null;
            }
        });
        renderer.setProperty(Property.BACKGROUND_IMAGE, new BackgroundImage.Builder().setLinearGradientBuilder(new StrategyBasedLinearGradientBuilder()
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue())).addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue())))
                .setBackgroundPosition(new BackgroundPosition().setPositionX(BackgroundPosition.PositionX.RIGHT).setPositionY(BackgroundPosition.PositionY.BOTTOM)
                        .setYShift(UnitValue.createPercentValue(70)).setXShift(UnitValue.createPercentValue(33))).build());
        renderer.drawBackground(context);
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
            public PdfCanvas addXObjectFittedIntoRectangle(PdfXObject xObject, Rectangle rect) {
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
                new BackgroundImage.Builder().setImage(new PdfImageXObject(ImageDataFactory.createRawImage(listBytes.get(1))) {
                    @Override
                    public float getWidth() {
                        return 10.0f;
                    }

                    @Override
                    public float getHeight() {
                        return 10.0f;
                    }
                }).build(), (BackgroundImage)
                new BackgroundImage.Builder().setImage(new PdfImageXObject(ImageDataFactory.createRawImage(listBytes.get(0))) {
                    @Override
                    public float getWidth() {
                        return 10.0f;
                    }

                    @Override
                    public float getHeight() {
                        return 10.0f;
                    }
                }).build()));
        renderer.drawBackground(context);
        Assert.assertEquals(listBytes.size(), counter[0]);
    }

    @Test
    public void drawBackgroundImagesWithPositionsTest() {
        final AbstractRenderer renderer = new DivRenderer(new Div()) {
            @Override
            public Rectangle getOccupiedAreaBBox() {
                return new Rectangle(100.0f, 50.0f);
            }
        };
        final List<byte[]> listBytes = Arrays.asList(
                new byte[]{54, 25, 47, 15, 2, 2, 2, 44, 55, 77, 86, 24},
                new byte[]{4, 15, 41, 23, 3, 2, 7, 14, 55, 27, 46, 12, 14, 14, 7, 7, 24, 25});
        final float widthHeight = 10.0f;
        final List<Rectangle> listRectangles = Arrays.asList(
                new Rectangle(81, 20, widthHeight, widthHeight),
                new Rectangle(0, 40, widthHeight, widthHeight)
        );
        final int[] counter = new int[]{0};
        PdfDocument document = new PdfDocument(new PdfWriter(new java.io.ByteArrayOutputStream()));
        document.addNewPage();
        DrawContext context = new DrawContext(document, new PdfCanvas(document, 1) {
            PdfXObject object = null;

            @Override
            public PdfCanvas addXObjectFittedIntoRectangle(PdfXObject xObject, Rectangle rect) {
                if (object == xObject) {
                    return null;
                }
                object = xObject;
                Assert.assertTrue(xObject instanceof PdfImageXObject);
                Assert.assertEquals(Arrays.toString(((PdfImageXObject) xObject).getImageBytes(false)),
                        Arrays.toString(listBytes.get(counter[0])));
                Assert.assertEquals((int) listRectangles.get(counter[0]).getX(), (int) rect.getX());
                Assert.assertEquals((int) listRectangles.get(counter[0]++).getY(), (int) rect.getY());
                return null;
            }
        });
        renderer.setProperty(Property.BACKGROUND_IMAGE, Arrays.asList((BackgroundImage)
                new BackgroundImage.Builder().setImage(new PdfImageXObject(ImageDataFactory.createRawImage(listBytes.get(1))) {
                    @Override
                    public float getWidth() {
                        return widthHeight;
                    }

                    @Override
                    public float getHeight() {
                        return widthHeight;
                    }
                }).build(), (BackgroundImage)
                new BackgroundImage.Builder().setImage(new PdfImageXObject(ImageDataFactory.createRawImage(listBytes.get(0))) {
                    @Override
                    public float getWidth() {
                        return widthHeight;
                    }

                    @Override
                    public float getHeight() {
                        return widthHeight;
                    }
                }).setBackgroundPosition(new BackgroundPosition().setPositionX(BackgroundPosition.PositionX.RIGHT)
                        .setPositionY(BackgroundPosition.PositionY.CENTER).setXShift(new UnitValue(UnitValue.PERCENT, 10))).build()));
        renderer.drawBackground(context);
        Assert.assertEquals(listBytes.size(), counter[0]);
    }

    @Test
    public void backgroundColorClipTest() {
        final PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        final PdfCanvas pdfCanvas = new PdfCanvas(pdfDocument.addNewPage()) {
            @Override
            public PdfCanvas rectangle(double x, double y, double width, double height) {
                Assert.assertEquals(130.0, x, 0);
                Assert.assertEquals(230.0, y, 0);
                Assert.assertEquals(240.0, width, 0);
                Assert.assertEquals(340.0, height, 0);
                return this;
            }
        };
        final DrawContext drawContext = new DrawContext(pdfDocument, pdfCanvas);
        final AbstractRenderer renderer = new DivRenderer(new Div().setPadding(20).setBorder(new DashedBorder(10)));
        renderer.occupiedArea = new LayoutArea(1, new Rectangle(100f, 200f, 300f, 400f));
        renderer.setProperty(Property.BACKGROUND, new Background(new DeviceRgb(), 1, BackgroundBox.CONTENT_BOX));
        renderer.drawBackground(drawContext);
    }

    @Test
    public void backgroundImageClipOriginNoRepeatTest() {
        final PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        final byte[] bytes = new byte[] {54, 25, 47, 15, 2, 2, 2, 44, 55, 77, 86, 24};
        final PdfXObject rawImage = new PdfImageXObject(ImageDataFactory.createRawImage(bytes)) {
            @Override
            public float getWidth() {
                return 50f;
            }

            @Override
            public float getHeight() {
                return 50f;
            }
        };
        final PdfCanvas pdfCanvas = new PdfCanvas(pdfDocument.addNewPage()) {
            @Override
            public PdfCanvas rectangle(double x, double y, double width, double height) {
                Assert.assertEquals(130.0, x, 0);
                Assert.assertEquals(230.0, y, 0);
                Assert.assertEquals(240.0, width, 0);
                Assert.assertEquals(340.0, height, 0);
                return this;
            }

            @Override
            public PdfCanvas addXObjectFittedIntoRectangle(PdfXObject xObject, Rectangle rect) {
                Assert.assertEquals(rawImage, xObject);
                Assert.assertEquals(100f, rect.getX(), 0);
                Assert.assertEquals(550f, rect.getY(), 0);
                Assert.assertEquals(50f, rect.getWidth(), 0);
                Assert.assertEquals(50f, rect.getHeight(), 0);
                return this;
            }
        };
        final DrawContext drawContext = new DrawContext(pdfDocument, pdfCanvas);
        final AbstractRenderer renderer = new DivRenderer(new Div().setPadding(20).setBorder(new DashedBorder(10)));
        renderer.occupiedArea = new LayoutArea(1, new Rectangle(100f, 200f, 300f, 400f));
        final BackgroundImage backgroundImage = new Builder().setImage(rawImage)
                .setBackgroundRepeat(new BackgroundRepeat(BackgroundRepeatValue.NO_REPEAT)).setBackgroundClip(BackgroundBox.CONTENT_BOX)
                .setBackgroundOrigin(BackgroundBox.BORDER_BOX).build();
        renderer.setProperty(Property.BACKGROUND_IMAGE, backgroundImage);
        renderer.drawBackground(drawContext);
    }

    @Test
    public void backgroundLinearGradientClipOriginNoRepeatTest() {
        final PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        final byte[] bytes = new byte[] {54, 25, 47, 15, 2, 2, 2, 44, 55, 77, 86, 24};
        final PdfCanvas pdfCanvas = new PdfCanvas(pdfDocument.addNewPage()) {
            @Override
            public PdfCanvas rectangle(double x, double y, double width, double height) {
                Assert.assertEquals(130.0, x, 0);
                Assert.assertEquals(230.0, y, 0);
                Assert.assertEquals(240.0, width, 0);
                Assert.assertEquals(340.0, height, 0);
                return this;
            }

            @Override
            public PdfCanvas addXObjectFittedIntoRectangle(PdfXObject xObject, Rectangle rect) {
                Assert.assertEquals(100f, rect.getX(), 0);
                Assert.assertEquals(200f, rect.getY(), 0);
                Assert.assertEquals(300f, rect.getWidth(), 0);
                Assert.assertEquals(400f, rect.getHeight(), 0);
                return this;
            }
        };
        final DrawContext drawContext = new DrawContext(pdfDocument, pdfCanvas);
        final AbstractRenderer renderer = new DivRenderer(new Div().setPadding(20).setBorder(new DashedBorder(10)));
        renderer.occupiedArea = new LayoutArea(1, new Rectangle(100f, 200f, 300f, 400f));
        Rectangle targetBoundingBox = new Rectangle(50f, 150f, 300f, 300f);
        AbstractLinearGradientBuilder gradientBuilder = new LinearGradientBuilder()
                .setGradientVector(targetBoundingBox.getLeft() + 100f, targetBoundingBox.getBottom() + 100f,
                        targetBoundingBox.getRight() - 100f, targetBoundingBox.getTop() - 100f)
                .setSpreadMethod(GradientSpreadMethod.PAD)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.BLUE.getColorValue(), 1d, OffsetType.RELATIVE));
        final BackgroundImage backgroundImage = new Builder().setLinearGradientBuilder(gradientBuilder)
                .setBackgroundRepeat(new BackgroundRepeat(BackgroundRepeatValue.NO_REPEAT)).setBackgroundClip(BackgroundBox.CONTENT_BOX)
                .setBackgroundOrigin(BackgroundBox.BORDER_BOX).build();
        renderer.setProperty(Property.BACKGROUND_IMAGE, backgroundImage);
        renderer.drawBackground(drawContext);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.PAGE_WAS_FLUSHED_ACTION_WILL_NOT_BE_PERFORMED))
    public void applyLinkAnnotationFlushedPageTest() {
        AbstractRenderer abstractRenderer = new DivRenderer(new Div());
        abstractRenderer.occupiedArea = new LayoutArea(1, new Rectangle(100, 100));

        abstractRenderer.setProperty(Property.LINK_ANNOTATION, new PdfLinkAnnotation(new Rectangle(0, 0, 0, 0)));

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        pdfDocument.addNewPage();
        pdfDocument.getPage(1).flush();

        abstractRenderer.applyLinkAnnotation(pdfDocument);

        // This test checks that there is log message and there is no NPE so assertions are not required
        Assert.assertTrue(true);
    }
}
