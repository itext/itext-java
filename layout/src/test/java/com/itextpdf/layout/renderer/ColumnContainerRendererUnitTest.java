/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.IPropertyContainer;
import com.itextpdf.layout.element.ColumnContainer;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.LineHeight;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.splitting.DefaultSplitCharacters;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(UnitTest.class)
public class ColumnContainerRendererUnitTest extends ExtendedITextTest {

    @Test
    public void simpleTest() throws IOException {
        Div columnContainer = new ColumnContainer();
        Paragraph paragraph = new Paragraph("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor " +
                "incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation " +
                "ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in " +
                "voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non " +
                "proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");

        columnContainer.add(fillTextProperties(paragraph));
        columnContainer.setProperty(Property.COLUMN_COUNT, 3);

        ColumnContainerRenderer renderer = (ColumnContainerRenderer) columnContainer.createRendererSubTree();
        LayoutResult result = renderer.layout(new LayoutContext(new LayoutArea(1, new Rectangle(600f, 200.0f))));
        Assert.assertTrue(result.getSplitRenderer() instanceof ColumnContainerRenderer);
        Assert.assertEquals(3, result.getSplitRenderer().getChildRenderers().size());
        Assert.assertEquals(9, result.getSplitRenderer().getChildRenderers().get(0).getChildRenderers().size());
    }

    @Test
    public void keepTogetherParagraphTest() throws IOException {
        Div columnContainer = new ColumnContainer();
        Paragraph paragraph = new Paragraph("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor " +
                "incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation " +
                "ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in " +
                "voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non " +
                "proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
        paragraph.setProperty(Property.KEEP_TOGETHER, true);
        columnContainer.add(fillTextProperties(paragraph));

        columnContainer.setProperty(Property.COLUMN_COUNT, 3);

        ColumnContainerRenderer renderer = (ColumnContainerRenderer) columnContainer.createRendererSubTree();
        Assert.assertThrows(IllegalStateException.class, () -> renderer.layout(new LayoutContext(new LayoutArea(1, new Rectangle(200f, 20f)))));
    }

    @Test
    public void divWithNoHeightTest() throws IOException {
        Div div = new ColumnContainer();
        Paragraph paragraph = new Paragraph("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor " +
                "incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation " +
                "ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in " +
                "voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non " +
                "proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
        div.add(fillTextProperties(paragraph));

        div.setProperty(Property.COLUMN_COUNT, 3);

        ColumnContainerRenderer renderer = (ColumnContainerRenderer) div.createRendererSubTree();
        LayoutResult result = renderer.layout(new LayoutContext(new LayoutArea(1, new Rectangle(20.0f, 20.0f))));
        Assert.assertEquals(LayoutResult.NOTHING, result.getStatus());
    }

    @Test
    public void multipleParagraphsTest() throws IOException {
        Div div = new ColumnContainer();
        Div child = new Div();
        Paragraph firstParagraph = new Paragraph("Lorem ipsum dolor sit");
        Paragraph secondParagraph = new Paragraph("consectetur adipiscing elit");
        Paragraph thirdParagraph = new Paragraph("sed do eiusmod tempor incididunt");
        child.add(fillTextProperties(firstParagraph));
        child.add(fillTextProperties(secondParagraph));
        child.add(fillTextProperties(thirdParagraph));

        div.add(child);
        div.setProperty(Property.COLUMN_COUNT, 3);

        ColumnContainerRenderer renderer = (ColumnContainerRenderer) div.createRendererSubTree();
        LayoutResult result = renderer.layout(new LayoutContext(new LayoutArea(1, new Rectangle(600f, 30.0f))));
        Assert.assertTrue(result.getSplitRenderer() instanceof ColumnContainerRenderer);
        Assert.assertEquals(3, result.getSplitRenderer().getChildRenderers().size());
        Assert.assertEquals(1, result.getSplitRenderer().getChildRenderers().get(0).getChildRenderers().size());
        Assert.assertEquals(2, ((ParagraphRenderer)result.getSplitRenderer().getChildRenderers().get(0)
                .getChildRenderers().get(0)).getLines().size());
    }

    private static IBlockElement fillTextProperties(IBlockElement container) throws IOException {
        container.setProperty(Property.TEXT_RISE, 5.0f);
        container.setProperty(Property.CHARACTER_SPACING, 5.0f);
        container.setProperty(Property.WORD_SPACING, 5.0f);
        container.setProperty(Property.FONT, PdfFontFactory.createFont(StandardFonts.HELVETICA));
        container.setProperty(Property.FONT_SIZE, new UnitValue(UnitValue.POINT, 10));
        container.setProperty(Property.SPLIT_CHARACTERS, new DefaultSplitCharacters());
        return container;
    }
}
