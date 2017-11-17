package com.itextpdf.layout.tagging;

interface ITaggingRule {
    boolean onTagFinish(LayoutTaggingHelper taggingHelper, TaggingHintKey taggingHintKey);
}
