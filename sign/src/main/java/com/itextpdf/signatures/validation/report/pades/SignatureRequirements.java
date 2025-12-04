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
 * This class contains the rules specific for signature.
 */
class SignatureRequirements extends AbstractPadesLevelRequirements {

    private static Map<PAdESLevel, LevelChecks> checks;

    static {
        checks = new HashMap<PAdESLevel, LevelChecks>();
        LevelChecks bbChecks = new LevelChecks();
        checks.put(PAdESLevel.B_B, bbChecks);

        bbChecks.shalls.add(new CheckAndMessage(
                r -> r.signatureDictionaryEntrySubFilterValueIsETSICadesDetached,
                "SubFilter entry value is not ETSI.CAdES.detached"));

        bbChecks.shalls.add(new CheckAndMessage(
                r -> r.contentTypeValueIsIdData,
                CMS_CONTENT_TYPE_MUST_BE_ID_DATA));

        bbChecks.shalls.add(new CheckAndMessage(
                r -> !r.cmsSigningTimeAttributePresent,
                CLAIMED_TIME_OF_SIGNING_SHALL_NOT_BE_INCLUDED_IN_THE_CMS));
        bbChecks.shalls.add(new CheckAndMessage(
                r -> r.dictionaryEntryMPresent,
                DICTIONARY_ENTRY_M_IS_MISSING));

        bbChecks.shalls.add(new CheckAndMessage(
                r -> r.dictionaryEntryMHasCorrectFormat,
                DICTIONARY_ENTRY_M_IS_NOT_IN_THE_CORRECT_FORMAT));


        LevelChecks btChecks = new LevelChecks();
        checks.put(PAdESLevel.B_T, btChecks);

        btChecks.shalls.add(new CheckAndMessage(
                r -> r.poeSignaturePresent || r.documentTimestampPresent,
                THERE_MUST_BE_A_SIGNATURE_OR_DOCUMENT_TIMESTAMP_AVAILABLE));

        LevelChecks bltChecks = new LevelChecks();
        checks.put(PAdESLevel.B_LT, bltChecks);

        bltChecks.shalls.add(new CheckAndMessage(
                r -> r.isDSSPresent,
                DSS_DICTIONARY_IS_MISSING));

        LevelChecks bltaChecks = new LevelChecks();
        checks.put(PAdESLevel.B_LTA, bltaChecks);

        bltaChecks.shalls.add(new CheckAndMessage(
                r -> r.documentTimestampPresent,
                DOCUMENT_TIMESTAMP_IS_MISSING));

        bltaChecks.shalls.add(new CheckAndMessage(
                r -> r.poeDssPresent,
                DSS_DICTIONARY_IS_NOT_COVERED_BY_A_DOCUMENT_TIMESTAMP));
    }

    /**
     * Creates a new instance.
     */
    public SignatureRequirements() {
        super();
    }

    @Override
    protected Map<PAdESLevel, LevelChecks> getChecks() {
        return checks;
    }
}
