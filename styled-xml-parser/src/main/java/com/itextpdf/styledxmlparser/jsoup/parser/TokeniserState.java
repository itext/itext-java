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
package com.itextpdf.styledxmlparser.jsoup.parser;

import com.itextpdf.styledxmlparser.jsoup.nodes.DocumentType;

/**
 * States and transition activations for the Tokeniser.
 */
abstract class TokeniserState {
    static TokeniserState Data = new DataTS();
    
    static TokeniserState CharacterReferenceInData = new CharacterReferenceInDataTS();
    
    static TokeniserState Rcdata = new RcDataTS();
    
    static TokeniserState CharacterReferenceInRcdata = new CharacterReferenceInRcdataTS();
    
    static TokeniserState Rawtext = new RawTextTS();
    
    static TokeniserState ScriptData = new ScriptDataTS();
    
    static TokeniserState PLAINTEXT = new PlainTextTS();
    
    static TokeniserState TagOpen = new TagOpenTS();
    
    static TokeniserState EndTagOpen = new EndTagOpenTS();
    
    static TokeniserState TagName = new TagNameTS();
    
    static TokeniserState RcdataLessthanSign = new RcDataLessThanSignTS();
    
    static TokeniserState RCDATAEndTagOpen = new RcDataEndTagOpenTS();
    
    static TokeniserState RCDATAEndTagName = new RcDataEndTagNameTS();
    
    static TokeniserState RawtextLessthanSign = new RawTextLessThanSignTS();
    
    static TokeniserState RawtextEndTagOpen = new RawTextEndTagOpenTS();
    
    static TokeniserState RawtextEndTagName = new RawTextEndTagNameTS();
    
    static TokeniserState ScriptDataLessthanSign = new ScriptDataLessThanSignTS();
    
    static TokeniserState ScriptDataEndTagOpen = new ScriptDataEndTagOpenTS();
    
    static TokeniserState ScriptDataEndTagName = new ScriptDataEndTagNameTS();
    
    static TokeniserState ScriptDataEscapeStart = new ScriptDataEscapeStartTS();
    
    static TokeniserState ScriptDataEscapeStartDash = new ScriptDataEscapeStartDashTS();
    
    static TokeniserState ScriptDataEscaped = new ScriptDataEscapedTS();
    
    static TokeniserState ScriptDataEscapedDash = new ScriptDataEscapedDashTS();
    
    static TokeniserState ScriptDataEscapedDashDash = new ScriptDataEscapedDashDashTS();
    
    static TokeniserState ScriptDataEscapedLessthanSign = new ScriptDataEscapedLessThanSignTS();
    
    static TokeniserState ScriptDataEscapedEndTagOpen = new ScriptDataEscapedEndTagOpenTS();
    
    static TokeniserState ScriptDataEscapedEndTagName = new ScriptDataEscapedEndTagNameTS();
    
    static TokeniserState ScriptDataDoubleEscapeStart = new ScriptDataDoubleEscapeStartTS();
    
    static TokeniserState ScriptDataDoubleEscaped = new ScriptDataDoubleEscapedTS();
    
    static TokeniserState ScriptDataDoubleEscapedDash = new ScriptDataDoubleEscapedDashTS();
    
    static TokeniserState ScriptDataDoubleEscapedDashDash = new ScriptDataDoubleEscapedDashDashTS();
    
    static TokeniserState ScriptDataDoubleEscapedLessthanSign = new ScriptDataDoubleEscapedLessThanSignTS();
    
    static TokeniserState ScriptDataDoubleEscapeEnd = new ScriptDataDoubleEscapeEndTS();
    
    static TokeniserState BeforeAttributeName = new BeforeAttributeNameTS();
    
    static TokeniserState AttributeName = new AttributeNameTS();
    
    static TokeniserState AfterAttributeName = new AfterAttributeNameTS();
    
    static TokeniserState BeforeAttributeValue = new BeforeAttributeValueTS();
    
    static TokeniserState AttributeValue_doubleQuoted = new AttributeValueDoubleQuotedTS();
    
    static TokeniserState AttributeValue_singleQuoted = new AttributeValueSingleQuotedTS();
    
    static TokeniserState AttributeValue_unquoted = new AttributeValueUnquotedTS();
    // CharacterReferenceInAttributeValue state handled inline
    static TokeniserState AfterAttributeValue_quoted = new AfterAttributeValueQuotedTS();
    
    static TokeniserState SelfClosingStartTag = new SelfClosingStartTagTS();
    
    static TokeniserState BogusComment = new BogusCommentTS();
    
    static TokeniserState MarkupDeclarationOpen = new MarkupDeclarationOpenTS();
    
    static TokeniserState CommentStart = new CommentStartTS();
    
    static TokeniserState CommentStartDash = new CommentStartDashTS();
    
    static TokeniserState Comment = new CommentTS();
    
    static TokeniserState CommentEndDash = new CommentEndDashTS();
    
    static TokeniserState CommentEnd = new CommentEndTS();
    
    static TokeniserState CommentEndBang = new CommentEndBangTS();
    
    static TokeniserState Doctype = new DocTypeTS();
    
    static TokeniserState BeforeDoctypeName = new BeforeDocTypeNameTS();
    
    static TokeniserState DoctypeName = new DocTypeNameTS();
    
    static TokeniserState AfterDoctypeName = new AfterDocTypeNameTS();
    
    static TokeniserState AfterDoctypePublicKeyword = new AfterDocTypePublicKeywordTS();
    
    static TokeniserState BeforeDoctypePublicIdentifier = new BeforeDocTypePublicIdentifierTS();
    
    static TokeniserState DoctypePublicIdentifier_doubleQuoted = new DocTypePublicIdentifierDoubleQuotedTS();
    
    static TokeniserState DoctypePublicIdentifier_singleQuoted = new DocTypePublicIdentifierSingleQuotedTS();
    
    static TokeniserState AfterDoctypePublicIdentifier = new AfterDocTypePublicIdentifierTS();
    
    static TokeniserState BetweenDoctypePublicAndSystemIdentifiers = new BetweenDocTypePublicAndSystemIdentifiersTS();
    
    static TokeniserState AfterDoctypeSystemKeyword = new AfterDocTypeSystemKeywordTS();
    
    static TokeniserState BeforeDoctypeSystemIdentifier = new BeforeDocTypeSystemIdentifierTS();
    
    static TokeniserState DoctypeSystemIdentifier_doubleQuoted = new DocTypeSystemIdentifierDoubleQuotedTS();
    
    static TokeniserState DoctypeSystemIdentifier_singleQuoted = new DocTypeSystemIdentifierSingleQuotedTS();
    
    static TokeniserState AfterDoctypeSystemIdentifier = new AfterDocTypeSystemIdentifierTS();
    
    static TokeniserState BogusDoctype = new BogusDocTypeTS();
    
    static TokeniserState CdataSection = new CDataSectionTS();


    abstract void read(Tokeniser t, CharacterReader r);

