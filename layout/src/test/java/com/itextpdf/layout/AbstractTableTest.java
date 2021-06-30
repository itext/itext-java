package com.itextpdf.layout;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class AbstractTableTest extends ExtendedITextTest {
    static Document addTableBelowToCheckThatOccupiedAreaIsCorrect(Document doc) {
        doc.add(new Table(UnitValue.createPercentArray(1))
                .useAllAvailableWidth()
                .setBorder(new SolidBorder(ColorConstants.ORANGE, 2))
                .addCell("Is my occupied area correct?"));
        return doc;
    }
}
