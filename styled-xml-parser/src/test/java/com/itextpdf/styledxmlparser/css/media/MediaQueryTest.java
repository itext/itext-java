package com.itextpdf.styledxmlparser.css.media;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)

public class MediaQueryTest extends ExtendedITextTest {
    @Test
    public void matchTest() {
        MediaDeviceDescription deviceDescription = new MediaDeviceDescription("all");
        MediaQuery query = MediaQueryParser.parseMediaQuery("not all and (min-width: 600px)");
        List<MediaQuery> queries = MediaQueryParser.parseMediaQueries("not all and (min-width: 600px), not all and (min-width: 500px)");
        Assert.assertTrue(query.matches(deviceDescription));
        Assert.assertTrue(queries.get(0).matches(deviceDescription));
        Assert.assertTrue(queries.get(1).matches(deviceDescription));
    }
}
