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
package com.itextpdf.styledxmlparser.resolver.resource;

import com.itextpdf.kernel.pdf.xobject.PdfXObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Simple implementation of an image cache.
 */
class SimpleImageCache {
    
    /** The cache mapping a source path to an Image XObject. */
    private Map<String, PdfXObject> cache = new LinkedHashMap<>();
    
    /** Stores how many times each image is used. */
    private Map<String, Integer> imagesFrequency = new LinkedHashMap<>();
    
    /** The capacity of the cache. */
    private int capacity;

    /**
     * Creates a new {@link SimpleImageCache} instance.
     */
    SimpleImageCache() {
        this.capacity = 100;
    }

    /**
     * Creates a new {@link SimpleImageCache} instance.
     *
     * @param capacity the capacity
     */
    SimpleImageCache(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("capacity");
        }
        this.capacity = capacity;
    }

    /**
     * Adds an image to the cache.
     *
     * @param src the source path
     * @param imageXObject the image XObject to be cached
     */
    void putImage(String src, PdfXObject imageXObject) {
        if (cache.containsKey(src)) {
            return;
        }
        ensureCapacity();
        cache.put(src, imageXObject);
    }

    /**
     * Gets an image from the cache.
     *
     * @param src the source path
     * @return the image XObject
     */
    PdfXObject getImage(String src) {
        Integer frequency = imagesFrequency.get(src);
        if (frequency != null) {
            imagesFrequency.put(src, frequency + 1);
        } else {
            imagesFrequency.put(src, 1);
        }

        return cache.get(src);
    }

    /**
     * Gets the size of the cache.
     *
     * @return the cache size
     */
    int size() {
        return cache.size();
    }

    /**
     * Resets the cache.
     */
    void reset() {
        cache.clear();
        imagesFrequency.clear();
    }

    /**
     * Ensures the capacity of the cache by removing the least important images
     * (based on the number of times an image is used).
     */
    private void ensureCapacity() {
        if (cache.size() >= capacity) {
            String mostUnpopularImg = null;
            int minFrequency = Integer.MAX_VALUE;
            
            // the keySet method preserves the LinkedList order.
            for (String imgSrc : cache.keySet()) {
                Integer imgFrequency = imagesFrequency.get(imgSrc);
                if (imgFrequency == null || imgFrequency < minFrequency) {
                    mostUnpopularImg = imgSrc;
                    if (imgFrequency == null) {
                        break;
                    } else {
                        minFrequency = (int) imgFrequency;
                    }
                }
            }

            cache.remove(mostUnpopularImg);
        }
    }
}
