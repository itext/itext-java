/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.itextpdf.model.hyphenation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>This class is the main entry point to the hyphenation package.
 * You can use only the static methods or create an instance.</p>
 *
 * <p>This work was authored by Carlos Villegas (cav@uniscope.co.jp).</p>
 */
public final class Hyphenator {

    private static final String HYPHENATION_DEFAULT_RESOURCE = "com/itextpdf/hyph/";

    /** logging instance */
    private static Logger log = LoggerFactory.getLogger(Hyphenator.class);

    private static HyphenationTreeCache hTreeCache;

    private static List<String> additionalHyphenationFileDirectories;

    protected String lang;
    protected String country;
    int leftMin;
    int rightMin;
    Map<String, String> hyphPathNames;

    /**
     * Creates a new hyphenator.
     * @param lang the language
     * @param country the optional country code (may be null or "none")
     * @param leftMin the minimum number of characters before the hyphenation point
     * @param rightMin the minimum number of characters after the hyphenation point
     */
    public Hyphenator(String lang, String country, int leftMin, int rightMin) {
        this.lang = lang;
        this.country = country;
        this.leftMin = leftMin;
        this.rightMin = rightMin;
    }

    /**
     * Creates a new hyphenator.
     * @param lang the language
     * @param country the optional country code (may be null or "none")
     * @param hyphPathNames the map with user-configured hyphenation pattern file names
     * @param leftMin the minimum number of characters before the hyphenation point
     * @param rightMin the minimum number of characters after the hyphenation point
     */
    public Hyphenator(String lang, String country, int leftMin, int rightMin, Map<String, String> hyphPathNames) {
        this(lang, country, leftMin, rightMin);
        this.hyphPathNames = hyphPathNames;
    }

    public static synchronized void registerAdditionalHyphenationFileDirectory(String directory) {
        if (additionalHyphenationFileDirectories == null) {
            additionalHyphenationFileDirectories = new ArrayList<>();
        }
        additionalHyphenationFileDirectories.add(directory);
    }

    /** @return the default (static) hyphenation tree cache */
    public static synchronized HyphenationTreeCache getHyphenationTreeCache() {
        if (hTreeCache == null) {
            hTreeCache = new HyphenationTreeCache();
        }
        return hTreeCache;
    }

    /**
     * Clears the default hyphenation tree cache.<br>
     * This method can be used if the underlying data files are changed at runtime.
     */
    public static synchronized void clearHyphenationTreeCache() {
        hTreeCache = new HyphenationTreeCache();
    }

    /**
     * Returns a hyphenation tree for a given language and country,
     * with fallback from (lang,country) to (lang).
     * The hyphenation trees are cached.
     * @param lang the language
     * @param country the country (may be null or "none")
     * @param hyphPathNames the map with user-configured hyphenation pattern file names
     * @return the hyphenation tree
     */
    public static HyphenationTree getHyphenationTree(String lang, String country, Map<String, String> hyphPathNames) {
        String llccKey = HyphenationTreeCache.constructLlccKey(lang, country);
        HyphenationTreeCache cache = getHyphenationTreeCache();

        // If this hyphenation tree has been registered as missing, return immediately
        if (cache.isMissing(llccKey)) {
            return null;
        }

        HyphenationTree hTree = getHyphenationTree2(lang, country, hyphPathNames);

        // fallback to lang only
        if (hTree == null && country != null && !country.equals("none")) {
            String llKey = HyphenationTreeCache.constructLlccKey(lang, null);
            if (!cache.isMissing(llKey)) {
                hTree = getHyphenationTree2(lang, null, hyphPathNames);
                if (hTree != null && log.isDebugEnabled()) {
                    log.debug("Couldn't find hyphenation pattern "
                              + "for lang=\"" + lang + "\",country=\"" + country + "\"."
                              + " Using general language pattern "
                              + "for lang=\"" + lang + "\" instead.");
                }
                if (hTree == null) {
                    // no fallback; register as missing
                    cache.noteMissing(llKey);
                } else {
                    // also register for (lang,country)
                    cache.cache(llccKey, hTree);
                }
            }
        }

        if (hTree == null) {
            // (lang,country) and (lang) tried; register as missing
            cache.noteMissing(llccKey);
            log.error("Couldn't find hyphenation pattern "
                      + "for lang=\"" + lang + "\""
                      + (country != null && !country.equals("none")
                              ? ",country=\"" + country + "\""
                              : "")
                      + ".");
        }

        return hTree;
    }

