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
package com.itextpdf.layout.renderer;

import com.itextpdf.io.font.constants.StandardFontFamilies;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.properties.AlignmentPropertyValue;
import com.itextpdf.layout.properties.FlexDirectionPropertyValue;
import com.itextpdf.layout.properties.FlexWrapPropertyValue;
import com.itextpdf.layout.properties.JustifyContent;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class FlexUtilTest extends ExtendedITextTest {

    /* To see integration tests for flex algorithm go to FlexAlgoTest in html2pdf module.
    The names are preserved: one can go to FlexAlgoTest and see the corresponding tests, but be aware that with
    time they might change and we will not maintain such correspondence */

    private static final float EPS = 0.001f;

    private static final Style DEFAULT_STYLE;
    private static final Style WRAP_STYLE;
    private static final Style COLUMN_STYLE;

    private static final List<UnitValue> NULL_FLEX_BASIS_LIST;

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/FlexUtilTest/";

    static {
        DEFAULT_STYLE = new Style().setWidth(400).setHeight(100);

        WRAP_STYLE = new Style().setWidth(400).setHeight(100);
        WRAP_STYLE.setProperty(Property.FLEX_WRAP, FlexWrapPropertyValue.WRAP);

        COLUMN_STYLE = new Style().setWidth(100).setHeight(400);
        COLUMN_STYLE.setProperty(Property.FLEX_DIRECTION, FlexDirectionPropertyValue.COLUMN);

        NULL_FLEX_BASIS_LIST = new ArrayList<UnitValue>();
        for (int i = 0; i < 3; i++) {
            NULL_FLEX_BASIS_LIST.add(null);
        }
    }

    @Test
    public void defaultTest01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                NULL_FLEX_BASIS_LIST,
                Arrays.asList(1f, 1f, 1f),
                Arrays.asList(0f, 0f, 0f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(400f / 3, flexItemInfo.getRectangle().getWidth(), EPS);
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
    }

    @Test
    public void item1BasisGtWidthGrow0Shrink01Test01() {
        Rectangle bBox = new Rectangle(545, 842);
        List<UnitValue> flexBasisValues = Arrays.<UnitValue>asList(
                UnitValue.createPointValue(150f),
                UnitValue.createPointValue(50f)
        );

        Div div = new Div().setWidth(100).setHeight(100);

        DocumentRenderer documentRenderer = new DocumentRenderer(
                new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))));

        FlexContainerRenderer flexContainerRenderer = new FlexContainerRenderer(div);
        flexContainerRenderer.setParent(documentRenderer);
        div.setNextRenderer(flexContainerRenderer);

        for (UnitValue flexBasisValue : flexBasisValues) {
            Div flexItem = new Div().add(new Paragraph("x"));
            AbstractRenderer flexItemRenderer = (AbstractRenderer) flexItem.createRendererSubTree()
                    .setParent(flexContainerRenderer);
            flexItemRenderer.setProperty(Property.FLEX_GROW, 0f);
            flexItemRenderer.setProperty(Property.FLEX_SHRINK, 0.1f);
            flexItemRenderer.setProperty(Property.FLEX_BASIS, flexBasisValue);
            flexContainerRenderer.addChild(flexItemRenderer);
        }

        List<List<FlexItemInfo>> rectangleTable =
                FlexUtil.calculateChildrenRectangles(bBox, (FlexContainerRenderer) div.getRenderer());

        Assertions.assertEquals(135f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(45f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisGrow1Shrink0MarginBorderPaddingOnContainerTest01() {
        Style style = new Style()
                .setWidth(100)
                .setHeight(100)
                .setMargin(15)
                .setBorder(new SolidBorder(10))
                .setPadding(50);

        List<List<FlexItemInfo>> rectangleTable = testFlex(
                style,
                Arrays.<UnitValue>asList(UnitValue.createPointArray(new float[]{10f, 20f, 30f})),
                Arrays.asList(1f, 1f, 1f),
                Arrays.asList(0f, 0f, 0f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());

        Assertions.assertEquals(23.333334f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(33.333336f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(43.333336f, rectangleTable.get(0).get(2).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisGrow1Shrink0MarginBorderPaddingOnContainerNoWidthTest01() {
        Style style = new Style()
                .setMargin(15)
                .setBorder(new SolidBorder(10))
                .setPadding(5);

        List<List<FlexItemInfo>> rectangleTable = testFlex(
                style,
                Arrays.<UnitValue>asList(UnitValue.createPointArray(new float[]{50f, 100f, 150f})),
                Arrays.asList(1f, 1f, 1f),
                Arrays.asList(0f, 0f, 0f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());

        Assertions.assertEquals(104.333336f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(154.33334f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(204.33334f, rectangleTable.get(0).get(2).getRectangle().getWidth(), EPS);
    }

    @Test
    public void simpleStretchTest01() {
        Style stretchStyle = new Style(WRAP_STYLE);
        stretchStyle.setProperty(Property.ALIGN_CONTENT, AlignmentPropertyValue.STRETCH);
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                stretchStyle,
                Arrays.<UnitValue>asList(UnitValue.createPointValue(100f)),
                Arrays.asList(0f),
                Arrays.asList(0f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100f, flexItemInfo.getRectangle().getWidth(), EPS);
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
    }

    @Test
    public void basis100Grow0Shrink0ColumnTest() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                COLUMN_STYLE,
                Arrays.<UnitValue>asList(UnitValue.createPointValue(100f), UnitValue.createPointValue(100f), UnitValue.createPointValue(100f)),
                Arrays.asList(0f, 0f, 0f),
                Arrays.asList(0f, 0f, 0f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getWidth(), EPS);
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
    }

    @Test
    public void basis100Grow1Shrink0ColumnTest() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                COLUMN_STYLE,
                Arrays.<UnitValue>asList(UnitValue.createPointValue(100f), UnitValue.createPointValue(100f), UnitValue.createPointValue(100f)),
                Arrays.asList(1f, 1f, 1f),
                Arrays.asList(0f, 0f, 0f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getWidth(), EPS);
                Assertions.assertEquals(133.3333f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
    }

    @Test
    public void basis100Grow01Shrink0ColumnTest() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                COLUMN_STYLE,
                Arrays.<UnitValue>asList(UnitValue.createPointValue(100f), UnitValue.createPointValue(100f), UnitValue.createPointValue(100f)),
                Arrays.asList(0.1f, 0.1f, 0.1f),
                Arrays.asList(0f, 0f, 0f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getWidth(), EPS);
                Assertions.assertEquals(110.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
    }

    @Test
    public void basis200Grow0Shrink1ColumnTest() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                COLUMN_STYLE,
                Arrays.<UnitValue>asList(UnitValue.createPointValue(200f), UnitValue.createPointValue(200f), UnitValue.createPointValue(200f)),
                Arrays.asList(0f, 0f, 0f),
                Arrays.asList(1f, 1f, 1f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getWidth(), EPS);
                Assertions.assertEquals(133.33333f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
    }

    @Test
    public void basis100Grow0CustomShrinkContainerHeight50ColumnTest() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                new Style(COLUMN_STYLE).setHeight(50),
                Arrays.<UnitValue>asList(UnitValue.createPointValue(100f), UnitValue.createPointValue(100f)),
                Arrays.asList(0f, 0f),
                Arrays.asList(1f, 3f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getWidth(), EPS);
            }
        }

        // Expected because content of the element cannot be less than this value
        Assertions.assertEquals(25.9375f, rectangleTable.get(0).get(0).getRectangle().getHeight(), EPS);
        Assertions.assertEquals(25.9375f, rectangleTable.get(0).get(1).getRectangle().getHeight(), EPS);
    }

    @Test
    public void basis200Grow0CustomShrinkColumnTest1() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                COLUMN_STYLE,
                Arrays.<UnitValue>asList(UnitValue.createPointValue(200f), UnitValue.createPointValue(200f), UnitValue.createPointValue(200f)),
                Arrays.asList(0f, 0f, 0f),
                Arrays.asList(0f, 1f, 3f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getWidth(), EPS);
            }
        }

        Assertions.assertEquals(200f, rectangleTable.get(0).get(0).getRectangle().getHeight(), EPS);
        Assertions.assertEquals(150f, rectangleTable.get(0).get(1).getRectangle().getHeight(), EPS);
        Assertions.assertEquals(50f, rectangleTable.get(0).get(2).getRectangle().getHeight(), EPS);
    }

    @Test
    public void basis200Grow0Shrink01ColumnTest() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                COLUMN_STYLE,
                Arrays.<UnitValue>asList(UnitValue.createPointValue(200f), UnitValue.createPointValue(200f), UnitValue.createPointValue(200f)),
                Arrays.asList(0f, 0f, 0f),
                Arrays.asList(0.1f, 0.1f, 0.1f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getWidth(), EPS);
                Assertions.assertEquals(180f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
    }

    @Test
    public void basis200Height150Grow0Shrink1ColumnTest() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                COLUMN_STYLE,
                Arrays.<UnitValue>asList(UnitValue.createPointValue(200f), UnitValue.createPointValue(200f), UnitValue.createPointValue(200f)),
                Arrays.asList(0f, 0f, 0f),
                Arrays.asList(1f, 1f, 1f),
                new Style().setHeight(UnitValue.createPointValue(150))
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getWidth(), EPS);
                Assertions.assertEquals(133.3333f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
    }

    @Test
    public void basis100Height150Grow1Shrink0ColumnTest() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                COLUMN_STYLE,
                Arrays.<UnitValue>asList(UnitValue.createPointValue(100f), UnitValue.createPointValue(100f), UnitValue.createPointValue(100f)),
                Arrays.asList(1f, 1f, 1f),
                Arrays.asList(0f, 0f, 0f),
                new Style().setHeight(UnitValue.createPointValue(150))
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getWidth(), EPS);
                Assertions.assertEquals(133.3333f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
    }

    @Test
    public void basis100Height50Grow1Shrink0ColumnTest() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                COLUMN_STYLE,
                Arrays.<UnitValue>asList(UnitValue.createPointValue(100f), UnitValue.createPointValue(100f), UnitValue.createPointValue(100f)),
                Arrays.asList(1f, 1f, 1f),
                Arrays.asList(0f, 0f, 0f),
                new Style().setHeight(UnitValue.createPointValue(50))
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getWidth(), EPS);
                Assertions.assertEquals(133.3333f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
    }

    @Test
    public void basis100MaxHeight100Grow1Shrink0ColumnTest() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                COLUMN_STYLE,
                Arrays.<UnitValue>asList(UnitValue.createPointValue(100f), UnitValue.createPointValue(100f), UnitValue.createPointValue(100f)),
                Arrays.asList(1f, 1f, 1f),
                Arrays.asList(0f, 0f, 0f),
                new Style().setMaxHeight(UnitValue.createPointValue(100))
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getWidth(), EPS);
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
    }

    @Test
    public void basis200MinHeight150Grow0Shrink1ColumnTest() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                COLUMN_STYLE,
                Arrays.<UnitValue>asList(UnitValue.createPointValue(200f), UnitValue.createPointValue(200f), UnitValue.createPointValue(200f)),
                Arrays.asList(0f, 0f, 0f),
                Arrays.asList(1f, 1f, 1f),
                new Style().setMinHeight(UnitValue.createPointValue(150))
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100f, flexItemInfo.getRectangle().getWidth(), EPS);
                Assertions.assertEquals(150f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
    }

    @Test
    public void usualDirectionColumnWithDefiniteWidthTest() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                COLUMN_STYLE,
                Arrays.<UnitValue>asList(UnitValue.createPointValue(100f), UnitValue.createPointValue(100f), UnitValue.createPointValue(100f)),
                Arrays.asList(1f, 1f, 1f),
                Arrays.asList(0f, 0f, 0f),
                new Style().setWidth(UnitValue.createPointValue(50))
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(50.0f, flexItemInfo.getRectangle().getWidth(), EPS);
                Assertions.assertEquals(133.3333f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
    }

    @Test
    public void usualDirectionColumnWithDefiniteMaxWidthTest() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                COLUMN_STYLE,
                Arrays.<UnitValue>asList(UnitValue.createPointValue(100f), UnitValue.createPointValue(100f), UnitValue.createPointValue(100f)),
                Arrays.asList(1f, 1f, 1f),
                Arrays.asList(0f, 0f, 0f),
                new Style().setMaxWidth(UnitValue.createPointValue(50))
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(50.0f, flexItemInfo.getRectangle().getWidth(), EPS);
                Assertions.assertEquals(133.3333f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
    }

    @Test
    public void usualDirectionColumnWithDefiniteMinWidthTest() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                COLUMN_STYLE,
                Arrays.<UnitValue>asList(UnitValue.createPointValue(100f), UnitValue.createPointValue(100f), UnitValue.createPointValue(100f)),
                Arrays.asList(1f, 1f, 1f),
                Arrays.asList(0f, 0f, 0f),
                new Style().setMinWidth(UnitValue.createPointValue(150))
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(150.0f, flexItemInfo.getRectangle().getWidth(), EPS);
                Assertions.assertEquals(133.3333f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
    }

    @Test
    public void directionColumnWithoutBasisWithDefiniteHeightTest() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                COLUMN_STYLE,
                NULL_FLEX_BASIS_LIST,
                Arrays.asList(1f, 1f, 1f),
                Arrays.asList(0f, 0f, 0f),
                new Style().setHeight(UnitValue.createPointValue(50))
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getWidth(), EPS);
                Assertions.assertEquals(133.33333f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
    }

    @Test
    public void directionColumnWithWrapElementsToGrowTest() {
        Style columnWrapStyle = new Style(WRAP_STYLE);
        columnWrapStyle.setProperty(Property.FLEX_DIRECTION, FlexDirectionPropertyValue.COLUMN);
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                columnWrapStyle,
                Arrays.<UnitValue>asList(UnitValue.createPointValue(75f), UnitValue.createPointValue(75f), UnitValue.createPointValue(75f)),
                Arrays.asList(1f, 1f, 1f),
                Arrays.asList(0f, 0f, 0f)
        );

        // after checks
        Assertions.assertEquals(3, rectangleTable.size());

        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(133.33333f, flexItemInfo.getRectangle().getWidth(), EPS);
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
    }

    @Test
    public void directionColumnWithWrapElementsNotToGrowTest() {
        Style columnWrapStyle = new Style(WRAP_STYLE);
        columnWrapStyle.setProperty(Property.FLEX_DIRECTION, FlexDirectionPropertyValue.COLUMN);
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                columnWrapStyle,
                Arrays.<UnitValue>asList(UnitValue.createPointValue(75f), UnitValue.createPointValue(75f), UnitValue.createPointValue(75f)),
                Arrays.asList(0f, 0f, 0f),
                Arrays.asList(0f, 0f, 0f)
        );

        // after checks
        Assertions.assertEquals(3, rectangleTable.size());

        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(133.33333f, flexItemInfo.getRectangle().getWidth(), EPS);
                Assertions.assertEquals(75.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
    }

    @Test
    public void directionColumnWithWrapElementsToShrinkTest() {
        Style columnWrapStyle = new Style(WRAP_STYLE);
        columnWrapStyle.setProperty(Property.FLEX_DIRECTION, FlexDirectionPropertyValue.COLUMN);
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                columnWrapStyle,
                Arrays.<UnitValue>asList(UnitValue.createPointValue(120f), UnitValue.createPointValue(120f), UnitValue.createPointValue(120f)),
                Arrays.asList(0f, 0f, 0f),
                Arrays.asList(1f, 1f, 1f)
        );

        // after checks
        Assertions.assertEquals(3, rectangleTable.size());

        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(133.33333f, flexItemInfo.getRectangle().getWidth(), EPS);
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
    }

    @Test
    public void directionColumnWithWrapElementsNotToShrinkTest() {
        Style columnWrapStyle = new Style(WRAP_STYLE);
        columnWrapStyle.setProperty(Property.FLEX_DIRECTION, FlexDirectionPropertyValue.COLUMN);
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                columnWrapStyle,
                Arrays.<UnitValue>asList(UnitValue.createPointValue(120f), UnitValue.createPointValue(120f), UnitValue.createPointValue(120f)),
                Arrays.asList(0f, 0f, 0f),
                Arrays.asList(0f, 0f, 0f)
        );

        // after checks
        Assertions.assertEquals(3, rectangleTable.size());

        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(133.33333f, flexItemInfo.getRectangle().getWidth(), EPS);
                Assertions.assertEquals(120.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
    }

    @Test
    public void directionColumnWithWrapDefiniteWidthAndHeightTest() {
        Style columnWrapStyle = new Style(WRAP_STYLE);
        columnWrapStyle.setProperty(Property.FLEX_DIRECTION, FlexDirectionPropertyValue.COLUMN);
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                columnWrapStyle,
                Arrays.<UnitValue>asList(UnitValue.createPointValue(75f), UnitValue.createPointValue(75f), UnitValue.createPointValue(75f)),
                Arrays.asList(0f, 0f, 0f),
                Arrays.asList(0f, 0f, 0f),
                new Style().setWidth(100f).setHeight(120f)
        );

        // after checks
        Assertions.assertEquals(3, rectangleTable.size());

        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getWidth(), EPS);
                Assertions.assertEquals(75.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
    }

    @Test
    public void directionColumnWithWrapWithAlignItemsAndJustifyContentTest() {
        Style columnWrapStyle = new Style(WRAP_STYLE);
        columnWrapStyle.setProperty(Property.FLEX_DIRECTION, FlexDirectionPropertyValue.COLUMN);
        columnWrapStyle.setProperty(Property.ALIGN_ITEMS, AlignmentPropertyValue.FLEX_START);
        columnWrapStyle.setProperty(Property.JUSTIFY_CONTENT, JustifyContent.FLEX_END);
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                columnWrapStyle,
                Arrays.<UnitValue>asList(UnitValue.createPointValue(75f), UnitValue.createPointValue(75f), UnitValue.createPointValue(75f)),
                Arrays.asList(0f, 0f, 0f),
                Arrays.asList(0f, 0f, 0f)
        );

        // after checks
        Assertions.assertEquals(3, rectangleTable.size());

        for (int i = 0; i < rectangleTable.size(); ++i) {
            List<FlexItemInfo> line = rectangleTable.get(i);
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(6.0f, flexItemInfo.getRectangle().getWidth(), EPS);
                Assertions.assertEquals(75.0f, flexItemInfo.getRectangle().getHeight(), EPS);
                Assertions.assertEquals(i == 0 ? 0.0f : 127.33334, flexItemInfo.getRectangle().getX(), EPS);
                Assertions.assertEquals(25.0f, flexItemInfo.getRectangle().getY(), EPS);
            }
        }
    }

    @Test
    public void directionColumnWithAlignItemsAndJustifyContentTest1() {
        Style columnWrapStyle = new Style(COLUMN_STYLE);
        columnWrapStyle.setProperty(Property.ALIGN_ITEMS, AlignmentPropertyValue.FLEX_START);
        columnWrapStyle.setProperty(Property.JUSTIFY_CONTENT, JustifyContent.FLEX_END);
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                columnWrapStyle,
                Arrays.<UnitValue>asList(UnitValue.createPointValue(75f), UnitValue.createPointValue(75f), UnitValue.createPointValue(75f)),
                Arrays.asList(0f, 0f, 0f),
                Arrays.asList(0f, 0f, 0f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(6.0f, flexItemInfo.getRectangle().getWidth(), EPS);
                Assertions.assertEquals(75.0f, flexItemInfo.getRectangle().getHeight(), EPS);
                Assertions.assertEquals(0.0f, flexItemInfo.getRectangle().getX(), EPS);
            }
            Assertions.assertEquals(175.0f, line.get(0).getRectangle().getY(), EPS);
        }
    }

    @Test
    public void directionColumnWithAlignItemsAndJustifyContentTest2() {
        Style columnWrapStyle = new Style(COLUMN_STYLE);
        columnWrapStyle.setProperty(Property.ALIGN_ITEMS, AlignmentPropertyValue.CENTER);
        columnWrapStyle.setProperty(Property.JUSTIFY_CONTENT, JustifyContent.FLEX_START);
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                columnWrapStyle,
                Arrays.<UnitValue>asList(UnitValue.createPointValue(75f), UnitValue.createPointValue(75f), UnitValue.createPointValue(75f)),
                Arrays.asList(0f, 0f, 0f),
                Arrays.asList(0f, 0f, 0f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(6.0f, flexItemInfo.getRectangle().getWidth(), EPS);
                Assertions.assertEquals(75.0f, flexItemInfo.getRectangle().getHeight(), EPS);
                Assertions.assertEquals(47.0f, flexItemInfo.getRectangle().getX(), EPS);
                Assertions.assertEquals(0.0f, flexItemInfo.getRectangle().getY(), EPS);
            }
        }
    }

    @Test
    public void directionColumnWithAlignItemsAndJustifyContentTest3() {
        Style columnWrapStyle = new Style(COLUMN_STYLE);
        columnWrapStyle.setProperty(Property.ALIGN_ITEMS, AlignmentPropertyValue.FLEX_END);
        columnWrapStyle.setProperty(Property.JUSTIFY_CONTENT, JustifyContent.CENTER);
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                columnWrapStyle,
                Arrays.<UnitValue>asList(UnitValue.createPointValue(75f), UnitValue.createPointValue(75f), UnitValue.createPointValue(75f)),
                Arrays.asList(0f, 0f, 0f),
                Arrays.asList(0f, 0f, 0f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(6.0f, flexItemInfo.getRectangle().getWidth(), EPS);
                Assertions.assertEquals(75.0f, flexItemInfo.getRectangle().getHeight(), EPS);
                Assertions.assertEquals(94.0f, flexItemInfo.getRectangle().getX(), EPS);
            }
            Assertions.assertEquals(87.5f, line.get(0).getRectangle().getY(), EPS);
        }
    }

    @Test
    public void imgAsFlexItemTest01() throws MalformedURLException {
        Rectangle bBox = new Rectangle(575, 842);
        List<UnitValue> flexBasisValues = Arrays.<UnitValue>asList(
                UnitValue.createPointValue(50f),
                UnitValue.createPointValue(30f)
        );

        Div div = new Div().setWidth(100).setHeight(100);

        DocumentRenderer documentRenderer = new DocumentRenderer(
                new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))));

        FlexContainerRenderer flexContainerRenderer = new FlexContainerRenderer(div);
        flexContainerRenderer.setParent(documentRenderer);
        div.setNextRenderer(flexContainerRenderer);

        flexContainerRenderer.setProperty(Property.FLEX_DIRECTION, FlexDirectionPropertyValue.COLUMN);

        for (int i = 0; i < flexBasisValues.size(); i++) {
            IElement flexItem = (i == 0)
                    ? (IElement) new Image(ImageDataFactory.create(SOURCE_FOLDER + "itis.jpg"))
                    : (IElement) new Div().add(new Paragraph(Integer.toString(i)));
            flexItem.setProperty(Property.FLEX_GROW, 0f);
            flexItem.setProperty(Property.FLEX_SHRINK, 0f);
            flexItem.setProperty(Property.FLEX_BASIS, flexBasisValues.get(i));
            if (i == 0) {
                flexItem.setProperty(Property.MAX_HEIGHT, UnitValue.createPointValue(40));
                div.add((Image) flexItem);
            } else {
                div.add((IBlockElement) flexItem);
            }
            AbstractRenderer flexItemRenderer = (AbstractRenderer) flexItem.createRendererSubTree().setParent(flexContainerRenderer);
            flexContainerRenderer.addChild(flexItemRenderer);
        }

        List<List<FlexItemInfo>> rectangleTable =
                FlexUtil.calculateChildrenRectangles(bBox, (FlexContainerRenderer) div.getRenderer());

        Assertions.assertEquals(100f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(100f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(40f, rectangleTable.get(0).get(0).getRectangle().getHeight(), EPS);
        Assertions.assertEquals(30f, rectangleTable.get(0).get(1).getRectangle().getHeight(), EPS);
    }

    @Test
    public void basisGtWidthGrow0Shrink0Test01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                WRAP_STYLE,
                Arrays.<UnitValue>asList(UnitValue.createPointValue(500f)),
                Arrays.asList(0f),
                Arrays.asList(0f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(500f, flexItemInfo.getRectangle().getWidth(), EPS);
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
    }

    @Test
    public void basisGtWidthGrow0Shrink1Test01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                WRAP_STYLE,
                Arrays.<UnitValue>asList(UnitValue.createPointValue(500f)),
                Arrays.asList(0f),
                Arrays.asList(1f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(400f, flexItemInfo.getRectangle().getWidth(), EPS);
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
    }

    @Test
    public void basisMinGrow0Shrink1Item2Grow05Test01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                NULL_FLEX_BASIS_LIST,
                Arrays.asList(0f, 0.5f, 0f),
                Arrays.asList(1f, 1f, 1f));

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (int i = 0; i < rectangleTable.size(); i++) {
            FlexItemInfo flexItemInfo = rectangleTable.get(0).get(i);
            Assertions.assertEquals(i == 1 ? 197 : 6f, flexItemInfo.getRectangle().getWidth(), EPS);
            Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
        }
    }

    @Test
    public void basisMinGrow0Shrink1Item2Grow2Test01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                NULL_FLEX_BASIS_LIST,
                Arrays.asList(0f, 2f, 0f),
                Arrays.asList(1f, 1f, 1f));

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (int i = 0; i < rectangleTable.size(); i++) {
            FlexItemInfo flexItemInfo = rectangleTable.get(0).get(i);
            Assertions.assertEquals(i == 1 ? 388f : 6f, flexItemInfo.getRectangle().getWidth(), EPS);
            Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
        }
    }

    @Test
    public void basisMinGrow2Shrink1Test01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                NULL_FLEX_BASIS_LIST,
                Arrays.asList(2f, 2f, 2f),
                Arrays.asList(1f, 1f, 1f));

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(400f / 3, flexItemInfo.getRectangle().getWidth(), EPS);
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
    }

    @Test
    public void basisMinGrow05SumGt1Shrink1Test01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                NULL_FLEX_BASIS_LIST,
                Arrays.asList(0.5f, 0.5f, 0.5f),
                Arrays.asList(1f, 1f, 1f));

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(400f / 3, flexItemInfo.getRectangle().getWidth(), EPS);
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
    }

    @Test
    public void basisMinGrow01SumLt1Shrink1Test01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                NULL_FLEX_BASIS_LIST,
                Arrays.asList(0.1f, 0.1f, 0.1f),
                Arrays.asList(1f, 1f, 1f));

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(44.2f, flexItemInfo.getRectangle().getWidth(), EPS);
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
    }

    @Test
    public void basisMinGrow0Shrink05SumGt1Test01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                NULL_FLEX_BASIS_LIST,
                Arrays.asList(0f, 0f, 0f),
                Arrays.asList(0.5f, 0.5f, 0.5f));

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(6f, flexItemInfo.getRectangle().getWidth(), EPS);
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
    }

    @Test
    public void basisMinGrow0Shrink01SumLt1Test01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                NULL_FLEX_BASIS_LIST,
                Arrays.asList(0f, 0f, 0f),
                Arrays.asList(0.1f, 0.1f, 0.1f));

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(6f, flexItemInfo.getRectangle().getWidth(), EPS);
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
    }

    @Test
    public void basis50SumLtWidthGrow0Shrink1Test01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                Arrays.<UnitValue>asList(
                        UnitValue.createPointValue(50f),
                        UnitValue.createPointValue(50f),
                        UnitValue.createPointValue(50f)),
                Arrays.asList(0f, 0f, 0f),
                Arrays.asList(1f, 1f, 1f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(50f, flexItemInfo.getRectangle().getWidth(), EPS);
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
    }

    @Test
    public void basis250SumGtWidthGrow0Shrink1Test01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                Arrays.<UnitValue>asList(
                        UnitValue.createPointValue(250f),
                        UnitValue.createPointValue(250f),
                        UnitValue.createPointValue(250f)),
                Arrays.asList(0f, 0f, 0f),
                Arrays.asList(1f, 1f, 1f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(400f / 3, flexItemInfo.getRectangle().getWidth(), EPS);
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
    }

    @Test
    public void differentBasisSumLtWidthGrow0Shrink1Test01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                Arrays.<UnitValue>asList(
                        UnitValue.createPointValue(50f),
                        UnitValue.createPointValue(80f),
                        UnitValue.createPointValue(100f)),
                Arrays.asList(0f, 0f, 0f),
                Arrays.asList(1f, 1f, 1f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
        Assertions.assertEquals(50f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(80f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(100f, rectangleTable.get(0).get(2).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisSumLtWidthGrow1Shrink1Test01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                Arrays.<UnitValue>asList(
                        UnitValue.createPointValue(50f),
                        UnitValue.createPointValue(80f),
                        UnitValue.createPointValue(100f)),
                Arrays.asList(1f, 1f, 1f),
                Arrays.asList(1f, 1f, 1f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
        Assertions.assertEquals(106.66667f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(136.66667f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(156.66667f, rectangleTable.get(0).get(2).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisSumLtWidthGrow1Shrink0Item2MarginBorderPadding30Test01() {
        Rectangle bBox = new Rectangle(575, 842);
        List<UnitValue> flexBasisValues = Arrays.<UnitValue>asList(
                UnitValue.createPointValue(50f),
                UnitValue.createPointValue(80f),
                UnitValue.createPointValue(100f)
        );

        Div div = new Div().setWidth(400).setHeight(100);

        DocumentRenderer documentRenderer = new DocumentRenderer(
                new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))));

        FlexContainerRenderer flexContainerRenderer = new FlexContainerRenderer(div);
        flexContainerRenderer.setParent(documentRenderer);
        div.setNextRenderer(flexContainerRenderer);

        for (int i = 0; i < flexBasisValues.size(); i++) {
            Div flexItem = new Div().add(new Paragraph("x"));
            if (1 == i) {
                flexItem.setMargin(10).setBorder(new SolidBorder(15)).setPadding(5);
            }
            AbstractRenderer flexItemRenderer = (AbstractRenderer) flexItem.createRendererSubTree()
                    .setParent(flexContainerRenderer);
            flexItemRenderer.setProperty(Property.FLEX_GROW, 1f);
            flexItemRenderer.setProperty(Property.FLEX_SHRINK, 0f);
            flexItemRenderer.setProperty(Property.FLEX_BASIS, flexBasisValues.get(i));
            div.add(flexItem);
            flexContainerRenderer.addChild(flexItemRenderer);
        }

        List<List<FlexItemInfo>> rectangleTable =
                FlexUtil.calculateChildrenRectangles(bBox, (FlexContainerRenderer) div.getRenderer());

        Assertions.assertEquals(86.66667f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(176.66667f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(136.66667f, rectangleTable.get(0).get(2).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisSumLtWidthGrow1Shrink1Item2MarginBorderPadding30Test01() {
        Rectangle bBox = new Rectangle(575, 842);
        List<UnitValue> flexBasisValues = Arrays.<UnitValue>asList(
                UnitValue.createPointValue(50f),
                UnitValue.createPointValue(80f),
                UnitValue.createPointValue(100f)
        );

        Div div = new Div().setWidth(200).setHeight(100);

        DocumentRenderer documentRenderer = new DocumentRenderer(
                new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))));

        FlexContainerRenderer flexContainerRenderer = new FlexContainerRenderer(div);
        flexContainerRenderer.setParent(documentRenderer);
        div.setNextRenderer(flexContainerRenderer);

        for (int i = 0; i < flexBasisValues.size(); i++) {
            Div flexItem = new Div().add(new Paragraph("x"));
            if (1 == i) {
                flexItem.setMargin(10).setBorder(new SolidBorder(15)).setPadding(5);
            }
            AbstractRenderer flexItemRenderer = (AbstractRenderer) flexItem.createRendererSubTree()
                    .setParent(documentRenderer);
            flexItemRenderer.setProperty(Property.FLEX_GROW, 1f);
            flexItemRenderer.setProperty(Property.FLEX_SHRINK, 1f);
            flexItemRenderer.setProperty(Property.FLEX_BASIS, flexBasisValues.get(i));
            flexContainerRenderer.addChild(flexItemRenderer);
        }

        List<List<FlexItemInfo>> rectangleTable =
                FlexUtil.calculateChildrenRectangles(bBox, (FlexContainerRenderer) div.getRenderer());

        Assertions.assertEquals(30.434784f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(108.69565f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(60.869568f, rectangleTable.get(0).get(2).getRectangle().getWidth(), EPS);
    }

    @Test
    public void ltWidthGrow0Shrink1Item2MarginBorderPadding30Test01() {
        Rectangle bBox = new Rectangle(575, 842);
        List<UnitValue> flexBasisValues = Arrays.<UnitValue>asList(
                UnitValue.createPointValue(50f),
                UnitValue.createPointValue(50f),
                UnitValue.createPointValue(50f)
        );

        Div div = new Div().setWidth(200).setHeight(300);

        DocumentRenderer documentRenderer = new DocumentRenderer(
                new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))));

        FlexContainerRenderer flexContainerRenderer = new FlexContainerRenderer(div);
        flexContainerRenderer.setParent(documentRenderer);
        flexContainerRenderer.setProperty(Property.FLEX_WRAP, FlexWrapPropertyValue.WRAP);
        div.setNextRenderer(flexContainerRenderer);

        for (int i = 0; i < flexBasisValues.size(); i++) {
            Div flexItem = new Div().add(new Paragraph("x"));
            if (1 == i) {
                flexItem.setMargin(10).setBorder(new SolidBorder(15)).setPadding(5);
                flexItem.setHeight(50);
            }
            AbstractRenderer flexItemRenderer = (AbstractRenderer) flexItem.createRendererSubTree()
                    .setParent(documentRenderer);
            flexItemRenderer.setProperty(Property.FLEX_GROW, 0f);
            flexItemRenderer.setProperty(Property.FLEX_SHRINK, 1f);
            flexItemRenderer.setProperty(Property.FLEX_BASIS, flexBasisValues.get(i));
            flexContainerRenderer.addChild(flexItemRenderer);
        }

        List<List<FlexItemInfo>> rectangleTable =
                FlexUtil.calculateChildrenRectangles(bBox, (FlexContainerRenderer) div.getRenderer());

        Assertions.assertEquals(192.03125f, rectangleTable.get(0).get(0).getRectangle().getHeight(), EPS);
        Assertions.assertEquals(110.0f, rectangleTable.get(0).get(1).getRectangle().getHeight(), EPS);
        Assertions.assertEquals(107.96875f, rectangleTable.get(1).get(0).getRectangle().getHeight(), EPS);

        Assertions.assertEquals(50.0f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(110.0f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(50.0f, rectangleTable.get(1).get(0).getRectangle().getWidth(), EPS);
    }

    @Test
    public void ltWidthGrow0Shrink1Item2MBP30JustifyContentCenterTest() {
        Rectangle bBox = new Rectangle(575, 842);
        List<UnitValue> flexBasisValues = Arrays.<UnitValue>asList(
                UnitValue.createPointValue(50f),
                UnitValue.createPointValue(50f),
                UnitValue.createPointValue(50f)
        );

        Div div = new Div().setWidth(200).setHeight(300);

        DocumentRenderer documentRenderer = new DocumentRenderer(
                new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))));

        FlexContainerRenderer flexContainerRenderer = new FlexContainerRenderer(div);
        flexContainerRenderer.setParent(documentRenderer);
        flexContainerRenderer.setProperty(Property.FLEX_WRAP, FlexWrapPropertyValue.WRAP);
        flexContainerRenderer.setProperty(Property.JUSTIFY_CONTENT, JustifyContent.CENTER);
        flexContainerRenderer.setProperty(Property.ALIGN_ITEMS, AlignmentPropertyValue.CENTER);
        div.setNextRenderer(flexContainerRenderer);

        for (int i = 0; i < flexBasisValues.size(); i++) {
            Div flexItem = new Div().add(new Paragraph("x"));
            if (1 == i) {
                flexItem.setMargin(10).setBorder(new SolidBorder(15)).setPadding(5);
                flexItem.setHeight(50);
            }
            AbstractRenderer flexItemRenderer = (AbstractRenderer) flexItem.createRendererSubTree()
                    .setParent(documentRenderer);
            flexItemRenderer.setProperty(Property.FLEX_GROW, 0f);
            flexItemRenderer.setProperty(Property.FLEX_SHRINK, 1f);
            flexItemRenderer.setProperty(Property.FLEX_BASIS, flexBasisValues.get(i));
            flexContainerRenderer.addChild(flexItemRenderer);
        }

        List<List<FlexItemInfo>> rectangleTable =
                FlexUtil.calculateChildrenRectangles(bBox, (FlexContainerRenderer) div.getRenderer());

        Assertions.assertEquals(25.9375f, rectangleTable.get(0).get(0).getRectangle().getHeight(), EPS);
        Assertions.assertEquals(110.0f, rectangleTable.get(0).get(1).getRectangle().getHeight(), EPS);
        Assertions.assertEquals(25.9375f, rectangleTable.get(1).get(0).getRectangle().getHeight(), EPS);

        Assertions.assertEquals(50.0f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(110.0f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(50.0f, rectangleTable.get(1).get(0).getRectangle().getWidth(), EPS);

        Assertions.assertEquals(20.0f, rectangleTable.get(0).get(0).getRectangle().getX(), EPS);
        Assertions.assertEquals(0f, rectangleTable.get(0).get(1).getRectangle().getX(), EPS);
        Assertions.assertEquals(75.0f, rectangleTable.get(1).get(0).getRectangle().getX(), EPS);

        Assertions.assertEquals(83.046875f, rectangleTable.get(0).get(0).getRectangle().getY(), EPS);
        Assertions.assertEquals(41.015625f, rectangleTable.get(0).get(1).getRectangle().getY(), EPS);
        Assertions.assertEquals(82.03125f, rectangleTable.get(1).get(0).getRectangle().getY(), EPS);
    }

    @Test
    public void ltWidthGrow0Shrink1Item2MBP30JustifyContentFlexStartTest() {
        JustifyContent[] justifyContentValues = {
                JustifyContent.NORMAL,
                JustifyContent.START,
                JustifyContent.STRETCH,
                JustifyContent.LEFT,
                JustifyContent.SELF_START,
                JustifyContent.FLEX_START
        };
        AlignmentPropertyValue[] alignItemsValues = {
                AlignmentPropertyValue.START,
                AlignmentPropertyValue.SELF_START,
                AlignmentPropertyValue.BASELINE,
                AlignmentPropertyValue.SELF_START,
                AlignmentPropertyValue.FLEX_START,
                AlignmentPropertyValue.FLEX_START
        };
        for (int j = 0; j < justifyContentValues.length; ++j) {
            Rectangle bBox = new Rectangle(575, 842);
            List<UnitValue> flexBasisValues = Arrays.<UnitValue>asList(
                    UnitValue.createPointValue(50f),
                    UnitValue.createPointValue(50f),
                    UnitValue.createPointValue(50f)
            );

            Div div = new Div().setWidth(200).setHeight(300);

            DocumentRenderer documentRenderer = new DocumentRenderer(
                    new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))));

            FlexContainerRenderer flexContainerRenderer = new FlexContainerRenderer(div);
            flexContainerRenderer.setParent(documentRenderer);
            flexContainerRenderer.setProperty(Property.FLEX_WRAP, FlexWrapPropertyValue.WRAP);
            flexContainerRenderer.setProperty(Property.JUSTIFY_CONTENT, justifyContentValues[j]);
            flexContainerRenderer.setProperty(Property.ALIGN_ITEMS, alignItemsValues[j]);
            div.setNextRenderer(flexContainerRenderer);

            for (int i = 0; i < flexBasisValues.size(); i++) {
                Div flexItem = new Div().add(new Paragraph("x"));
                if (1 == i) {
                    flexItem.setMargin(10).setBorder(new SolidBorder(15)).setPadding(5);
                    flexItem.setHeight(50);
                }
                AbstractRenderer flexItemRenderer = (AbstractRenderer) flexItem.createRendererSubTree()
                        .setParent(documentRenderer);
                flexItemRenderer.setProperty(Property.FLEX_GROW, 0f);
                flexItemRenderer.setProperty(Property.FLEX_SHRINK, 1f);
                flexItemRenderer.setProperty(Property.FLEX_BASIS, flexBasisValues.get(i));
                flexContainerRenderer.addChild(flexItemRenderer);
            }

            List<List<FlexItemInfo>> rectangleTable =
                    FlexUtil.calculateChildrenRectangles(bBox, (FlexContainerRenderer) div.getRenderer());

            Assertions.assertEquals(25.9375f, rectangleTable.get(0).get(0).getRectangle().getHeight(), EPS);
            Assertions.assertEquals(110.0f, rectangleTable.get(0).get(1).getRectangle().getHeight(), EPS);
            Assertions.assertEquals(25.9375f, rectangleTable.get(1).get(0).getRectangle().getHeight(), EPS);

            Assertions.assertEquals(50.0f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
            Assertions.assertEquals(110.0f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
            Assertions.assertEquals(50.0f, rectangleTable.get(1).get(0).getRectangle().getWidth(), EPS);

            Assertions.assertEquals(0f, rectangleTable.get(0).get(0).getRectangle().getX(), EPS);
            Assertions.assertEquals(0f, rectangleTable.get(0).get(1).getRectangle().getX(), EPS);
            Assertions.assertEquals(0f, rectangleTable.get(1).get(0).getRectangle().getX(), EPS);

            Assertions.assertEquals(0f, rectangleTable.get(0).get(0).getRectangle().getY(), EPS);
            Assertions.assertEquals(0f, rectangleTable.get(0).get(1).getRectangle().getY(), EPS);
            Assertions.assertEquals(82.03125f, rectangleTable.get(1).get(0).getRectangle().getY(), EPS);
        }
    }

    @Test
    public void ltWidthGrow0Shrink1Item2MBP30JustifyContentFlexEndTest() {
        JustifyContent[] justifyContentValues = {
                JustifyContent.END,
                JustifyContent.RIGHT,
                JustifyContent.SELF_END,
                JustifyContent.FLEX_END
        };
        AlignmentPropertyValue[] alignItemsValues = {
                AlignmentPropertyValue.END,
                AlignmentPropertyValue.SELF_END,
                AlignmentPropertyValue.FLEX_END,
                AlignmentPropertyValue.FLEX_END
        };
        for (int j = 0; j < justifyContentValues.length; ++j) {
            Rectangle bBox = new Rectangle(575, 842);
            List<UnitValue> flexBasisValues = Arrays.<UnitValue>asList(
                    UnitValue.createPointValue(50f),
                    UnitValue.createPointValue(50f),
                    UnitValue.createPointValue(50f)
            );

            Div div = new Div().setWidth(200).setHeight(300);

            DocumentRenderer documentRenderer = new DocumentRenderer(
                    new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))));

            FlexContainerRenderer flexContainerRenderer = new FlexContainerRenderer(div);
            flexContainerRenderer.setParent(documentRenderer);
            flexContainerRenderer.setProperty(Property.FLEX_WRAP, FlexWrapPropertyValue.WRAP);
            flexContainerRenderer.setProperty(Property.JUSTIFY_CONTENT, justifyContentValues[j]);
            flexContainerRenderer.setProperty(Property.ALIGN_ITEMS, alignItemsValues[j]);
            div.setNextRenderer(flexContainerRenderer);

            for (int i = 0; i < flexBasisValues.size(); i++) {
                Div flexItem = new Div().add(new Paragraph("x"));
                if (1 == i) {
                    flexItem.setMargin(10).setBorder(new SolidBorder(15)).setPadding(5);
                    flexItem.setHeight(50);
                }
                AbstractRenderer flexItemRenderer = (AbstractRenderer) flexItem.createRendererSubTree()
                        .setParent(documentRenderer);
                flexItemRenderer.setProperty(Property.FLEX_GROW, 0f);
                flexItemRenderer.setProperty(Property.FLEX_SHRINK, 1f);
                flexItemRenderer.setProperty(Property.FLEX_BASIS, flexBasisValues.get(i));
                flexContainerRenderer.addChild(flexItemRenderer);
            }

            List<List<FlexItemInfo>> rectangleTable =
                    FlexUtil.calculateChildrenRectangles(bBox, (FlexContainerRenderer) div.getRenderer());

            Assertions.assertEquals(25.9375f, rectangleTable.get(0).get(0).getRectangle().getHeight(), EPS);
            Assertions.assertEquals(110.0f, rectangleTable.get(0).get(1).getRectangle().getHeight(), EPS);
            Assertions.assertEquals(25.9375f, rectangleTable.get(1).get(0).getRectangle().getHeight(), EPS);

            Assertions.assertEquals(50.0f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
            Assertions.assertEquals(110.0f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
            Assertions.assertEquals(50.0f, rectangleTable.get(1).get(0).getRectangle().getWidth(), EPS);

            Assertions.assertEquals(40.0f, rectangleTable.get(0).get(0).getRectangle().getX(), EPS);
            Assertions.assertEquals(0f, rectangleTable.get(0).get(1).getRectangle().getX(), EPS);
            Assertions.assertEquals(150.0f, rectangleTable.get(1).get(0).getRectangle().getX(), EPS);

            Assertions.assertEquals(166.09375f, rectangleTable.get(0).get(0).getRectangle().getY(), EPS);
            Assertions.assertEquals(82.03125f, rectangleTable.get(0).get(1).getRectangle().getY(), EPS);
            Assertions.assertEquals(82.03125f, rectangleTable.get(1).get(0).getRectangle().getY(), EPS);
        }
    }

    @Test
    public void ltWidthGrow0Shrink1Item2MBP30AlignItemsStretchTest() {
        AlignmentPropertyValue[] alignItemsValues = {
                AlignmentPropertyValue.STRETCH,
                AlignmentPropertyValue.NORMAL
        };
        for (AlignmentPropertyValue alignItemsValue : alignItemsValues) {
            Rectangle bBox = new Rectangle(575, 842);
            List<UnitValue> flexBasisValues = Arrays.<UnitValue>asList(
                    UnitValue.createPointValue(50f),
                    UnitValue.createPointValue(50f),
                    UnitValue.createPointValue(50f)
            );

            Div div = new Div().setWidth(200).setHeight(300);

            DocumentRenderer documentRenderer = new DocumentRenderer(
                    new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))));

            FlexContainerRenderer flexContainerRenderer = new FlexContainerRenderer(div);
            flexContainerRenderer.setParent(documentRenderer);
            flexContainerRenderer.setProperty(Property.FLEX_WRAP, FlexWrapPropertyValue.WRAP);
            flexContainerRenderer.setProperty(Property.ALIGN_ITEMS, alignItemsValue);
            div.setNextRenderer(flexContainerRenderer);

            for (int i = 0; i < flexBasisValues.size(); i++) {
                Div flexItem = new Div().add(new Paragraph("x"));
                if (1 == i) {
                    flexItem.setMargin(10).setBorder(new SolidBorder(15)).setPadding(5);
                    flexItem.setHeight(50);
                }
                AbstractRenderer flexItemRenderer = (AbstractRenderer) flexItem.createRendererSubTree()
                        .setParent(documentRenderer);
                flexItemRenderer.setProperty(Property.FLEX_GROW, 0f);
                flexItemRenderer.setProperty(Property.FLEX_SHRINK, 1f);
                flexItemRenderer.setProperty(Property.FLEX_BASIS, flexBasisValues.get(i));
                flexContainerRenderer.addChild(flexItemRenderer);
            }

            List<List<FlexItemInfo>> rectangleTable =
                    FlexUtil.calculateChildrenRectangles(bBox, (FlexContainerRenderer) div.getRenderer());

            Assertions.assertEquals(192.03125f, rectangleTable.get(0).get(0).getRectangle().getHeight(), EPS);
            Assertions.assertEquals(110.0f, rectangleTable.get(0).get(1).getRectangle().getHeight(), EPS);
            Assertions.assertEquals(107.96875f, rectangleTable.get(1).get(0).getRectangle().getHeight(), EPS);

            Assertions.assertEquals(50.0f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
            Assertions.assertEquals(110.0f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
            Assertions.assertEquals(50.0f, rectangleTable.get(1).get(0).getRectangle().getWidth(), EPS);

            Assertions.assertEquals(0f, rectangleTable.get(0).get(0).getRectangle().getX(), EPS);
            Assertions.assertEquals(0f, rectangleTable.get(0).get(1).getRectangle().getX(), EPS);
            Assertions.assertEquals(0f, rectangleTable.get(1).get(0).getRectangle().getX(), EPS);

            Assertions.assertEquals(0f, rectangleTable.get(0).get(0).getRectangle().getY(), EPS);
            Assertions.assertEquals(0f, rectangleTable.get(0).get(1).getRectangle().getY(), EPS);
            Assertions.assertEquals(0f, rectangleTable.get(1).get(0).getRectangle().getY(), EPS);
        }
    }

    @Test
    public void ltWidthGrow0Shrink1Item2MBP30JustifyContentCenterDontFitTest() {
        Rectangle bBox = new Rectangle(575, 842);
        List<UnitValue> flexBasisValues = Arrays.<UnitValue>asList(
                UnitValue.createPointValue(100f),
                UnitValue.createPointValue(100f),
                UnitValue.createPointValue(100f)
        );

        Div div = new Div().setWidth(200).setHeight(200);

        DocumentRenderer documentRenderer = new DocumentRenderer(
                new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))));

        FlexContainerRenderer flexContainerRenderer = new FlexContainerRenderer(div);
        flexContainerRenderer.setParent(documentRenderer);
        flexContainerRenderer.setProperty(Property.JUSTIFY_CONTENT, JustifyContent.CENTER);
        flexContainerRenderer.setProperty(Property.ALIGN_ITEMS, AlignmentPropertyValue.CENTER);
        div.setNextRenderer(flexContainerRenderer);

        for (int i = 0; i < flexBasisValues.size(); i++) {
            Div flexItem = new Div().add(new Paragraph("x"));
            if (1 == i) {
                flexItem.setMargin(10).setBorder(new SolidBorder(15)).setPadding(5);
                flexItem.setHeight(220);
            }
            AbstractRenderer flexItemRenderer = (AbstractRenderer) flexItem.createRendererSubTree()
                    .setParent(documentRenderer);
            flexItemRenderer.setProperty(Property.FLEX_GROW, 0f);
            flexItemRenderer.setProperty(Property.FLEX_SHRINK, 0f);
            flexItemRenderer.setProperty(Property.FLEX_BASIS, flexBasisValues.get(i));
            flexContainerRenderer.addChild(flexItemRenderer);
        }

        List<List<FlexItemInfo>> rectangleTable =
                FlexUtil.calculateChildrenRectangles(bBox, (FlexContainerRenderer) div.getRenderer());

        Assertions.assertEquals(25.9375f, rectangleTable.get(0).get(0).getRectangle().getHeight(), EPS);
        Assertions.assertEquals(280.0f, rectangleTable.get(0).get(1).getRectangle().getHeight(), EPS);
        Assertions.assertEquals(25.9375f, rectangleTable.get(0).get(2).getRectangle().getHeight(), EPS);

        Assertions.assertEquals(100.0f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(160.0f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(100.0f, rectangleTable.get(0).get(2).getRectangle().getWidth(), EPS);

        Assertions.assertEquals(-80.0f, rectangleTable.get(0).get(0).getRectangle().getX(), EPS);
        Assertions.assertEquals(0f, rectangleTable.get(0).get(1).getRectangle().getX(), EPS);
        Assertions.assertEquals(0f, rectangleTable.get(0).get(2).getRectangle().getX(), EPS);

        Assertions.assertEquals(87.03125f, rectangleTable.get(0).get(0).getRectangle().getY(), EPS);
        Assertions.assertEquals(-40.0f, rectangleTable.get(0).get(1).getRectangle().getY(), EPS);
        Assertions.assertEquals(87.03125f, rectangleTable.get(0).get(2).getRectangle().getY(), EPS);
    }

    @Test
    public void differentBasisSumLtWidthGrow1Shrink1Item2MuchContentTest01() {
        Rectangle bBox = new Rectangle(575, 842);
        List<UnitValue> flexBasisValues = Arrays.<UnitValue>asList(
                UnitValue.createPointValue(37.5f),
                UnitValue.createPointValue(60f),
                UnitValue.createPointValue(75f)
        );

        Div div = new Div().setWidth(300).setHeight(100);

        // We use Courier as a monotype font to ensure that min width calculated by iText
        // is more or less the same as the width calculated by browsers
        FontProvider provider = new FontProvider();
        provider.getFontSet().addFont(StandardFonts.COURIER, null, "courier");

        DocumentRenderer documentRenderer = new DocumentRenderer(
                new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))));
        documentRenderer.setProperty(Property.FONT_PROVIDER, provider);

        FlexContainerRenderer flexContainerRenderer = new FlexContainerRenderer(div);
        flexContainerRenderer.setParent(documentRenderer);
        div.setNextRenderer(flexContainerRenderer);

        for (int i = 0; i < flexBasisValues.size(); i++) {
            Div flexItem = new Div().add(new Paragraph(1 == i ? "2222222222222222222222222" : Integer.toString(i)));
            if (1 == i) {
                flexItem.setFontFamily(StandardFontFamilies.COURIER);
            }
            AbstractRenderer flexItemRenderer = (AbstractRenderer) flexItem.createRendererSubTree()
                    .setParent(flexContainerRenderer);
            flexItemRenderer.setProperty(Property.FLEX_GROW, 1f);
            flexItemRenderer.setProperty(Property.FLEX_SHRINK, 1f);
            flexItemRenderer.setProperty(Property.FLEX_BASIS, flexBasisValues.get(i));
            flexContainerRenderer.addChild(flexItemRenderer);
        }

        List<List<FlexItemInfo>> rectangleTable =
                FlexUtil.calculateChildrenRectangles(bBox, (FlexContainerRenderer) div.getRenderer());

        Assertions.assertEquals(41.250023f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(179.99995f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(78.75002f, rectangleTable.get(0).get(2).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisSumLtWidthGrow1Shrink1Item2MuchContentSetMinWidthLtBasisTest01() {
        Rectangle bBox = new Rectangle(575, 842);
        List<UnitValue> flexBasisValues = Arrays.<UnitValue>asList(
                UnitValue.createPointValue(37.5f),
                UnitValue.createPointValue(60f),
                UnitValue.createPointValue(75f)
        );

        Div div = new Div().setWidth(300).setHeight(100);

        // We use Courier as a monotype font to ensure that min width calculated by iText
        // is more or less the same as the width calculated by browsers
        FontProvider provider = new FontProvider();
        provider.getFontSet().addFont(StandardFonts.COURIER, null, "courier");

        DocumentRenderer documentRenderer = new DocumentRenderer(
                new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))));
        documentRenderer.setProperty(Property.FONT_PROVIDER, provider);

        FlexContainerRenderer flexContainerRenderer = new FlexContainerRenderer(div);
        flexContainerRenderer.setParent(documentRenderer);
        div.setNextRenderer(flexContainerRenderer);

        for (int i = 0; i < flexBasisValues.size(); i++) {
            Div flexItem = new Div().add(new Paragraph(1 == i ? "2222222222222222222222222" : Integer.toString(i)));
            if (1 == i) {
                flexItem
                        .setFontFamily(StandardFontFamilies.COURIER)
                        .setMinWidth(37.5f);
            }
            AbstractRenderer flexItemRenderer = (AbstractRenderer) flexItem.createRendererSubTree()
                    .setParent(flexContainerRenderer);
            flexItemRenderer.setProperty(Property.FLEX_GROW, 1f);
            flexItemRenderer.setProperty(Property.FLEX_SHRINK, 1f);
            flexItemRenderer.setProperty(Property.FLEX_BASIS, flexBasisValues.get(i));
            flexContainerRenderer.addChild(flexItemRenderer);
        }

        List<List<FlexItemInfo>> rectangleTable =
                FlexUtil.calculateChildrenRectangles(bBox, (FlexContainerRenderer) div.getRenderer());

        Assertions.assertEquals(80f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(102.5f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(117.5f, rectangleTable.get(0).get(2).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisSumLtWidthGrow1Shrink1Item2MaxWidthLtBasisTest01() {
        Rectangle bBox = new Rectangle(575, 842);
        List<UnitValue> flexBasisValues = Arrays.<UnitValue>asList(
                UnitValue.createPointValue(50f),
                UnitValue.createPointValue(80f),
                UnitValue.createPointValue(100f)
        );

        Div div = new Div().setWidth(400).setHeight(100);

        DocumentRenderer documentRenderer = new DocumentRenderer(
                new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))));

        FlexContainerRenderer flexContainerRenderer = new FlexContainerRenderer(div);
        flexContainerRenderer.setParent(documentRenderer);
        div.setNextRenderer(flexContainerRenderer);

        for (int i = 0; i < flexBasisValues.size(); i++) {
            Div flexItem = new Div().add(new Paragraph(Integer.toString(i)));
            if (1 == i) {
                flexItem.setMaxWidth(50f);
            }
            AbstractRenderer flexItemRenderer = (AbstractRenderer) flexItem.createRendererSubTree()
                    .setParent(flexContainerRenderer);
            flexItemRenderer.setProperty(Property.FLEX_GROW, 1f);
            flexItemRenderer.setProperty(Property.FLEX_SHRINK, 1f);
            flexItemRenderer.setProperty(Property.FLEX_BASIS, flexBasisValues.get(i));

            flexContainerRenderer.addChild(flexItemRenderer);
        }

        List<List<FlexItemInfo>> rectangleTable =
                FlexUtil.calculateChildrenRectangles(bBox, (FlexContainerRenderer) div.getRenderer());

        Assertions.assertEquals(150f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(50f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(200f, rectangleTable.get(0).get(2).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisSumLtWidthGrow1Shrink1Item2MaxWidthLtBasisTest02() {
        Rectangle bBox = new Rectangle(575, 842);
        List<UnitValue> flexBasisValues = Arrays.<UnitValue>asList(
                UnitValue.createPointValue(50f),
                UnitValue.createPointValue(80f),
                UnitValue.createPointValue(100f)
        );

        Div div = new Div().setWidth(100).setHeight(100);

        DocumentRenderer documentRenderer = new DocumentRenderer(
                new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))));

        FlexContainerRenderer flexContainerRenderer = new FlexContainerRenderer(div);
        flexContainerRenderer.setParent(documentRenderer);
        div.setNextRenderer(flexContainerRenderer);

        for (int i = 0; i < flexBasisValues.size(); i++) {
            Div flexItem = new Div().add(new Paragraph(Integer.toString(i)));
            if (1 == i) {
                flexItem.setMaxWidth(30f);
            }
            AbstractRenderer flexItemRenderer = (AbstractRenderer) flexItem.createRendererSubTree()
                    .setParent(documentRenderer);
            flexItemRenderer.setProperty(Property.FLEX_GROW, 1f);
            flexItemRenderer.setProperty(Property.FLEX_SHRINK, 1f);
            flexItemRenderer.setProperty(Property.FLEX_BASIS, flexBasisValues.get(i));
            flexItemRenderer.setParent(flexContainerRenderer);
            flexContainerRenderer.addChild(flexItemRenderer);
        }

        List<List<FlexItemInfo>> rectangleTable =
                FlexUtil.calculateChildrenRectangles(bBox, (FlexContainerRenderer) div.getRenderer());

        Assertions.assertEquals(23.333332f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(30f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(46.666664f, rectangleTable.get(0).get(2).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisSumLtWidthGrow1Shrink1Item2MaxWidthLtBasisTest03() {
        Rectangle bBox = new Rectangle(575, 842);
        List<UnitValue> flexBasisValues = Arrays.<UnitValue>asList(
                UnitValue.createPointValue(7f),
                UnitValue.createPointValue(80f),
                UnitValue.createPointValue(7f)
        );

        Div div = new Div().setWidth(100).setHeight(100);

        DocumentRenderer documentRenderer = new DocumentRenderer(
                new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))));

        FlexContainerRenderer flexContainerRenderer = new FlexContainerRenderer(div);
        flexContainerRenderer.setParent(documentRenderer);
        div.setNextRenderer(flexContainerRenderer);

        for (int i = 0; i < flexBasisValues.size(); i++) {
            Div flexItem = new Div().add(new Paragraph(Integer.toString(i)));
            if (1 == i) {
                flexItem.setMaxWidth(30f);
            }
            AbstractRenderer flexItemRenderer = (AbstractRenderer) flexItem.createRendererSubTree()
                    .setParent(flexContainerRenderer);
            flexItemRenderer.setProperty(Property.FLEX_GROW, 1f);
            flexItemRenderer.setProperty(Property.FLEX_SHRINK, 1f);
            flexItemRenderer.setProperty(Property.FLEX_BASIS, flexBasisValues.get(i));
            flexContainerRenderer.addChild(flexItemRenderer);
        }

        List<List<FlexItemInfo>> rectangleTable =
                FlexUtil.calculateChildrenRectangles(bBox, (FlexContainerRenderer) div.getRenderer());

        Assertions.assertEquals(35f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(30f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(35f, rectangleTable.get(0).get(2).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisSumGtWidthGrow1Shrink1Item1MinWidthGtBasisTest01() {
        Rectangle bBox = new Rectangle(575, 842);
        List<UnitValue> flexBasisValues = Arrays.<UnitValue>asList(
                UnitValue.createPointValue(100f),
                UnitValue.createPointValue(150f),
                UnitValue.createPointValue(200f)
        );

        Div div = new Div().setWidth(400).setHeight(100);

        DocumentRenderer documentRenderer = new DocumentRenderer(
                new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))));

        FlexContainerRenderer flexContainerRenderer = new FlexContainerRenderer(div);
        flexContainerRenderer.setParent(documentRenderer);
        div.setNextRenderer(flexContainerRenderer);

        for (int i = 0; i < flexBasisValues.size(); i++) {
            Div flexItem = new Div().add(new Paragraph(Integer.toString(i)));
            if (0 == i) {
                flexItem.setMinWidth(150f);
            }
            AbstractRenderer flexItemRenderer = (AbstractRenderer) flexItem.createRendererSubTree()
                    .setParent(flexContainerRenderer);
            flexItemRenderer.setProperty(Property.FLEX_GROW, 1f);
            flexItemRenderer.setProperty(Property.FLEX_SHRINK, 1f);
            flexItemRenderer.setProperty(Property.FLEX_BASIS, flexBasisValues.get(i));
            div.add(flexItem);
            flexContainerRenderer.addChild(flexItemRenderer);
        }

        List<List<FlexItemInfo>> rectangleTable =
                FlexUtil.calculateChildrenRectangles(bBox, (FlexContainerRenderer) div.getRenderer());

        Assertions.assertEquals(150f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(107.14285f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(142.85715f, rectangleTable.get(0).get(2).getRectangle().getWidth(), EPS);
    }

    @Test
    public void imgGtUsedWidthTest01() throws MalformedURLException {
        Rectangle bBox = new Rectangle(575, 842);
        List<UnitValue> flexBasisValues = Arrays.<UnitValue>asList(
                UnitValue.createPointValue(50f),
                UnitValue.createPointValue(30f)
        );

        Div div = new Div().setWidth(100).setHeight(100);

        DocumentRenderer documentRenderer = new DocumentRenderer(
                new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))));

        FlexContainerRenderer flexContainerRenderer = new FlexContainerRenderer(div);
        flexContainerRenderer.setParent(documentRenderer);
        div.setNextRenderer(flexContainerRenderer);

        for (int i = 0; i < flexBasisValues.size(); i++) {
            IElement flexItem = (0 == i)
                    ? (IElement) new Image(ImageDataFactory.create(SOURCE_FOLDER + "itis.jpg"))
                    : (IElement) new Div().add(new Paragraph(Integer.toString(i)));
            if (0 == i) {
                flexItem.setProperty(Property.MAX_WIDTH, UnitValue.createPointValue(50f));
                div.add((Image) flexItem);
            } else {
                div.add((IBlockElement) flexItem);
            }
            AbstractRenderer flexItemRenderer = (AbstractRenderer) flexItem.createRendererSubTree()
                    .setParent(flexContainerRenderer);
            flexItemRenderer.setProperty(Property.FLEX_GROW, 0f);
            flexItemRenderer.setProperty(Property.FLEX_SHRINK, 0f);
            flexItemRenderer.setProperty(Property.FLEX_BASIS, flexBasisValues.get(i));
            flexContainerRenderer.addChild(flexItemRenderer);
        }

        List<List<FlexItemInfo>> rectangleTable =
                FlexUtil.calculateChildrenRectangles(bBox, (FlexContainerRenderer) div.getRenderer());

        Assertions.assertEquals(50f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(30f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisSumLtWidthGrow1Shrink1Item2MuchContentSetMinWidthGtBasisTest01() {
        Rectangle bBox = new Rectangle(575, 842);
        List<UnitValue> flexBasisValues = Arrays.<UnitValue>asList(
                UnitValue.createPointValue(37.5f),
                UnitValue.createPointValue(60f),
                UnitValue.createPointValue(75f)
        );

        Div div = new Div().setWidth(300).setHeight(100);

        // We use Courier as a monotype font to ensure that min width calculated by iText
        // is more or less the same as the width calculated by browsers
        FontProvider provider = new FontProvider();
        provider.getFontSet().addFont(StandardFonts.COURIER, null, "courier");

        DocumentRenderer documentRenderer = new DocumentRenderer(
                new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))));
        documentRenderer.setProperty(Property.FONT_PROVIDER, provider);

        FlexContainerRenderer flexContainerRenderer = new FlexContainerRenderer(div);
        flexContainerRenderer.setParent(documentRenderer);
        div.setNextRenderer(flexContainerRenderer);

        for (int i = 0; i < flexBasisValues.size(); i++) {
            Div flexItem = new Div().add(new Paragraph(1 == i ? "2222222222222222222222222" : Integer.toString(i)));
            if (1 == i) {
                flexItem
                        .setFontFamily(StandardFontFamilies.COURIER)
                        .setMinWidth(75f);
            }
            AbstractRenderer flexItemRenderer = (AbstractRenderer) flexItem.createRendererSubTree()
                    .setParent(flexContainerRenderer);
            flexItemRenderer.setProperty(Property.FLEX_GROW, 1f);
            flexItemRenderer.setProperty(Property.FLEX_SHRINK, 1f);
            flexItemRenderer.setProperty(Property.FLEX_BASIS, flexBasisValues.get(i));
            flexContainerRenderer.addChild(flexItemRenderer);
        }

        List<List<FlexItemInfo>> rectangleTable =
                FlexUtil.calculateChildrenRectangles(bBox, (FlexContainerRenderer) div.getRenderer());

        Assertions.assertEquals(80f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(102.5f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(117.5f, rectangleTable.get(0).get(2).getRectangle().getWidth(), EPS);
    }

    @Test
    public void basis1Grow0Test01() {
        Rectangle bBox = new Rectangle(575, 842);
        List<UnitValue> flexBasisValues = Arrays.<UnitValue>asList(
                UnitValue.createPointValue(1f),
                UnitValue.createPointValue(30f)
        );

        Div div = new Div().setWidth(100).setHeight(100);

        // We use Courier as a monotype font to ensure that min width calculated by iText
        // is more or less the same as the width calculated by browsers
        FontProvider provider = new FontProvider();
        provider.getFontSet().addFont(StandardFonts.COURIER, null, "courier");

        DocumentRenderer documentRenderer = new DocumentRenderer(
                new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))));
        documentRenderer.setProperty(Property.FONT_PROVIDER, provider);

        FlexContainerRenderer flexContainerRenderer = new FlexContainerRenderer(div);
        flexContainerRenderer.setParent(documentRenderer);
        div.setNextRenderer(flexContainerRenderer);

        for (int i = 0; i < flexBasisValues.size(); i++) {
            Div flexItem = new Div().add(new Paragraph(Integer.toString(i)))
                    .setFontFamily(StandardFontFamilies.COURIER);
            if (0 == i) {
                flexItem.setFontSize(100f);
            }
            AbstractRenderer flexItemRenderer = (AbstractRenderer) flexItem.createRendererSubTree()
                    .setParent(flexContainerRenderer);
            flexItemRenderer.setProperty(Property.FLEX_GROW, 0f);
            flexItemRenderer.setProperty(Property.FLEX_SHRINK, 0f);
            flexItemRenderer.setProperty(Property.FLEX_BASIS, flexBasisValues.get(i));
            div.add(flexItem);
            flexContainerRenderer.addChild(flexItemRenderer);
        }

        List<List<FlexItemInfo>> rectangleTable =
                FlexUtil.calculateChildrenRectangles(bBox, (FlexContainerRenderer) div.getRenderer());

        Assertions.assertEquals(60f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(30f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisSumLtWidthGrow1Shrink1Item2MuchContentSetMinWidthGtBasisTest02() {
        Rectangle bBox = new Rectangle(575, 842);
        List<UnitValue> flexBasisValues = Arrays.<UnitValue>asList(
                UnitValue.createPointValue(37.5f),
                UnitValue.createPointValue(60f),
                UnitValue.createPointValue(75f)
        );

        Div div = new Div().setWidth(300).setHeight(100);

        // We use Courier as a monotype font to ensure that min width calculated by iText
        // is more or less the same as the width calculated by browsers
        FontProvider provider = new FontProvider();
        provider.getFontSet().addFont(StandardFonts.COURIER, null, "courier");

        DocumentRenderer documentRenderer = new DocumentRenderer(
                new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))));
        documentRenderer.setProperty(Property.FONT_PROVIDER, provider);

        FlexContainerRenderer flexContainerRenderer = new FlexContainerRenderer(div);
        flexContainerRenderer.setParent(documentRenderer);
        div.setNextRenderer(flexContainerRenderer);

        for (int i = 0; i < flexBasisValues.size(); i++) {
            Div flexItem = new Div().add(new Paragraph(1 == i ? "2222222222222222222222222" : Integer.toString(i)));
            if (1 == i) {
                flexItem
                        .setFontFamily(StandardFontFamilies.COURIER)
                        .setMinWidth(150f);
            }
            AbstractRenderer flexItemRenderer = (AbstractRenderer) flexItem.createRendererSubTree()
                    .setParent(flexContainerRenderer);
            flexItemRenderer.setProperty(Property.FLEX_GROW, 1f);
            flexItemRenderer.setProperty(Property.FLEX_SHRINK, 1f);
            flexItemRenderer.setProperty(Property.FLEX_BASIS, flexBasisValues.get(i));
            flexContainerRenderer.addChild(flexItemRenderer);
        }

        List<List<FlexItemInfo>> rectangleTable =
                FlexUtil.calculateChildrenRectangles(bBox, (FlexContainerRenderer) div.getRenderer());

        Assertions.assertEquals(56.25f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(150f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(93.75f, rectangleTable.get(0).get(2).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisSumLtWidthGrow1Shrink1Item2MuchContentSetMinWidthGtBasisTest03() {
        Rectangle bBox = new Rectangle(575, 842);
        List<UnitValue> flexBasisValues = Arrays.<UnitValue>asList(
                UnitValue.createPointValue(112.5f),
                UnitValue.createPointValue(60f),
                UnitValue.createPointValue(187.5f)
        );

        Div div = new Div().setWidth(300).setHeight(100);

        // We use Courier as a monotype font to ensure that min width calculated by iText
        // is more or less the same as the width calculated by browsers
        FontProvider provider = new FontProvider();
        provider.getFontSet().addFont(StandardFonts.COURIER, null, "courier");

        DocumentRenderer documentRenderer = new DocumentRenderer(
                new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))));
        documentRenderer.setProperty(Property.FONT_PROVIDER, provider);

        FlexContainerRenderer flexContainerRenderer = new FlexContainerRenderer(div);
        flexContainerRenderer.setParent(documentRenderer);
        div.setNextRenderer(flexContainerRenderer);

        for (int i = 0; i < flexBasisValues.size(); i++) {
            Div flexItem = new Div().add(new Paragraph(1 == i ? "2222222222222222222222222" : Integer.toString(i)));
            if (1 == i) {
                flexItem
                        .setFontFamily(StandardFontFamilies.COURIER)
                        .setMinWidth(150f);
            }
            AbstractRenderer flexItemRenderer = (AbstractRenderer) flexItem.createRendererSubTree()
                    .setParent(flexContainerRenderer);
            flexItemRenderer.setProperty(Property.FLEX_GROW, 1f);
            flexItemRenderer.setProperty(Property.FLEX_SHRINK, 1f);
            flexItemRenderer.setProperty(Property.FLEX_BASIS, flexBasisValues.get(i));
            flexContainerRenderer.addChild(flexItemRenderer);
        }

        List<List<FlexItemInfo>> rectangleTable =
                FlexUtil.calculateChildrenRectangles(bBox, (FlexContainerRenderer) div.getRenderer());

        Assertions.assertEquals(56.25f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(150f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(93.75f, rectangleTable.get(0).get(2).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisSumEqWidthGrow1Shrink1Item2Basis0Test01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                WRAP_STYLE,
                Arrays.<UnitValue>asList(
                        UnitValue.createPointValue(400f),
                        UnitValue.createPointValue(0f),
                        UnitValue.createPointValue(100f)),
                Arrays.asList(1f, 1f, 1f),
                Arrays.asList(1f, 1f, 1f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());

        Assertions.assertEquals(1, rectangleTable.get(0).size());
        Assertions.assertEquals(2, rectangleTable.get(1).size());

        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(50.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }

        Assertions.assertEquals(400f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(150f, rectangleTable.get(1).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(250f, rectangleTable.get(1).get(1).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisSumEqWidthGrow1Shrink1Item2Basis0NoContentTest02() {
        Rectangle bBox = new Rectangle(575, 842);
        List<UnitValue> flexBasisValues = Arrays.<UnitValue>asList(
                UnitValue.createPointValue(400f),
                UnitValue.createPointValue(0f),
                UnitValue.createPointValue(100f)
        );

        Div div = new Div().setWidth(400).setHeight(100);
        div.setProperty(Property.FLEX_WRAP, FlexWrapPropertyValue.WRAP);

        DocumentRenderer documentRenderer = new DocumentRenderer(
                new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))));

        FlexContainerRenderer flexContainerRenderer = new FlexContainerRenderer(div);
        div.setNextRenderer(flexContainerRenderer);

        for (int i = 0; i < flexBasisValues.size(); i++) {
            Div flexItem = new Div();
            if (1 != i) {
                flexItem.add(new Paragraph(Integer.toString(i)));
            }
            AbstractRenderer flexItemRenderer = (AbstractRenderer) flexItem.createRendererSubTree()
                    .setParent(documentRenderer);
            flexItemRenderer.setProperty(Property.FLEX_GROW, 1f);
            flexItemRenderer.setProperty(Property.FLEX_SHRINK, 1f);
            flexItemRenderer.setProperty(Property.FLEX_BASIS, flexBasisValues.get(i));
            div.add(flexItem);
            flexContainerRenderer.addChild(flexItemRenderer);
        }

        List<List<FlexItemInfo>> rectangleTable =
                FlexUtil.calculateChildrenRectangles(bBox, (FlexContainerRenderer) div.getRenderer());

        Assertions.assertEquals(2, rectangleTable.get(0).size());
        Assertions.assertEquals(1, rectangleTable.get(1).size());

        Assertions.assertEquals(400f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(0f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(400f, rectangleTable.get(1).get(0).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisSumLtWidthGrow0Shrink0Test01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                Arrays.<UnitValue>asList(
                        UnitValue.createPointValue(50f),
                        UnitValue.createPointValue(80f),
                        UnitValue.createPointValue(100f)),
                Arrays.asList(0f, 0f, 0f),
                Arrays.asList(0f, 0f, 0f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
        Assertions.assertEquals(50f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(80f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(100f, rectangleTable.get(0).get(2).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisSumLtWidthGrow0Shrink0Item2Grow2Test01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                Arrays.<UnitValue>asList(
                        UnitValue.createPointValue(50f),
                        UnitValue.createPointValue(80f),
                        UnitValue.createPointValue(100f)),
                Arrays.asList(0f, 2f, 0f),
                Arrays.asList(0f, 0f, 0f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
        Assertions.assertEquals(50f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(250f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(100f, rectangleTable.get(0).get(2).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisSumLtWidthGrow1Shrink0Test01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                Arrays.<UnitValue>asList(
                        UnitValue.createPointValue(50f),
                        UnitValue.createPointValue(80f),
                        UnitValue.createPointValue(100f)),
                Arrays.asList(1f, 1f, 1f),
                Arrays.asList(0f, 0f, 0f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
        Assertions.assertEquals(106.66667f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(136.66667f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(156.66667f, rectangleTable.get(0).get(2).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisSumGtWidthGrow0Shrink1Test01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                Arrays.<UnitValue>asList(
                        UnitValue.createPointValue(100f),
                        UnitValue.createPointValue(200f),
                        UnitValue.createPointValue(300f)),
                Arrays.asList(0f, 0f, 0f),
                Arrays.asList(1f, 1f, 1f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
        Assertions.assertEquals(200f / 3, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(400f / 3, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(600f / 3, rectangleTable.get(0).get(2).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisSumGtWidthGrow0Shrink05Test01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                Arrays.<UnitValue>asList(
                        UnitValue.createPointValue(100f),
                        UnitValue.createPointValue(200f),
                        UnitValue.createPointValue(300f)),
                Arrays.asList(0f, 0f, 0f),
                Arrays.asList(0.5f, 0.5f, 0.5f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
        Assertions.assertEquals(200f / 3, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(400f / 3, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(600f / 3, rectangleTable.get(0).get(2).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisSumGtWidthGrow0Shrink01Test01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                Arrays.<UnitValue>asList(
                        UnitValue.createPointValue(100f),
                        UnitValue.createPointValue(200f),
                        UnitValue.createPointValue(300f)),
                Arrays.asList(0f, 0f, 0f),
                Arrays.asList(0.1f, 0.1f, 0.1f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
        Assertions.assertEquals(90f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(180f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(270f, rectangleTable.get(0).get(2).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisSumGtWidthGrow0Shrink5Test01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                Arrays.<UnitValue>asList(
                        UnitValue.createPointValue(100f),
                        UnitValue.createPointValue(200f),
                        UnitValue.createPointValue(300f)),
                Arrays.asList(0f, 0f, 0f),
                Arrays.asList(5f, 5f, 5f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
        Assertions.assertEquals(200f / 3, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(400f / 3, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(600f / 3, rectangleTable.get(0).get(2).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisSumGtWidthGrow1Shrink1Test01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                Arrays.<UnitValue>asList(
                        UnitValue.createPointValue(100f),
                        UnitValue.createPointValue(200f),
                        UnitValue.createPointValue(300f)),
                Arrays.asList(1f, 1f, 1f),
                Arrays.asList(1f, 1f, 1f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
        Assertions.assertEquals(200f / 3, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(400f / 3, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(600f / 3, rectangleTable.get(0).get(2).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisSumGtWidthGrow1Shrink1Item3Shrink50Test01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                Arrays.<UnitValue>asList(
                        UnitValue.createPointValue(100f),
                        UnitValue.createPointValue(200f),
                        UnitValue.createPointValue(300f)),
                Arrays.asList(1f, 1f, 1f),
                Arrays.asList(1f, 1f, 50f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
        Assertions.assertEquals(98.69281f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(197.38562f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(103.92157f, rectangleTable.get(0).get(2).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisSumGtWidthGrow1Shrink1Item3Shrink5Test01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                Arrays.<UnitValue>asList(
                        UnitValue.createPointValue(100f),
                        UnitValue.createPointValue(200f),
                        UnitValue.createPointValue(300f)),
                Arrays.asList(1f, 1f, 1f),
                Arrays.asList(1f, 1f, 5f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
        Assertions.assertEquals(88.888885f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(177.77777f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(133.33334f, rectangleTable.get(0).get(2).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisSumGtWidthGrow0Shrink0Test01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                Arrays.<UnitValue>asList(
                        UnitValue.createPointValue(100f),
                        UnitValue.createPointValue(200f),
                        UnitValue.createPointValue(300f)),
                Arrays.asList(0f, 0f, 0f),
                Arrays.asList(0f, 0f, 0f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
        Assertions.assertEquals(100f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(200f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(300f, rectangleTable.get(0).get(2).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisSumGtWidthGrow1Shrink0Test01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                Arrays.<UnitValue>asList(
                        UnitValue.createPointValue(100f),
                        UnitValue.createPointValue(200f),
                        UnitValue.createPointValue(300f)),
                Arrays.asList(1f, 1f, 1f),
                Arrays.asList(0f, 0f, 0f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
        Assertions.assertEquals(100f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(200f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(300f, rectangleTable.get(0).get(2).getRectangle().getWidth(), EPS);
    }

    @Test
    public void basis250SumGtWidthGrow0Shrink1WrapTest01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                WRAP_STYLE,
                Arrays.<UnitValue>asList(
                        UnitValue.createPointValue(250f),
                        UnitValue.createPointValue(250f),
                        UnitValue.createPointValue(250f)),
                Arrays.asList(0f, 0f, 0f),
                Arrays.asList(1f, 1f, 1f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(250f, flexItemInfo.getRectangle().getWidth(), EPS);
                Assertions.assertEquals(33.333332f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
    }

    @Test
    public void differentBasisSumGtWidthGrow0Shrink1WrapTest01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                WRAP_STYLE,
                Arrays.<UnitValue>asList(
                        UnitValue.createPointValue(100f),
                        UnitValue.createPointValue(200f),
                        UnitValue.createPointValue(300f)),
                Arrays.asList(0f, 0f, 0f),
                Arrays.asList(1f, 1f, 1f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(50.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
        Assertions.assertEquals(100f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(200f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(300f, rectangleTable.get(1).get(0).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisSumGtWidthGrow0Shrink05WrapTest01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                WRAP_STYLE,
                Arrays.<UnitValue>asList(
                        UnitValue.createPointValue(100f),
                        UnitValue.createPointValue(200f),
                        UnitValue.createPointValue(300f)),
                Arrays.asList(0f, 0f, 0f),
                Arrays.asList(0.5f, 0.5f, 0.5f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(50.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
        Assertions.assertEquals(100f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(200f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(300f, rectangleTable.get(1).get(0).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisSumGtWidthGrow0Shrink01WrapTest01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                WRAP_STYLE,
                Arrays.<UnitValue>asList(
                        UnitValue.createPointValue(100f),
                        UnitValue.createPointValue(200f),
                        UnitValue.createPointValue(300f)),
                Arrays.asList(0f, 0f, 0f),
                Arrays.asList(0.1f, 0.1f, 0.1f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(50.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
        Assertions.assertEquals(100f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(200f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(300f, rectangleTable.get(1).get(0).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisSumGtWidthGrow0Shrink5WrapTest01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                WRAP_STYLE,
                Arrays.<UnitValue>asList(
                        UnitValue.createPointValue(100f),
                        UnitValue.createPointValue(200f),
                        UnitValue.createPointValue(300f)),
                Arrays.asList(0f, 0f, 0f),
                Arrays.asList(5f, 5f, 5f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(50.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
        Assertions.assertEquals(100f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(200f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(300f, rectangleTable.get(1).get(0).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisSumGtWidthGrow1Shrink1WrapTest01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                WRAP_STYLE,
                Arrays.<UnitValue>asList(
                        UnitValue.createPointValue(100f),
                        UnitValue.createPointValue(200f),
                        UnitValue.createPointValue(300f)),
                Arrays.asList(1f, 1f, 1f),
                Arrays.asList(1f, 1f, 1f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(50.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
        Assertions.assertEquals(150f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(250f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(400f, rectangleTable.get(1).get(0).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisSumGtWidthGrow1Shrink1Item3Shrink50WrapTest01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                WRAP_STYLE,
                Arrays.<UnitValue>asList(
                        UnitValue.createPointValue(100f),
                        UnitValue.createPointValue(200f),
                        UnitValue.createPointValue(300f)),
                Arrays.asList(1f, 1f, 1f),
                Arrays.asList(1f, 1f, 50f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(50.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
        Assertions.assertEquals(150f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(250f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(400f, rectangleTable.get(1).get(0).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisSumGtWidthGrow1Shrink1Item3Shrink5WrapTest01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                WRAP_STYLE,
                Arrays.<UnitValue>asList(
                        UnitValue.createPointValue(100f),
                        UnitValue.createPointValue(200f),
                        UnitValue.createPointValue(300f)),
                Arrays.asList(1f, 1f, 1f),
                Arrays.asList(1f, 1f, 5f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(50.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
        Assertions.assertEquals(150f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(250f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(400f, rectangleTable.get(1).get(0).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisSumGtWidthGrow0Shrink0WrapTest01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                WRAP_STYLE,
                Arrays.<UnitValue>asList(
                        UnitValue.createPointValue(100f),
                        UnitValue.createPointValue(200f),
                        UnitValue.createPointValue(300f)),
                Arrays.asList(0f, 0f, 0f),
                Arrays.asList(0f, 0f, 0f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(50.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
        Assertions.assertEquals(100f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(200f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(300f, rectangleTable.get(1).get(0).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisSumGtWidthGrow1Shrink0WrapTest01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                WRAP_STYLE,
                Arrays.<UnitValue>asList(
                        UnitValue.createPointValue(100f),
                        UnitValue.createPointValue(200f),
                        UnitValue.createPointValue(300f)),
                Arrays.asList(1f, 1f, 1f),
                Arrays.asList(0f, 0f, 0f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(50.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
        Assertions.assertEquals(150f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(250f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(400f, rectangleTable.get(1).get(0).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisPercentSumLtWidthGrow0Shrink1Test01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                Arrays.<UnitValue>asList(
                        UnitValue.createPercentValue(10),
                        UnitValue.createPercentValue(20),
                        UnitValue.createPercentValue(30)),
                Arrays.asList(0f, 0f, 0f),
                Arrays.asList(1f, 1f, 1f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
        Assertions.assertEquals(40f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(80f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(120f, rectangleTable.get(0).get(2).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisPercentSumLtWidthGrow1Shrink1Test01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                Arrays.<UnitValue>asList(
                        UnitValue.createPercentValue(10),
                        UnitValue.createPercentValue(20),
                        UnitValue.createPercentValue(30)),
                Arrays.asList(1f, 1f, 1f),
                Arrays.asList(1f, 1f, 1f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
        Assertions.assertEquals(93.333336f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(133.33333f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(173.33333f, rectangleTable.get(0).get(2).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisPercentSumLtWidthGrow0Shrink0Test01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                Arrays.<UnitValue>asList(
                        UnitValue.createPercentValue(10),
                        UnitValue.createPercentValue(20),
                        UnitValue.createPercentValue(30)),
                Arrays.asList(0f, 0f, 0f),
                Arrays.asList(0f, 0f, 0f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
        Assertions.assertEquals(40f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(80f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(120f, rectangleTable.get(0).get(2).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisPercentSumLtWidthGrow0Shrink0Item2Grow2Test01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                Arrays.<UnitValue>asList(
                        UnitValue.createPercentValue(10),
                        UnitValue.createPercentValue(20),
                        UnitValue.createPercentValue(30)),
                Arrays.asList(0f, 2f, 0f),
                Arrays.asList(0f, 0f, 0f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
        Assertions.assertEquals(40f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(240f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(120f, rectangleTable.get(0).get(2).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisPercentSumLtWidthGrow1Shrink0Test01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                Arrays.<UnitValue>asList(
                        UnitValue.createPercentValue(10),
                        UnitValue.createPercentValue(20),
                        UnitValue.createPercentValue(30)),
                Arrays.asList(1f, 1f, 1f),
                Arrays.asList(0f, 0f, 0f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
        Assertions.assertEquals(93.333336f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(133.33333f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(173.33333f, rectangleTable.get(0).get(2).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisPercentSumGtWidthGrow0Shrink1Test01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                Arrays.<UnitValue>asList(
                        UnitValue.createPercentValue(30),
                        UnitValue.createPercentValue(40),
                        UnitValue.createPercentValue(50)),
                Arrays.asList(0f, 0f, 0f),
                Arrays.asList(1f, 1f, 1f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
        Assertions.assertEquals(300f / 3, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(400f / 3, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(500f / 3, rectangleTable.get(0).get(2).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisPercentSumGtWidthGrow0Shrink05Test01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                Arrays.<UnitValue>asList(
                        UnitValue.createPercentValue(30),
                        UnitValue.createPercentValue(40),
                        UnitValue.createPercentValue(50)),
                Arrays.asList(0f, 0f, 0f),
                Arrays.asList(0.5f, 0.5f, 0.5f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
        Assertions.assertEquals(100f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(400f / 3, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(500f / 3, rectangleTable.get(0).get(2).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisPercentSumGtWidthGrow0Shrink01Test01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                Arrays.<UnitValue>asList(
                        UnitValue.createPercentValue(30),
                        UnitValue.createPercentValue(40),
                        UnitValue.createPercentValue(50)),
                Arrays.asList(0f, 0f, 0f),
                Arrays.asList(0.1f, 0.1f, 0.1f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
        Assertions.assertEquals(114f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(152f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(190f, rectangleTable.get(0).get(2).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisPercentSumGtWidthGrow0Shrink5Test01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                Arrays.<UnitValue>asList(
                        UnitValue.createPercentValue(30),
                        UnitValue.createPercentValue(40),
                        UnitValue.createPercentValue(50)),
                Arrays.asList(0f, 0f, 0f),
                Arrays.asList(5f, 5f, 5f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
        Assertions.assertEquals(300f / 3, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(400f / 3, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(500f / 3, rectangleTable.get(0).get(2).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisPercentSumGtWidthGrow1Shrink1Test01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                Arrays.<UnitValue>asList(
                        UnitValue.createPercentValue(30),
                        UnitValue.createPercentValue(40),
                        UnitValue.createPercentValue(50)),
                Arrays.asList(1f, 1f, 1f),
                Arrays.asList(1f, 1f, 1f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
        Assertions.assertEquals(300f / 3, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(400f / 3, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(500f / 3, rectangleTable.get(0).get(2).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisPercentSumGtWidthGrow1Shrink1Item3Shrink50Test01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                Arrays.<UnitValue>asList(
                        UnitValue.createPercentValue(30),
                        UnitValue.createPercentValue(40),
                        UnitValue.createPercentValue(50)),
                Arrays.asList(1f, 1f, 1f),
                Arrays.asList(1f, 1f, 50f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
        Assertions.assertEquals(119.06615f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(158.75487f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(122.178986f, rectangleTable.get(0).get(2).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisPercentSumGtWidthGrow1Shrink1Item3Shrink5Test01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                Arrays.<UnitValue>asList(
                        UnitValue.createPercentValue(30),
                        UnitValue.createPercentValue(40),
                        UnitValue.createPercentValue(50)),
                Arrays.asList(1f, 1f, 1f),
                Arrays.asList(1f, 1f, 5f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
        Assertions.assertEquals(112.5f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(150f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(137.5f, rectangleTable.get(0).get(2).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisPercentSumGtWidthGrow0Shrink0Test01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                Arrays.<UnitValue>asList(
                        UnitValue.createPercentValue(30),
                        UnitValue.createPercentValue(40),
                        UnitValue.createPercentValue(50)),
                Arrays.asList(0f, 0f, 0f),
                Arrays.asList(0f, 0f, 0f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }
        Assertions.assertEquals(120f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(160f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(200f, rectangleTable.get(0).get(2).getRectangle().getWidth(), EPS);
    }

    @Test
    public void differentBasisPercentSumGtWidthGrow1Shrink0Test01() {
        List<List<FlexItemInfo>> rectangleTable = testFlex(
                Arrays.<UnitValue>asList(
                        UnitValue.createPercentValue(30),
                        UnitValue.createPercentValue(40),
                        UnitValue.createPercentValue(50)),
                Arrays.asList(1f, 1f, 1f),
                Arrays.asList(0f, 0f, 0f)
        );

        // after checks
        Assertions.assertFalse(rectangleTable.isEmpty());
        for (List<FlexItemInfo> line : rectangleTable) {
            for (FlexItemInfo flexItemInfo : line) {
                Assertions.assertEquals(100.0f, flexItemInfo.getRectangle().getHeight(), EPS);
            }
        }

        Assertions.assertEquals(120f, rectangleTable.get(0).get(0).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(160f, rectangleTable.get(0).get(1).getRectangle().getWidth(), EPS);
        Assertions.assertEquals(200f, rectangleTable.get(0).get(2).getRectangle().getWidth(), EPS);
    }

    @Test
    public void calculateMinContentWithMinWidthTest() {
        DivRenderer divRenderer = new DivRenderer(new Div());
        divRenderer.setProperty(Property.WIDTH, UnitValue.createPointValue(100));
        divRenderer.setProperty(Property.MIN_WIDTH, UnitValue.createPointValue(30));

        FlexUtil.FlexItemCalculationInfo info = createFlexItemCalculationInfo(divRenderer);
        Assertions.assertEquals(30f, info.minContent, EPS);

        divRenderer.setProperty(Property.WIDTH, UnitValue.createPointValue(30));
        divRenderer.setProperty(Property.MIN_WIDTH, UnitValue.createPointValue(100));

        info = createFlexItemCalculationInfo(divRenderer);
        Assertions.assertEquals(100f, info.minContent, EPS);
    }

    @Test
    public void calculateMinContentForDivWithContentTest() {
        Div div = new Div();
        div.add(new Div().setWidth(50));
        IRenderer divRenderer = div.createRendererSubTree();

        FlexUtil.FlexItemCalculationInfo info = createFlexItemCalculationInfo((AbstractRenderer) divRenderer);
        Assertions.assertEquals(50.0f, info.minContent, EPS);
    }

    @Test
    public void calculateMinContentForDivWithWidthTest() {
        DivRenderer divRenderer = new DivRenderer(new Div());
        divRenderer.setProperty(Property.WIDTH, UnitValue.createPointValue(100));

        FlexUtil.FlexItemCalculationInfo info = createFlexItemCalculationInfo(divRenderer);
        Assertions.assertEquals(0.0f, info.minContent, EPS);
    }

    @Test
    public void calculateMinContentForDivWithWidthAndContentTest() {
        Div div = new Div();
        div.add(new Div().setWidth(50));
        IRenderer divRenderer = div.createRendererSubTree();
        divRenderer.setProperty(Property.WIDTH, UnitValue.createPointValue(100));

        FlexUtil.FlexItemCalculationInfo info = createFlexItemCalculationInfo((AbstractRenderer) divRenderer);
        Assertions.assertEquals(50.0f, info.minContent, EPS);

        div = new Div();
        div.add(new Div().setWidth(150));
        divRenderer = div.createRendererSubTree();
        divRenderer.setProperty(Property.WIDTH, UnitValue.createPointValue(100));

        info = createFlexItemCalculationInfo((AbstractRenderer) divRenderer);
        Assertions.assertEquals(100.0f, info.minContent, EPS);
    }

    @Test
    public void calculateMinContentForDivWithWidthMaxWidthAndContentTest() {
        Div div = new Div();
        div.add(new Div().setWidth(50));
        div.setProperty(Property.MAX_WIDTH, UnitValue.createPointValue(45));
        IRenderer divRenderer = div.createRendererSubTree();
        divRenderer.setProperty(Property.WIDTH, UnitValue.createPointValue(100));

        FlexUtil.FlexItemCalculationInfo info = createFlexItemCalculationInfo((AbstractRenderer) divRenderer);
        Assertions.assertEquals(45.0f, info.minContent, EPS);

        div = new Div();
        div.add(new Div().setWidth(150));
        div.setProperty(Property.MAX_WIDTH, UnitValue.createPointValue(120));
        divRenderer = div.createRendererSubTree();
        divRenderer.setProperty(Property.WIDTH, UnitValue.createPointValue(100));

        info = createFlexItemCalculationInfo((AbstractRenderer) divRenderer);
        Assertions.assertEquals(100.0f, info.minContent, EPS);
    }

    @Test
    public void calculateMinContentForImageTest() {
        Image image = new Image(new PdfFormXObject(new Rectangle(60, 150)));
        IRenderer imageRenderer = image.createRendererSubTree();

        FlexUtil.FlexItemCalculationInfo info = createFlexItemCalculationInfo((AbstractRenderer) imageRenderer);
        Assertions.assertEquals(60.0f, info.minContent, EPS);
    }

    @Test
    public void calculateMinContentForImageWithHeightTest() {
        Image image = new Image(new PdfFormXObject(new Rectangle(60, 150)));
        image.setHeight(300);
        IRenderer imageRenderer = image.createRendererSubTree();

        FlexUtil.FlexItemCalculationInfo info = createFlexItemCalculationInfo((AbstractRenderer) imageRenderer);
        Assertions.assertEquals(60.0f, info.minContent, EPS);

        image = new Image(new PdfFormXObject(new Rectangle(60, 150)));
        image.setHeight(100);
        imageRenderer = image.createRendererSubTree();

        info = createFlexItemCalculationInfo((AbstractRenderer) imageRenderer);
        Assertions.assertEquals(40.0f, info.minContent, EPS);
    }

    @Test
    public void calculateMinContentForImageWithHeightAndMinMaxHeightsTest() {
        Image image = new Image(new PdfFormXObject(new Rectangle(60, 150)));
        image.setHeight(300);
        image.setMinHeight(20);
        image.setMaxHeight(100);
        IRenderer imageRenderer = image.createRendererSubTree();

        FlexUtil.FlexItemCalculationInfo info = createFlexItemCalculationInfo((AbstractRenderer) imageRenderer);
        Assertions.assertEquals(40.0f, info.minContent, EPS);

        image = new Image(new PdfFormXObject(new Rectangle(60, 150)));
        image.setHeight(100);
        image.setMinHeight(20);
        image.setMaxHeight(75);
        imageRenderer = image.createRendererSubTree();

        info = createFlexItemCalculationInfo((AbstractRenderer) imageRenderer);
        Assertions.assertEquals(30.0f, info.minContent, EPS);
    }

    @Test
    public void calculateMinContentForImageWithHeightAndWidthTest() {
        Image image = new Image(new PdfFormXObject(new Rectangle(60, 150)));
        image.setHeight(50);
        image.setWidth(100);
        IRenderer imageRenderer = image.createRendererSubTree();

        FlexUtil.FlexItemCalculationInfo info = createFlexItemCalculationInfo((AbstractRenderer) imageRenderer);
        Assertions.assertEquals(60.0f, info.minContent, EPS);

        image = new Image(new PdfFormXObject(new Rectangle(60, 150)));
        image.setHeight(50);
        image.setWidth(50);
        imageRenderer = image.createRendererSubTree();

        info = createFlexItemCalculationInfo((AbstractRenderer) imageRenderer);
        Assertions.assertEquals(50.0f, info.minContent, EPS);
    }

    private static FlexUtil.FlexItemCalculationInfo createFlexItemCalculationInfo(AbstractRenderer renderer) {
        return new FlexUtil.FlexItemCalculationInfo(renderer, 0, 0, 0, 0, false, false, 0);
    }

    private static List<List<FlexItemInfo>> testFlex(List<UnitValue> flexBasisValues, List<Float> flexGrowValues,
                                                     List<Float> flexShrinkValues) {
        return testFlex(DEFAULT_STYLE, flexBasisValues, flexGrowValues, flexShrinkValues);
    }

    private static List<List<FlexItemInfo>> testFlex(Style containerStyle, List<UnitValue> flexBasisValues,
                                                     List<Float> flexGrowValues,
                                                     List<Float> flexShrinkValues) {
        return testFlex(containerStyle, flexBasisValues, flexGrowValues, flexShrinkValues, null);
    }

    private static List<List<FlexItemInfo>> testFlex(Style containerStyle, List<UnitValue> flexBasisValues,
                                                     List<Float> flexGrowValues,
                                                     List<Float> flexShrinkValues, Style elementStyle) {
        assert flexBasisValues.size() == flexGrowValues.size();
        assert flexBasisValues.size() == flexShrinkValues.size();

        Rectangle bBox = new Rectangle(PageSize.A4);
        bBox.applyMargins(36f, 36f, 36f, 36f, false);

        Div div = new Div();
        div.addStyle(containerStyle);

        DocumentRenderer documentRenderer = new DocumentRenderer(
                new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))));

        FlexContainerRenderer flexContainerRenderer = new FlexContainerRenderer(div);
        flexContainerRenderer.setParent(documentRenderer);
        div.setNextRenderer(flexContainerRenderer);

        for (int i = 0; i < flexBasisValues.size(); i++) {
            Div flexItem = new Div().add(new Paragraph("x"));
            if (elementStyle != null) {
                flexItem.addStyle(elementStyle);
            }
            AbstractRenderer flexItemRenderer = (AbstractRenderer) flexItem.createRendererSubTree()
                    .setParent(flexContainerRenderer);

            UnitValue flexBasis = null == flexBasisValues.get(i) ? UnitValue
                    .createPointValue(flexItemRenderer.getMinMaxWidth().getMinWidth()) : flexBasisValues.get(i);

            flexItemRenderer.setProperty(Property.FLEX_GROW, flexGrowValues.get(i));
            flexItemRenderer.setProperty(Property.FLEX_SHRINK, flexShrinkValues.get(i));
            flexItemRenderer.setProperty(Property.FLEX_BASIS, flexBasis);

            div.add(flexItem);
            flexContainerRenderer.addChild(flexItemRenderer);
        }

        return FlexUtil.calculateChildrenRectangles(bBox, (FlexContainerRenderer) div.getRenderer());
    }
}
