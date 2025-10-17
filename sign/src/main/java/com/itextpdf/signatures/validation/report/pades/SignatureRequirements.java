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
