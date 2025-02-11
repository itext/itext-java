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

import com.itextpdf.styledxmlparser.jsoup.internal.StringUtil;
import com.itextpdf.styledxmlparser.jsoup.nodes.Attribute;
import com.itextpdf.styledxmlparser.jsoup.nodes.Attributes;
import com.itextpdf.styledxmlparser.jsoup.nodes.Document;
import com.itextpdf.styledxmlparser.jsoup.nodes.DocumentType;
import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.nodes.Node;

import java.util.ArrayList;

/**
 * The Tree Builder's current state. Each state embodies the processing for the state, and transitions to other states.
 */
abstract class HtmlTreeBuilderState {

    public static HtmlTreeBuilderState Initial = new InitialBS();

    public static HtmlTreeBuilderState BeforeHtml = new BeforeHtmlBS();

    public static HtmlTreeBuilderState BeforeHead = new BeforeHeadBS();

    public static HtmlTreeBuilderState InHead = new InHeadBS();

    public static HtmlTreeBuilderState InHeadNoscript = new InHeadNoScriptBS();

    public static HtmlTreeBuilderState AfterHead = new AfterHeadBS();

    public static HtmlTreeBuilderState InBody = new InBodyBS();

    public static HtmlTreeBuilderState Text = new TextBS();

    public static HtmlTreeBuilderState InTable = new InTableBS();

    public static HtmlTreeBuilderState InTableText = new InTableTextBS();

    public static HtmlTreeBuilderState InCaption = new InCaptionBS();

    public static HtmlTreeBuilderState InColumnGroup = new InColumnGroupBS();

    public static HtmlTreeBuilderState InTableBody = new InTableBodyBS();

    public static HtmlTreeBuilderState InRow = new InRowBS();

    public static HtmlTreeBuilderState InCell = new InCellBS();

    public static HtmlTreeBuilderState InSelect = new InSelectBS();

    public static HtmlTreeBuilderState InSelectInTable = new InSelectInTableBS();

    public static HtmlTreeBuilderState AfterBody = new AfterBodyBS();

    public static HtmlTreeBuilderState InFrameset = new InFrameSetBS();

    public static HtmlTreeBuilderState AfterFrameset = new AfterFrameSetBS();

    public static HtmlTreeBuilderState AfterAfterBody = new AfterAfterBodyBS();

    public static HtmlTreeBuilderState AfterAfterFrameset = new AfterAfterFrameSetBS();

    public static HtmlTreeBuilderState ForeignContent = new ForeignContentBS();

    private static final String nullString = String.valueOf('\u0000');

    abstract boolean process(Token t, HtmlTreeBuilder tb);

    private static boolean isWhitespace(Token t) {
        if (t.isCharacter()) {
            String data = t.asCharacter().getData();
            return StringUtil.isBlank(data);
        }
        return false;
    }

    private static boolean isWhitespace(String data) {
        return StringUtil.isBlank(data);
    }

    private static void handleRcData(Token.StartTag startTag, HtmlTreeBuilder tb) {
        tb.tokeniser.transition(TokeniserState.Rcdata);
        tb.markInsertionMode();
        tb.transition(Text);
        tb.insert(startTag);
    }

    private static void handleRawtext(Token.StartTag startTag, HtmlTreeBuilder tb) {
        tb.tokeniser.transition(TokeniserState.Rawtext);
        tb.markInsertionMode();
        tb.transition(Text);
        tb.insert(startTag);
    }

    // lists of tags to search through
    static final class Constants {
        static final String[] InHeadEmpty = new String[]{"base", "basefont", "bgsound", "command", "link"};
        static final String[] InHeadRaw = new String[]{"noframes", "style"};
        static final String[] InHeadEnd = new String[]{"body", "br", "html"};
        static final String[] AfterHeadBody = new String[]{"body", "html"};
        static final String[] BeforeHtmlToHead = new String[]{"body", "br", "head", "html", };
        static final String[] InHeadNoScriptHead = new String[]{"basefont", "bgsound", "link", "meta", "noframes", "style"};
        static final String[] InBodyStartToHead = new String[]{"base", "basefont", "bgsound", "command", "link", "meta", "noframes", "script", "style", "title"};
        static final String[] InBodyStartPClosers = new String[]{"address", "article", "aside", "blockquote", "center", "details", "dir", "div", "dl",
            "fieldset", "figcaption", "figure", "footer", "header", "hgroup", "menu", "nav", "ol",
            "p", "section", "summary", "ul"};
        static final String[] Headings = new String[]{"h1", "h2", "h3", "h4", "h5", "h6"};
        static final String[] InBodyStartLiBreakers = new String[]{"address", "div", "p"};
        static final String[] DdDt = new String[]{"dd", "dt"};
        static final String[] Formatters = new String[]{"b", "big", "code", "em", "font", "i", "s", "small", "strike", "strong", "tt", "u"};
        static final String[] InBodyStartApplets = new String[]{"applet", "marquee", "object"};
        static final String[] InBodyStartEmptyFormatters = new String[]{"area", "br", "embed", "img", "keygen", "wbr"};
        static final String[] InBodyStartMedia = new String[]{"param", "source", "track"};
        static final String[] InBodyStartInputAttribs = new String[]{"action", "name", "prompt"};
        static final String[] InBodyStartDrop = new String[]{"caption", "col", "colgroup", "frame", "head", "tbody", "td", "tfoot", "th", "thead", "tr"};
        static final String[] InBodyEndClosers = new String[]{"address", "article", "aside", "blockquote", "button", "center", "details", "dir", "div",
            "dl", "fieldset", "figcaption", "figure", "footer", "header", "hgroup", "listing", "menu",
            "nav", "ol", "pre", "section", "summary", "ul"};
        static final String[] InBodyEndAdoptionFormatters = new String[]{"a", "b", "big", "code", "em", "font", "i", "nobr", "s", "small", "strike", "strong", "tt", "u"};
        static final String[] InBodyEndTableFosters = new String[]{"table", "tbody", "tfoot", "thead", "tr"};
        static final String[] InTableToBody = new String[]{"tbody", "tfoot", "thead"};
        static final String[] InTableAddBody = new String[]{"td", "th", "tr"};
        static final String[] InTableToHead = new String[]{"script", "style"};
        static final String[] InCellNames = new String[]{"td", "th"};
        static final String[] InCellBody = new String[]{"body", "caption", "col", "colgroup", "html"};
        static final String[] InCellTable = new String[]{ "table", "tbody", "tfoot", "thead", "tr"};
        static final String[] InCellCol = new String[]{"caption", "col", "colgroup", "tbody", "td", "tfoot", "th", "thead", "tr"};
        static final String[] InTableEndErr = new String[]{"body", "caption", "col", "colgroup", "html", "tbody", "td", "tfoot", "th", "thead", "tr"};
        static final String[] InTableFoster = new String[]{"table", "tbody", "tfoot", "thead", "tr"};
        static final String[] InTableBodyExit = new String[]{"caption", "col", "colgroup", "tbody", "tfoot", "thead"};
        static final String[] InTableBodyEndIgnore = new String[]{"body", "caption", "col", "colgroup", "html", "td", "th", "tr"};
        static final String[] InRowMissing = new String[]{"caption", "col", "colgroup", "tbody", "tfoot", "thead", "tr"};
        static final String[] InRowIgnore = new String[]{"body", "caption", "col", "colgroup", "html", "td", "th"};
        static final String[] InSelectEnd = new String[]{"input", "keygen", "textarea"};
        static final String[] InSelecTableEnd = new String[]{"caption", "table", "tbody", "td", "tfoot", "th", "thead", "tr"};
        static final String[] InTableEndIgnore = new String[]{"tbody", "tfoot", "thead"};
        static final String[] InHeadNoscriptIgnore = new String[]{"head", "noscript"};
        static final String[] InCaptionIgnore = new String[]{"body", "col", "colgroup", "html", "tbody", "td", "tfoot", "th", "thead", "tr"};
    }

