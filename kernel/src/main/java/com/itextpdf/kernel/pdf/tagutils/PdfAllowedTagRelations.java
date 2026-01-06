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
package com.itextpdf.kernel.pdf.tagutils;

import com.itextpdf.kernel.pdf.tagging.StandardRoles;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * This class defines the allowed parent-child relations for the PDF2.0 standard.
 */
public class PdfAllowedTagRelations {

    public static final String NUMBERED_HEADER = "Hn";
    public static final String ACTUAL_CONTENT = "CONTENT";
    private static final Pattern numberedHeaderPattern = Pattern.compile("H(\\d+)");

    protected final Map<String, Collection<String>> allowedParentChildRelations = new HashMap<>();

    /**
     * Creates a new instance of {@link PdfAllowedTagRelations}.
     */
    public PdfAllowedTagRelations() {
        allowedParentChildRelations.put("StructTreeRoot", Collections.singleton(StandardRoles.DOCUMENT));
        allowedParentChildRelations.put(StandardRoles.DOCUMENT,
                Arrays.asList(StandardRoles.DOCUMENT, StandardRoles.DOCUMENTFRAGMENT, StandardRoles.PART,
                        StandardRoles.ART, StandardRoles.DIV, StandardRoles.SECT, StandardRoles.TOC,
                        StandardRoles.ASIDE, StandardRoles.BLOCKQUOTE, StandardRoles.NONSTRUCT, StandardRoles.PRIVATE,
                        StandardRoles.P, StandardRoles.NOTE, StandardRoles.CODE, NUMBERED_HEADER, StandardRoles.H,
                        StandardRoles.TITLE, StandardRoles.LINK, StandardRoles.ANNOT, StandardRoles.FORM,
                        StandardRoles.FENOTE, StandardRoles.INDEX, StandardRoles.L, StandardRoles.TABLE,
                        StandardRoles.FIGURE, StandardRoles.FORMULA, StandardRoles.ARTIFACT));
        allowedParentChildRelations.put(StandardRoles.DOCUMENTFRAGMENT,
                Arrays.asList(StandardRoles.DOCUMENT, StandardRoles.DOCUMENTFRAGMENT, StandardRoles.PART,
                        StandardRoles.ART, StandardRoles.DIV, StandardRoles.SECT, StandardRoles.TOC,
                        StandardRoles.ASIDE, StandardRoles.BLOCKQUOTE, StandardRoles.NONSTRUCT, StandardRoles.PRIVATE,
                        StandardRoles.P, StandardRoles.NOTE, StandardRoles.CODE, NUMBERED_HEADER, StandardRoles.H,
                        StandardRoles.TITLE, StandardRoles.LINK, StandardRoles.ANNOT, StandardRoles.FORM,
                        StandardRoles.FENOTE, StandardRoles.INDEX, StandardRoles.L, StandardRoles.TABLE,
                        StandardRoles.FIGURE, StandardRoles.FORMULA, StandardRoles.ARTIFACT));
        allowedParentChildRelations.put(StandardRoles.PART,
                Arrays.asList(StandardRoles.DOCUMENT, StandardRoles.DOCUMENTFRAGMENT, StandardRoles.PART,
                        StandardRoles.ART, StandardRoles.DIV, StandardRoles.SECT, StandardRoles.TOC, StandardRoles.TOCI,
                        StandardRoles.ASIDE, StandardRoles.BLOCKQUOTE, StandardRoles.NONSTRUCT, StandardRoles.PRIVATE,
                        StandardRoles.P, StandardRoles.NOTE, StandardRoles.CODE, NUMBERED_HEADER, StandardRoles.H,
                        StandardRoles.TITLE, StandardRoles.SUB, StandardRoles.LBL, StandardRoles.LINK,
                        StandardRoles.REFERENCE, StandardRoles.ANNOT, StandardRoles.FORM, StandardRoles.FENOTE,
                        StandardRoles.INDEX, StandardRoles.L, StandardRoles.BIBENTRY, StandardRoles.TABLE,
                        StandardRoles.CAPTION, StandardRoles.FIGURE, StandardRoles.FORMULA, StandardRoles.ARTIFACT));
        allowedParentChildRelations.put(StandardRoles.DIV,
                Arrays.asList(StandardRoles.DOCUMENT, StandardRoles.DOCUMENTFRAGMENT, StandardRoles.PART,
                        StandardRoles.ART, StandardRoles.DIV, StandardRoles.SECT, StandardRoles.TOC, StandardRoles.TOCI,
                        StandardRoles.ASIDE, StandardRoles.BLOCKQUOTE, StandardRoles.NONSTRUCT, StandardRoles.PRIVATE,
                        StandardRoles.P, StandardRoles.NOTE, StandardRoles.CODE, NUMBERED_HEADER, StandardRoles.H,
                        StandardRoles.TITLE, StandardRoles.SUB, StandardRoles.LBL, StandardRoles.EM,
                        StandardRoles.STRONG, StandardRoles.SPAN, StandardRoles.QUOTE, StandardRoles.LINK,
                        StandardRoles.REFERENCE, StandardRoles.ANNOT, StandardRoles.FORM, StandardRoles.RUBY,
                        StandardRoles.RB, StandardRoles.RT, StandardRoles.RP, StandardRoles.WARICHU, StandardRoles.WT,
                        StandardRoles.WP, StandardRoles.FENOTE, StandardRoles.INDEX, StandardRoles.L, StandardRoles.LI,
                        StandardRoles.LBODY, StandardRoles.BIBENTRY, StandardRoles.TABLE, StandardRoles.TR,
                        StandardRoles.TH, StandardRoles.TD, StandardRoles.THEAD, StandardRoles.TBODY,
                        StandardRoles.TFOOT, StandardRoles.CAPTION, StandardRoles.FIGURE, StandardRoles.FORMULA,
                        StandardRoles.ARTIFACT));
        allowedParentChildRelations.put(StandardRoles.ART,
                Arrays.asList(StandardRoles.DOCUMENTFRAGMENT, StandardRoles.PART, StandardRoles.DIV, StandardRoles.SECT,
                        StandardRoles.TOC, StandardRoles.ASIDE, StandardRoles.BLOCKQUOTE, StandardRoles.NONSTRUCT,
                        StandardRoles.PRIVATE, StandardRoles.P, StandardRoles.NOTE, StandardRoles.CODE, NUMBERED_HEADER,
                        StandardRoles.H, StandardRoles.TITLE, StandardRoles.LBL, StandardRoles.LINK,
                        StandardRoles.ANNOT, StandardRoles.FORM, StandardRoles.FENOTE, StandardRoles.INDEX,
                        StandardRoles.L, StandardRoles.TABLE, StandardRoles.CAPTION, StandardRoles.FIGURE,
                        StandardRoles.FORMULA, StandardRoles.ARTIFACT));
        allowedParentChildRelations.put(StandardRoles.SECT,
                Arrays.asList(StandardRoles.DOCUMENTFRAGMENT, StandardRoles.PART, StandardRoles.ART, StandardRoles.DIV,
                        StandardRoles.SECT, StandardRoles.TOC, StandardRoles.ASIDE, StandardRoles.BLOCKQUOTE,
                        StandardRoles.NONSTRUCT, StandardRoles.PRIVATE, StandardRoles.P, StandardRoles.NOTE,
                        StandardRoles.CODE, NUMBERED_HEADER, StandardRoles.H, StandardRoles.TITLE, StandardRoles.LBL,
                        StandardRoles.LINK, StandardRoles.ANNOT, StandardRoles.FORM, StandardRoles.FENOTE,
                        StandardRoles.INDEX, StandardRoles.L, StandardRoles.TABLE, StandardRoles.CAPTION,
                        StandardRoles.FIGURE, StandardRoles.FORMULA, StandardRoles.ARTIFACT));
        allowedParentChildRelations.put(StandardRoles.TOC,
                Arrays.asList(StandardRoles.PART, StandardRoles.TOC, StandardRoles.TOCI, StandardRoles.NONSTRUCT,
                        StandardRoles.PRIVATE, StandardRoles.CAPTION, StandardRoles.ARTIFACT));
        allowedParentChildRelations.put(StandardRoles.TOCI,
                Arrays.asList(StandardRoles.DIV, StandardRoles.TOC, StandardRoles.NONSTRUCT, StandardRoles.PRIVATE,
                        StandardRoles.P, StandardRoles.LBL, StandardRoles.REFERENCE, StandardRoles.ARTIFACT));
        allowedParentChildRelations.put(StandardRoles.ASIDE,
                Arrays.asList(StandardRoles.DOCUMENT, StandardRoles.DOCUMENTFRAGMENT, StandardRoles.PART,
                        StandardRoles.ART, StandardRoles.DIV, StandardRoles.SECT, StandardRoles.TOC,
                        StandardRoles.BLOCKQUOTE, StandardRoles.NONSTRUCT, StandardRoles.PRIVATE, StandardRoles.P,
                        StandardRoles.NOTE, StandardRoles.CODE, NUMBERED_HEADER, StandardRoles.H, StandardRoles.LBL,
                        StandardRoles.LINK, StandardRoles.REFERENCE, StandardRoles.ANNOT, StandardRoles.FORM,
                        StandardRoles.FENOTE, StandardRoles.INDEX, StandardRoles.L, StandardRoles.TABLE,
                        StandardRoles.CAPTION, StandardRoles.FIGURE, StandardRoles.FORMULA, StandardRoles.ARTIFACT,
                        ACTUAL_CONTENT));
        allowedParentChildRelations.put(StandardRoles.BLOCKQUOTE,
                Arrays.asList(StandardRoles.DOCUMENT, StandardRoles.DOCUMENTFRAGMENT, StandardRoles.PART,
                        StandardRoles.ART, StandardRoles.DIV, StandardRoles.SECT, StandardRoles.TOC,
                        StandardRoles.BLOCKQUOTE, StandardRoles.NONSTRUCT, StandardRoles.PRIVATE, StandardRoles.P,
                        StandardRoles.NOTE, StandardRoles.CODE, NUMBERED_HEADER, StandardRoles.H, StandardRoles.TITLE,
                        StandardRoles.LBL, StandardRoles.LINK, StandardRoles.REFERENCE, StandardRoles.ANNOT,
                        StandardRoles.FORM, StandardRoles.FENOTE, StandardRoles.INDEX, StandardRoles.L,
                        StandardRoles.TABLE, StandardRoles.CAPTION, StandardRoles.FIGURE, StandardRoles.FORMULA,
                        StandardRoles.ARTIFACT, ACTUAL_CONTENT));
        allowedParentChildRelations.put(StandardRoles.NONSTRUCT,
                Arrays.asList(StandardRoles.DOCUMENT, StandardRoles.DOCUMENTFRAGMENT, StandardRoles.PART,
                        StandardRoles.ART, StandardRoles.DIV, StandardRoles.SECT, StandardRoles.TOC, StandardRoles.TOCI,
                        StandardRoles.ASIDE, StandardRoles.BLOCKQUOTE, StandardRoles.NONSTRUCT, StandardRoles.PRIVATE,
                        StandardRoles.P, StandardRoles.NOTE, StandardRoles.CODE, NUMBERED_HEADER, StandardRoles.H,
                        StandardRoles.TITLE, StandardRoles.SUB, StandardRoles.LBL, StandardRoles.EM,
                        StandardRoles.STRONG, StandardRoles.SPAN, StandardRoles.QUOTE, StandardRoles.LINK,
                        StandardRoles.REFERENCE, StandardRoles.ANNOT, StandardRoles.FORM, StandardRoles.RUBY,
                        StandardRoles.RB, StandardRoles.RT, StandardRoles.RP, StandardRoles.WARICHU, StandardRoles.WT,
                        StandardRoles.WP, StandardRoles.FENOTE, StandardRoles.INDEX, StandardRoles.L, StandardRoles.LI,
                        StandardRoles.LBODY, StandardRoles.BIBENTRY, StandardRoles.TABLE, StandardRoles.TR,
                        StandardRoles.TH, StandardRoles.TD, StandardRoles.THEAD, StandardRoles.TBODY,
                        StandardRoles.TFOOT, StandardRoles.CAPTION, StandardRoles.FIGURE, StandardRoles.FORMULA,
                        StandardRoles.ARTIFACT, ACTUAL_CONTENT));
        allowedParentChildRelations.put(StandardRoles.PRIVATE,
                Arrays.asList(StandardRoles.DOCUMENT, StandardRoles.DOCUMENTFRAGMENT, StandardRoles.PART,
                        StandardRoles.ART, StandardRoles.DIV, StandardRoles.SECT, StandardRoles.TOC, StandardRoles.TOCI,
                        StandardRoles.ASIDE, StandardRoles.BLOCKQUOTE, StandardRoles.NONSTRUCT, StandardRoles.PRIVATE,
                        StandardRoles.P, StandardRoles.NOTE, StandardRoles.CODE, NUMBERED_HEADER, StandardRoles.H,
                        StandardRoles.TITLE, StandardRoles.SUB, StandardRoles.LBL, StandardRoles.EM,
                        StandardRoles.STRONG, StandardRoles.SPAN, StandardRoles.QUOTE, StandardRoles.LINK,
                        StandardRoles.REFERENCE, StandardRoles.ANNOT, StandardRoles.FORM, StandardRoles.RUBY,
                        StandardRoles.RB, StandardRoles.RT, StandardRoles.RP, StandardRoles.WARICHU, StandardRoles.WT,
                        StandardRoles.WP, StandardRoles.FENOTE, StandardRoles.INDEX, StandardRoles.L, StandardRoles.LI,
                        StandardRoles.LBODY, StandardRoles.BIBENTRY, StandardRoles.TABLE, StandardRoles.TR,
                        StandardRoles.TH, StandardRoles.TD, StandardRoles.THEAD, StandardRoles.TBODY,
                        StandardRoles.TFOOT, StandardRoles.CAPTION, StandardRoles.FIGURE, StandardRoles.FORMULA,
                        StandardRoles.ARTIFACT, ACTUAL_CONTENT));
        allowedParentChildRelations.put(StandardRoles.TITLE,
                Arrays.asList(StandardRoles.PART, StandardRoles.DIV, StandardRoles.ASIDE, StandardRoles.NONSTRUCT,
                        StandardRoles.PRIVATE, StandardRoles.P, StandardRoles.NOTE, StandardRoles.CODE,
                        StandardRoles.LBL, StandardRoles.EM, StandardRoles.STRONG, StandardRoles.SPAN,
                        StandardRoles.QUOTE, StandardRoles.LINK, StandardRoles.REFERENCE, StandardRoles.ANNOT,
                        StandardRoles.FORM, StandardRoles.RUBY, StandardRoles.WARICHU, StandardRoles.FENOTE,
                        StandardRoles.L, StandardRoles.BIBENTRY, StandardRoles.TABLE, StandardRoles.CAPTION,
                        StandardRoles.FIGURE, StandardRoles.FORMULA, StandardRoles.ARTIFACT, ACTUAL_CONTENT));
        allowedParentChildRelations.put(StandardRoles.SUB,
                Arrays.asList(StandardRoles.NONSTRUCT, StandardRoles.PRIVATE, StandardRoles.NOTE, StandardRoles.CODE,
                        StandardRoles.LBL, StandardRoles.EM, StandardRoles.STRONG, StandardRoles.SPAN,
                        StandardRoles.QUOTE, StandardRoles.LINK, StandardRoles.REFERENCE, StandardRoles.ANNOT,
                        StandardRoles.FORM, StandardRoles.RUBY, StandardRoles.WARICHU, StandardRoles.FENOTE,
                        StandardRoles.L, StandardRoles.BIBENTRY, StandardRoles.FIGURE, StandardRoles.FORMULA,
                        StandardRoles.ARTIFACT, ACTUAL_CONTENT));
        allowedParentChildRelations.put(StandardRoles.P,
                Arrays.asList(StandardRoles.NONSTRUCT, StandardRoles.PRIVATE, StandardRoles.NOTE, StandardRoles.CODE,
                        StandardRoles.SUB, StandardRoles.LBL, StandardRoles.EM, StandardRoles.STRONG,
                        StandardRoles.SPAN, StandardRoles.QUOTE, StandardRoles.LINK, StandardRoles.REFERENCE,
                        StandardRoles.ANNOT, StandardRoles.FORM, StandardRoles.RUBY, StandardRoles.WARICHU,
                        StandardRoles.FENOTE, StandardRoles.L, StandardRoles.BIBENTRY, StandardRoles.TABLE,
                        StandardRoles.FIGURE, StandardRoles.FORMULA, StandardRoles.ARTIFACT, ACTUAL_CONTENT));
        allowedParentChildRelations.put(StandardRoles.NOTE,
                Arrays.asList(StandardRoles.DOCUMENTFRAGMENT, StandardRoles.PART, StandardRoles.ART, StandardRoles.DIV,
                        StandardRoles.SECT, StandardRoles.ASIDE, StandardRoles.BLOCKQUOTE, StandardRoles.NONSTRUCT,
                        StandardRoles.PRIVATE, StandardRoles.P, StandardRoles.NOTE, StandardRoles.CODE,
                        StandardRoles.SUB, StandardRoles.LBL, StandardRoles.EM, StandardRoles.STRONG,
                        StandardRoles.SPAN, StandardRoles.QUOTE, StandardRoles.LINK, StandardRoles.REFERENCE,
                        StandardRoles.ANNOT, StandardRoles.FORM, StandardRoles.RUBY, StandardRoles.WARICHU,
                        StandardRoles.FENOTE, StandardRoles.INDEX, StandardRoles.L, StandardRoles.BIBENTRY,
                        StandardRoles.TABLE, StandardRoles.CAPTION, StandardRoles.FIGURE, StandardRoles.FORMULA,
                        StandardRoles.ARTIFACT, ACTUAL_CONTENT));
        allowedParentChildRelations.put(StandardRoles.CODE,
                Arrays.asList(StandardRoles.DOCUMENTFRAGMENT, StandardRoles.PART, StandardRoles.DIV,
                        StandardRoles.NONSTRUCT, StandardRoles.PRIVATE, StandardRoles.NOTE, StandardRoles.EM,
                        StandardRoles.STRONG, StandardRoles.SPAN, StandardRoles.LINK, StandardRoles.REFERENCE,
                        StandardRoles.ANNOT, StandardRoles.FENOTE, StandardRoles.BIBENTRY, StandardRoles.ARTIFACT,
                        ACTUAL_CONTENT));
        allowedParentChildRelations.put(NUMBERED_HEADER,
                Arrays.asList(StandardRoles.ART, StandardRoles.SECT, StandardRoles.NONSTRUCT, StandardRoles.PRIVATE,
                        StandardRoles.NOTE, StandardRoles.CODE, StandardRoles.SUB, StandardRoles.LBL, StandardRoles.EM,
                        StandardRoles.STRONG, StandardRoles.SPAN, StandardRoles.QUOTE, StandardRoles.LINK,
                        StandardRoles.REFERENCE, StandardRoles.ANNOT, StandardRoles.FORM, StandardRoles.RUBY,
                        StandardRoles.WARICHU, StandardRoles.FENOTE, StandardRoles.BIBENTRY, StandardRoles.FIGURE,
                        StandardRoles.FORMULA, StandardRoles.ARTIFACT, ACTUAL_CONTENT));
        allowedParentChildRelations.put(StandardRoles.H,
                Arrays.asList(StandardRoles.ART, StandardRoles.SECT, StandardRoles.NONSTRUCT, StandardRoles.PRIVATE,
                        StandardRoles.NOTE, StandardRoles.CODE, StandardRoles.SUB, StandardRoles.LBL, StandardRoles.EM,
                        StandardRoles.STRONG, StandardRoles.SPAN, StandardRoles.QUOTE, StandardRoles.LINK,
                        StandardRoles.REFERENCE, StandardRoles.ANNOT, StandardRoles.FORM, StandardRoles.RUBY,
                        StandardRoles.WARICHU, StandardRoles.FENOTE, StandardRoles.BIBENTRY, StandardRoles.FIGURE,
                        StandardRoles.FORMULA, StandardRoles.ARTIFACT, ACTUAL_CONTENT));
        allowedParentChildRelations.put(StandardRoles.LBL,
                Arrays.asList(StandardRoles.NONSTRUCT, StandardRoles.PRIVATE, StandardRoles.NOTE, StandardRoles.CODE,
                        StandardRoles.SUB, StandardRoles.EM, StandardRoles.STRONG, StandardRoles.SPAN,
                        StandardRoles.QUOTE, StandardRoles.LINK, StandardRoles.REFERENCE, StandardRoles.ANNOT,
                        StandardRoles.FORM, StandardRoles.RUBY, StandardRoles.WARICHU, StandardRoles.FENOTE,
                        StandardRoles.BIBENTRY, StandardRoles.FIGURE, StandardRoles.FORMULA, StandardRoles.ARTIFACT,
                        ACTUAL_CONTENT));
        allowedParentChildRelations.put(StandardRoles.EM,
                Arrays.asList(StandardRoles.NONSTRUCT, StandardRoles.PRIVATE, StandardRoles.NOTE, StandardRoles.CODE,
                        StandardRoles.SUB, StandardRoles.LBL, StandardRoles.EM, StandardRoles.STRONG,
                        StandardRoles.SPAN, StandardRoles.QUOTE, StandardRoles.LINK, StandardRoles.REFERENCE,
                        StandardRoles.ANNOT, StandardRoles.FORM, StandardRoles.RUBY, StandardRoles.WARICHU,
                        StandardRoles.FENOTE, StandardRoles.BIBENTRY, StandardRoles.FIGURE, StandardRoles.FORMULA,
                        StandardRoles.ARTIFACT, ACTUAL_CONTENT));
        allowedParentChildRelations.put(StandardRoles.STRONG,
                Arrays.asList(StandardRoles.NONSTRUCT, StandardRoles.PRIVATE, StandardRoles.NOTE, StandardRoles.CODE,
                        StandardRoles.SUB, StandardRoles.LBL, StandardRoles.EM, StandardRoles.STRONG,
                        StandardRoles.SPAN, StandardRoles.QUOTE, StandardRoles.LINK, StandardRoles.REFERENCE,
                        StandardRoles.ANNOT, StandardRoles.FORM, StandardRoles.RUBY, StandardRoles.WARICHU,
                        StandardRoles.FENOTE, StandardRoles.BIBENTRY, StandardRoles.FIGURE, StandardRoles.FORMULA,
                        StandardRoles.ARTIFACT, ACTUAL_CONTENT));
        allowedParentChildRelations.put(StandardRoles.SPAN,
                Arrays.asList(StandardRoles.NONSTRUCT, StandardRoles.PRIVATE, StandardRoles.NOTE, StandardRoles.CODE,
                        StandardRoles.SUB, StandardRoles.LBL, StandardRoles.EM, StandardRoles.STRONG,
                        StandardRoles.SPAN, StandardRoles.QUOTE, StandardRoles.LINK, StandardRoles.REFERENCE,
                        StandardRoles.ANNOT, StandardRoles.FORM, StandardRoles.RUBY, StandardRoles.WARICHU,
                        StandardRoles.FENOTE, StandardRoles.BIBENTRY, StandardRoles.FIGURE, StandardRoles.FORMULA,
                        StandardRoles.ARTIFACT, ACTUAL_CONTENT));
        allowedParentChildRelations.put(StandardRoles.QUOTE,
                Arrays.asList(StandardRoles.NONSTRUCT, StandardRoles.PRIVATE, StandardRoles.NOTE, StandardRoles.CODE,
                        StandardRoles.SUB, StandardRoles.LBL, StandardRoles.EM, StandardRoles.STRONG,
                        StandardRoles.SPAN, StandardRoles.QUOTE, StandardRoles.LINK, StandardRoles.REFERENCE,
                        StandardRoles.ANNOT, StandardRoles.FORM, StandardRoles.RUBY, StandardRoles.WARICHU,
                        StandardRoles.FENOTE, StandardRoles.BIBENTRY, StandardRoles.FIGURE, StandardRoles.FORMULA,
                        StandardRoles.ARTIFACT, ACTUAL_CONTENT));
        allowedParentChildRelations.put(StandardRoles.LINK,
                Arrays.asList(StandardRoles.DOCUMENTFRAGMENT, StandardRoles.PART, StandardRoles.ART, StandardRoles.DIV,
                        StandardRoles.SECT, StandardRoles.ASIDE, StandardRoles.BLOCKQUOTE, StandardRoles.NONSTRUCT,
                        StandardRoles.PRIVATE, StandardRoles.P, StandardRoles.NOTE, StandardRoles.CODE, NUMBERED_HEADER,
                        StandardRoles.H, StandardRoles.TITLE, StandardRoles.SUB, StandardRoles.LBL, StandardRoles.EM,
                        StandardRoles.STRONG, StandardRoles.SPAN, StandardRoles.QUOTE, StandardRoles.REFERENCE,
                        StandardRoles.ANNOT, StandardRoles.FORM, StandardRoles.RUBY, StandardRoles.WARICHU,
                        StandardRoles.FENOTE, StandardRoles.L, StandardRoles.BIBENTRY, StandardRoles.TABLE,
                        StandardRoles.CAPTION, StandardRoles.FIGURE, StandardRoles.FORMULA, StandardRoles.ARTIFACT,
                        ACTUAL_CONTENT));
        allowedParentChildRelations.put(StandardRoles.REFERENCE,
                Arrays.asList(StandardRoles.NONSTRUCT, StandardRoles.PRIVATE, StandardRoles.NOTE, StandardRoles.LBL,
                        StandardRoles.EM, StandardRoles.STRONG, StandardRoles.SPAN, StandardRoles.LINK,
                        StandardRoles.ANNOT, StandardRoles.FENOTE, StandardRoles.BIBENTRY, StandardRoles.FIGURE,
                        StandardRoles.ARTIFACT, ACTUAL_CONTENT));
        allowedParentChildRelations.put(StandardRoles.ANNOT,
                Arrays.asList(StandardRoles.DOCUMENTFRAGMENT, StandardRoles.PART, StandardRoles.ART, StandardRoles.DIV,
                        StandardRoles.SECT, StandardRoles.ASIDE, StandardRoles.BLOCKQUOTE, StandardRoles.NONSTRUCT,
                        StandardRoles.P, StandardRoles.NOTE, StandardRoles.CODE, NUMBERED_HEADER, StandardRoles.H,
                        StandardRoles.TITLE, StandardRoles.SUB, StandardRoles.LBL, StandardRoles.EM,
                        StandardRoles.STRONG, StandardRoles.SPAN, StandardRoles.QUOTE, StandardRoles.LINK,
                        StandardRoles.REFERENCE, StandardRoles.ANNOT, StandardRoles.FORM, StandardRoles.RUBY,
                        StandardRoles.WARICHU, StandardRoles.FENOTE, StandardRoles.L, StandardRoles.BIBENTRY,
                        StandardRoles.TABLE, StandardRoles.CAPTION, StandardRoles.FIGURE, StandardRoles.FORMULA,
                        StandardRoles.ARTIFACT, ACTUAL_CONTENT));
        allowedParentChildRelations.put(StandardRoles.FORM,
                Arrays.asList(StandardRoles.PART, StandardRoles.DIV, StandardRoles.NONSTRUCT, StandardRoles.PRIVATE,
                        StandardRoles.NOTE, StandardRoles.CODE, StandardRoles.LBL, StandardRoles.REFERENCE,
                        StandardRoles.FENOTE, StandardRoles.L, StandardRoles.BIBENTRY, StandardRoles.TABLE,
                        StandardRoles.CAPTION, StandardRoles.FIGURE, StandardRoles.FORMULA, StandardRoles.ARTIFACT,
                        ACTUAL_CONTENT));
        allowedParentChildRelations.put(StandardRoles.RUBY,
                Arrays.asList(StandardRoles.NONSTRUCT, StandardRoles.PRIVATE, StandardRoles.RB, StandardRoles.RT,
                        StandardRoles.RP, ACTUAL_CONTENT));
        allowedParentChildRelations.put(StandardRoles.RB,
                Arrays.asList(StandardRoles.NONSTRUCT, StandardRoles.PRIVATE, StandardRoles.SUB, StandardRoles.EM,
                        StandardRoles.STRONG, StandardRoles.SPAN, StandardRoles.QUOTE, StandardRoles.LINK,
                        StandardRoles.REFERENCE, StandardRoles.ANNOT, StandardRoles.FORM, StandardRoles.ARTIFACT,
                        ACTUAL_CONTENT));
        allowedParentChildRelations.put(StandardRoles.RT,
                Arrays.asList(StandardRoles.NONSTRUCT, StandardRoles.PRIVATE, StandardRoles.SUB, StandardRoles.EM,
                        StandardRoles.STRONG, StandardRoles.SPAN, StandardRoles.QUOTE, StandardRoles.LINK,
                        StandardRoles.REFERENCE, StandardRoles.ANNOT, StandardRoles.FORM, StandardRoles.ARTIFACT,
                        ACTUAL_CONTENT));
        allowedParentChildRelations.put(StandardRoles.RP,
                Arrays.asList(StandardRoles.NONSTRUCT, StandardRoles.PRIVATE, StandardRoles.SUB, StandardRoles.EM,
                        StandardRoles.STRONG, StandardRoles.SPAN, StandardRoles.QUOTE, StandardRoles.LINK,
                        StandardRoles.REFERENCE, StandardRoles.ANNOT, StandardRoles.FORM, StandardRoles.ARTIFACT,
                        ACTUAL_CONTENT));
        allowedParentChildRelations.put(StandardRoles.WARICHU,
                Arrays.asList(StandardRoles.NONSTRUCT, StandardRoles.PRIVATE, StandardRoles.WT, StandardRoles.WP,
                        ACTUAL_CONTENT));
        allowedParentChildRelations.put(StandardRoles.WT,
                Arrays.asList(StandardRoles.NONSTRUCT, StandardRoles.PRIVATE, StandardRoles.SUB, StandardRoles.EM,
                        StandardRoles.STRONG, StandardRoles.SPAN, StandardRoles.QUOTE, StandardRoles.LINK,
                        StandardRoles.REFERENCE, StandardRoles.ANNOT, StandardRoles.FORM, StandardRoles.ARTIFACT,
                        ACTUAL_CONTENT));
        allowedParentChildRelations.put(StandardRoles.WP,
                Arrays.asList(ACTUAL_CONTENT, StandardRoles.NONSTRUCT, StandardRoles.PRIVATE, StandardRoles.SUB,
                        StandardRoles.EM, StandardRoles.STRONG, StandardRoles.SPAN, StandardRoles.QUOTE,
                        StandardRoles.LINK, StandardRoles.REFERENCE, StandardRoles.ANNOT, StandardRoles.FORM,
                        StandardRoles.FIGURE, StandardRoles.ARTIFACT, ACTUAL_CONTENT));
        allowedParentChildRelations.put(StandardRoles.FENOTE,
                Arrays.asList(StandardRoles.DOCUMENTFRAGMENT, StandardRoles.PART, StandardRoles.ART, StandardRoles.DIV,
                        StandardRoles.SECT, StandardRoles.ASIDE, StandardRoles.BLOCKQUOTE, StandardRoles.NONSTRUCT,
                        StandardRoles.PRIVATE, StandardRoles.P, StandardRoles.NOTE, StandardRoles.CODE,
                        StandardRoles.SUB, StandardRoles.LBL, StandardRoles.EM, StandardRoles.STRONG,
                        StandardRoles.SPAN, StandardRoles.QUOTE, StandardRoles.LINK, StandardRoles.REFERENCE,
                        StandardRoles.ANNOT, StandardRoles.FORM, StandardRoles.RUBY, StandardRoles.WARICHU,
                        StandardRoles.FENOTE, StandardRoles.L, StandardRoles.TABLE, StandardRoles.CAPTION,
                        StandardRoles.FIGURE, StandardRoles.FORMULA, StandardRoles.ARTIFACT, ACTUAL_CONTENT));
        allowedParentChildRelations.put(StandardRoles.INDEX,
                Arrays.asList(StandardRoles.PART, StandardRoles.DIV, StandardRoles.SECT, StandardRoles.NONSTRUCT,
                        StandardRoles.PRIVATE, StandardRoles.P, StandardRoles.NOTE, NUMBERED_HEADER, StandardRoles.H,
                        StandardRoles.REFERENCE, StandardRoles.ANNOT, StandardRoles.FENOTE, StandardRoles.L,
                        StandardRoles.TABLE, StandardRoles.CAPTION, StandardRoles.FIGURE, StandardRoles.FORMULA,
                        StandardRoles.ARTIFACT));
        allowedParentChildRelations.put(StandardRoles.L,
                Arrays.asList(StandardRoles.NONSTRUCT, StandardRoles.PRIVATE, StandardRoles.L, StandardRoles.LI,
                        StandardRoles.CAPTION, StandardRoles.ARTIFACT));
        allowedParentChildRelations.put(StandardRoles.LI,
                Arrays.asList(StandardRoles.DIV, StandardRoles.NONSTRUCT, StandardRoles.PRIVATE, StandardRoles.LBL,
                        StandardRoles.LBODY, StandardRoles.ARTIFACT, ACTUAL_CONTENT));
        allowedParentChildRelations.put(StandardRoles.LBODY,
                Arrays.asList(StandardRoles.PART, StandardRoles.ART, StandardRoles.DIV, StandardRoles.SECT,
                        StandardRoles.ASIDE, StandardRoles.BLOCKQUOTE, StandardRoles.NONSTRUCT, StandardRoles.PRIVATE,
                        StandardRoles.P, StandardRoles.NOTE, StandardRoles.CODE, NUMBERED_HEADER, StandardRoles.H,
                        StandardRoles.SUB, StandardRoles.EM, StandardRoles.STRONG, StandardRoles.SPAN,
                        StandardRoles.QUOTE, StandardRoles.LINK, StandardRoles.REFERENCE, StandardRoles.ANNOT,
                        StandardRoles.FORM, StandardRoles.RUBY, StandardRoles.WARICHU, StandardRoles.FENOTE,
                        StandardRoles.INDEX, StandardRoles.L, StandardRoles.BIBENTRY, StandardRoles.TABLE,
                        StandardRoles.CAPTION, StandardRoles.FIGURE, StandardRoles.FORMULA, StandardRoles.ARTIFACT,
                        ACTUAL_CONTENT));
        allowedParentChildRelations.put(StandardRoles.BIBENTRY,
                Arrays.asList(StandardRoles.PART, StandardRoles.DIV, StandardRoles.NONSTRUCT, StandardRoles.PRIVATE,
                        StandardRoles.P, StandardRoles.NOTE, StandardRoles.LBL, StandardRoles.EM, StandardRoles.STRONG,
                        StandardRoles.SPAN, StandardRoles.LINK, StandardRoles.REFERENCE, StandardRoles.ANNOT,
                        StandardRoles.FENOTE, StandardRoles.FIGURE, StandardRoles.ARTIFACT, ACTUAL_CONTENT));
        allowedParentChildRelations.put(StandardRoles.TABLE,
                Arrays.asList(StandardRoles.NONSTRUCT, StandardRoles.PRIVATE, StandardRoles.TR, StandardRoles.THEAD,
                        StandardRoles.TBODY, StandardRoles.TFOOT, StandardRoles.CAPTION, StandardRoles.ARTIFACT));
        allowedParentChildRelations.put(StandardRoles.TR,
                Arrays.asList(StandardRoles.NONSTRUCT, StandardRoles.PRIVATE, StandardRoles.TH, StandardRoles.TD,
                        StandardRoles.ARTIFACT));
        allowedParentChildRelations.put(StandardRoles.TH,
                Arrays.asList(StandardRoles.ART, StandardRoles.DIV, StandardRoles.SECT, StandardRoles.NONSTRUCT,
                        StandardRoles.PRIVATE, StandardRoles.P, StandardRoles.NOTE, StandardRoles.CODE, NUMBERED_HEADER,
                        StandardRoles.H, StandardRoles.LBL, StandardRoles.EM, StandardRoles.STRONG, StandardRoles.SPAN,
                        StandardRoles.QUOTE, StandardRoles.LINK, StandardRoles.REFERENCE, StandardRoles.ANNOT,
                        StandardRoles.FORM, StandardRoles.RUBY, StandardRoles.WARICHU, StandardRoles.FENOTE,
                        StandardRoles.INDEX, StandardRoles.L, StandardRoles.BIBENTRY, StandardRoles.TABLE,
                        StandardRoles.FIGURE, StandardRoles.FORMULA, StandardRoles.ARTIFACT, ACTUAL_CONTENT));
        allowedParentChildRelations.put(StandardRoles.TD,
                Arrays.asList(StandardRoles.ART, StandardRoles.DIV, StandardRoles.SECT, StandardRoles.NONSTRUCT,
                        StandardRoles.PRIVATE, StandardRoles.P, StandardRoles.NOTE, StandardRoles.CODE, NUMBERED_HEADER,
                        StandardRoles.H, StandardRoles.LBL, StandardRoles.EM, StandardRoles.STRONG, StandardRoles.SPAN,
                        StandardRoles.QUOTE, StandardRoles.LINK, StandardRoles.REFERENCE, StandardRoles.ANNOT,
                        StandardRoles.FORM, StandardRoles.RUBY, StandardRoles.WARICHU, StandardRoles.FENOTE,
                        StandardRoles.INDEX, StandardRoles.L, StandardRoles.BIBENTRY, StandardRoles.TABLE,
                        StandardRoles.FIGURE, StandardRoles.FORMULA, StandardRoles.ARTIFACT, ACTUAL_CONTENT));
        allowedParentChildRelations.put(StandardRoles.THEAD,
                Arrays.asList(StandardRoles.NONSTRUCT, StandardRoles.PRIVATE, StandardRoles.TR,
                        StandardRoles.ARTIFACT));
        allowedParentChildRelations.put(StandardRoles.TBODY,
                Arrays.asList(StandardRoles.NONSTRUCT, StandardRoles.PRIVATE, StandardRoles.TR,
                        StandardRoles.ARTIFACT));
        allowedParentChildRelations.put(StandardRoles.TFOOT,
                Arrays.asList(StandardRoles.NONSTRUCT, StandardRoles.PRIVATE, StandardRoles.TR,
                        StandardRoles.ARTIFACT));
        allowedParentChildRelations.put(StandardRoles.CAPTION,
                Arrays.asList(StandardRoles.DOCUMENTFRAGMENT, StandardRoles.PART, StandardRoles.ART, StandardRoles.DIV,
                        StandardRoles.SECT, StandardRoles.ASIDE, StandardRoles.BLOCKQUOTE, StandardRoles.NONSTRUCT,
                        StandardRoles.PRIVATE, StandardRoles.P, StandardRoles.NOTE, StandardRoles.CODE, NUMBERED_HEADER,
                        StandardRoles.H, StandardRoles.SUB, StandardRoles.LBL, StandardRoles.EM, StandardRoles.STRONG,
                        StandardRoles.SPAN, StandardRoles.QUOTE, StandardRoles.LINK, StandardRoles.REFERENCE,
                        StandardRoles.ANNOT, StandardRoles.FORM, StandardRoles.RUBY, StandardRoles.WARICHU,
                        StandardRoles.FENOTE, StandardRoles.INDEX, StandardRoles.L, StandardRoles.BIBENTRY,
                        StandardRoles.TABLE, StandardRoles.FIGURE, StandardRoles.FORMULA, StandardRoles.ARTIFACT,
                        ACTUAL_CONTENT));
        allowedParentChildRelations.put(StandardRoles.FIGURE,
                Arrays.asList(StandardRoles.PART, StandardRoles.ART, StandardRoles.DIV, StandardRoles.SECT,
                        StandardRoles.ASIDE, StandardRoles.BLOCKQUOTE, StandardRoles.NONSTRUCT, StandardRoles.PRIVATE,
                        StandardRoles.P, StandardRoles.NOTE, StandardRoles.CODE, NUMBERED_HEADER, StandardRoles.H,
                        StandardRoles.SUB, StandardRoles.LBL, StandardRoles.EM, StandardRoles.STRONG,
                        StandardRoles.SPAN, StandardRoles.QUOTE, StandardRoles.LINK, StandardRoles.REFERENCE,
                        StandardRoles.ANNOT, StandardRoles.FORM, StandardRoles.RUBY, StandardRoles.WARICHU,
                        StandardRoles.FENOTE, StandardRoles.INDEX, StandardRoles.L, StandardRoles.BIBENTRY,
                        StandardRoles.TABLE, StandardRoles.CAPTION, StandardRoles.FIGURE, StandardRoles.FORMULA,
                        StandardRoles.ARTIFACT, ACTUAL_CONTENT));
        allowedParentChildRelations.put(StandardRoles.FORMULA,
                Arrays.asList(StandardRoles.PART, StandardRoles.DIV, StandardRoles.ASIDE, StandardRoles.BLOCKQUOTE,
                        StandardRoles.NONSTRUCT, StandardRoles.PRIVATE, StandardRoles.P, StandardRoles.NOTE,
                        StandardRoles.CODE, NUMBERED_HEADER, StandardRoles.H, StandardRoles.SUB, StandardRoles.LBL,
                        StandardRoles.EM, StandardRoles.STRONG, StandardRoles.SPAN, StandardRoles.QUOTE,
                        StandardRoles.LINK, StandardRoles.REFERENCE, StandardRoles.ANNOT, StandardRoles.FORM,
                        StandardRoles.RUBY, StandardRoles.WARICHU, StandardRoles.FENOTE, StandardRoles.INDEX,
                        StandardRoles.L, StandardRoles.BIBENTRY, StandardRoles.TABLE, StandardRoles.CAPTION,
                        StandardRoles.FIGURE, StandardRoles.FORMULA, StandardRoles.ARTIFACT, ACTUAL_CONTENT));
        allowedParentChildRelations.put(StandardRoles.ARTIFACT,
                Arrays.asList(StandardRoles.DOCUMENT, StandardRoles.DOCUMENTFRAGMENT, StandardRoles.PART,
                        StandardRoles.ART, StandardRoles.DIV, StandardRoles.SECT, StandardRoles.TOC, StandardRoles.TOCI,
                        StandardRoles.ASIDE, StandardRoles.BLOCKQUOTE, StandardRoles.NONSTRUCT, StandardRoles.PRIVATE,
                        StandardRoles.P, StandardRoles.NOTE, StandardRoles.CODE, NUMBERED_HEADER, StandardRoles.H,
                        StandardRoles.TITLE, StandardRoles.SUB, StandardRoles.LBL, StandardRoles.EM,
                        StandardRoles.STRONG, StandardRoles.SPAN, StandardRoles.QUOTE, StandardRoles.LINK,
                        StandardRoles.REFERENCE, StandardRoles.ANNOT, StandardRoles.FORM, StandardRoles.RUBY,
                        StandardRoles.RB, StandardRoles.RT, StandardRoles.RP, StandardRoles.WARICHU, StandardRoles.WT,
                        StandardRoles.WP, StandardRoles.FENOTE, StandardRoles.INDEX, StandardRoles.L, StandardRoles.LI,
                        StandardRoles.LBODY, StandardRoles.BIBENTRY, StandardRoles.TABLE, StandardRoles.TR,
                        StandardRoles.TH, StandardRoles.TD, StandardRoles.THEAD, StandardRoles.TBODY,
                        StandardRoles.TFOOT, StandardRoles.CAPTION, StandardRoles.FIGURE, StandardRoles.FORMULA,
                        StandardRoles.ARTIFACT, ACTUAL_CONTENT));

    }

