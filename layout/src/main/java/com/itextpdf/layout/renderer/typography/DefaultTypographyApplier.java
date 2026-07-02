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
package com.itextpdf.layout.renderer.typography;

import com.itextpdf.commons.actions.contexts.IMetaInfo;
import com.itextpdf.commons.actions.sequence.SequenceId;
import com.itextpdf.commons.datastructures.ConcurrentWeakMap;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.TrueTypeFont;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.io.font.otf.OpenTableLookup;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;

import java.lang.Character.UnicodeScript;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DefaultTypographyApplier extends AbstractTypographyApplier {

    private static final String SCRIPT = "script";
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultTypographyApplier.class);
    private static final ConcurrentWeakMap<SequenceId, Collection<String>> IDS_WITH_WARNING =
            new ConcurrentWeakMap<SequenceId, Collection<String>>();
    private static final ConcurrentWeakMap<SequenceId, Collection<String>> IDS_WITH_INFO =
            new ConcurrentWeakMap<SequenceId, Collection<String>>();

    public DefaultTypographyApplier() {
    }

    @Override
    public boolean isPdfCalligraphInstance() {
        return false;
    }

    @Override
    public Collection<UnicodeScript> getSupportedScripts() {
        return ScriptInfo.getSupportedScripts();
    }

    @Override
    public Collection<UnicodeScript> getSupportedScripts(Object configurator) {
        return ScriptInfo.getSupportedScripts();
    }

    @Override
    public boolean applyOtfScript(TrueTypeFont font, GlyphLine glyphLine, UnicodeScript script, Object configurator,
            SequenceId id, IMetaInfo metaInfo) {
        checkTypographyRequired(font, script, id);
        return super.applyOtfScript(font, glyphLine, script, configurator, id, metaInfo);
    }

    @Override
    public boolean applyKerning(FontProgram fontProgram, GlyphLine text, SequenceId sequenceId, IMetaInfo metaInfo) {
        if (fontProgram.hasKernPairs()) {
            logWarning(sequenceId, "kerning", "kerning enabled");
        }
        return super.applyKerning(fontProgram, text, sequenceId, metaInfo);
    }

    @Override
    public List<Integer> getPossibleBreaks(String str) {
        return Collections.<Integer>emptyList();
    }

    @Override
    public Map<String, byte[]> loadShippedFonts() {
        return new HashMap<>();
    }

    private static void checkTypographyRequired(TrueTypeFont font, UnicodeScript script, SequenceId id) {
        if (ScriptInfo.scriptSupported(script)) {
            ScriptRequirements reqs = ScriptInfo.getRequirements(script);
            if (!hasWarning(id, script.name())) {
                if (reqs.isHardCodedHandling()) {
                    logWarning(id, script.name(), SCRIPT, script.name(), "which requires special handling.");
                } else if (fontHasFeature(font, reqs.getOtfScriptNames(), reqs.getRequiredFeatures())) {
                    logWarning(id, script.name(), SCRIPT, script.name(), "with required features",
                            reqs.getRequiredFeatures().toString());
                }
                if (!hasInfo(id, script.name())
                    && fontHasFeature(font, reqs.getOtfScriptNames(), reqs.getAffectingFeatures())) {
                    logInfo(id, script.name(), reqs.getAffectingFeatures().toString());
                }
            }
        }
    }

    private static boolean fontHasFeature(TrueTypeFont font, Collection<String> otfScriptNames,
            Collection<String> features) {
        Map<String, List<OpenTableLookup>> featuresFound = new HashMap<String, List<OpenTableLookup>>();
        font.extractFeatures(otfScriptNames, featuresFound);
        for (String feature : features) {
            if (featuresFound.containsKey(feature)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasWarning(SequenceId id, String script) {
        return IDS_WITH_WARNING.containsKey(id) && IDS_WITH_WARNING.get(id).contains(script);
    }

    private static boolean hasInfo(SequenceId id, String script) {
        return (IDS_WITH_INFO.containsKey(id) && IDS_WITH_INFO.get(id).contains(script))
                || hasWarning(id, script);
    }

    private static void logWarning(SequenceId id, String script, String... messageParts) {
        if (LOGGER.isWarnEnabled()) {
            if (IDS_WITH_WARNING.containsKey(id)) {
                if (IDS_WITH_WARNING.get(id).contains(script)) {
                    return;
                }
                IDS_WITH_WARNING.get(id).add(script);
            } else {
                IDS_WITH_WARNING.put(id, new HashSet<>(Collections.singleton(script)));
            }
            StringBuilder message = new StringBuilder();
            for (String part : messageParts) {
                message.append(part).append(' ');
            }
            LOGGER.warn(MessageFormatUtil.format(LayoutLogMessageConstant.TYPOGRAPHY_NOT_FOUND_WARNING, message));
        }
    }

    private static void logInfo(SequenceId id, String script, String features) {
        if (LOGGER.isInfoEnabled()) {
            if ((IDS_WITH_WARNING.containsKey(id) && IDS_WITH_WARNING.get(id).contains(script))
                    || (IDS_WITH_INFO.containsKey(id) && IDS_WITH_INFO.get(id).contains(script))) {
                return;
            }
            if (IDS_WITH_INFO.containsKey(id)) {
                IDS_WITH_INFO.get(id).add(script);
            } else {
                IDS_WITH_INFO.put(id, new HashSet<>(Collections.singleton(script)));
            }

            LOGGER.info(MessageFormatUtil.format(LayoutLogMessageConstant.TYPOGRAPHY_NOT_FOUND_INFO, script, features));
        }
    }
}
