/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.layout.renderer;

import com.itextpdf.io.logs.IoLogMessageConstant;
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
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.DashedBorder;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.properties.Background;
import com.itextpdf.layout.properties.BackgroundBox;
import com.itextpdf.layout.properties.BackgroundImage;
import com.itextpdf.layout.properties.BackgroundImage.Builder;
import com.itextpdf.layout.properties.BackgroundPosition;
import com.itextpdf.layout.properties.BackgroundRepeat;
import com.itextpdf.layout.properties.BackgroundRepeat.BackgroundRepeatValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class AbstractRendererUnitTest extends ExtendedITextTest {

    @Test
    public void createXObjectTest() {
        AbstractLinearGradientBuilder gradientBuilder = new StrategyBasedLinearGradientBuilder()
                .setGradientDirectionAsStrategy(StrategyBasedLinearGradientBuilder.GradientStrategy.TO_BOTTOM_LEFT)
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue(), 0d, GradientColorStop.OffsetType.RELATIVE))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue(), 0.5, GradientColorStop.OffsetType.RELATIVE));

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfXObject pdfXObject = AbstractRenderer.createXObject(gradientBuilder, new Rectangle(0, 0, 20, 20), pdfDocument);
        Assertions.assertNotNull(pdfXObject.getPdfObject().get(PdfName.Resources));
    }

    @Test
    public void createXObjectWithNullLinearGradientTest() {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfXObject pdfXObject = AbstractRenderer.createXObject(null, new Rectangle(0, 0, 20, 20), pdfDocument);
        Assertions.assertNull(pdfXObject.getPdfObject().get(PdfName.Resources));
    }

    @Test
    public void createXObjectWithInvalidColorTest() {
        AbstractLinearGradientBuilder gradientBuilder = new StrategyBasedLinearGradientBuilder();

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfXObject pdfXObject = AbstractRenderer.createXObject(gradientBuilder, new Rectangle(0, 0, 20, 20), pdfDocument);
        Assertions.assertNull(pdfXObject.getPdfObject().get(PdfName.Resources));
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
                Assertions.assertTrue(xObject instanceof PdfImageXObject);
                Assertions.assertEquals(Arrays.toString(((PdfImageXObject) xObject).getImageBytes(false)),
                        Arrays.toString(bytes));
                return null;
            }
        });
        List<BackgroundImage> images = new ArrayList<>();
        images.add(new BackgroundImage.Builder().setImage(
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
        renderer.setProperty(Property.BACKGROUND_IMAGE, images);
        renderer.drawBackground(context);
        Assertions.assertEquals(50, counter[0]);
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
                Assertions.assertTrue(xObject instanceof PdfImageXObject);
                Assertions.assertEquals(Arrays.toString(((PdfImageXObject) xObject).getImageBytes(false)),
                        Arrays.toString(bytes));
                return null;
            }
        });
        List<BackgroundImage> images = new ArrayList<>();
        images.add(new BackgroundImage.Builder().setImage(
                new PdfImageXObject(ImageDataFactory.createRawImage(bytes)) {
                    @Override
                    public float getWidth() {
                        return 10.0f;
                    }

                    @Override
                    public float getHeight() {
                        return 10.0f;
                    }
                }).setBackgroundRepeat(new BackgroundRepeat(BackgroundRepeatValue.NO_REPEAT, BackgroundRepeatValue.REPEAT))
                .build());
        renderer.setProperty(Property.BACKGROUND_IMAGE, images);
        renderer.drawBackground(context);
        Assertions.assertEquals(5, counter[0]);
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
                Assertions.assertTrue(xObject instanceof PdfImageXObject);
                Assertions.assertEquals(Arrays.toString(((PdfImageXObject) xObject).getImageBytes(false)),
                        Arrays.toString(bytes));
                return null;
            }
        });
        List<BackgroundImage> images = new ArrayList<>();
        images.add(new BackgroundImage.Builder().setImage(
                new PdfImageXObject(ImageDataFactory.createRawImage(bytes)) {
                    @Override
                    public float getWidth() {
                        return 10.0f;
                    }

                    @Override
                    public float getHeight() {
                        return 10.0f;
                    }
                }).setBackgroundRepeat(new BackgroundRepeat(BackgroundRepeatValue.REPEAT, BackgroundRepeatValue.NO_REPEAT))
                .build());
        renderer.setProperty(Property.BACKGROUND_IMAGE, images);
        renderer.drawBackground(context);
        Assertions.assertEquals(10, counter[0]);
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
                Assertions.assertTrue(xObject instanceof PdfImageXObject);
                Assertions.assertEquals(Arrays.toString(((PdfImageXObject) xObject).getImageBytes(false)),
                        Arrays.toString(bytes));
                return null;
            }
        });
        List<BackgroundImage> images = new ArrayList<>();
        images.add(new BackgroundImage.Builder().setImage(
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
                new BackgroundRepeat(BackgroundRepeatValue.NO_REPEAT))
                .build());
        renderer.setProperty(Property.BACKGROUND_IMAGE, images);
        renderer.drawBackground(context);
        Assertions.assertEquals(1, counter[0]);
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
                Assertions.assertTrue(xObject instanceof PdfImageXObject);
                Assertions.assertEquals(Arrays.toString(((PdfImageXObject) xObject).getImageBytes(false)),
                        Arrays.toString(bytes));
                Assertions.assertEquals(27, (int) rect.getX());
                Assertions.assertEquals(40, (int) rect.getY());
                return null;
            }
        });
        List<BackgroundImage> images = new ArrayList<>();
        images.add(new BackgroundImage.Builder().setImage(
                new PdfImageXObject(ImageDataFactory.createRawImage(bytes)) {
                    @Override
                    public float getWidth() {
                        return 10.0f;
                    }

                    @Override
                    public float getHeight() {
                        return 10.0f;
                    }
                }).setBackgroundPosition(new BackgroundPosition().setXShift(new UnitValue(UnitValue.PERCENT, 30)))
                .build());
        renderer.setProperty(Property.BACKGROUND_IMAGE, images);
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
            public PdfCanvas addXObjectFittedIntoRectangle(PdfXObject xObject, Rectangle rect) {
                if (object == xObject) {
                    return null;
                }
                object = xObject;
                Assertions.assertTrue(xObject instanceof PdfFormXObject);
                Assertions.assertEquals(-30, (int) rect.getX());
                Assertions.assertEquals(100, (int) rect.getY());
                return null;
            }
        });
        List<BackgroundImage> images = new ArrayList<>();
        images.add(new BackgroundImage.Builder().setLinearGradientBuilder(new StrategyBasedLinearGradientBuilder()
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue()))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue())))
                .setBackgroundPosition(new BackgroundPosition().setPositionX(BackgroundPosition.PositionX.RIGHT)
                        .setPositionY(BackgroundPosition.PositionY.BOTTOM)
                        .setYShift(UnitValue.createPointValue(100)).setXShift(UnitValue.createPointValue(30)))
                .build());
        renderer.setProperty(Property.BACKGROUND_IMAGE, images);
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
            public PdfCanvas addXObjectFittedIntoRectangle(PdfXObject xObject, Rectangle rect) {
                if (object == xObject) {
                    return null;
                }
                object = xObject;
                Assertions.assertTrue(xObject instanceof PdfFormXObject);
                Assertions.assertEquals(0, (int) rect.getX());
                Assertions.assertEquals(0, (int) rect.getY());
                return null;
            }
        });
        List<BackgroundImage> images = new ArrayList<>();
        images.add(new BackgroundImage.Builder().setLinearGradientBuilder(new StrategyBasedLinearGradientBuilder()
                .addColorStop(new GradientColorStop(ColorConstants.RED.getColorValue()))
                .addColorStop(new GradientColorStop(ColorConstants.GREEN.getColorValue())))
                .setBackgroundPosition(new BackgroundPosition().setPositionX(BackgroundPosition.PositionX.RIGHT)
                        .setPositionY(BackgroundPosition.PositionY.BOTTOM)
                        .setYShift(UnitValue.createPercentValue(70)).setXShift(UnitValue.createPercentValue(33)))
                .build());
        renderer.setProperty(Property.BACKGROUND_IMAGE, images);
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
                Assertions.assertTrue(xObject instanceof PdfImageXObject);
                Assertions.assertEquals(Arrays.toString(((PdfImageXObject) xObject).getImageBytes(false)),
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
        Assertions.assertEquals(listBytes.size(), counter[0]);
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
                Assertions.assertTrue(xObject instanceof PdfImageXObject);
                Assertions.assertEquals(Arrays.toString(((PdfImageXObject) xObject).getImageBytes(false)),
                        Arrays.toString(listBytes.get(counter[0])));
                Assertions.assertEquals((int) listRectangles.get(counter[0]).getX(), (int) rect.getX());
                Assertions.assertEquals((int) listRectangles.get(counter[0]++).getY(), (int) rect.getY());
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
        Assertions.assertEquals(listBytes.size(), counter[0]);
    }

    @Test
    public void backgroundColorClipTest() {
        final PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        final PdfCanvas pdfCanvas = new PdfCanvas(pdfDocument.addNewPage()) {
            @Override
            public PdfCanvas rectangle(double x, double y, double width, double height) {
                Assertions.assertEquals(130.0, x, 0);
                Assertions.assertEquals(230.0, y, 0);
                Assertions.assertEquals(240.0, width, 0);
                Assertions.assertEquals(340.0, height, 0);
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
                Assertions.assertEquals(130.0, x, 0);
                Assertions.assertEquals(230.0, y, 0);
                Assertions.assertEquals(240.0, width, 0);
                Assertions.assertEquals(340.0, height, 0);
                return this;
            }

            @Override
            public PdfCanvas addXObjectFittedIntoRectangle(PdfXObject xObject, Rectangle rect) {
                Assertions.assertEquals(rawImage, xObject);
                Assertions.assertEquals(100f, rect.getX(), 0);
                Assertions.assertEquals(550f, rect.getY(), 0);
                Assertions.assertEquals(50f, rect.getWidth(), 0);
                Assertions.assertEquals(50f, rect.getHeight(), 0);
                return this;
            }
        };
        final DrawContext drawContext = new DrawContext(pdfDocument, pdfCanvas);
        final AbstractRenderer renderer = new DivRenderer(new Div().setPadding(20).setBorder(new DashedBorder(10)));
        renderer.occupiedArea = new LayoutArea(1, new Rectangle(100f, 200f, 300f, 400f));
        final BackgroundImage backgroundImage = new Builder().setImage(rawImage)
                .setBackgroundRepeat(new BackgroundRepeat(BackgroundRepeatValue.NO_REPEAT)).setBackgroundClip(BackgroundBox.CONTENT_BOX)
                .setBackgroundOrigin(BackgroundBox.BORDER_BOX).build();

        List<BackgroundImage> images = new ArrayList<>();
        images.add(backgroundImage);
        renderer.setProperty(Property.BACKGROUND_IMAGE, images);
        renderer.drawBackground(drawContext);
    }

    @Test
    public void backgroundLinearGradientClipOriginNoRepeatTest() {
        final PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        final byte[] bytes = new byte[] {54, 25, 47, 15, 2, 2, 2, 44, 55, 77, 86, 24};
        final PdfCanvas pdfCanvas = new PdfCanvas(pdfDocument.addNewPage()) {
            @Override
            public PdfCanvas rectangle(double x, double y, double width, double height) {
                Assertions.assertEquals(130.0, x, 0);
                Assertions.assertEquals(230.0, y, 0);
                Assertions.assertEquals(240.0, width, 0);
                Assertions.assertEquals(340.0, height, 0);
                return this;
            }

            @Override
            public PdfCanvas addXObjectFittedIntoRectangle(PdfXObject xObject, Rectangle rect) {
                Assertions.assertEquals(100f, rect.getX(), 0);
                Assertions.assertEquals(200f, rect.getY(), 0);
                Assertions.assertEquals(300f, rect.getWidth(), 0);
                Assertions.assertEquals(400f, rect.getHeight(), 0);
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
        List<BackgroundImage> images = new ArrayList<>();
        images.add(backgroundImage);
        renderer.setProperty(Property.BACKGROUND_IMAGE, images);
        renderer.drawBackground(drawContext);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.PAGE_WAS_FLUSHED_ACTION_WILL_NOT_BE_PERFORMED))
    public void applyLinkAnnotationFlushedPageTest() {
        AbstractRenderer abstractRenderer = new DivRenderer(new Div());
        abstractRenderer.occupiedArea = new LayoutArea(1, new Rectangle(100, 100));

        abstractRenderer.setProperty(Property.LINK_ANNOTATION, new PdfLinkAnnotation(new Rectangle(0, 0, 0, 0)));

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        pdfDocument.addNewPage();
        pdfDocument.getPage(1).flush();

        abstractRenderer.applyLinkAnnotation(pdfDocument);

        // This test checks that there is log message and there is no NPE so assertions are not required
        Assertions.assertTrue(true);
    }

    @Test
    public void nullChildTest() {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        pdfDocument.addNewPage();
        try (Document doc = new Document(pdfDocument)) {
            DocumentRenderer renderer = new DocumentRenderer(doc);
            DivRenderer divRenderer = new DivRenderer(new Div());
            divRenderer.childRenderers.add(null);

            AssertUtil.doesNotThrow(() -> renderer.linkRenderToDocument(divRenderer, doc.getPdfDocument()));
        }
    }

    @Test
    //TODO DEVSIX-6372 Obtaining DocumentRenderer's margins results in a ClassCastException
    public void obtainingMarginsErrorTest() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Document document = new Document(doc);
        RootRenderer renderer = document.getRenderer();
        Rectangle rect = new Rectangle(0, 0);
        Assertions.assertThrows(ClassCastException.class, () -> renderer.applyMargins(rect, false));
    }
}