    static final char nullChar = '\u0000';
    // char searches. must be sorted, used in inSorted. MUST update TokenisetStateTest if more arrays are added.
    static final char[] attributeNameCharsSorted = new char[]{nullChar, '\t', '\n', '\f', '\r', ' ', '"', '\'', '/', '<', '=', '>'};
    static final char[] attributeValueUnquoted = new char[]{nullChar, '\t', '\n', '\f', '\r', ' ', '"', '&', '\'', '<', '=', '>', '`'};

    private static final char replacementChar = Tokeniser.replacementChar;
    private static final String replacementStr = String.valueOf(Tokeniser.replacementChar);
    private static final char eof = CharacterReader.EOF;

    /**
     * Handles RawtextEndTagName, ScriptDataEndTagName, and ScriptDataEscapedEndTagName. Same body impl, just
     * different else exit transitions.
     */
    private static void handleDataEndTag(Tokeniser t, CharacterReader r, TokeniserState elseTransition) {
        if (r.matchesLetter()) {
            String name = r.consumeLetterSequence();
            t.tagPending.appendTagName(name);
            t.dataBuffer.append(name);
            return;
        }

        boolean needsExitTransition = false;
        if (t.isAppropriateEndTagToken() && !r.isEmpty()) {
            char c = r.consume();
            switch (c) {
                case '\t':
                case '\n':
                case '\r':
                case '\f':
                case ' ':
                    t.transition(BeforeAttributeName);
                    break;
                case '/':
                    t.transition(SelfClosingStartTag);
                    break;
                case '>':
                    t.emitTagPending();
                    t.transition(Data);
                    break;
                default:
                    t.dataBuffer.append(c);
                    needsExitTransition = true;
            }
        } else {
            needsExitTransition = true;
        }

        if (needsExitTransition) {
            t.emit("</");
            t.emit(t.dataBuffer);
            t.transition(elseTransition);
        }
    }

    private static void readRawData(Tokeniser t, CharacterReader r, TokeniserState current, TokeniserState advance) {
        switch (r.current()) {
            case '<':
                t.advanceTransition(advance);
                break;
            case nullChar:
                t.error(current);
                r.advance();
                t.emit(replacementChar);
                break;
            case eof:
                t.emit(new Token.EOF());
                break;
            default:
                String data = r.consumeRawData();
                t.emit(data);
                break;
        }
    }

    private static void readCharRef(Tokeniser t, TokeniserState advance) {
        int[] c = t.consumeCharacterReference(null, false);
        if (c == null)
            t.emit('&');
        else
            t.emit(c);
        t.transition(advance);
    }

    private static void readEndTag(Tokeniser t, CharacterReader r, TokeniserState a, TokeniserState b) {
        if (r.matchesLetter()) {
            t.createTagPending(false);
            t.transition(a);
        } else {
            t.emit("</");
            t.transition(b);
        }
    }

    private static void handleDataDoubleEscapeTag(Tokeniser t, CharacterReader r, TokeniserState primary, TokeniserState fallback) {
        if (r.matchesLetter()) {
            String name = r.consumeLetterSequence();
            t.dataBuffer.append(name);
            t.emit(name);
            return;
        }

        char c = r.consume();
        switch (c) {
            case '\t':
            case '\n':
            case '\r':
            case '\f':
            case ' ':
            case '/':
            case '>':
                if (t.dataBuffer.toString().equals("script"))
                    t.transition(primary);
                else
                    t.transition(fallback);
                t.emit(c);
                break;
            default:
                r.unconsume();
                t.transition(fallback);
        }
    }

    private static final class DataTS extends TokeniserState {

        @Override
        public String toString() {
            return "Data";
        }

        // in data state, gather characters until a character reference or tag is found
        void read(Tokeniser t, CharacterReader r) {
            switch (r.current()) {
                case '&':
                    t.advanceTransition(CharacterReferenceInData);
                    break;
                case '<':
                    t.advanceTransition(TagOpen);
                    break;
                case nullChar:
                    t.error(this); // NOT replacement character (oddly?)
                    t.emit(r.consume());
                    break;
                case eof:
                    t.emit(new Token.EOF());
                    break;
                default:
                    String data = r.consumeData();
                    t.emit(data);
                    break;
            }
        }
    }

    private static final class CharacterReferenceInDataTS extends TokeniserState {

        @Override
        public String toString() {
            return "CharacterReferenceInData";
        }

        // from & in data
        void read(Tokeniser t, CharacterReader r) {
            readCharRef(t, Data);
        }
    }

    private static final class RcDataTS extends TokeniserState {

        @Override
        public String toString() {
            return "Rcdata";
        }

        /// handles data in title, textarea etc
        void read(Tokeniser t, CharacterReader r) {
            switch (r.current()) {
                case '&':
                    t.advanceTransition(CharacterReferenceInRcdata);
                    break;
                case '<':
                    t.advanceTransition(RcdataLessthanSign);
                    break;
                case nullChar:
                    t.error(this);
                    r.advance();
                    t.emit(replacementChar);
                    break;
                case eof:
                    t.emit(new Token.EOF());
                    break;
                default:
                    String data = r.consumeData();
                    t.emit(data);
                    break;
            }
        }
    }

    private static final class CharacterReferenceInRcdataTS extends TokeniserState {

        @Override
        public String toString() {
            return "CharacterReferenceInRcdata";
        }

        void read(Tokeniser t, CharacterReader r) {
            readCharRef(t, Rcdata);
        }
    }

    private static final class RawTextTS extends TokeniserState {

        @Override
        public String toString() {
            return "Rawtext";
        }

        void read(Tokeniser t, CharacterReader r) {
            readRawData(t, r, this, RawtextLessthanSign);
        }
    }

    private static final class ScriptDataTS extends TokeniserState {

        @Override
        public String toString() {
            return "ScriptData";
        }

        void read(Tokeniser t, CharacterReader r) {
            readRawData(t, r, this, ScriptDataLessthanSign);
        }
    }

    private static final class PlainTextTS extends TokeniserState {

        @Override
        public String toString() {
            return "PLAINTEXT";
        }

        void read(Tokeniser t, CharacterReader r) {
            switch (r.current()) {
                case nullChar:
                    t.error(this);
                    r.advance();
                    t.emit(replacementChar);
                    break;
                case eof:
                    t.emit(new Token.EOF());
                    break;
                default:
                    String data = r.consumeTo(nullChar);
                    t.emit(data);
                    break;
            }
        }
    }

    private static final class TagOpenTS extends TokeniserState {

        @Override
        public String toString() {
            return "TagOpen";
        }

        // from < in data
        void read(Tokeniser t, CharacterReader r) {
            switch (r.current()) {
                case '!':
                    t.advanceTransition(MarkupDeclarationOpen);
                    break;
                case '/':
                    t.advanceTransition(EndTagOpen);
                    break;
                case '?':
                    t.createBogusCommentPending();
                    t.advanceTransition(BogusComment);
                    break;
                default:
                    if (r.matchesLetter()) {
                        t.createTagPending(true);
                        t.transition(TagName);
                    } else {
                        t.error(this);
                        t.emit('<'); // char that got us here
                        t.transition(Data);
                    }
                    break;
            }
        }
    }

