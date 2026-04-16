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
