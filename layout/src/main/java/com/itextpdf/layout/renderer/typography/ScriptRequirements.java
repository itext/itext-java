package com.itextpdf.layout.renderer.typography;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Class that contains requirements for correct shaping and layout of text for a script.
 * <p>
 * Intended for internal use
 */
public class ScriptRequirements {
    private final Collection<String> requiredFeatures;
    private final Collection<String> affectingFeatures;
    private final Collection<String> otfScriptNames;
    private final boolean hardCodedHandling;
    private final boolean supported;

    ScriptRequirements(Collection<String> requiredFeatures, Collection<String> affectingFeatures,
            boolean hardCodedHandling) {
        this(Collections.<String>emptyList(), requiredFeatures, affectingFeatures, hardCodedHandling, true);
    }

    /**
     * Creates an immutable set of requirements for rendering a script
     *
     * @param otfScriptNames    names of OpenType scripts corresponding to the Utf script
     * @param requiredFeatures  font features required for correct shaping and layout of text in the script
     * @param affectingFeatures font features that can affect shaping and layout of text
     *                          in the script but that are not required
     * @param hardCodedHandling flag indicating if the script requires hard coded handling for actions not supported
     *                          by OpenType features. For example custom line splitting.
     * @param supported         flag indicating if the script is supported by pdfCalligraphy.
     */
    ScriptRequirements(Collection<String> otfScriptNames, Collection<String> requiredFeatures,
            Collection<String> affectingFeatures, boolean hardCodedHandling, boolean supported) {
        this.otfScriptNames = otfScriptNames;
        this.requiredFeatures = requiredFeatures;
        this.affectingFeatures = affectingFeatures;
        this.hardCodedHandling = hardCodedHandling;
        this.supported = supported;
    }

    /**
     * Creates a new immutable set of requirements for rendering a script based on the existing one,
     * but with different OpenType script names.
     *
     * @param other          existing set of requirements for rendering a script
     * @param otfScriptNames names of OpenType scripts corresponding to the Utf script
     */
    private ScriptRequirements(ScriptRequirements other, Collection<String> otfScriptNames) {
        this.otfScriptNames = otfScriptNames;
        this.requiredFeatures = other.requiredFeatures;
        this.affectingFeatures = other.affectingFeatures;
        this.hardCodedHandling = other.hardCodedHandling;
        this.supported = true;
    }

    /**
     * Creates a set of requirements for an unsupported script.
     * The set contains only names of OpenType scripts corresponding to the Utf script,
     * and empty collections of required and affecting features.
     *
     * @param otfScriptNames names of OpenType scripts corresponding to the Utf script
     * @return a new set of requirements for an unsupported script
     */
    static ScriptRequirements createUnsupported(Collection<String> otfScriptNames) {
        return new ScriptRequirements(otfScriptNames, Collections.<String>emptyList(), Collections.<String>emptyList(),
                false, false);
    }

    /**
     * Get features required for correct shaping and layout of text in the script.
     * @return collection of required features
     */
    public Collection<String> getRequiredFeatures() {
        return requiredFeatures;
    }

    /**
     * Get features that affect shaping and layout of text in the script, but are not required.
     *
     * @return collection of affecting features
     */
    public Collection<String> getAffectingFeatures() {
        return affectingFeatures;
    }

    /**
     * Get names of OpenType scripts corresponding to the Utf script.
     *
     * @return collection of OpenType script names
     */
    public Collection<String> getOtfScriptNames() {
        return otfScriptNames;
    }

    /**
     * Check if the script requires hard coded handling in layout for actions not supported
     * by OpenType features. For example custom line splitting.
     *
     * @return <code>true</code> if the script requires hard coded handling and <code>false</code> otherwise
     */
    public boolean isHardCodedHandling() {
        return hardCodedHandling;
    }

    /**
     * Check if the script is supported by pdfCalligraphy.
     *
     * @return <code>true</code> if the script is supported and <code>false</code> otherwise
     */
    public boolean isSupported() {
        return supported;
    }

    /**
     * Creates a new set of requirements for rendering a script based on the existing one,
     * but with different OpenType script names.
     * @param scriptNames names of OpenType scripts corresponding to the Utf script
     * @return a new set of requirements for rendering a script with the specified OpenType script names
     */
    ScriptRequirements withOtfScriptNames(String... scriptNames) {
        return new ScriptRequirements(this, Arrays.<String>asList(scriptNames));
    }

    /**
     * Creates a new set of requirements for rendering a script based on the existing one,
     * but with different value of the hard coded handling flag.
     *
     * @param isHardcoded flag indicating if the script requires hard coded handling for actions not supported
     *                     by OpenType features. For example custom line splitting.
     * @return a new set of requirements for rendering a script with the specified value of the hard coded handling flag
     */
    ScriptRequirements withIsHardcoded(boolean isHardcoded) {
        return new ScriptRequirements(otfScriptNames, requiredFeatures, affectingFeatures, isHardcoded, supported);
    }
}
