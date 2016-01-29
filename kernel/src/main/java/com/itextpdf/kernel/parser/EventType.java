package com.itextpdf.kernel.parser;

/**
 * Specifies different types of events where a callback should be notified.
 */
public enum EventType {
    BEGIN_TEXT,
    RENDER_TEXT,
    END_TEXT,
    RENDER_IMAGE,
    RENDER_PATH,
    CLIP_PATH_CHANGED
}
