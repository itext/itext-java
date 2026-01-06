/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.FlexDirectionPropertyValue;
import com.itextpdf.layout.properties.FlexWrapPropertyValue;
import com.itextpdf.layout.properties.JustifyContent;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("UnitTest")
public class FlexUtilJustifyContentTest extends ExtendedITextTest {
    private static final float EPS = 0.001f;

    public static Iterable<Object[]> justifyContentAndFlexDirectionAndShiftsProperties() {
        return Arrays.asList(new Object[][]{
                {JustifyContent.SPACE_AROUND, FlexDirectionPropertyValue.ROW,
                        new float[] {1.666f, 3.333f, 3.333f, 10, 20}},
                {JustifyContent.SPACE_BETWEEN, FlexDirectionPropertyValue.ROW,
                        new float[] {0, 5, 5, 0, 40}},
                {JustifyContent.SPACE_EVENLY, FlexDirectionPropertyValue.ROW,
                        new float[] {2.5f, 2.5f, 2.5f, 13.333f, 13.333f}},

                {JustifyContent.SPACE_AROUND, FlexDirectionPropertyValue.ROW_REVERSE,
                        new float[] {3.333f, 3.333f, 1.666f, 20, 10}},
                {JustifyContent.SPACE_BETWEEN, FlexDirectionPropertyValue.ROW_REVERSE,
                        new float[] {5, 5, 0, 40, 0}},
                {JustifyContent.SPACE_EVENLY, FlexDirectionPropertyValue.ROW_REVERSE,
                        new float[] {2.5f, 2.5f, 2.5f, 13.333f, 13.333f}},

                {JustifyContent.SPACE_AROUND, FlexDirectionPropertyValue.COLUMN,
                        new float[] {1.666f, 3.333f, 3.333f, 10, 20}},
                {JustifyContent.SPACE_BETWEEN, FlexDirectionPropertyValue.COLUMN,
                        new float[] {0, 5, 5, 0, 40}},
                {JustifyContent.SPACE_EVENLY, FlexDirectionPropertyValue.COLUMN,
                        new float[] {2.5f, 2.5f, 2.5f, 13.333f, 13.333f}},

                {JustifyContent.SPACE_AROUND, FlexDirectionPropertyValue.COLUMN_REVERSE,
                        new float[] {3.333f, 3.333f, 1.666f, 20, 10}},
                {JustifyContent.SPACE_BETWEEN, FlexDirectionPropertyValue.COLUMN_REVERSE,
                        new float[] {5, 5, 0, 40, 0}},
                {JustifyContent.SPACE_EVENLY, FlexDirectionPropertyValue.COLUMN_REVERSE,
                        new float[] {2.5f, 2.5f, 2.5f, 13.333f, 13.333f}},

        });
    }

