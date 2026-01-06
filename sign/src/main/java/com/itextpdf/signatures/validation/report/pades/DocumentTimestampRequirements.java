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
package com.itextpdf.signatures.validation.report.pades;

import java.util.HashMap;
import java.util.Map;

/**
 * This class contains the rules specific for a document timestamp signature.
 */
class DocumentTimestampRequirements extends AbstractPadesLevelRequirements {

    public static final String SUBFILTER_NOT_ETSI_RFC3161 = "Timestamp SubFilter entry value is not ETSI.RFC3161";

    private static final Map<PAdESLevel, LevelChecks> CHECKS = new HashMap<PAdESLevel, LevelChecks>();
    private static final Map<PAdESLevel, LevelChecks> DSS_COVERED_TS_CHECKS = new HashMap<PAdESLevel, LevelChecks>();
    private final boolean coveredByDss;

    static {
        LevelChecks bbChecks = new LevelChecks();
        CHECKS.put(PAdESLevel.B_B, bbChecks);

        LevelChecks btChecks = new LevelChecks();
        CHECKS.put(PAdESLevel.B_T, btChecks);

        LevelChecks bltChecks = new LevelChecks();
        CHECKS.put(PAdESLevel.B_LT, bltChecks);

        LevelChecks bltaChecks = new LevelChecks();
        CHECKS.put(PAdESLevel.B_LTA, bltaChecks);

        bltaChecks.shalls.add(new CheckAndMessage(
                r -> r.timestampDictionaryEntrySubFilterValueEtsiRfc3161,
                SUBFILTER_NOT_ETSI_RFC3161));

        LevelChecks dssBbChecks = new LevelChecks();
        dssBbChecks.shalls.addAll(bbChecks.shalls);
        dssBbChecks.shoulds.addAll(bbChecks.shoulds);
        DSS_COVERED_TS_CHECKS.put(PAdESLevel.B_B, dssBbChecks);

        LevelChecks dssBtChecks = new LevelChecks();
        dssBtChecks.shalls.addAll(btChecks.shalls);
        dssBtChecks.shoulds.addAll(btChecks.shoulds);
        DSS_COVERED_TS_CHECKS.put(PAdESLevel.B_T, dssBtChecks);

        LevelChecks dssBltChecks = new LevelChecks();
        dssBltChecks.shalls.addAll(bltChecks.shalls);
        dssBltChecks.shoulds.addAll(bltChecks.shoulds);
        DSS_COVERED_TS_CHECKS.put(PAdESLevel.B_LT, dssBltChecks);

        dssBltChecks.shalls.add(createRevocationDssUsageCheck());
        bltChecks.shalls.add(createCertificateExternalRetrievalCheck());
        dssBltChecks.shoulds.add(createCertificatesDssUsageCheck());

        LevelChecks dssBltaChecks = new LevelChecks();
        dssBltaChecks.shalls.addAll(bltaChecks.shalls);
        dssBltaChecks.shoulds.addAll(bltaChecks.shoulds);
        dssBltaChecks.shalls.add(createRevocationDssPoECoverage());
        DSS_COVERED_TS_CHECKS.put(PAdESLevel.B_LTA, dssBltaChecks);
    }

    /**
     * Creates a new instance.
     */
    public DocumentTimestampRequirements(String name, boolean coveredByDss) {
        super(name);
        this.coveredByDss = coveredByDss;
    }

    @Override
    protected Map<PAdESLevel, LevelChecks> getChecks() {
        if (coveredByDss) {
            return DSS_COVERED_TS_CHECKS;
        }
        return CHECKS;
    }
}