    private static final class EndTagOpenTS extends TokeniserState {

        @Override
        public String toString() {
            return "EndTagOpen";
        }

        void read(Tokeniser t, CharacterReader r) {
            if (r.isEmpty()) {
                t.eofError(this);
                t.emit("</");
                t.transition(Data);
            } else if (r.matchesLetter()) {
                t.createTagPending(false);
                t.transition(TagName);
            } else if (r.matches('>')) {
                t.error(this);
                t.advanceTransition(Data);
            } else {
                t.error(this);
                t.createBogusCommentPending();
                t.advanceTransition(BogusComment);
            }
        }
    }

    private static final class TagNameTS extends TokeniserState {

        @Override
        public String toString() {
            return "TagName";
        }

        // from < or </ in data, will have start or end tag pending
        void read(Tokeniser t, CharacterReader r) {
            // previous TagOpen state did NOT consume, will have a letter char in current
            //String tagName = r.consumeToAnySorted(tagCharsSorted).toLowerCase();
            String tagName = r.consumeTagName();
            t.tagPending.appendTagName(tagName);

            char c = r.consume();
            switch (c) {
                case '\t':
                case '\n':
                case '\r':
                case '\f':
                case ' ':
                    t.transition(BeforeAttributeName);
                    break;
                case '/':
                    t.transition(SelfClosingStartTag);
                    break;
                case '<': // NOTE: out of spec, but clear author intent
                    r.unconsume();
                    t.error(this);
                    // intended fall through to next >
                case '>':
                    t.emitTagPending();
                    t.transition(Data);
                    break;
                case nullChar: // replacement
                    t.tagPending.appendTagName(replacementStr);
                    break;
                case eof: // should emit pending tag?
                    t.eofError(this);
                    t.transition(Data);
                    break;
                default: // buffer underrun
                    t.tagPending.appendTagName(c);
            }
        }
    }

    private static final class RcDataLessThanSignTS extends TokeniserState {

        @Override
        public String toString() {
            return "RcdataLessthanSign";
        }

        // from < in rcdata
        void read(Tokeniser t, CharacterReader r) {
            if (r.matches('/')) {
                t.createTempBuffer();
                t.advanceTransition(RCDATAEndTagOpen);
            } else if (r.matchesLetter() && t.appropriateEndTagName() != null && !r.containsIgnoreCase("</" + t.appropriateEndTagName())) {
                // diverge from spec: got a start tag, but there's no appropriate end tag (</title>), so rather than
                // consuming to EOF; break out here
                t.tagPending = t.createTagPending(false).name(t.appropriateEndTagName());
                t.emitTagPending();
                t.transition(TagOpen); // straight into TagOpen, as we came from < and looks like we're on a start tag
            } else {
                t.emit("<");
                t.transition(Rcdata);
            }
        }
    }

    private static final class RcDataEndTagOpenTS extends TokeniserState {

        @Override
        public String toString() {
            return "RCDATAEndTagOpen";
        }

        void read(Tokeniser t, CharacterReader r) {
            if (r.matchesLetter()) {
                t.createTagPending(false);
                t.tagPending.appendTagName(r.current());
                t.dataBuffer.append(r.current());
                t.advanceTransition(RCDATAEndTagName);
            } else {
                t.emit("</");
                t.transition(Rcdata);
            }
        }
    }

    private static final class RcDataEndTagNameTS extends TokeniserState {

        @Override
        public String toString() {
            return "RCDATAEndTagName";
        }

        void read(Tokeniser t, CharacterReader r) {
            if (r.matchesLetter()) {
                String name = r.consumeLetterSequence();
                t.tagPending.appendTagName(name);
                t.dataBuffer.append(name);
                return;
            }

            char c = r.consume();
            switch (c) {
                case '\t':
                case '\n':
                case '\r':
                case '\f':
                case ' ':
                    if (t.isAppropriateEndTagToken())
                        t.transition(BeforeAttributeName);
                    else
                        anythingElse(t, r);
                    break;
                case '/':
                    if (t.isAppropriateEndTagToken())
                        t.transition(SelfClosingStartTag);
                    else
                        anythingElse(t, r);
                    break;
                case '>':
                    if (t.isAppropriateEndTagToken()) {
                        t.emitTagPending();
                        t.transition(Data);
                    }
                    else
                        anythingElse(t, r);
                    break;
                default:
                    anythingElse(t, r);
            }
        }

        private void anythingElse(Tokeniser t, CharacterReader r) {
            t.emit("</");
            t.emit(t.dataBuffer);
            r.unconsume();
            t.transition(Rcdata);
        }
    }

    private static final class RawTextLessThanSignTS extends TokeniserState {

        @Override
        public String toString() {
            return "RawtextLessthanSign";
        }

        void read(Tokeniser t, CharacterReader r) {
            if (r.matches('/')) {
                t.createTempBuffer();
                t.advanceTransition(RawtextEndTagOpen);
            } else {
                t.emit('<');
                t.transition(Rawtext);
            }
        }
    }

    private static final class RawTextEndTagOpenTS extends TokeniserState {

        @Override
        public String toString() {
            return "RawtextEndTagOpen";
        }

        void read(Tokeniser t, CharacterReader r) {
            readEndTag(t, r, RawtextEndTagName, Rawtext);
        }
    }

    private static final class RawTextEndTagNameTS extends TokeniserState {

        @Override
        public String toString() {
            return "RawtextEndTagName";
        }

        void read(Tokeniser t, CharacterReader r) {
            handleDataEndTag(t, r, Rawtext);
        }
    }

    private static final class ScriptDataLessThanSignTS extends TokeniserState {

        @Override
        public String toString() {
            return "ScriptDataLessthanSign";
        }

        void read(Tokeniser t, CharacterReader r) {
            switch (r.consume()) {
                case '/':
                    t.createTempBuffer();
                    t.transition(ScriptDataEndTagOpen);
                    break;
                case '!':
                    t.emit("<!");
                    t.transition(ScriptDataEscapeStart);
                    break;
                case eof:
                    t.emit("<");
                    t.eofError(this);
                    t.transition(Data);
                    break;
                default:
                    t.emit("<");
                    r.unconsume();
                    t.transition(ScriptData);
            }
        }
    }

    private static final class ScriptDataEndTagOpenTS extends TokeniserState {

        @Override
        public String toString() {
            return "ScriptDataEndTagOpen";
        }

        void read(Tokeniser t, CharacterReader r) {
            readEndTag(t, r, ScriptDataEndTagName, ScriptData);
        }
    }

    private static final class ScriptDataEndTagNameTS extends TokeniserState {

        @Override
        public String toString() {
            return "ScriptDataEndTagName";
        }

