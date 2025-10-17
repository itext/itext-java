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
