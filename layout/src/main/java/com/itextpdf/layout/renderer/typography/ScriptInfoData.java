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

import java.lang.Character.UnicodeScript;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

class ScriptInfoData {

    private final Map<UnicodeScript, ScriptRequirements> scriptReqFeature;
    private final Set<UnicodeScript> supportedScripts;

    ScriptInfoData(int initialSize) {
        this.scriptReqFeature = new java.util.HashMap<>(initialSize);
        this.supportedScripts = new java.util.HashSet<>(initialSize);
    }

    public Set<UnicodeScript> getSupportedScripts() {
        return Collections.unmodifiableSet(supportedScripts);
    }

    ScriptRequirements get(UnicodeScript script) {
        return scriptReqFeature.get(script);
    }

    boolean scriptSupported(UnicodeScript script) {
        return supportedScripts.contains(script);
    }

    void addRequirements(UnicodeScript script, ScriptRequirements requirements) {
        scriptReqFeature.put(script, requirements);
        if (requirements.isSupported()) {
            supportedScripts.add(script);
        }
    }
}
