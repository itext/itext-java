package com.itextpdf.forms.fields;

import com.itextpdf.commons.actions.contexts.IMetaInfo;
import com.itextpdf.layout.renderer.MetaInfoContainer;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class FormsMetaInfoStaticContainerTest extends ExtendedITextTest {

    @Test
    public void useMetaInfoDuringTheActionOneThreadTest() {
        MetaInfoContainer metaInfo1 = new MetaInfoContainer(new IMetaInfo() {});
        MetaInfoContainer metaInfo2 = new MetaInfoContainer(new IMetaInfo() {});

        FormsMetaInfoStaticContainer.useMetaInfoDuringTheAction(metaInfo1, () -> {
            Assert.assertSame(metaInfo1, FormsMetaInfoStaticContainer.getMetaInfoForLayout());

            FormsMetaInfoStaticContainer.useMetaInfoDuringTheAction(metaInfo2,
                    () -> Assert.assertSame(metaInfo2, FormsMetaInfoStaticContainer.getMetaInfoForLayout()));

            Assert.assertNull(FormsMetaInfoStaticContainer.getMetaInfoForLayout());
        });

        Assert.assertNull(FormsMetaInfoStaticContainer.getMetaInfoForLayout());
    }

    @Test
    public void useMetaInfoDuringTheActionSeveralThreadsTest() throws InterruptedException {
        MetaInfoCheckClass metaInfoCheckClass1 = new MetaInfoCheckClass(null);
        MetaInfoCheckClass metaInfoCheckClass2 = new MetaInfoCheckClass(metaInfoCheckClass1);
        MetaInfoCheckClass metaInfoCheckClass3 = new MetaInfoCheckClass(metaInfoCheckClass2);

        Thread thread = new Thread(() -> metaInfoCheckClass3.checkMetaInfo());
        thread.start();
        thread.join();

        Assert.assertFalse(metaInfoCheckClass1.isCheckFailed());
        Assert.assertFalse(metaInfoCheckClass2.isCheckFailed());
        Assert.assertFalse(metaInfoCheckClass3.isCheckFailed());
    }

    private static class MetaInfoCheckClass {

        private MetaInfoCheckClass metaInfoCheckClass = null;
        private boolean checkFailed = false;

        public MetaInfoCheckClass(MetaInfoCheckClass metaInfoCheckClass) {
            this.metaInfoCheckClass = metaInfoCheckClass;
        }

        public void checkMetaInfo() {
            MetaInfoContainer metaInfo = new MetaInfoContainer(new IMetaInfo() {
            });

            FormsMetaInfoStaticContainer.useMetaInfoDuringTheAction(metaInfo, () -> {
                if (metaInfoCheckClass != null) {
                    Thread thread = new Thread(() -> metaInfoCheckClass.checkMetaInfo());
                    thread.start();
                    try {
                        thread.join();
                    } catch (Exception ignored) {
                        checkFailed = true;
                    }
                }

                checkFailed |= metaInfo != FormsMetaInfoStaticContainer.getMetaInfoForLayout();
            });

            checkFailed |= FormsMetaInfoStaticContainer.getMetaInfoForLayout() != null;
        }

        public boolean isCheckFailed() {
            return checkFailed;
        }
    }
}