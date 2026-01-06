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

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * This report gathers PAdES level information one signature.
 */
public class PAdESLevelReport {
    private static final PAdESLevel[] PADES_LEVELS =
            new PAdESLevel[] {PAdESLevel.B_B, PAdESLevel.B_T, PAdESLevel.B_LT, PAdESLevel.B_LTA};

    private final String signatureName;
    private final PAdESLevel highestAchievedLevel;
    private final Map<PAdESLevel, Collection<String>> nonConformaties;
    private final Map<PAdESLevel, Collection<String>> warnings;

    /**
     * Creates new instance.
     *
     * @param reqs             the requirements gathered for this signature
     * @param timestampReports the timestamp reports gathered before for this signature
     */
    PAdESLevelReport(AbstractPadesLevelRequirements reqs, Iterable<PAdESLevelReport> timestampReports) {
        this.signatureName = reqs.getSignatureName();
        this.highestAchievedLevel = reqs.getHighestAchievedPadesLevel(timestampReports);

        this.nonConformaties = reqs.getNonConformaties();
        this.warnings = reqs.getWarnings();
    }

    /**
     * Returns the signature name for the signature this report is about.
     *
     * @return the signature name for the signature this report is about
     */
    public String getSignatureName() {
        return signatureName;
    }

    /**
     * Returns the highest achieved PAdES level for this signature.
     *
     * @return the highest achieved PAdES level for this signature
     */
    public PAdESLevel getLevel() {
        return highestAchievedLevel;
    }

    /**
     * Returns non-conformaties, violated must have rules, per PAdES level.
     *
     * @return non-conformaties, violated must have rules, per PAdES level
     */
    public Map<PAdESLevel, Collection<String>> getNonConformaties() {
        return nonConformaties;
    }

    /**
     * Returns warnings, violated should have rules, per PAdES level.
     *
     * @return warnings, violated should have rules, per PAdES level
     */
    public Map<PAdESLevel, Collection<String>> getWarnings() {
        return warnings;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(100);
        sb.append("Signature: ").append(signatureName)
                .append("\n\tHighestAchievedLevel: ")
                .append(getLevel()).append('\n');
        for (PAdESLevel l : PADES_LEVELS) {
            if (getNonConformaties().containsKey(l)
                    && !getNonConformaties().get(l).isEmpty()) {
                sb.append('\t').append(l).append(" nonconformaties: \n");
                for (String message : getNonConformaties().get(l)) {
                    sb.append("\t\t").append(message).append('\n');
                }
            }
            if (getWarnings().containsKey(l)
                    && !getWarnings().get(l).isEmpty()) {
                sb.append('\t').append(l).append(" warnings:\n");
                for (String message : getWarnings().get(l)) {
                    sb.append("\t\t").append(message).append('\n');
                }
            }
        }
        return sb.toString();
    }
}
