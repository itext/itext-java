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
package com.itextpdf.forms.fields;

import com.itextpdf.commons.actions.contexts.IMetaInfo;
import com.itextpdf.layout.renderer.MetaInfoContainer;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class FormsMetaInfoStaticContainerTest extends ExtendedITextTest {

    @Test
    public void useMetaInfoDuringTheActionOneThreadTest() {
        MetaInfoContainer metaInfo1 = new MetaInfoContainer(new IMetaInfo() {});
        MetaInfoContainer metaInfo2 = new MetaInfoContainer(new IMetaInfo() {});

        FormsMetaInfoStaticContainer.useMetaInfoDuringTheAction(metaInfo1, () -> {
            Assertions.assertSame(metaInfo1, FormsMetaInfoStaticContainer.getMetaInfoForLayout());

            FormsMetaInfoStaticContainer.useMetaInfoDuringTheAction(metaInfo2,
                    () -> Assertions.assertSame(metaInfo2, FormsMetaInfoStaticContainer.getMetaInfoForLayout()));

            Assertions.assertNull(FormsMetaInfoStaticContainer.getMetaInfoForLayout());
        });

        Assertions.assertNull(FormsMetaInfoStaticContainer.getMetaInfoForLayout());
    }

    @Test
    public void useMetaInfoDuringTheActionSeveralThreadsTest() throws InterruptedException {
        MetaInfoCheckClass metaInfoCheckClass1 = new MetaInfoCheckClass(null);
        MetaInfoCheckClass metaInfoCheckClass2 = new MetaInfoCheckClass(metaInfoCheckClass1);
        MetaInfoCheckClass metaInfoCheckClass3 = new MetaInfoCheckClass(metaInfoCheckClass2);

        Thread thread = new Thread(() -> metaInfoCheckClass3.checkMetaInfo());
        thread.start();
        thread.join();

        Assertions.assertFalse(metaInfoCheckClass1.isCheckFailed());
        Assertions.assertFalse(metaInfoCheckClass2.isCheckFailed());
        Assertions.assertFalse(metaInfoCheckClass3.isCheckFailed());
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
