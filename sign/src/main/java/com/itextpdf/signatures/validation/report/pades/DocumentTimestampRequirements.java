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
package com.itextpdf.signatures.validation.report.pades;

import java.util.HashMap;
import java.util.Map;

/**
 * This class contains the rules specific for a document timestamp signature.
 */
class DocumentTimestampRequirements extends AbstractPadesLevelRequirements {

    public static final String SUBFILTER_NOT_ETSI_RFC3161 = "Timestamp SubFilter entry value is not ETSI.RFC3161";

    private static Map<PAdESLevel, LevelChecks> checks = new HashMap<PAdESLevel, LevelChecks>();
    private static Map<PAdESLevel, LevelChecks> dssCoveredTsChecks = new HashMap<PAdESLevel, LevelChecks>();
    private final boolean firstTimestamp;

    static {
        LevelChecks bbChecks = new LevelChecks();
        checks.put(PAdESLevel.B_B, bbChecks);

        LevelChecks btChecks = new LevelChecks();
        checks.put(PAdESLevel.B_T, btChecks);

        LevelChecks bltChecks = new LevelChecks();
        checks.put(PAdESLevel.B_LT, bltChecks);

        LevelChecks bltaChecks = new LevelChecks();
        checks.put(PAdESLevel.B_LTA, bltaChecks);

        bltaChecks.shalls.add(new CheckAndMessage(
                r -> r.timestampDictionaryEntrySubFilterValueEtsiRfc3161,
                SUBFILTER_NOT_ETSI_RFC3161));



        bbChecks = new LevelChecks();
        dssCoveredTsChecks.put(PAdESLevel.B_B, bbChecks);

        btChecks = new LevelChecks();
        dssCoveredTsChecks.put(PAdESLevel.B_T, btChecks);

        bltChecks = new LevelChecks();
        dssCoveredTsChecks.put(PAdESLevel.B_LT, bltChecks);

        bltaChecks = new LevelChecks();
        dssCoveredTsChecks.put(PAdESLevel.B_LTA, bltaChecks);
    }

    /**
     * Creates a new instance.
     */
    public DocumentTimestampRequirements(boolean firstTimestamp) {
        super();
        this.firstTimestamp = firstTimestamp;
    }

    @Override
    protected Map<PAdESLevel, LevelChecks> getChecks() {
        return checks;
    }
}
