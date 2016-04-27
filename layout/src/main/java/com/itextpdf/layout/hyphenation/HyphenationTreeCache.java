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


package com.itextpdf.layout.hyphenation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p>This is a cache for HyphenationTree instances.</p>
 */
public class HyphenationTreeCache {

    /** Contains the cached hyphenation trees */
    private Map<String, HyphenationTree> hyphenTrees = new HashMap<>();
    /** Used to avoid multiple error messages for the same language if a pattern file is missing. */
    private Set<String> missingHyphenationTrees;

    /**
     * Looks in the cache if a hyphenation tree is available and returns it if it is found.
     * @param lang the language
     * @param country the country (may be null or "none")
     * @return the HyhenationTree instance or null if it's not in the cache
     */
    public HyphenationTree getHyphenationTree(String lang, String country) {
        String key = constructLlccKey(lang, country);

        if (key == null) {
            return null;
        }

        // first try to find it in the cache
        if (hyphenTrees.containsKey(key)) {
            return hyphenTrees.get(key);
        } else if (hyphenTrees.containsKey(lang)) {
            return hyphenTrees.get(lang);
        } else {
            return null;
        }
    }

    /**
     * Constructs the key for the hyphenation pattern file.
     * @param lang the language
     * @param country the country (may be null or "none")
     * @return the resulting key
     */
    public static String constructLlccKey(String lang, String country) {
        String key = lang;
        // check whether the country code has been used
        if (country != null && !country.equals("none")) {
            key += "_" + country;
        }
        return key;
    }

    /**
     * If the user configured a hyphenation pattern file name
     * for this (lang,country) value, return it. If not, return null.
     * @param lang the language
     * @param country the country (may be null or "none")
     * @param hyphPatNames the map of user-configured hyphenation pattern file names
     * @return the hyphenation pattern file name or null
     */
    public static String constructUserKey(String lang, String country, Map<String, String> hyphPatNames) {
        String userKey = null;
        if (hyphPatNames != null) {
            String key = constructLlccKey(lang, country);
            key = key.replace('_', '-');
            userKey = hyphPatNames.get(key);
        }
        return userKey;
    }

    /**
     * Cache a hyphenation tree under its key.
     * @param key the key (ex. "de_CH" or "en")
     * @param hTree the hyphenation tree
     */
    public void cache(String key, HyphenationTree hTree) {
        hyphenTrees.put(key, hTree);
    }

    /**
     * Notes a key to a hyphenation tree as missing.
     * This is to avoid searching a second time for a hyphenation pattern file which is not
     * available.
     * @param key the key (ex. "de_CH" or "en")
     */
    public void noteMissing(String key) {
        if (missingHyphenationTrees == null) {
            missingHyphenationTrees = new HashSet<>();
        }
        missingHyphenationTrees.add(key);
    }

    /**
     * Indicates whether a hyphenation file has been requested before but it wasn't available.
     * This is to avoid searching a second time for a hyphenation pattern file which is not
     * available.
     * @param key the key (ex. "de_CH" or "en")
     * @return true if the hyphenation tree is unavailable
     */
    public boolean isMissing(String key) {
        return (missingHyphenationTrees != null && missingHyphenationTrees.contains(key));
    }
}