    /**
     * Returns a hyphenation tree for a given language and country
     * The hyphenation trees are cached.
     * @param lang the language
     * @param country the country (may be null or "none")
     * @param hyphPathNames the map with user-configured hyphenation pattern file names
     * @return the hyphenation tree
     */
    public static HyphenationTree getHyphenationTree2(String lang, String country, Map<String, String> hyphPathNames) {
        String llccKey = HyphenationTreeCache.constructLlccKey(lang, country);
        HyphenationTreeCache cache = getHyphenationTreeCache();

        HyphenationTree hTree;
        // first try to find it in the cache
        hTree = getHyphenationTreeCache().getHyphenationTree(lang, country);
        if (hTree != null) {
            return hTree;
        }

        String key = HyphenationTreeCache.constructUserKey(lang, country, hyphPathNames);
        if (key == null) {
            key = llccKey;
        }

        if (additionalHyphenationFileDirectories != null) {
            for (String dir : additionalHyphenationFileDirectories) {
                hTree = getHyphenationTree(new File(dir), key);
                if (hTree != null) {
                    break;
                }
            }
        }

        if (hTree == null) {
            // get from the default directory
            URL defaultHyphenationResource = ClassLoader.getSystemResource(HYPHENATION_DEFAULT_RESOURCE);
            if (defaultHyphenationResource != null) {
                hTree = getHyphenationTree(new File(defaultHyphenationResource.getPath()), key);
            }
        }

        // put it into the pattern cache
        if (hTree != null) {
            cache.cache(llccKey, hTree);
        }

        return hTree;
    }

    /**
     * Load tree from xml file using configuration settings
     * @param searchDirectory the directory to search the file into
     * @param key language key for the requested hyphenation file
     * @return the requested HypenationTree or null if it is not available
     */
    public static HyphenationTree getHyphenationTree(File searchDirectory, String key) {
        HyphenationTree hTree;
        // try the raw XML file
        String name = key + ".xml";
        hTree = new HyphenationTree();
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(new File(searchDirectory, name)));
            try {
                InputSource src = new InputSource(in);
                src.setSystemId(name);
                hTree.loadPatterns(src);
            } finally {
                try {
                    in.close();
                } catch (Exception ignored) {}
            }
            return hTree;
        } catch (HyphenationException ex) {
            log.error("Can't load user patterns from XML file " + name + ": " + ex.getMessage());
            return null;
        } catch (IOException ioe) {
            if (log.isDebugEnabled()) {
                log.debug("I/O problem while trying to load " + name, ioe);
            }
            return null;
        }
    }

    /**
     * Hyphenates a word.
     * @param lang the language
     * @param country the optional country code (may be null or "none")
     * @param hyphPathNames the map with user-configured hyphenation pattern file names
     * @param word the word to hyphenate
     * @param leftMin the minimum number of characters before the hyphenation point
     * @param rightMin the minimum number of characters after the hyphenation point
     * @return the hyphenation result
     */
    public static Hyphenation hyphenate(String lang, String country, Map<String, String> hyphPathNames, String word, int leftMin, int rightMin) {
        HyphenationTree hTree = getHyphenationTree(lang, country, hyphPathNames);
        if (hTree == null) {
            log.warn("Soft hyphen unicode symbols will be used as hints for hyphenation");
            final char softHyphen = '\u00ad';
            List<Integer> softHyphens = new ArrayList<>();
            int lastSoftHyphenIndex = -1;
            int curSoftHyphenIndex;
            while ((curSoftHyphenIndex = word.indexOf(softHyphen, lastSoftHyphenIndex + 1)) > 0) {
                softHyphens.add(curSoftHyphenIndex);
                lastSoftHyphenIndex = curSoftHyphenIndex;
            }
            int leftInd = 0, rightInd = softHyphens.size() - 1;
            while (leftInd < softHyphens.size() && word.substring(0, softHyphens.get(leftInd)).replace(String.valueOf(softHyphen), "").length() < leftMin) {
                leftInd++;
            }
            while (rightInd >= 0 && word.substring(softHyphens.get(rightInd) + 1).replace(String.valueOf(softHyphen), "").length() < rightMin) {
                rightInd--;
            }
            if (leftInd <= rightInd) {
                int[] hyphenationPoints = new int[rightInd - leftInd + 1];
                for (int i = leftInd; i <= rightInd; i++) {
                    hyphenationPoints[i] = softHyphens.get(i);
                }
                return new Hyphenation(word, hyphenationPoints);
            } else {
                return null;
            }
        }
        return hTree.hyphenate(word, leftMin, rightMin);
    }

    /**
     * Hyphenates a word.
     * @param lang the language
     * @param country the optional country code (may be null or "none")
     * @param word the word to hyphenate
     * @param leftMin the minimum number of characters before the hyphenation point
     * @param rightMin the minimum number of characters after the hyphenation point
     * @return the hyphenation result
     */
    public static Hyphenation hyphenate(String lang, String country, String word, int leftMin, int rightMin) {
        return hyphenate(lang, country, null, word, leftMin, rightMin);
    }

    /**
     * Hyphenates a word.
     * @param word the word to hyphenate
     * @return the hyphenation result
     */
    public Hyphenation hyphenate(String word) {
        return hyphenate(lang, country, hyphPathNames, word, leftMin, rightMin);
    }
}