        void read(Tokeniser t, CharacterReader r) {
            handleDataEndTag(t, r, ScriptData);
        }
    }

    private static final class ScriptDataEscapeStartTS extends TokeniserState {

        @Override
        public String toString() {
            return "ScriptDataEscapeStart";
        }

        void read(Tokeniser t, CharacterReader r) {
            if (r.matches('-')) {
                t.emit('-');
                t.advanceTransition(ScriptDataEscapeStartDash);
            } else {
                t.transition(ScriptData);
            }
        }
    }

    private static final class ScriptDataEscapeStartDashTS extends TokeniserState {

        @Override
        public String toString() {
            return "ScriptDataEscapeStartDash";
        }

        void read(Tokeniser t, CharacterReader r) {
            if (r.matches('-')) {
                t.emit('-');
                t.advanceTransition(ScriptDataEscapedDashDash);
            } else {
                t.transition(ScriptData);
            }
        }
    }

    private static final class ScriptDataEscapedTS extends TokeniserState {

        @Override
        public String toString() {
            return "ScriptDataEscaped";
        }

        void read(Tokeniser t, CharacterReader r) {
            if (r.isEmpty()) {
                t.eofError(this);
                t.transition(Data);
                return;
            }

            switch (r.current()) {
                case '-':
                    t.emit('-');
                    t.advanceTransition(ScriptDataEscapedDash);
                    break;
                case '<':
                    t.advanceTransition(ScriptDataEscapedLessthanSign);
                    break;
                case nullChar:
                    t.error(this);
                    r.advance();
                    t.emit(replacementChar);
                    break;
                default:
                    String data = r.consumeToAny('-', '<', nullChar);
                    t.emit(data);
            }
        }
    }

    private static final class ScriptDataEscapedDashTS extends TokeniserState {

        @Override
        public String toString() {
            return "ScriptDataEscapedDash";
        }

        void read(Tokeniser t, CharacterReader r) {
            if (r.isEmpty()) {
                t.eofError(this);
                t.transition(Data);
                return;
            }

            char c = r.consume();
            switch (c) {
                case '-':
                    t.emit(c);
                    t.transition(ScriptDataEscapedDashDash);
                    break;
                case '<':
                    t.transition(ScriptDataEscapedLessthanSign);
                    break;
                case nullChar:
                    t.error(this);
                    t.emit(replacementChar);
                    t.transition(ScriptDataEscaped);
                    break;
                default:
                    t.emit(c);
                    t.transition(ScriptDataEscaped);
            }
        }
    }

    private static final class ScriptDataEscapedDashDashTS extends TokeniserState {

        @Override
        public String toString() {
            return "ScriptDataEscapedDashDash";
        }

        void read(Tokeniser t, CharacterReader r) {
            if (r.isEmpty()) {
                t.eofError(this);
                t.transition(Data);
                return;
            }

            char c = r.consume();
            switch (c) {
                case '-':
                    t.emit(c);
                    break;
                case '<':
                    t.transition(ScriptDataEscapedLessthanSign);
                    break;
                case '>':
                    t.emit(c);
                    t.transition(ScriptData);
                    break;
                case nullChar:
                    t.error(this);
                    t.emit(replacementChar);
                    t.transition(ScriptDataEscaped);
                    break;
                default:
                    t.emit(c);
                    t.transition(ScriptDataEscaped);
            }
        }
    }

    private static final class ScriptDataEscapedLessThanSignTS extends TokeniserState {

        @Override
        public String toString() {
            return "ScriptDataEscapedLessthanSign";
        }

        void read(Tokeniser t, CharacterReader r) {
            if (r.matchesLetter()) {
                t.createTempBuffer();
                t.dataBuffer.append(r.current());
                t.emit("<");
                t.emit(r.current());
                t.advanceTransition(ScriptDataDoubleEscapeStart);
            } else if (r.matches('/')) {
                t.createTempBuffer();
                t.advanceTransition(ScriptDataEscapedEndTagOpen);
            } else {
                t.emit('<');
                t.transition(ScriptDataEscaped);
            }
        }
    }

    private static final class ScriptDataEscapedEndTagOpenTS extends TokeniserState {

        @Override
        public String toString() {
            return "ScriptDataEscapedEndTagOpen";
        }

        void read(Tokeniser t, CharacterReader r) {
            if (r.matchesLetter()) {
                t.createTagPending(false);
                t.tagPending.appendTagName(r.current());
                t.dataBuffer.append(r.current());
                t.advanceTransition(ScriptDataEscapedEndTagName);
            } else {
                t.emit("</");
                t.transition(ScriptDataEscaped);
            }
        }
    }

    private static final class ScriptDataEscapedEndTagNameTS extends TokeniserState {

        @Override
        public String toString() {
            return "ScriptDataEscapedEndTagName";
        }

        void read(Tokeniser t, CharacterReader r) {
            handleDataEndTag(t, r, ScriptDataEscaped);
        }
    }

    private static final class ScriptDataDoubleEscapeStartTS extends TokeniserState {

        @Override
        public String toString() {
            return "ScriptDataDoubleEscapeStart";
        }

        void read(Tokeniser t, CharacterReader r) {
            handleDataDoubleEscapeTag(t, r, ScriptDataDoubleEscaped, ScriptDataEscaped);
        }
    }

    private static final class ScriptDataDoubleEscapedTS extends TokeniserState {

        @Override
        public String toString() {
            return "ScriptDataDoubleEscaped";
        }

        void read(Tokeniser t, CharacterReader r) {
            char c = r.current();
            switch (c) {
                case '-':
                    t.emit(c);
                    t.advanceTransition(ScriptDataDoubleEscapedDash);
                    break;
                case '<':
                    t.emit(c);
                    t.advanceTransition(ScriptDataDoubleEscapedLessthanSign);
                    break;
                case nullChar:
                    t.error(this);
                    r.advance();
                    t.emit(replacementChar);
                    break;
                case eof:
                    t.eofError(this);
                    t.transition(Data);
                    break;
                default:
                    String data = r.consumeToAny('-', '<', nullChar);
                    t.emit(data);
            }
        }
    }

    private static final class ScriptDataDoubleEscapedDashTS extends TokeniserState {