    private static final class InitialBS extends HtmlTreeBuilderState {

        @Override
        public String toString() {
            return "Initial";
        }

        boolean process(Token t, HtmlTreeBuilder tb) {
            if (isWhitespace(t)) {
                return true; // ignore whitespace until we get the first content
            } else if (t.isComment()) {
                tb.insert(t.asComment());
            } else if (t.isDoctype()) {
                Token.Doctype d = t.asDoctype();
                DocumentType doctype = new DocumentType(
                        tb.settings.normalizeTag(d.getName()), d.getPublicIdentifier(), d.getSystemIdentifier());
                doctype.setPubSysKey(d.getPubSysKey());
                tb.getDocument().appendChild(doctype);
                if (d.isForceQuirks())
                    tb.getDocument().quirksMode(Document.QuirksMode.quirks);
                tb.transition(BeforeHtml);
            } else {
                tb.transition(BeforeHtml);
                return tb.process(t); // re-process token
            }
            return true;
        }
    }

    private static final class BeforeHtmlBS extends HtmlTreeBuilderState {

        @Override
        public String toString() {
            return "BeforeHtml";
        }

        boolean process(Token t, HtmlTreeBuilder tb) {
            if (t.isDoctype()) {
                tb.error(this);
                return false;
            } else if (t.isComment()) {
                tb.insert(t.asComment());
            } else if (isWhitespace(t)) {
                tb.insert(t.asCharacter()); // out of spec - include whitespace
            } else if (t.isStartTag() && t.asStartTag().normalName().equals("html")) {
                tb.insert(t.asStartTag());
                tb.transition(BeforeHead);
            } else if (t.isEndTag() && (StringUtil.inSorted(t.asEndTag().normalName(), Constants.BeforeHtmlToHead))) {
                return anythingElse(t, tb);
            } else if (t.isEndTag()) {
                tb.error(this);
                return false;
            } else {
                return anythingElse(t, tb);
            }
            return true;
        }

