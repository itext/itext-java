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

import com.itextpdf.styledxmlparser.jsoup.PortUtil;
import com.itextpdf.styledxmlparser.jsoup.helper.Validate;
import com.itextpdf.styledxmlparser.jsoup.internal.Normalizer;
import com.itextpdf.styledxmlparser.jsoup.nodes.Attributes;

/**
 * Parse tokens for the Tokeniser.
 */
public abstract class Token {
    TokenType type;

    private Token() {
    }
    
    String tokenType() {
        return this.getClass().getSimpleName();
    }

    /**
     * Reset the data represent by this token, for reuse. Prevents the need to create transfer objects for every
     * piece of data, which immediately get GCed.
     */
    abstract Token reset();

    static void reset(StringBuilder sb) {
        if (sb != null) {
            sb.delete(0, sb.length());
        }
    }

    static final class Doctype extends Token {
        final StringBuilder name = new StringBuilder();
        String pubSysKey = null;
        final StringBuilder publicIdentifier = new StringBuilder();
        final StringBuilder systemIdentifier = new StringBuilder();
        boolean forceQuirks = false;

        Doctype() {
            type = TokenType.Doctype;
        }

        @Override
        Token reset() {
            reset(name);
            pubSysKey = null;
            reset(publicIdentifier);
            reset(systemIdentifier);
            forceQuirks = false;
            return this;
        }

        String getName() {
            return name.toString();
        }

        String getPubSysKey() {
            return pubSysKey;
        }

        String getPublicIdentifier() {
            return publicIdentifier.toString();
        }

        public String getSystemIdentifier() {
            return systemIdentifier.toString();
        }

        public boolean isForceQuirks() {
            return forceQuirks;
        }
    }

    static abstract class Tag extends Token {
        protected String tagName;
        protected String normalName; // lc version of tag name, for case insensitive tree build
        private String pendingAttributeName; // attribute names are generally caught in one hop, not accumulated
        private StringBuilder pendingAttributeValue = new StringBuilder(); // but values are accumulated, from e.g. & in hrefs
        private String pendingAttributeValueS; // try to get attr vals in one shot, vs Builder
        private boolean hasEmptyAttributeValue = false; // distinguish boolean attribute from empty string value
        private boolean hasPendingAttributeValue = false;
        boolean selfClosing = false;
        Attributes attributes; // start tags get attributes on construction. End tags get attributes on first new attribute (but only for parser convenience, not used).

        @Override
        Token reset() {
            tagName = null;
            normalName = null;
            pendingAttributeName = null;
            reset(pendingAttributeValue);
            pendingAttributeValueS = null;
            hasEmptyAttributeValue = false;
            hasPendingAttributeValue = false;
            selfClosing = false;
            attributes = null;
            return this;
        }

        final void newAttribute() {
            if (attributes == null)
                attributes = new Attributes();

            if (pendingAttributeName != null) {
                // the tokeniser has skipped whitespace control chars, but trimming could collapse to empty for other control codes, so verify here
                pendingAttributeName = PortUtil.trimControlCodes(pendingAttributeName);
                if (pendingAttributeName.length() > 0) {
                    String value;
                    if (hasPendingAttributeValue)
                        value = pendingAttributeValue.length() > 0 ? pendingAttributeValue.toString() : pendingAttributeValueS;
                    else if (hasEmptyAttributeValue)
                        value = "";
                    else
                        value = null;
                    // note that we add, not put. So that the first is kept, and rest are deduped, once in a context where case sensitivity is known (the appropriate tree builder).
                    attributes.add(pendingAttributeName, value);
                }
            }
            pendingAttributeName = null;
            hasEmptyAttributeValue = false;
            hasPendingAttributeValue = false;
            reset(pendingAttributeValue);
            pendingAttributeValueS = null;
        }

        final boolean hasAttributes() {
            return attributes != null;
        }

        final boolean hasAttribute(String key) {
            return attributes != null && attributes.hasKey(key);
        }

        final void finaliseTag() {
            // finalises for emit
            if (pendingAttributeName != null) {
                newAttribute();
            }
        }

        /** Preserves case */
        final String name() { // preserves case, for input into Tag.valueOf (which may drop case)
            Validate.isFalse(tagName == null || tagName.length() == 0);
            return tagName;
        }

        /** Lower case */
        final String normalName() { // lower case, used in tree building for working out where in tree it should go
            return normalName;
        }

        final String toStringName() {
            return tagName != null ? tagName : "[unset]";
        }

        final Tag name(String name) {
            tagName = name;
            normalName = Normalizer.lowerCase(name);
            return this;
        }

        final boolean isSelfClosing() {
            return selfClosing;
        }

        // these appenders are rarely hit in not null state-- caused by null chars.
        final void appendTagName(String append) {
            tagName = tagName == null ? append : tagName + append;
            normalName = Normalizer.lowerCase(tagName);
        }