    /**
     * Checks if the given parent-child relation is allowed.
     *
     * @param parentRole The parent role.
     * @param childRole  The child role.
     * @return {@code true} if the relation is allowed, {@code false} otherwise.
     */
    public boolean isRelationAllowed(String parentRole, String childRole) {
        Collection<String> allowedChildren = allowedParentChildRelations.get(normalizeRole(parentRole));
        if (allowedChildren != null) {
            return allowedChildren.contains(normalizeRole(childRole));
        }
        throw new IllegalArgumentException("parentRole " + parentRole + " is not a valid structure tree role");
    }

    /**
     * Checks if the given parent role allows content.
     *
     * @param parentRole The parent role.
     * @return {@code true} if the parent role allows content, {@code false} otherwise.
     */
    public boolean isContentAllowedInRole(String parentRole) {
        Collection<String> allowedChildren = allowedParentChildRelations.get(normalizeRole(parentRole));
        if (allowedChildren != null) {
            return allowedChildren.contains(ACTUAL_CONTENT);
        }
        throw new IllegalArgumentException("parentRole " + parentRole + " is not a valid structure tree role");
    }

    /**
     * Normalizes the role.
     *
     * @param role The role to normalize.
     * @return The normalized role.
     */
    public String normalizeRole(String role) {
        if (role == null) {
            return null;
        }
        if (numberedHeaderPattern.matcher(role).matches()) {
            return NUMBERED_HEADER;
        }
        return role;
    }
}