        private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
            tb.insertStartTag("html");
            tb.transition(BeforeHead);
            return tb.process(t);
        }
    }

    private static final class BeforeHeadBS extends HtmlTreeBuilderState {

        @Override
        public String toString() {
            return "BeforeHead";
        }

        boolean process(Token t, HtmlTreeBuilder tb) {
            if (isWhitespace(t)) {
                tb.insert(t.asCharacter()); // out of spec - include whitespace
            } else if (t.isComment()) {
                tb.insert(t.asComment());
            } else if (t.isDoctype()) {
                tb.error(this);
                return false;
            } else if (t.isStartTag() && t.asStartTag().normalName().equals("html")) {
                return InBody.process(t, tb); // does not transition
            } else if (t.isStartTag() && t.asStartTag().normalName().equals("head")) {
                Element head = tb.insert(t.asStartTag());
                tb.setHeadElement(head);
                tb.transition(InHead);
            } else if (t.isEndTag() && (StringUtil.inSorted(t.asEndTag().normalName(), Constants.BeforeHtmlToHead))) {
                tb.processStartTag("head");
                return tb.process(t);
            } else if (t.isEndTag()) {
                tb.error(this);
                return false;
            } else {
                tb.processStartTag("head");
                return tb.process(t);
            }
            return true;
        }
    }

    private static final class InHeadBS extends HtmlTreeBuilderState {

        @Override
        public String toString() {
            return "InHead";
        }

        boolean process(Token t, HtmlTreeBuilder tb) {
            if (isWhitespace(t)) {
                tb.insert(t.asCharacter()); // out of spec - include whitespace
                return true;
            }
            String name;
            switch (t.type) {
                case Comment:
                    tb.insert(t.asComment());
                    break;
                case Doctype:
                    tb.error(this);
                    return false;
                case StartTag:
                    Token.StartTag start = t.asStartTag();
                    name = start.normalName();
                    if (name.equals("html")) {
                        return InBody.process(t, tb);
                    } else if (StringUtil.inSorted(name, Constants.InHeadEmpty)) {
                        Element el = tb.insertEmpty(start);
                        // jsoup special: update base the first time it is seen
                        if (name.equals("base") && el.hasAttr("href"))
                            tb.maybeSetBaseUri(el);
                    } else if (name.equals("meta")) {
                        tb.insertEmpty(start);
                    } else if (name.equals("title")) {
                        handleRcData(start, tb);
                    } else if (StringUtil.inSorted(name, Constants.InHeadRaw)) {
                        handleRawtext(start, tb);
                    } else if (name.equals("noscript")) {
                        // else if noscript && scripting flag = true: rawtext (jsoup doesn't run script, to handle as noscript)
                        tb.insert(start);
                        tb.transition(InHeadNoscript);
                    } else if (name.equals("script")) {
                        // skips some script rules as won't execute them

                        tb.tokeniser.transition(TokeniserState.ScriptData);
                        tb.markInsertionMode();
                        tb.transition(Text);
                        tb.insert(start);
                    } else if (name.equals("head")) {
                        tb.error(this);
                        return false;
                    } else {
                        return anythingElse(t, tb);
                    }
                    break;
                case EndTag:
                    Token.EndTag end = t.asEndTag();
                    name = end.normalName();
                    if (name.equals("head")) {
                        tb.pop();
                        tb.transition(AfterHead);
                    } else if (StringUtil.inSorted(name, Constants.InHeadEnd)) {
                        return anythingElse(t, tb);
                    } else {
                        tb.error(this);
                        return false;
                    }
                    break;
                default:
                    return anythingElse(t, tb);
            }
            return true;
        }

        private boolean anythingElse(Token t, TreeBuilder tb) {
            tb.processEndTag("head");
            return tb.process(t);
        }
    }

    private static final class InHeadNoScriptBS extends HtmlTreeBuilderState {

        @Override
        public String toString() {
            return "InHeadNoscript";
        }

        boolean process(Token t, HtmlTreeBuilder tb) {
            if (t.isDoctype()) {
                tb.error(this);
            } else if (t.isStartTag() && t.asStartTag().normalName().equals("html")) {
                return tb.process(t, InBody);
            } else if (t.isEndTag() && t.asEndTag().normalName().equals("noscript")) {
                tb.pop();
                tb.transition(InHead);
            } else if (isWhitespace(t) || t.isComment() || (t.isStartTag() && StringUtil.inSorted(t.asStartTag().normalName(),
                    Constants.InHeadNoScriptHead))) {
                return tb.process(t, InHead);
            } else if (t.isEndTag() && t.asEndTag().normalName().equals("br")) {
                return anythingElse(t, tb);
            } else if ((t.isStartTag() && StringUtil.inSorted(t.asStartTag().normalName(), Constants.InHeadNoscriptIgnore)) || t.isEndTag()) {
                tb.error(this);
                return false;
            } else {
                return anythingElse(t, tb);
            }
            return true;
        }

        private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
            // note that this deviates from spec, which is to pop out of noscript and reprocess in head:
            // https://html.spec.whatwg.org/multipage/parsing.html#parsing-main-inheadnoscript
            // allows content to be inserted as data
            tb.error(this);
            tb.insert(new Token.Character().data(t.toString()));
            return true;
        }
    }

    private static final class AfterHeadBS extends HtmlTreeBuilderState {

        @Override
        public String toString() {
            return "AfterHead";
        }

        boolean process(Token t, HtmlTreeBuilder tb) {
            if (isWhitespace(t)) {
                tb.insert(t.asCharacter());
            } else if (t.isComment()) {
                tb.insert(t.asComment());
            } else if (t.isDoctype()) {
                tb.error(this);
            } else if (t.isStartTag()) {
                Token.StartTag startTag = t.asStartTag();
                String name = startTag.normalName();
                if (name.equals("html")) {
                    return tb.process(t, InBody);
                } else if (name.equals("body")) {
                    tb.insert(startTag);
                    tb.framesetOk(false);
                    tb.transition(InBody);
                } else if (name.equals("frameset")) {
                    tb.insert(startTag);
                    tb.transition(InFrameset);
                } else if (StringUtil.inSorted(name, Constants.InBodyStartToHead)) {
                    tb.error(this);
                    Element head = tb.getHeadElement();
                    tb.push(head);
                    tb.process(t, InHead);
                    tb.removeFromStack(head);
                } else if (name.equals("head")) {
                    tb.error(this);
                    return false;
                } else {
                    anythingElse(t, tb);
                }
            } else if (t.isEndTag()) {
                if (StringUtil.inSorted(t.asEndTag().normalName(), Constants.AfterHeadBody)) {
                    anythingElse(t, tb);
                } else {
                    tb.error(this);
                    return false;
                }
            } else {
                anythingElse(t, tb);
            }
            return true;
        }

        private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
            tb.processStartTag("body");
            tb.framesetOk(true);
            return tb.process(t);
        }
    }

    private static final class InBodyBS extends HtmlTreeBuilderState {

        @Override
        public String toString() {
            return "InBody";
        }

        boolean process(Token t, HtmlTreeBuilder tb) {
            switch (t.type) {
                case Character: {
                    Token.Character c = t.asCharacter();
                    if (c.getData().equals(nullString)) {
                        tb.error(this);
                        return false;
                    } else if (tb.framesetOk() && isWhitespace(c)) { // don't check if whitespace if frames already closed
                        tb.reconstructFormattingElements();
                        tb.insert(c);
                    } else {
                        tb.reconstructFormattingElements();
                        tb.insert(c);
                        tb.framesetOk(false);
                    }
                    break;
                }
                case Comment: {
                    tb.insert(t.asComment());
                    break;
                }
                case Doctype: {
                    tb.error(this);
                    return false;
                }
                case StartTag:
                    return inBodyStartTag(t, tb);
                case EndTag:
                    return inBodyEndTag(t, tb);
                case EOF:
                    // stop parsing
                    break;
            }
            return true;
        }

        private boolean inBodyStartTag(Token t, HtmlTreeBuilder tb) {
            final Token.StartTag startTag = t.asStartTag();
            final String name = startTag.normalName();
            final ArrayList<Element> stack;
            Element el;

            switch (name) {
                case "a":
                    if (tb.getActiveFormattingElement("a") != null) {
                        tb.error(this);
                        tb.processEndTag("a");

                        // still on stack?
                        Element remainingA = tb.getFromStack("a");
                        if (remainingA != null) {
                            tb.removeFromActiveFormattingElements(remainingA);
                            tb.removeFromStack(remainingA);
                        }
                    }
                    tb.reconstructFormattingElements();
                    el = tb.insert(startTag);
                    tb.pushActiveFormattingElements(el);
                    break;
                case "span":
                    // same as final else, but short circuits lots of checks
                    tb.reconstructFormattingElements();
                    tb.insert(startTag);
                    break;
                case "li":
                    tb.framesetOk(false);
                    stack = tb.getStack();
                    for (int i = stack.size() - 1; i > 0; i--) {
                        el = stack.get(i);
                        if (el.normalName().equals("li")) {
                            tb.processEndTag("li");
                            break;
                        }
                        if (tb.isSpecial(el) && !StringUtil.inSorted(el.normalName(), Constants.InBodyStartLiBreakers))
                            break;
                    }
                    if (tb.inButtonScope("p")) {
                        tb.processEndTag("p");
                    }
                    tb.insert(startTag);
                    break;
                case "html":
                    tb.error(this);
                    // merge attributes onto real html
                    Element html = tb.getStack().get(0);
                    if (startTag.hasAttributes()) {
                        for (Attribute attribute : startTag.attributes) {
                            if (!html.hasAttr(attribute.getKey()))
                                html.attributes().put(attribute);
                        }
                    }
                    break;
                case "body":
                    tb.error(this);
                    stack = tb.getStack();
                    if (stack.size() == 1 || (stack.size() > 2 && !stack.get(1).normalName().equals("body"))) {
                        // only in fragment case
                        return false; // ignore
                    } else {
                        tb.framesetOk(false);
                        Element body = stack.get(1);
                        if (startTag.hasAttributes()) {
                            for (Attribute attribute : startTag.attributes) {
                                if (!body.hasAttr(attribute.getKey()))
                                    body.attributes().put(attribute);
                            }
                        }
                    }
                    break;
                case "frameset":
                    tb.error(this);
                    stack = tb.getStack();
                    if (stack.size() == 1 || (stack.size() > 2 && !stack.get(1).normalName().equals("body"))) {
                        // only in fragment case
                        return false; // ignore
                    } else if (!tb.framesetOk()) {
                        return false; // ignore frameset
                    } else {
                        Element second = stack.get(1);
                        if (second.parent() != null)
                            second.remove();
                        // pop up to html element
                        while (stack.size() > 1)
                            stack.remove(stack.size() - 1);
                        tb.insert(startTag);
                        tb.transition(InFrameset);
                    }
                    break;
                case "form":
                    if (tb.getFormElement() != null) {
                        tb.error(this);
                        return false;
                    }
                    if (tb.inButtonScope("p")) {
                        tb.processEndTag("p");
                    }
                    tb.insertForm(startTag, true);
                    break;
                case "plaintext":
                    if (tb.inButtonScope("p")) {
                        tb.processEndTag("p");
                    }
                    tb.insert(startTag);
                    tb.tokeniser.transition(TokeniserState.PLAINTEXT); // once in, never gets out
                    break;
                case "button":
                    if (tb.inButtonScope("button")) {
                        // close and reprocess
                        tb.error(this);
                        tb.processEndTag("button");
                        tb.process(startTag);
                    } else {
                        tb.reconstructFormattingElements();
                        tb.insert(startTag);
                        tb.framesetOk(false);
                    }
                    break;
                case "nobr":
                    tb.reconstructFormattingElements();
                    if (tb.inScope("nobr")) {
                        tb.error(this);
                        tb.processEndTag("nobr");
                        tb.reconstructFormattingElements();
                    }
                    el = tb.insert(startTag);
                    tb.pushActiveFormattingElements(el);
                    break;
                case "table":
                    if (tb.getDocument().quirksMode() != Document.QuirksMode.quirks && tb.inButtonScope("p")) {
                        tb.processEndTag("p");
                    }
                    tb.insert(startTag);
                    tb.framesetOk(false);
                    tb.transition(InTable);
                    break;
                case "input":
                    tb.reconstructFormattingElements();
                    el = tb.insertEmpty(startTag);
                    if (!el.attr("type").equalsIgnoreCase("hidden"))
                        tb.framesetOk(false);
                    break;
                case "hr":
                    if (tb.inButtonScope("p")) {
                        tb.processEndTag("p");
                    }
                    tb.insertEmpty(startTag);
                    tb.framesetOk(false);
                    break;
                case "image":
                    if (tb.getFromStack("svg") == null)
                        return tb.process(startTag.name("img")); // change <image> to <img>, unless in svg
                    else
                        tb.insert(startTag);
                    break;
                case "isindex":
                    // how much do we care about the early 90s?
                    tb.error(this);
                    if (tb.getFormElement() != null)
                        return false;

                    tb.processStartTag("form");
                    if (startTag.hasAttribute("action")) {
                        Element form = tb.getFormElement();
                        form.attr("action", startTag.attributes.get("action"));
                    }
                    tb.processStartTag("hr");
                    tb.processStartTag("label");
                    // hope you like english.
                    String prompt = startTag.hasAttribute("prompt") ?
                        startTag.attributes.get("prompt") :
                        "This is a searchable index. Enter search keywords: ";

                    tb.process(new Token.Character().data(prompt));

                    // input
                    Attributes inputAttribs = new Attributes();
                    if (startTag.hasAttributes()) {
                        for (Attribute attr : startTag.attributes) {
                            if (!StringUtil.inSorted(attr.getKey(), Constants.InBodyStartInputAttribs))
                                inputAttribs.put(attr);
                        }
                    }
                    inputAttribs.put("name", "isindex");
                    tb.processStartTag("input", inputAttribs);
                    tb.processEndTag("label");
                    tb.processStartTag("hr");
                    tb.processEndTag("form");
                    break;
                case "textarea":
                    tb.insert(startTag);
                    if (!startTag.isSelfClosing()) {
                        tb.tokeniser.transition(TokeniserState.Rcdata);
                        tb.markInsertionMode();
                        tb.framesetOk(false);
                        tb.transition(Text);
                    }
                    break;
                case "xmp":
                    if (tb.inButtonScope("p")) {
                        tb.processEndTag("p");
                    }
                    tb.reconstructFormattingElements();
                    tb.framesetOk(false);
                    handleRawtext(startTag, tb);
                    break;
                case "iframe":
                    tb.framesetOk(false);
                    handleRawtext(startTag, tb);
                    break;
                case "noembed":
                    // also handle noscript if script enabled
                    handleRawtext(startTag, tb);
                    break;
                case "select":
                    tb.reconstructFormattingElements();
                    tb.insert(startTag);
                    tb.framesetOk(false);

                    HtmlTreeBuilderState state = tb.state();
                    if (state.equals(InTable) || state.equals(InCaption) || state.equals(InTableBody) || state.equals(InRow) || state.equals(InCell))
                        tb.transition(InSelectInTable);
                    else
                        tb.transition(InSelect);
                    break;
                case "math":
                    tb.reconstructFormattingElements();
                    tb.insert(startTag);
                    break;
                case "svg":
                    tb.reconstructFormattingElements();
                    tb.insert(startTag);
                    break;
                // static final String[] Headings = new String[]{"h1", "h2", "h3", "h4", "h5", "h6"};
                case "h1":
                case "h2":
                case "h3":
                case "h4":
                case "h5":
                case "h6":
                    if (tb.inButtonScope("p")) {
                        tb.processEndTag("p");
                    }
                    if (StringUtil.inSorted(tb.currentElement().normalName(), Constants.Headings)) {
                        tb.error(this);
                        tb.pop();
                    }
                    tb.insert(startTag);
                    break;
                // static final String[] InBodyStartPreListing = new String[]{"listing", "pre"};
                case "pre":
                case "listing":
                    if (tb.inButtonScope("p")) {
                        tb.processEndTag("p");
                    }
                    tb.insert(startTag);
                    tb.reader.matchConsume("\n"); // ignore LF if next token
                    tb.framesetOk(false);
                    break;
                // static final String[] DdDt = new String[]{"dd", "dt"};
                case "dd":
                case "dt":
                    tb.framesetOk(false);
                    stack = tb.getStack();
                    for (int i = stack.size() - 1; i > 0; i--) {
                        el = stack.get(i);
                        if (StringUtil.inSorted(el.normalName(), Constants.DdDt)) {
                            tb.processEndTag(el.normalName());
                            break;
                        }
                        if (tb.isSpecial(el) && !StringUtil.inSorted(el.normalName(), Constants.InBodyStartLiBreakers))
                            break;
                    }
                    if (tb.inButtonScope("p")) {
                        tb.processEndTag("p");
                    }
                    tb.insert(startTag);
                    break;
                // static final String[] InBodyStartOptions = new String[]{"optgroup", "option"};
                case "optgroup":
                case "option":
                    if (tb.currentElement().normalName().equals("option"))
                        tb.processEndTag("option");
                    tb.reconstructFormattingElements();
                    tb.insert(startTag);
                    break;
                // static final String[] InBodyStartRuby = new String[]{"rp", "rt"};
                case "rp":
                case "rt":
                    if (tb.inScope("ruby")) {
                        tb.generateImpliedEndTags();
                        if (!tb.currentElement().normalName().equals("ruby")) {
                            tb.error(this);
                            tb.popStackToBefore("ruby"); // i.e. close up to but not include name
                        }
                        tb.insert(startTag);
                    }
                    break;
                default:
                    if (StringUtil.inSorted(name, Constants.InBodyStartEmptyFormatters)) {
                        tb.reconstructFormattingElements();
                        tb.insertEmpty(startTag);
                        tb.framesetOk(false);
                    } else if (StringUtil.inSorted(name, Constants.InBodyStartPClosers)) {
                        if (tb.inButtonScope("p")) {
                            tb.processEndTag("p");
                        }
                        tb.insert(startTag);
                    } else if (StringUtil.inSorted(name, Constants.InBodyStartToHead)) {
                        return tb.process(t, InHead);
                    } else if (StringUtil.inSorted(name, Constants.Formatters)) {
                        tb.reconstructFormattingElements();
                        el = tb.insert(startTag);
                        tb.pushActiveFormattingElements(el);
                    } else if (StringUtil.inSorted(name, Constants.InBodyStartApplets)) {
                        tb.reconstructFormattingElements();
                        tb.insert(startTag);
                        tb.insertMarkerToFormattingElements();
                        tb.framesetOk(false);
                    } else if (StringUtil.inSorted(name, Constants.InBodyStartMedia)) {
                        tb.insertEmpty(startTag);
                    } else if (StringUtil.inSorted(name, Constants.InBodyStartDrop)) {
                        tb.error(this);
                        return false;
                    } else {
                        tb.reconstructFormattingElements();
                        tb.insert(startTag);
                    }
            }
            return true;
        }

        private boolean inBodyEndTag(Token t, HtmlTreeBuilder tb) {
            final Token.EndTag endTag = t.asEndTag();
            final String name = endTag.normalName();

            switch (name) {
                case "sarcasm": // *sigh*
                case "span":
                    // same as final fall through, but saves short circuit
                    return anyOtherEndTag(t, tb);
                case "li":
                    if (!tb.inListItemScope(name)) {
                        tb.error(this);
                        return false;
                    } else {
                        tb.generateImpliedEndTags(name);
                        if (!tb.currentElement().normalName().equals(name))
                            tb.error(this);
                        tb.popStackToClose(name);
                    }
                    break;
                case "body":
                    if (!tb.inScope("body")) {
                        tb.error(this);
                        return false;
                    } else {
                        tb.transition(AfterBody);
                    }
                    break;
                case "html":
                    boolean notIgnored = tb.processEndTag("body");
                    if (notIgnored)
                        return tb.process(endTag);
                    break;
                case "form":
                    Element currentForm = tb.getFormElement();
                    tb.setFormElement(null);
                    if (currentForm == null || !tb.inScope(name)) {
                        tb.error(this);
                        return false;
                    } else {
                        tb.generateImpliedEndTags();
                        if (!tb.currentElement().normalName().equals(name))
                            tb.error(this);
                        // remove currentForm from stack. will shift anything under up.
                        tb.removeFromStack(currentForm);
                    }
                    break;
                case "p":
                    if (!tb.inButtonScope(name)) {
                        tb.error(this);
                        tb.processStartTag(name); // if no p to close, creates an empty <p></p>
                        return tb.process(endTag);
                    } else {
                        tb.generateImpliedEndTags(name);
                        if (!tb.currentElement().normalName().equals(name))
                            tb.error(this);
                        tb.popStackToClose(name);
                    }
                    break;
                case "dd":
                case "dt":
                    if (!tb.inScope(name)) {
                        tb.error(this);
                        return false;
                    } else {
                        tb.generateImpliedEndTags(name);
                        if (!tb.currentElement().normalName().equals(name))
                            tb.error(this);
                        tb.popStackToClose(name);
                    }
                    break;
                case "h1":
                case "h2":
                case "h3":
                case "h4":
                case "h5":
                case "h6":
                    if (!tb.inScope(Constants.Headings)) {
                        tb.error(this);
                        return false;
                    } else {
                        tb.generateImpliedEndTags(name);
                        if (!tb.currentElement().normalName().equals(name))
                            tb.error(this);
                        tb.popStackToClose(Constants.Headings);
                    }
                    break;
                case "br":
                    tb.error(this);
                    tb.processStartTag("br");
                    return false;
                default:
                    if (StringUtil.inSorted(name, Constants.InBodyEndAdoptionFormatters)) {
                        return inBodyEndTagAdoption(t, tb);
                    } else if (StringUtil.inSorted(name, Constants.InBodyEndClosers)) {
                        if (!tb.inScope(name)) {
                            // nothing to close
                            tb.error(this);
                            return false;
                        } else {
                            tb.generateImpliedEndTags();
                            if (!tb.currentElement().normalName().equals(name))
                                tb.error(this);
                            tb.popStackToClose(name);
                        }
                    } else if (StringUtil.inSorted(name, Constants.InBodyStartApplets)) {
                        if (!tb.inScope("name")) {
                            if (!tb.inScope(name)) {
                                tb.error(this);
                                return false;
                            }
                            tb.generateImpliedEndTags();
                            if (!tb.currentElement().normalName().equals(name))
                                tb.error(this);
                            tb.popStackToClose(name);
                            tb.clearFormattingElementsToLastMarker();
                        }
                    } else {
                        return anyOtherEndTag(t, tb);
                    }
            }
            return true;
        }

        boolean anyOtherEndTag(Token t, HtmlTreeBuilder tb) {
            final String name = t.asEndTag().normalName; // case insensitive search - goal is to preserve output case, not for the parse to be case sensitive
            final ArrayList<Element> stack = tb.getStack();
            for (int pos = stack.size() - 1; pos >= 0; pos--) {
                Element node = stack.get(pos);
                if (node.normalName().equals(name)) {
                    tb.generateImpliedEndTags(name);
                    if (!name.equals(tb.currentElement().normalName()))
                        tb.error(this);
                    tb.popStackToClose(name);
                    break;
                } else {
                    if (tb.isSpecial(node)) {
                        tb.error(this);
                        return false;
                    }
                }
            }
            return true;
        }

        // Adoption Agency Algorithm.
        private boolean inBodyEndTagAdoption(Token t, HtmlTreeBuilder tb) {
            final Token.EndTag endTag = t.asEndTag();
            final String name = endTag.normalName();

            final ArrayList<Element> stack = tb.getStack();
            Element el;
            for (int i = 0; i < 8; i++) {
                Element formatEl = tb.getActiveFormattingElement(name);
                if (formatEl == null)
                    return anyOtherEndTag(t, tb);
                else if (!tb.onStack(formatEl)) {
                    tb.error(this);
                    tb.removeFromActiveFormattingElements(formatEl);
                    return true;
                } else if (!tb.inScope(formatEl.normalName())) {
                    tb.error(this);
                    return false;
                } else if (tb.currentElement() != formatEl)
                    tb.error(this);

                Element furthestBlock = null;
                Element commonAncestor = null;
                boolean seenFormattingElement = false;
                // the spec doesn't limit to < 64, but in degenerate cases (9000+ stack depth) this prevents
                // run-aways
                final int stackSize = stack.size();
                int bookmark = -1;
                for (int si = 0; si < stackSize && si < 64; si++) {
                    el = stack.get(si);
                    if (el == formatEl) {
                        commonAncestor = stack.get(si - 1);
                        seenFormattingElement = true;
                        // Let a bookmark note the position of the formatting element in the list of active formatting elements relative to the elements on either side of it in the list.
                        bookmark = tb.positionOfElement(el);
                    } else if (seenFormattingElement && tb.isSpecial(el)) {
                        furthestBlock = el;
                        break;
                    }
                }
                if (furthestBlock == null) {
                    tb.popStackToClose(formatEl.normalName());
                    tb.removeFromActiveFormattingElements(formatEl);
                    return true;
                }

                Element node = furthestBlock;
                Element lastNode = furthestBlock;
                for (int j = 0; j < 3; j++) {
                    if (tb.onStack(node))
                        node = tb.aboveOnStack(node);
                    if (!tb.isInActiveFormattingElements(node)) { // note no bookmark check
                        tb.removeFromStack(node);
                        continue;
                    } else if (node == formatEl)
                        break;

                    Element replacement = new Element(Tag.valueOf(node.nodeName(), ParseSettings.preserveCase), tb.getBaseUri());
                    // case will follow the original node (so honours ParseSettings)
                    tb.replaceActiveFormattingElement(node, replacement);
                    tb.replaceOnStack(node, replacement);
                    node = replacement;

                    if (lastNode == furthestBlock) {
                        // move the aforementioned bookmark to be immediately after the new node in the list of active formatting elements.
                        // not getting how this bookmark both straddles the element above, but is inbetween here...
                        bookmark = tb.positionOfElement(node) + 1;
                    }
                    if (lastNode.parent() != null)
                        lastNode.remove();
                    node.appendChild(lastNode);

                    lastNode = node;
                }

                if (commonAncestor != null) { // safety check, but would be an error if null
                    if (StringUtil.inSorted(commonAncestor.normalName(), Constants.InBodyEndTableFosters)) {
                        if (lastNode.parent() != null)
                            lastNode.remove();
                        tb.insertInFosterParent(lastNode);
                    } else {
                        if (lastNode.parent() != null)
                            lastNode.remove();
                        commonAncestor.appendChild(lastNode);
                    }
                }

                Element adopter = new Element(formatEl.tag(), tb.getBaseUri());
                adopter.attributes().addAll(formatEl.attributes());
                Node[] childNodes = furthestBlock.childNodes().toArray(new Node[0]);
                for (Node childNode : childNodes) {
                    adopter.appendChild(childNode); // append will reparent. thus the clone to avoid concurrent mod.
                }
                furthestBlock.appendChild(adopter);
                tb.removeFromActiveFormattingElements(formatEl);
                // insert the new element into the list of active formatting elements at the position of the aforementioned bookmark.
                tb.pushWithBookmark(adopter, bookmark);
                tb.removeFromStack(formatEl);
                tb.insertOnStackAfter(furthestBlock, adopter);
            }
            return true;
        }
    }

    private static final class TextBS extends HtmlTreeBuilderState {

        @Override
        public String toString() {
            return "Text";
        }

        // in script, style etc. normally treated as data tags
        boolean process(Token t, HtmlTreeBuilder tb) {
            if (t.isCharacter()) {
                tb.insert(t.asCharacter());
            } else if (t.isEOF()) {
                tb.error(this);
                // if current node is script: already started
                tb.pop();
                tb.transition(tb.originalState());
                return tb.process(t);
            } else if (t.isEndTag()) {
                // if: An end tag whose tag name is "script" -- scripting nesting level, if evaluating scripts
                tb.pop();
                tb.transition(tb.originalState());
            }
            return true;
        }
    }

    private static final class InTableBS extends HtmlTreeBuilderState {

        @Override
        public String toString() {
            return "InTable";
        }

        boolean process(Token t, HtmlTreeBuilder tb) {
            if (t.isCharacter()) {
                tb.newPendingTableCharacters();
                tb.markInsertionMode();
                tb.transition(InTableText);
                return tb.process(t);
            } else if (t.isComment()) {
                tb.insert(t.asComment());
                return true;
            } else if (t.isDoctype()) {
                tb.error(this);
                return false;
            } else if (t.isStartTag()) {
                Token.StartTag startTag = t.asStartTag();
                String name = startTag.normalName();
                if (name.equals("caption")) {
                    tb.clearStackToTableContext();
                    tb.insertMarkerToFormattingElements();
                    tb.insert(startTag);
                    tb.transition(InCaption);
                } else if (name.equals("colgroup")) {
                    tb.clearStackToTableContext();
                    tb.insert(startTag);
                    tb.transition(InColumnGroup);
                } else if (name.equals("col")) {
                    tb.processStartTag("colgroup");
                    return tb.process(t);
                } else if (StringUtil.inSorted(name, Constants.InTableToBody)) {
                    tb.clearStackToTableContext();
                    tb.insert(startTag);
                    tb.transition(InTableBody);
                } else if (StringUtil.inSorted(name, Constants.InTableAddBody)) {
                    tb.processStartTag("tbody");
                    return tb.process(t);
                } else if (name.equals("table")) {
                    tb.error(this);
                    boolean processed = tb.processEndTag("table");
                    if (processed) // only ignored if in fragment
                        return tb.process(t);
                } else if (StringUtil.inSorted(name, Constants.InTableToHead)) {
                    return tb.process(t, InHead);
                } else if (name.equals("input")) {
                    if (!(startTag.hasAttributes() && startTag.attributes.get("type").equalsIgnoreCase("hidden"))) {
                        return anythingElse(t, tb);
                    } else {
                        tb.insertEmpty(startTag);
                    }
                } else if (name.equals("form")) {
                    tb.error(this);
                    if (tb.getFormElement() != null)
                        return false;
                    else {
                        tb.insertForm(startTag, false);
                    }
                } else {
                    return anythingElse(t, tb);
                }
                return true;
            } else if (t.isEndTag()) {
                Token.EndTag endTag = t.asEndTag();
                String name = endTag.normalName();

                if (name.equals("table")) {
                    if (!tb.inTableScope(name)) {
                        tb.error(this);
                        return false;
                    } else {
                        tb.popStackToClose("table");
                    }
                    tb.resetInsertionMode();
                } else if (StringUtil.inSorted(name, Constants.InTableEndErr)) {
                    tb.error(this);
                    return false;
                } else {
                    return anythingElse(t, tb);
                }
                return true;
            } else if (t.isEOF()) {
                if (tb.currentElement().normalName().equals("html"))
                    tb.error(this);
                return true; // stops parsing
            }
            return anythingElse(t, tb);
        }

        boolean anythingElse(Token t, HtmlTreeBuilder tb) {
            tb.error(this);
            boolean processed;
            if (StringUtil.inSorted(tb.currentElement().normalName(), Constants.InTableFoster)) {
                tb.setFosterInserts(true);
                processed = tb.process(t, InBody);
                tb.setFosterInserts(false);
            } else {
                processed = tb.process(t, InBody);
            }
            return processed;
        }
    }

    private static final class InTableTextBS extends HtmlTreeBuilderState {

        @Override
        public String toString() {
            return "InTableText";
        }

        boolean process(Token t, HtmlTreeBuilder tb) {
            if (t.type == Token.TokenType.Character) {
                Token.Character c = t.asCharacter();
                if (c.getData().equals(nullString)) {
                    tb.error(this);
                    return false;
                } else {
                    tb.getPendingTableCharacters().add(c.getData());
                }
            } else {
                if (tb.getPendingTableCharacters().size() > 0) {
                    for (String character : tb.getPendingTableCharacters()) {
                        if (!isWhitespace(character)) {
                            // InTable anything else section:
                            tb.error(this);
                            if (StringUtil.inSorted(tb.currentElement().normalName(), Constants.InTableFoster)) {
                                tb.setFosterInserts(true);
                                tb.process(new Token.Character().data(character), InBody);
                                tb.setFosterInserts(false);
                            } else {
                                tb.process(new Token.Character().data(character), InBody);
                            }
                        } else
                            tb.insert(new Token.Character().data(character));
                    }
                    tb.newPendingTableCharacters();
                }
                tb.transition(tb.originalState());
                return tb.process(t);
            }
            return true;
        }
    }

    private static final class InCaptionBS extends HtmlTreeBuilderState {

        @Override
        public String toString() {
            return "InCaption";
        }

        boolean process(Token t, HtmlTreeBuilder tb) {
            if (t.isEndTag() && t.asEndTag().normalName().equals("caption")) {
                Token.EndTag endTag = t.asEndTag();
                String name = endTag.normalName();
                if (!tb.inTableScope(name)) {
                    tb.error(this);
                    return false;
                } else {
                    tb.generateImpliedEndTags();
                    if (!tb.currentElement().normalName().equals("caption"))
                        tb.error(this);
                    tb.popStackToClose("caption");
                    tb.clearFormattingElementsToLastMarker();
                    tb.transition(InTable);
                }
            } else if ((
                    t.isStartTag() && StringUtil.inSorted(t.asStartTag().normalName(), Constants.InCellCol) ||
                            t.isEndTag() && t.asEndTag().normalName().equals("table"))
                    ) {
                tb.error(this);
                boolean processed = tb.processEndTag("caption");
                if (processed)
                    return tb.process(t);
            } else if (t.isEndTag() && StringUtil.inSorted(t.asEndTag().normalName(), Constants.InCaptionIgnore)) {
                tb.error(this);
                return false;
            } else {
                return tb.process(t, InBody);
            }
            return true;
        }
    }

    private static final class InColumnGroupBS extends HtmlTreeBuilderState {

        @Override
        public String toString() {
            return "InColumnGroup";
        }

        boolean process(Token t, HtmlTreeBuilder tb) {
            if (isWhitespace(t)) {
                tb.insert(t.asCharacter());
                return true;
            }
            switch (t.type) {
                case Comment:
                    tb.insert(t.asComment());
                    break;
                case Doctype:
                    tb.error(this);
                    break;
                case StartTag:
                    Token.StartTag startTag = t.asStartTag();
                    switch (startTag.normalName()) {
                        case "html":
                            return tb.process(t, InBody);
                        case "col":
                            tb.insertEmpty(startTag);
                            break;
                        default:
                            return anythingElse(t, tb);
                    }
                    break;
                case EndTag:
                    Token.EndTag endTag = t.asEndTag();
                    if (endTag.normalName.equals("colgroup")) {
                        if (tb.currentElement().normalName().equals("html")) { // frag case
                            tb.error(this);
                            return false;
                        } else {
                            tb.pop();
                            tb.transition(InTable);
                        }
                    } else
                        return anythingElse(t, tb);
                    break;
                case EOF:
                    if (tb.currentElement().normalName().equals("html"))
                        return true; // stop parsing; frag case
                    else
                        return anythingElse(t, tb);
                default:
                    return anythingElse(t, tb);
            }
            return true;
        }

        private boolean anythingElse(Token t, TreeBuilder tb) {
            boolean processed = tb.processEndTag("colgroup");
            if (processed) // only ignored in frag case
                return tb.process(t);
            return true;
        }
    }

    private static final class InTableBodyBS extends HtmlTreeBuilderState {

        @Override
        public String toString() {
            return "InTableBody";
        }

        boolean process(Token t, HtmlTreeBuilder tb) {
            String name;
            switch (t.type) {
                case StartTag:
                    Token.StartTag startTag = t.asStartTag();
                    name = startTag.normalName();
                    if (name.equals("template")) {
                        tb.insert(startTag);
                    } else if (name.equals("tr")) {
                        tb.clearStackToTableBodyContext();
                        tb.insert(startTag);
                        tb.transition(InRow);
                    } else if (StringUtil.inSorted(name, Constants.InCellNames)) {
                        tb.error(this);
                        tb.processStartTag("tr");
                        return tb.process(startTag);
                    } else if (StringUtil.inSorted(name, Constants.InTableBodyExit)) {
                        return exitTableBody(t, tb);
                    } else
                        return anythingElse(t, tb);
                    break;
                case EndTag:
                    Token.EndTag endTag = t.asEndTag();
                    name = endTag.normalName();
                    if (StringUtil.inSorted(name, Constants.InTableEndIgnore)) {
                        if (!tb.inTableScope(name)) {
                            tb.error(this);
                            return false;
                        } else {
                            tb.clearStackToTableBodyContext();
                            tb.pop();
                            tb.transition(InTable);
                        }
                    } else if (name.equals("table")) {
                        return exitTableBody(t, tb);
                    } else if (StringUtil.inSorted(name, Constants.InTableBodyEndIgnore)) {
                        tb.error(this);
                        return false;
                    } else
                        return anythingElse(t, tb);
                    break;
                default:
                    return anythingElse(t, tb);
            }
            return true;
        }

        private boolean exitTableBody(Token t, HtmlTreeBuilder tb) {
            if (!(tb.inTableScope("tbody") || tb.inTableScope("thead") || tb.inScope("tfoot"))) {
                // frag case
                tb.error(this);
                return false;
            }
            tb.clearStackToTableBodyContext();
            tb.processEndTag(tb.currentElement().normalName()); // tbody, tfoot, thead
            return tb.process(t);
        }

        private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
            return tb.process(t, InTable);
        }
    }

    private static final class InRowBS extends HtmlTreeBuilderState {

        @Override
        public String toString() {
            return "InRow";
        }

        boolean process(Token t, HtmlTreeBuilder tb) {
            if (t.isStartTag()) {
                Token.StartTag startTag = t.asStartTag();
                String name = startTag.normalName();

                if (name.equals("template")) {
                    tb.insert(startTag);
                } else if (StringUtil.inSorted(name, Constants.InCellNames)) {
                    tb.clearStackToTableRowContext();
                    tb.insert(startTag);
                    tb.transition(InCell);
                    tb.insertMarkerToFormattingElements();
                } else if (StringUtil.inSorted(name, Constants.InRowMissing)) {
                    return handleMissingTr(t, tb);
                } else {
                    return anythingElse(t, tb);
                }
            } else if (t.isEndTag()) {
                Token.EndTag endTag = t.asEndTag();
                String name = endTag.normalName();

                if (name.equals("tr")) {
                    if (!tb.inTableScope(name)) {
                        tb.error(this); // frag
                        return false;
                    }
                    tb.clearStackToTableRowContext();
                    tb.pop(); // tr
                    tb.transition(InTableBody);
                } else if (name.equals("table")) {
                    return handleMissingTr(t, tb);
                } else if (StringUtil.inSorted(name, Constants.InTableToBody)) {
                    if (!tb.inTableScope(name) || !tb.inTableScope("tr")) {
                        tb.error(this);
                        return false;
                    }
                    tb.clearStackToTableRowContext();
                    tb.pop(); // tr
                    tb.transition(InTableBody);
                } else if (StringUtil.inSorted(name, Constants.InRowIgnore)) {
                    tb.error(this);
                    return false;
                } else {
                    return anythingElse(t, tb);
                }
            } else {
                return anythingElse(t, tb);
            }
            return true;
        }

        private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
            return tb.process(t, InTable);
        }

        private boolean handleMissingTr(Token t, TreeBuilder tb) {
            boolean processed = tb.processEndTag("tr");
            if (processed)
                return tb.process(t);
            else
                return false;
        }
    }

    private static final class InCellBS extends HtmlTreeBuilderState {

        @Override
        public String toString() {
            return "InCell";
        }

        boolean process(Token t, HtmlTreeBuilder tb) {
            if (t.isEndTag()) {
                Token.EndTag endTag = t.asEndTag();
                String name = endTag.normalName();

                if (StringUtil.inSorted(name, Constants.InCellNames)) {
                    if (!tb.inTableScope(name)) {
                        tb.error(this);
                        tb.transition(InRow); // might not be in scope if empty: <td /> and processing fake end tag
                        return false;
                    }
                    tb.generateImpliedEndTags();
                    if (!tb.currentElement().normalName().equals(name))
                        tb.error(this);
                    tb.popStackToClose(name);
                    tb.clearFormattingElementsToLastMarker();
                    tb.transition(InRow);
                } else if (StringUtil.inSorted(name, Constants.InCellBody)) {
                    tb.error(this);
                    return false;
                } else if (StringUtil.inSorted(name, Constants.InCellTable)) {
                    if (!tb.inTableScope(name)) {
                        tb.error(this);
                        return false;
                    }
                    closeCell(tb);
                    return tb.process(t);
                } else {
                    return anythingElse(t, tb);
                }
            } else if (t.isStartTag() &&
                    StringUtil.inSorted(t.asStartTag().normalName(), Constants.InCellCol)) {
                if (!(tb.inTableScope("td") || tb.inTableScope("th"))) {
                    tb.error(this);
                    return false;
                }
                closeCell(tb);
                return tb.process(t);
            } else {
                return anythingElse(t, tb);
            }
            return true;
        }

        private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
            return tb.process(t, InBody);
        }

        private void closeCell(HtmlTreeBuilder tb) {
            if (tb.inTableScope("td"))
                tb.processEndTag("td");
            else
                tb.processEndTag("th"); // only here if th or td in scope
        }
    }

    private static final class InSelectBS extends HtmlTreeBuilderState {

        @Override
        public String toString() {
            return "InSelect";
        }

        boolean process(Token t, HtmlTreeBuilder tb) {
            String name;
            switch (t.type) {
                case Character:
                    Token.Character c = t.asCharacter();
                    if (c.getData().equals(nullString)) {
                        tb.error(this);
                        return false;
                    } else {
                        tb.insert(c);
                    }
                    break;
                case Comment:
                    tb.insert(t.asComment());
                    break;
                case Doctype:
                    tb.error(this);
                    return false;
                case StartTag:
                    Token.StartTag start = t.asStartTag();
                    name = start.normalName();
                    if (name.equals("html"))
                        return tb.process(start, InBody);
                    else if (name.equals("option")) {
                        if (tb.currentElement().normalName().equals("option"))
                            tb.processEndTag("option");
                        tb.insert(start);
                    } else if (name.equals("optgroup")) {
                        if (tb.currentElement().normalName().equals("option"))
                            tb.processEndTag("option"); // pop option and flow to pop optgroup
                        if (tb.currentElement().normalName().equals("optgroup"))
                            tb.processEndTag("optgroup");
                        tb.insert(start);
                    } else if (name.equals("select")) {
                        tb.error(this);
                        return tb.processEndTag("select");
                    } else if (StringUtil.inSorted(name, Constants.InSelectEnd)) {
                        tb.error(this);
                        if (!tb.inSelectScope("select"))
                            return false; // frag
                        tb.processEndTag("select");
                        return tb.process(start);
                    } else if (name.equals("script")) {
                        return tb.process(t, InHead);
                    } else {
                        return anythingElse(t, tb);
                    }
                    break;
                case EndTag:
                    Token.EndTag end = t.asEndTag();
                    name = end.normalName();
                    switch (name) {
                        case "optgroup":
                            if (tb.currentElement().normalName().equals("option") && tb.aboveOnStack(tb.currentElement()) != null && tb.aboveOnStack(tb.currentElement()).normalName().equals("optgroup"))
                                tb.processEndTag("option");
                            if (tb.currentElement().normalName().equals("optgroup"))
                                tb.pop();
                            else
                                tb.error(this);
                            break;
                        case "option":
                            if (tb.currentElement().normalName().equals("option"))
                                tb.pop();
                            else
                                tb.error(this);
                            break;
                        case "select":
                            if (!tb.inSelectScope(name)) {
                                tb.error(this);
                                return false;
                            } else {
                                tb.popStackToClose(name);
                                tb.resetInsertionMode();
                            }
                            break;
                        default:
                            return anythingElse(t, tb);
                    }
                    break;
                case EOF:
                    if (!tb.currentElement().normalName().equals("html"))
                        tb.error(this);
                    break;
                default:
                    return anythingElse(t, tb);
            }
            return true;
        }

        private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
            tb.error(this);
            return false;
        }
    }

    private static final class InSelectInTableBS extends HtmlTreeBuilderState {

        @Override
        public String toString() {
            return "InSelectInTable";
        }

        boolean process(Token t, HtmlTreeBuilder tb) {
            if (t.isStartTag() && StringUtil.inSorted(t.asStartTag().normalName(), Constants.InSelecTableEnd)) {
                tb.error(this);
                tb.processEndTag("select");
                return tb.process(t);
            } else if (t.isEndTag() && StringUtil.inSorted(t.asEndTag().normalName(),Constants.InSelecTableEnd )) {
                tb.error(this);
                if (tb.inTableScope(t.asEndTag().normalName())) {
                    tb.processEndTag("select");
                    return (tb.process(t));
                } else
                    return false;
            } else {
                return tb.process(t, InSelect);
            }
        }
    }

    private static final class AfterBodyBS extends HtmlTreeBuilderState {

        @Override
        public String toString() {
            return "AfterBody";
        }

        boolean process(Token t, HtmlTreeBuilder tb) {
            if (isWhitespace(t)) {
                tb.insert(t.asCharacter()); // out of spec - include whitespace. spec would move into body
            } else if (t.isComment()) {
                tb.insert(t.asComment()); // into html node
            } else if (t.isDoctype()) {
                tb.error(this);
                return false;
            } else if (t.isStartTag() && t.asStartTag().normalName().equals("html")) {
                return tb.process(t, InBody);
            } else if (t.isEndTag() && t.asEndTag().normalName().equals("html")) {
                if (tb.isFragmentParsing()) {
                    tb.error(this);
                    return false;
                } else {
                    tb.transition(AfterAfterBody);
                }
            } else if (t.isEOF()) {
                // chillax! we're done
            } else {
                tb.error(this);
                tb.transition(InBody);
                return tb.process(t);
            }
            return true;
        }
    }

    private static final class InFrameSetBS extends HtmlTreeBuilderState {

        @Override
        public String toString() {
            return "InFrameset";
        }

        boolean process(Token t, HtmlTreeBuilder tb) {
            if (isWhitespace(t)) {
                tb.insert(t.asCharacter());
            } else if (t.isComment()) {
                tb.insert(t.asComment());
            } else if (t.isDoctype()) {
                tb.error(this);
                return false;
            } else if (t.isStartTag()) {
                Token.StartTag start = t.asStartTag();
                switch (start.normalName()) {
                    case "html":
                        return tb.process(start, InBody);
                    case "frameset":
                        tb.insert(start);
                        break;
                    case "frame":
                        tb.insertEmpty(start);
                        break;
                    case "noframes":
                        return tb.process(start, InHead);
                    default:
                        tb.error(this);
                        return false;
                }
            } else if (t.isEndTag() && t.asEndTag().normalName().equals("frameset")) {
                if (tb.currentElement().normalName().equals("html")) { // frag
                    tb.error(this);
                    return false;
                } else {
                    tb.pop();
                    if (!tb.isFragmentParsing() && !tb.currentElement().normalName().equals("frameset")) {
                        tb.transition(AfterFrameset);
                    }
                }
            } else if (t.isEOF()) {
                if (!tb.currentElement().normalName().equals("html")) {
                    tb.error(this);
                    return true;
                }
            } else {
                tb.error(this);
                return false;
            }
            return true;
        }
    }

    private static final class AfterFrameSetBS extends HtmlTreeBuilderState {

        @Override
        public String toString() {
            return "AfterFrameset";
        }

        boolean process(Token t, HtmlTreeBuilder tb) {
            if (isWhitespace(t)) {
                tb.insert(t.asCharacter());
            } else if (t.isComment()) {
                tb.insert(t.asComment());
            } else if (t.isDoctype()) {
                tb.error(this);
                return false;
            } else if (t.isStartTag() && t.asStartTag().normalName().equals("html")) {
                return tb.process(t, InBody);
            } else if (t.isEndTag() && t.asEndTag().normalName().equals("html")) {
                tb.transition(AfterAfterFrameset);
            } else if (t.isStartTag() && t.asStartTag().normalName().equals("noframes")) {
                return tb.process(t, InHead);
            } else if (t.isEOF()) {
                // cool your heels, we're complete
            } else {
                tb.error(this);
                return false;
            }
            return true;
        }
    }

    private static final class AfterAfterBodyBS extends HtmlTreeBuilderState {

        @Override
        public String toString() {
            return "AfterAfterBody";
        }

        boolean process(Token t, HtmlTreeBuilder tb) {
            if (t.isComment()) {
                tb.insert(t.asComment());
            } else if (t.isDoctype() || (t.isStartTag() && t.asStartTag().normalName().equals("html"))) {
                return tb.process(t, InBody);
            } else if (isWhitespace(t)) {
                // allows space after </html>, and put the body back on stack to allow subsequent tags if any
                Element html = tb.popStackToClose("html");
                tb.insert(t.asCharacter());
                tb.stack.add(html);
                tb.stack.add(html.selectFirst("body"));
            } else if (t.isEOF()) {
                // nice work chuck
            } else {
                tb.error(this);
                tb.transition(InBody);
                return tb.process(t);
            }
            return true;
        }
    }

    private static final class AfterAfterFrameSetBS extends HtmlTreeBuilderState {

        @Override
        public String toString() {
            return "AfterAfterFrameset";
        }

        boolean process(Token t, HtmlTreeBuilder tb) {
            if (t.isComment()) {
                tb.insert(t.asComment());
            } else if (t.isDoctype() || isWhitespace(t) || (t.isStartTag() && t.asStartTag().normalName().equals("html"))) {
                return tb.process(t, InBody);
            } else if (t.isEOF()) {
                // nice work chuck
            } else if (t.isStartTag() && t.asStartTag().normalName().equals("noframes")) {
                return tb.process(t, InHead);
            } else {
                tb.error(this);
                return false;
            }
            return true;
        }
    }

    private static final class ForeignContentBS extends HtmlTreeBuilderState {

        @Override
        public String toString() {
            return "ForeignContent";
        }

        boolean process(Token t, HtmlTreeBuilder tb) {
            return true;
        }
    }
}