    @ParameterizedTest(name = "{index}: justify-content: {0}; flex-direction: {1}")
    @MethodSource("justifyContentAndFlexDirectionAndShiftsProperties")
    public void justifyContentShiftsTest(JustifyContent jstCnt, FlexDirectionPropertyValue flexDir, float[] shifts) {
        Rectangle bBox = new Rectangle(575, 842);
        Div div = new Div().setWidth(100).setHeight(100);

        DocumentRenderer documentRenderer = new DocumentRenderer(
                new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))));

        FlexContainerRenderer flexContainerRenderer = new FlexContainerRenderer(div);
        flexContainerRenderer.setProperty(Property.JUSTIFY_CONTENT, jstCnt);
        flexContainerRenderer.setProperty(Property.FLEX_DIRECTION, flexDir);
        flexContainerRenderer.setProperty(Property.FLEX_WRAP, FlexWrapPropertyValue.WRAP);
        flexContainerRenderer.setParent(documentRenderer);
        div.setNextRenderer(flexContainerRenderer);

        for (int i = 0; i < 5; i++) {
            Div flexItem = new Div().add(new Paragraph(Integer.toString(i)));
            flexItem.setHeight(30).setWidth(30);

            AbstractRenderer flexItemRenderer = (AbstractRenderer) flexItem.createRendererSubTree()
                    .setParent(documentRenderer);
            flexItemRenderer.setParent(flexContainerRenderer);
            flexContainerRenderer.addChild(flexItemRenderer);
        }

        List<List<FlexItemInfo>> rectangleTable =
                FlexUtil.calculateChildrenRectangles(bBox, (FlexContainerRenderer) div.getRenderer());

        if (FlexDirectionPropertyValue.ROW.equals(flexDir) || FlexDirectionPropertyValue.ROW_REVERSE.equals(flexDir)) {
            Assertions.assertEquals(shifts[0], rectangleTable.get(0).get(0).getRectangle().getX(), EPS);
            Assertions.assertEquals(shifts[1], rectangleTable.get(0).get(1).getRectangle().getX(), EPS);
            Assertions.assertEquals(shifts[2], rectangleTable.get(0).get(2).getRectangle().getX(), EPS);
            Assertions.assertEquals(shifts[3], rectangleTable.get(1).get(0).getRectangle().getX(), EPS);
            Assertions.assertEquals(shifts[4], rectangleTable.get(1).get(1).getRectangle().getX(), EPS);
        } else {
            Assertions.assertEquals(shifts[0], rectangleTable.get(0).get(0).getRectangle().getY(), EPS);
            Assertions.assertEquals(shifts[1], rectangleTable.get(0).get(1).getRectangle().getY(), EPS);
            Assertions.assertEquals(shifts[2], rectangleTable.get(0).get(2).getRectangle().getY(), EPS);
            Assertions.assertEquals(shifts[3], rectangleTable.get(1).get(0).getRectangle().getY(), EPS);
            Assertions.assertEquals(shifts[4], rectangleTable.get(1).get(1).getRectangle().getY(), EPS);
        }
    }

    @Test
    public void justifyContentShiftsItemsBiggerThanContainerTest() {
        JustifyContent[] jstCnts = new JustifyContent[] {JustifyContent.SPACE_AROUND, JustifyContent.SPACE_BETWEEN,
                JustifyContent.SPACE_EVENLY};
        FlexDirectionPropertyValue[] flexDirs = new FlexDirectionPropertyValue[] {FlexDirectionPropertyValue.ROW,
                FlexDirectionPropertyValue.ROW_REVERSE,FlexDirectionPropertyValue.COLUMN,
                FlexDirectionPropertyValue.COLUMN_REVERSE};

        for (JustifyContent jstCnt : jstCnts) {
            for (FlexDirectionPropertyValue flexDir : flexDirs) {
                Rectangle bBox = new Rectangle(575, 842);
                Div div = new Div().setWidth(100).setHeight(100);

                DocumentRenderer documentRenderer = new DocumentRenderer(
                        new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))));

                FlexContainerRenderer flexContainerRenderer = new FlexContainerRenderer(div);
                flexContainerRenderer.setProperty(Property.JUSTIFY_CONTENT, jstCnt);
                flexContainerRenderer.setProperty(Property.FLEX_DIRECTION, flexDir);
                flexContainerRenderer.setProperty(Property.FLEX_WRAP, FlexWrapPropertyValue.WRAP);
                flexContainerRenderer.setParent(documentRenderer);
                div.setNextRenderer(flexContainerRenderer);

                for (int i = 0; i < 2; i++) {
                    Div flexItem = new Div().add(new Paragraph(Integer.toString(i)));
                    flexItem.setHeight(110).setWidth(110).setMinHeight(110).setMinWidth(110);

                    AbstractRenderer flexItemRenderer = (AbstractRenderer) flexItem.createRendererSubTree()
                            .setParent(documentRenderer);
                    flexItemRenderer.setParent(flexContainerRenderer);
                    flexContainerRenderer.addChild(flexItemRenderer);
                }

                List<List<FlexItemInfo>> rectangleTable =
                        FlexUtil.calculateChildrenRectangles(bBox, (FlexContainerRenderer) div.getRenderer());

                Assertions.assertEquals(0, rectangleTable.get(0).get(0).getRectangle().getX(), EPS);
                Assertions.assertEquals(0, rectangleTable.get(1).get(0).getRectangle().getX(), EPS);
            }
        }
    }
}