        @Override
        public String toString() {
            return "ScriptDataDoubleEscapedDash";
        }

        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '-':
                    t.emit(c);
                    t.transition(ScriptDataDoubleEscapedDashDash);
                    break;
                case '<':
                    t.emit(c);
                    t.transition(ScriptDataDoubleEscapedLessthanSign);
                    break;
                case eof:
                    t.eofError(this);
                    t.transition(Data);
                    break;
                case nullChar:
                    t.error(this);
                    t.emit(replacementChar);
                    t.transition(ScriptDataDoubleEscaped);
                    break;
                default:
                    t.emit(c);
                    t.transition(ScriptDataDoubleEscaped);
            }
        }
    }

    private static final class ScriptDataDoubleEscapedDashDashTS extends TokeniserState {

        @Override
        public String toString() {
            return "ScriptDataDoubleEscapedDashDash";
        }

        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '-':
                    t.emit(c);
                    break;
                case '<':
                    t.emit(c);
                    t.transition(ScriptDataDoubleEscapedLessthanSign);
                    break;
                case '>':
                    t.emit(c);
                    t.transition(ScriptData);
                    break;
                case nullChar:
                    t.error(this);
                    t.emit(replacementChar);
                    t.transition(ScriptDataDoubleEscaped);
                    break;
                case eof:
                    t.eofError(this);
                    t.transition(Data);
                    break;
                default:
                    t.emit(c);
                    t.transition(ScriptDataDoubleEscaped);
            }
        }
    }

    private static final class ScriptDataDoubleEscapedLessThanSignTS extends TokeniserState {

        @Override
        public String toString() {
            return "ScriptDataDoubleEscapedLessthanSign";
        }

        void read(Tokeniser t, CharacterReader r) {
            if (r.matches('/')) {
                t.emit('/');
                t.createTempBuffer();
                t.advanceTransition(ScriptDataDoubleEscapeEnd);
            } else {
                t.transition(ScriptDataDoubleEscaped);
            }
        }
    }

    private static final class ScriptDataDoubleEscapeEndTS extends TokeniserState {

        @Override
        public String toString() {
            return "ScriptDataDoubleEscapeEnd";
        }

        void read(Tokeniser t, CharacterReader r) {
            handleDataDoubleEscapeTag(t,r, ScriptDataEscaped, ScriptDataDoubleEscaped);
        }
    }

    private static final class BeforeAttributeNameTS extends TokeniserState {

        @Override
        public String toString() {
            return "BeforeAttributeName";
        }

        // from tagname <xxx
        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '\t':
                case '\n':
                case '\r':
                case '\f':
                case ' ':
                    break; // ignore whitespace
                case '/':
                    t.transition(SelfClosingStartTag);
                    break;
                case '<': // NOTE: out of spec, but clear (spec has this as a part of the attribute name)
                    r.unconsume();
                    t.error(this);
                    // intended fall through as if >
                case '>':
                    t.emitTagPending();
                    t.transition(Data);
                    break;
                case nullChar:
                    r.unconsume();
                    t.error(this);
                    t.tagPending.newAttribute();
                    t.transition(AttributeName);
                    break;
                case eof:
                    t.eofError(this);
                    t.transition(Data);
                    break;
                case '"':
                case '\'':
                case '=':
                    t.error(this);
                    t.tagPending.newAttribute();
                    t.tagPending.appendAttributeName(c);
                    t.transition(AttributeName);
                    break;
                default: // A-Z, anything else
                    t.tagPending.newAttribute();
                    r.unconsume();
                    t.transition(AttributeName);
            }
        }
    }

    private static final class AttributeNameTS extends TokeniserState {

        @Override
        public String toString() {
            return "AttributeName";
        }

        // from before attribute name
        void read(Tokeniser t, CharacterReader r) {
            String name = r.consumeToAnySorted(attributeNameCharsSorted);
            t.tagPending.appendAttributeName(name);

            char c = r.consume();
            switch (c) {
                case '\t':
                case '\n':
                case '\r':
                case '\f':
                case ' ':
                    t.transition(AfterAttributeName);
                    break;
                case '/':
                    t.transition(SelfClosingStartTag);
                    break;
                case '=':
                    t.transition(BeforeAttributeValue);
                    break;
                case '>':
                    t.emitTagPending();
                    t.transition(Data);
                    break;
                case nullChar:
                    t.error(this);
                    t.tagPending.appendAttributeName(replacementChar);
                    break;
                case eof:
                    t.eofError(this);
                    t.transition(Data);
                    break;
                case '"':
                case '\'':
                case '<':
                    t.error(this);
                    t.tagPending.appendAttributeName(c);
                    break;
                default: // buffer underrun
                    t.tagPending.appendAttributeName(c);
            }
        }
    }

    private static final class AfterAttributeNameTS extends TokeniserState {

        @Override
        public String toString() {
            return "AfterAttributeName";
        }

        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '\t':
                case '\n':
                case '\r':
                case '\f':
                case ' ':
                    // ignore
                    break;
                case '/':
                    t.transition(SelfClosingStartTag);
                    break;
                case '=':
                    t.transition(BeforeAttributeValue);
                    break;
                case '>':
                    t.emitTagPending();
                    t.transition(Data);
                    break;
                case nullChar:
                    t.error(this);
                    t.tagPending.appendAttributeName(replacementChar);
                    t.transition(AttributeName);
                    break;
                case '\'':
                case '"':
                case '<':
                    t.error(this);
                    t.tagPending.newAttribute();
                    t.tagPending.appendAttributeName(c);
                    t.transition(AttributeName);
                    break;
                case eof:
                    t.eofError(this);
                    t.transition(Data);
                    break;
                default: // A-Z, anything else
                    t.tagPending.newAttribute();
                    r.unconsume();
                    t.transition(AttributeName);
            }
        }
    }

    private static final class BeforeAttributeValueTS extends TokeniserState {

        @Override
        public String toString() {
            return "BeforeAttributeValue";
        }

        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '\t':
                case '\n':
                case '\r':
                case '\f':
                case ' ':
                    // ignore
                    break;
                case '"':
                    t.transition(AttributeValue_doubleQuoted);
                    break;
                case '&':
                    r.unconsume();
                    t.transition(AttributeValue_unquoted);
                    break;
                case '\'':
                    t.transition(AttributeValue_singleQuoted);
                    break;
                case nullChar:
                    t.error(this);
                    t.tagPending.appendAttributeValue(replacementChar);
                    t.transition(AttributeValue_unquoted);
                    break;
                case eof:
                    t.eofError(this);
                    t.emitTagPending();
                    t.transition(Data);
                    break;
                case '>':
                    t.error(this);
                    t.emitTagPending();
                    t.transition(Data);
                    break;
                case '<':
                case '=':
                case '`':
                    t.error(this);
                    t.tagPending.appendAttributeValue(c);
                    t.transition(AttributeValue_unquoted);
                    break;
                default:
                    r.unconsume();
                    t.transition(AttributeValue_unquoted);
            }
        }
    }

    private static final class AttributeValueDoubleQuotedTS extends TokeniserState {

        @Override
        public String toString() {
            return "AttributeValue_doubleQuoted";
        }

        void read(Tokeniser t, CharacterReader r) {
            String value = r.consumeAttributeQuoted(false);
            if (value.length() > 0) {
                t.tagPending.appendAttributeValue(value);
            } else {
                t.tagPending.setEmptyAttributeValue();
            }

            char c = r.consume();
            switch (c) {
                case '&':
                    int[] ref = t.consumeCharacterReference('"', true);
                    if (ref != null)
                        t.tagPending.appendAttributeValue(ref);
                    else
                        t.tagPending.appendAttributeValue('&');
                    break;
                case '"':
                    t.transition(AfterAttributeValue_quoted);
                    break;
                case eof:
                    t.eofError(this);
                    t.transition(Data);
                    break;
                case nullChar:
                    t.error(this);
                    t.tagPending.appendAttributeValue(replacementChar);
                    break;
                default: // hit end of buffer in first read, still in attribute
                    t.tagPending.appendAttributeValue(c);
            }
        }
    }

    private static final class AttributeValueSingleQuotedTS extends TokeniserState {

        @Override
        public String toString() {
            return "AttributeValue_singleQuoted";
        }

        void read(Tokeniser t, CharacterReader r) {
            String value = r.consumeAttributeQuoted(true);
            if (value.length() > 0)
                t.tagPending.appendAttributeValue(value);
            else
                t.tagPending.setEmptyAttributeValue();

            char c = r.consume();
            switch (c) {
                case '\'':
                    t.transition(AfterAttributeValue_quoted);
                    break;
                case '&':
                    int[] ref = t.consumeCharacterReference('\'', true);
                    if (ref != null)
                        t.tagPending.appendAttributeValue(ref);
                    else
                        t.tagPending.appendAttributeValue('&');
                    break;
                case nullChar:
                    t.error(this);
                    t.tagPending.appendAttributeValue(replacementChar);
                    break;
                case eof:
                    t.eofError(this);
                    t.transition(Data);
                    break;
                default: // hit end of buffer in first read, still in attribute
                    t.tagPending.appendAttributeValue(c);
            }
        }
    }

    private static final class AttributeValueUnquotedTS extends TokeniserState {

        @Override
        public String toString() {
            return "AttributeValue_unquoted";
        }

        void read(Tokeniser t, CharacterReader r) {
            String value = r.consumeToAnySorted(attributeValueUnquoted);
            if (value.length() > 0)
                t.tagPending.appendAttributeValue(value);

            char c = r.consume();
            switch (c) {
                case '\t':
                case '\n':
                case '\r':
                case '\f':
                case ' ':
                    t.transition(BeforeAttributeName);
                    break;
                case '&':
                    int[] ref = t.consumeCharacterReference('>', true);
                    if (ref != null)
                        t.tagPending.appendAttributeValue(ref);
                    else
                        t.tagPending.appendAttributeValue('&');
                    break;
                case '>':
                    t.emitTagPending();
                    t.transition(Data);
                    break;
                case nullChar:
                    t.error(this);
                    t.tagPending.appendAttributeValue(replacementChar);
                    break;
                case eof:
                    t.eofError(this);
                    t.transition(Data);
                    break;
                case '"':
                case '\'':
                case '<':
                case '=':
                case '`':
                    t.error(this);
                    t.tagPending.appendAttributeValue(c);
                    break;
                default: // hit end of buffer in first read, still in attribute
                    t.tagPending.appendAttributeValue(c);
            }

        }
    }

    private static final class AfterAttributeValueQuotedTS extends TokeniserState {

        @Override
        public String toString() {
            return "AfterAttributeValue_quoted";
        }

        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '\t':
                case '\n':
                case '\r':
                case '\f':
                case ' ':
                    t.transition(BeforeAttributeName);
                    break;
                case '/':
                    t.transition(SelfClosingStartTag);
                    break;
                case '>':
                    t.emitTagPending();
                    t.transition(Data);
                    break;
                case eof:
                    t.eofError(this);
                    t.transition(Data);
                    break;
                default:
                    r.unconsume();
                    t.error(this);
                    t.transition(BeforeAttributeName);
            }

        }
    }

    private static final class SelfClosingStartTagTS extends TokeniserState {

        @Override
        public String toString() {
            return "SelfClosingStartTag";
        }

        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '>':
                    t.tagPending.selfClosing = true;
                    t.emitTagPending();
                    t.transition(Data);
                    break;
                case eof:
                    t.eofError(this);
                    t.transition(Data);
                    break;
                default:
                    r.unconsume();
                    t.error(this);
                    t.transition(BeforeAttributeName);
            }
        }
    }

    private static final class BogusCommentTS extends TokeniserState {

        @Override
        public String toString() {
            return "BogusComment";
        }

        void read(Tokeniser t, CharacterReader r) {
            // rewind to capture character that lead us here
            r.unconsume();
            t.commentPending.append(r.consumeTo('>'));
            char next = r.consume();
            if (next == '>' || next == eof) {
                t.emitCommentPending();
                t.transition(Data);
            }
        }
    }

    private static final class MarkupDeclarationOpenTS extends TokeniserState {

        @Override
        public String toString() {
            return "MarkupDeclarationOpen";
        }

        void read(Tokeniser t, CharacterReader r) {
            if (r.matchConsume("--")) {
                t.createCommentPending();
                t.transition(CommentStart);
            } else if (r.matchConsumeIgnoreCase("DOCTYPE")) {
                t.transition(Doctype);
            } else if (r.matchConsume("[CDATA[")) {
                // is implemented properly, keep handling as cdata
                //} else if (!t.currentNodeInHtmlNS() && r.matchConsume("[CDATA[")) {
                t.createTempBuffer();
                t.transition(CdataSection);
            } else {
                t.error(this);
                t.createBogusCommentPending();
                t.advanceTransition(BogusComment); // advance so this character gets in bogus comment data's rewind
            }
        }
    }

    private static final class CommentStartTS extends TokeniserState {

        @Override
        public String toString() {
            return "CommentStart";
        }

        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case eof:
                    t.eofError(this);
                    t.emitCommentPending();
                    t.transition(Data);
                    break;
                case '-':
                    t.transition(CommentStartDash);
                    break;
                case '>':
                    t.error(this);
                    t.emitCommentPending();
                    t.transition(Data);
                    break;
                case nullChar:
                    t.error(this);
                    t.commentPending.append(replacementChar);
                    t.transition(Comment);
                    break;
                default:
                    r.unconsume();
                    t.transition(Comment);
            }
        }
    }

    private static final class CommentStartDashTS extends TokeniserState {

        @Override
        public String toString() {
            return "CommentStartDash";
        }

        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '-':
                    t.transition(CommentStartDash);
                    break;
                case nullChar:
                    t.error(this);
                    t.commentPending.append(replacementChar);
                    t.transition(Comment);
                    break;
                case '>':
                    t.error(this);
                    t.emitCommentPending();
                    t.transition(Data);
                    break;
                case eof:
                    t.eofError(this);
                    t.emitCommentPending();
                    t.transition(Data);
                    break;
                default:
                    t.commentPending.append(c);
                    t.transition(Comment);
            }
        }
    }

    private static final class CommentTS extends TokeniserState {

        @Override
        public String toString() {
            return "Comment";
        }

        void read(Tokeniser t, CharacterReader r) {
            char c = r.current();
            switch (c) {
                case '-':
                    t.advanceTransition(CommentEndDash);
                    break;
                case nullChar:
                    t.error(this);
                    r.advance();
                    t.commentPending.append(replacementChar);
                    break;
                case eof:
                    t.eofError(this);
                    t.emitCommentPending();
                    t.transition(Data);
                    break;
                default:
                    t.commentPending.append(r.consumeToAny('-', nullChar));
            }
        }
    }

    private static final class CommentEndDashTS extends TokeniserState {

        @Override
        public String toString() {
            return "CommentEndDash";
        }

        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '-':
                    t.transition(CommentEnd);
                    break;
                case eof:
                    t.eofError(this);
                    t.emitCommentPending();
                    t.transition(Data);
                    break;
                case nullChar:
                    t.error(this);
                    t.commentPending.append('-').append(replacementChar);
                    t.transition(Comment);
                    break;
                default:
                    t.commentPending.append('-').append(c);
                    t.transition(Comment);
            }
        }
    }

    private static final class CommentEndTS extends TokeniserState {

        @Override
        public String toString() {
            return "CommentEnd";
        }

        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '>':
                    t.emitCommentPending();
                    t.transition(Data);
                    break;
                case nullChar:
                    t.error(this);
                    t.commentPending.append("--").append(replacementChar);
                    t.transition(Comment);
                    break;
                case '!':
                    t.error(this);
                    t.transition(CommentEndBang);
                    break;
                case '-':
                    t.error(this);
                    t.commentPending.append('-');
                    break;
                case eof:
                    t.eofError(this);
                    t.emitCommentPending();
                    t.transition(Data);
                    break;
                default:
                    t.error(this);
                    t.commentPending.append("--").append(c);
                    t.transition(Comment);
            }
        }
    }

    private static final class CommentEndBangTS extends TokeniserState {

        @Override
        public String toString() {
            return "CommentEndBang";
        }

        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '-':
                    t.commentPending.append("--!");
                    t.transition(CommentEndDash);
                    break;
                case '>':
                    t.emitCommentPending();
                    t.transition(Data);
                    break;
                case nullChar:
                    t.error(this);
                    t.commentPending.append("--!").append(replacementChar);
                    t.transition(Comment);
                    break;
                case eof:
                    t.eofError(this);
                    t.emitCommentPending();
                    t.transition(Data);
                    break;
                default:
                    t.commentPending.append("--!").append(c);
                    t.transition(Comment);
            }
        }
    }

    private static final class DocTypeTS extends TokeniserState {

        @Override
        public String toString() {
            return "Doctype";
        }

        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '\t':
                case '\n':
                case '\r':
                case '\f':
                case ' ':
                    t.transition(BeforeDoctypeName);
                    break;
                case eof:
                    t.eofError(this);
                    // note: fall through to > case
                case '>': // catch invalid <!DOCTYPE>
                    t.error(this);
                    t.createDoctypePending();
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                default:
                    t.error(this);
                    t.transition(BeforeDoctypeName);
            }
        }
    }

    private static final class BeforeDocTypeNameTS extends TokeniserState {

        @Override
        public String toString() {
            return "BeforeDoctypeName";
        }

        void read(Tokeniser t, CharacterReader r) {
            if (r.matchesLetter()) {
                t.createDoctypePending();
                t.transition(DoctypeName);
                return;
            }
            char c = r.consume();
            switch (c) {
                case '\t':
                case '\n':
                case '\r':
                case '\f':
                case ' ':
                    break; // ignore whitespace
                case nullChar:
                    t.error(this);
                    t.createDoctypePending();
                    t.doctypePending.name.append(replacementChar);
                    t.transition(DoctypeName);
                    break;
                case eof:
                    t.eofError(this);
                    t.createDoctypePending();
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                default:
                    t.createDoctypePending();
                    t.doctypePending.name.append(c);
                    t.transition(DoctypeName);
            }
        }
    }

    private static final class DocTypeNameTS extends TokeniserState {

        @Override
        public String toString() {
            return "DoctypeName";
        }

        void read(Tokeniser t, CharacterReader r) {
            if (r.matchesLetter()) {
                String name = r.consumeLetterSequence();
                t.doctypePending.name.append(name);
                return;
            }
            char c = r.consume();
            switch (c) {
                case '>':
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                case '\t':
                case '\n':
                case '\r':
                case '\f':
                case ' ':
                    t.transition(AfterDoctypeName);
                    break;
                case nullChar:
                    t.error(this);
                    t.doctypePending.name.append(replacementChar);
                    break;
                case eof:
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                default:
                    t.doctypePending.name.append(c);
            }
        }
    }

    private static final class AfterDocTypeNameTS extends TokeniserState {

        @Override
        public String toString() {
            return "AfterDoctypeName";
        }

        void read(Tokeniser t, CharacterReader r) {
            if (r.isEmpty()) {
                t.eofError(this);
                t.doctypePending.forceQuirks = true;
                t.emitDoctypePending();
                t.transition(Data);
                return;
            }
            if (r.matchesAny('\t', '\n', '\r', '\f', ' '))
                r.advance(); // ignore whitespace
            else if (r.matches('>')) {
                t.emitDoctypePending();
                t.advanceTransition(Data);
            } else if (r.matchConsumeIgnoreCase(DocumentType.PUBLIC_KEY)) {
                t.doctypePending.pubSysKey = DocumentType.PUBLIC_KEY;
                t.transition(AfterDoctypePublicKeyword);
            } else if (r.matchConsumeIgnoreCase(DocumentType.SYSTEM_KEY)) {
                t.doctypePending.pubSysKey = DocumentType.SYSTEM_KEY;
                t.transition(AfterDoctypeSystemKeyword);
            } else {
                t.error(this);
                t.doctypePending.forceQuirks = true;
                t.advanceTransition(BogusDoctype);
            }

        }
    }

    private static final class AfterDocTypePublicKeywordTS extends TokeniserState {

        @Override
        public String toString() {
            return "AfterDoctypePublicKeyword";
        }

        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '\t':
                case '\n':
                case '\r':
                case '\f':
                case ' ':
                    t.transition(BeforeDoctypePublicIdentifier);
                    break;
                case '"':
                    t.error(this);
                    // set public id to empty string
                    t.transition(DoctypePublicIdentifier_doubleQuoted);
                    break;
                case '\'':
                    t.error(this);
                    // set public id to empty string
                    t.transition(DoctypePublicIdentifier_singleQuoted);
                    break;
                case eof:
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                case '>':
                    t.error(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                default:
                    t.error(this);
                    t.doctypePending.forceQuirks = true;
                    t.transition(BogusDoctype);
            }
        }
    }

    private static final class BeforeDocTypePublicIdentifierTS extends TokeniserState {

        @Override
        public String toString() {
            return "BeforeDoctypePublicIdentifier";
        }

        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '\t':
                case '\n':
                case '\r':
                case '\f':
                case ' ':
                    break;
                case '"':
                    // set public id to empty string
                    t.transition(DoctypePublicIdentifier_doubleQuoted);
                    break;
                case '\'':
                    // set public id to empty string
                    t.transition(DoctypePublicIdentifier_singleQuoted);
                    break;
                case '>':
                    t.error(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                case eof:
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                default:
                    t.error(this);
                    t.doctypePending.forceQuirks = true;
                    t.transition(BogusDoctype);
            }
        }
    }

    private static final class DocTypePublicIdentifierDoubleQuotedTS extends TokeniserState {

        @Override
        public String toString() {
            return "DoctypePublicIdentifier_doubleQuoted";
        }

        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '"':
                    t.transition(AfterDoctypePublicIdentifier);
                    break;
                case eof:
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                case '>':
                    t.error(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                case nullChar:
                    t.error(this);
                    t.doctypePending.publicIdentifier.append(replacementChar);
                    break;
                default:
                    t.doctypePending.publicIdentifier.append(c);
            }
        }
    }

    private static final class DocTypePublicIdentifierSingleQuotedTS extends TokeniserState {

        @Override
        public String toString() {
            return "DoctypePublicIdentifier_singleQuoted";
        }

        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '\'':
                    t.transition(AfterDoctypePublicIdentifier);
                    break;
                case nullChar:
                    t.error(this);
                    t.doctypePending.publicIdentifier.append(replacementChar);
                    break;
                case '>':
                    t.error(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                case eof:
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                default:
                    t.doctypePending.publicIdentifier.append(c);
            }
        }
    }

    private static final class AfterDocTypePublicIdentifierTS extends TokeniserState {

        @Override
        public String toString() {
            return "AfterDoctypePublicIdentifier";
        }

        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '\t':
                case '\n':
                case '\r':
                case '\f':
                case ' ':
                    t.transition(BetweenDoctypePublicAndSystemIdentifiers);
                    break;
                case '"':
                    t.error(this);
                    // system id empty
                    t.transition(DoctypeSystemIdentifier_doubleQuoted);
                    break;
                case '>':
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                case eof:
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                case '\'':
                    t.error(this);
                    // system id empty
                    t.transition(DoctypeSystemIdentifier_singleQuoted);
                    break;
                default:
                    t.error(this);
                    t.doctypePending.forceQuirks = true;
                    t.transition(BogusDoctype);
            }
        }
    }

    private static final class BetweenDocTypePublicAndSystemIdentifiersTS extends TokeniserState {

        @Override
        public String toString() {
            return "BetweenDoctypePublicAndSystemIdentifiers";
        }

        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '\t':
                case '\n':
                case '\r':
                case '\f':
                case ' ':
                    break;
                case '>':
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                case '"':
                    t.error(this);
                    // system id empty
                    t.transition(DoctypeSystemIdentifier_doubleQuoted);
                    break;
                case '\'':
                    t.error(this);
                    // system id empty
                    t.transition(DoctypeSystemIdentifier_singleQuoted);
                    break;
                case eof:
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                default:
                    t.error(this);
                    t.doctypePending.forceQuirks = true;
                    t.transition(BogusDoctype);
            }
        }
    }

    private static final class AfterDocTypeSystemKeywordTS extends TokeniserState {

        @Override
        public String toString() {
            return "AfterDoctypeSystemKeyword";
        }

        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '>':
                    t.error(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                case '\t':
                case '\n':
                case '\r':
                case '\f':
                case ' ':
                    t.transition(BeforeDoctypeSystemIdentifier);
                    break;
                case '"':
                    t.error(this);
                    // system id empty
                    t.transition(DoctypeSystemIdentifier_doubleQuoted);
                    break;
                case eof:
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                case '\'':
                    t.error(this);
                    // system id empty
                    t.transition(DoctypeSystemIdentifier_singleQuoted);
                    break;
                default:
                    t.error(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
            }
        }
    }

    private static final class BeforeDocTypeSystemIdentifierTS extends TokeniserState {

        @Override
        public String toString() {
            return "BeforeDoctypeSystemIdentifier";
        }

        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case eof:
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                case '\t':
                case '\n':
                case '\r':
                case '\f':
                case ' ':
                    break;
                case '"':
                    // set system id to empty string
                    t.transition(DoctypeSystemIdentifier_doubleQuoted);
                    break;
                case '\'':
                    // set public id to empty string
                    t.transition(DoctypeSystemIdentifier_singleQuoted);
                    break;
                case '>':
                    t.error(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                default:
                    t.error(this);
                    t.doctypePending.forceQuirks = true;
                    t.transition(BogusDoctype);
            }
        }
    }

    private static final class DocTypeSystemIdentifierDoubleQuotedTS extends TokeniserState {

        @Override
        public String toString() {
            return "DoctypeSystemIdentifier_doubleQuoted";
        }

        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case eof:
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                case nullChar:
                    t.error(this);
                    t.doctypePending.systemIdentifier.append(replacementChar);
                    break;
                case '"':
                    t.transition(AfterDoctypeSystemIdentifier);
                    break;
                case '>':
                    t.error(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                default:
                    t.doctypePending.systemIdentifier.append(c);
            }
        }
    }

    private static final class DocTypeSystemIdentifierSingleQuotedTS extends TokeniserState {

        @Override
        public String toString() {
            return "DoctypeSystemIdentifier_singleQuoted";
        }

        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '\'':
                    t.transition(AfterDoctypeSystemIdentifier);
                    break;
                case '>':
                    t.error(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                case nullChar:
                    t.error(this);
                    t.doctypePending.systemIdentifier.append(replacementChar);
                    break;
                case eof:
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                default:
                    t.doctypePending.systemIdentifier.append(c);
            }
        }
    }

    private static final class AfterDocTypeSystemIdentifierTS extends TokeniserState {

        @Override
        public String toString() {
            return "AfterDoctypeSystemIdentifier";
        }

        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '\t':
                case '\n':
                case '\r':
                case '\f':
                case ' ':
                    break;
                case eof:
                    t.eofError(this);
                    t.doctypePending.forceQuirks = true;
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                case '>':
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                default:
                    t.error(this);
                    t.transition(BogusDoctype);
                    // NOT force quirks
            }
        }
    }

    private static final class BogusDocTypeTS extends TokeniserState {

        @Override
        public String toString() {
            return "BogusDoctype";
        }

        void read(Tokeniser t, CharacterReader r) {
            char c = r.consume();
            switch (c) {
                case '>':
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                case eof:
                    t.emitDoctypePending();
                    t.transition(Data);
                    break;
                default:
                    // ignore char
                    break;
            }
        }
    }

    private static final class CDataSectionTS extends TokeniserState {

        @Override
        public String toString() {
            return "CdataSection";
        }

        void read(Tokeniser t, CharacterReader r) {
            String data = r.consumeTo("]]>");
            t.dataBuffer.append(data);
            if (r.matchConsume("]]>") || r.isEmpty()) {
                t.emit(new Token.CData(t.dataBuffer.toString()));
                t.transition(Data);
            }// otherwise, buffer underrun, stay in data section
        }
    }
}