        final void appendTagName(char append) {
            appendTagName(String.valueOf(append));
        }

        final void appendAttributeName(String append) {
            pendingAttributeName = pendingAttributeName == null ? append : pendingAttributeName + append;
        }

        final void appendAttributeName(char append) {
            appendAttributeName(String.valueOf(append));
        }

        final void appendAttributeValue(String append) {
            ensureAttributeValue();
            if (pendingAttributeValue.length() == 0) {
                pendingAttributeValueS = append;
            } else {
                pendingAttributeValue.append(append);
            }
        }

        final void appendAttributeValue(char append) {
            ensureAttributeValue();
            pendingAttributeValue.append(append);
        }

        final void appendAttributeValue(char[] append) {
            ensureAttributeValue();
            pendingAttributeValue.append(append);
        }

        final void appendAttributeValue(int[] appendCodepoints) {
            ensureAttributeValue();
            for (int codepoint : appendCodepoints) {
                pendingAttributeValue.appendCodePoint(codepoint);
            }
        }
        
        final void setEmptyAttributeValue() {
            hasEmptyAttributeValue = true;
        }

        private void ensureAttributeValue() {
            hasPendingAttributeValue = true;
            // if on second hit, we'll need to move to the builder
            if (pendingAttributeValueS != null) {
                pendingAttributeValue.append(pendingAttributeValueS);
                pendingAttributeValueS = null;
            }
        }

        @Override
        abstract public String toString();
    }

    final static class StartTag extends Tag {
        StartTag() {
            super();
            type = TokenType.StartTag;
        }

        @Override
        Token reset() {
            super.reset();
            attributes = null;
            return this;
        }

        StartTag nameAttr(String name, Attributes attributes) {
            this.tagName = name;
            this.attributes = attributes;
            normalName = Normalizer.lowerCase(tagName);
            return this;
        }

        @Override
        public String toString() {
            if (hasAttributes() && attributes.size() > 0)
                return "<" + toStringName() + " " + attributes.toString() + ">";
            else
                return "<" + toStringName() + ">";
        }
    }

    final static class EndTag extends Tag{
        EndTag() {
            super();
            type = TokenType.EndTag;
        }

        @Override
        public String toString() {
            return "</" + toStringName() + ">";
        }
    }

    final static class Comment extends Token {
        private final StringBuilder data = new StringBuilder();
        private String dataS; // try to get in one shot
        boolean bogus = false;

        @Override
        Token reset() {
            reset(data);
            dataS = null;
            bogus = false;
            return this;
        }

        Comment() {
            type = TokenType.Comment;
        }

        String getData() {
            return dataS != null ? dataS : data.toString();
        }

        final Comment append(String append) {
            ensureData();
            if (data.length() == 0) {
                dataS = append;
            } else {
                data.append(append);
            }
            return this;
        }

        final Comment append(char append) {
            ensureData();
            data.append(append);
            return this;
        }

        private void ensureData() {
            // if on second hit, we'll need to move to the builder
            if (dataS != null) {
                data.append(dataS);
                dataS = null;
            }
        }

        @Override
        public String toString() {
            return "<!--" + getData() + "-->";
        }
    }

    static class Character extends Token {
        private String data;

        Character() {
            super();
            type = TokenType.Character;
        }

        @Override
        Token reset() {
            data = null;
            return this;
        }

        Character data(String data) {
            this.data = data;
            return this;
        }

        String getData() {
            return data;
        }

        @Override
        public String toString() {
            return getData();
        }
    }

    final static class CData extends Character {
        CData(String data) {
            super();
            this.data(data);
        }

        @Override
        public String toString() {
            return "<![CDATA[" + getData() + "]]>";
        }

    }

    final static class EOF extends Token {
        EOF() {
            type = Token.TokenType.EOF;
        }

        @Override
        Token reset() {
            return this;
        }

        @Override
        public String toString() {
            return "";
        }
    }

    final boolean isDoctype() {
        return type == TokenType.Doctype;
    }

    final Doctype asDoctype() {
        return (Doctype) this;
    }

    final boolean isStartTag() {
        return type == TokenType.StartTag;
    }

    final StartTag asStartTag() {
        return (StartTag) this;
    }

    final boolean isEndTag() {
        return type == TokenType.EndTag;
    }

    final EndTag asEndTag() {
        return (EndTag) this;
    }

    final boolean isComment() {
        return type == TokenType.Comment;
    }

    final Comment asComment() {
        return (Comment) this;
    }

    final boolean isCharacter() {
        return type == TokenType.Character;
    }

    final boolean isCData() {
        return this instanceof CData;
    }

    final Character asCharacter() {
        return (Character) this;
    }

    final boolean isEOF() {
        return type == TokenType.EOF;
    }

    public enum TokenType {
        Doctype,
        StartTag,
        EndTag,
        Comment,
        Character, // note no CData - treated in builder as an extension of Character
        EOF
    }
}
