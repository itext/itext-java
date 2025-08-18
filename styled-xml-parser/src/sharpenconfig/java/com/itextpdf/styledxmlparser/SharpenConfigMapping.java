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
package com.itextpdf.styledxmlparser;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import sharpen.config.MappingConfiguration;
import sharpen.config.MappingConfigurator;
import sharpen.config.ModuleOption;
import sharpen.config.ModulesConfigurator;
import sharpen.config.OptionsConfigurator;

public class SharpenConfigMapping implements MappingConfiguration {
    @Override
    public int getMappingPriority() {
        return 15;
    }

    @Override
    public String getModuleName() {
        return "styledxmlparser";
    }

    @Override
    public void applyMappingConfiguration(MappingConfigurator configurator) {
        configurator.mapNamespace("styledxmlparser", "StyledXmlParser");
        configurator.addFullName("iText.StyledXmlParser.Jsoup.Nodes.Node");
        configurator.addFullName("iText.StyledXmlParser.Jsoup.Nodes.Attribute");
        configurator.addFullName("iText.StyledXmlParser.Jsoup.Nodes.Element");
        configurator.addFullName("iText.StyledXmlParser.Jsoup.Internal.StringUtil");
        configurator.addFullName("iText.StyledXmlParser.Jsoup.Parser.Tag");
        configurator.addFullName("iText.StyledXmlParser.Jsoup.Parser.TokenType");
        configurator.addFullName("iText.StyledXmlParser.Logs.StyledXmlParserLogMessageConstant");
        configurator.addFullName("iText.StyledXmlParser.Jsoup.Nodes.Syntax");
        configurator.mapType("com.itextpdf.styledxmlparser.jsoup.parser.Token.TokenType", "iText.StyledXmlParser.Jsoup.Parser.TokenType");
        configurator.mapType("com.itextpdf.styledxmlparser.jsoup.nodes.Document.QuirksMode", "iText.StyledXmlParser.Jsoup.Nodes.QuirksMode");
        configurator.mapType("com.itextpdf.styledxmlparser.jsoup.nodes.Document.OutputSettings", "iText.StyledXmlParser.Jsoup.Nodes.OutputSettings");
        configurator.mapType("com.itextpdf.styledxmlparser.jsoup.nodes.Document.OutputSettings.Syntax", "iText.StyledXmlParser.Jsoup.Nodes.Syntax");
        configurator.mapType("com.itextpdf.styledxmlparser.jsoup.select.Evaluator.Matches", "MatchesElement");
        configurator.keepInternalProtected("com.itextpdf.styledxmlparser.jsoup.integration.SafelistExtensionTest.OpenSafelist.isSafeAttribute");
        configurator.keepInternalProtected("com.itextpdf.styledxmlparser.jsoup.integration.SafelistExtensionTest.OpenSafelist.isSafeTag");
        configurator.mapMethod("com.itextpdf.styledxmlparser.jsoup.helper.DataUtil.emptyByteBuffer", "iText.StyledXmlParser.Jsoup.Helper.ByteBuffer.EmptyByteBuffer");
        configurator.removeMethod("com.itextpdf.styledxmlparser.jsoup.helper.DataUtil.emptyByteBuffer", true);
        configurator.mapMethod("com.itextpdf.styledxmlparser.jsoup.nodes.Document.OutputSettings.partialClone", "MemberwiseClone");
        configurator.removeMethod("com.itextpdf.styledxmlparser.jsoup.nodes.Document.OutputSettings.partialClone", true);
        configurator.mapMethod("com.itextpdf.styledxmlparser.jsoup.nodes.Node.partialClone", "MemberwiseClone");
        configurator.removeMethod("com.itextpdf.styledxmlparser.jsoup.nodes.Node.partialClone", true);
        configurator.mapMethod("com.itextpdf.styledxmlparser.jsoup.nodes.Attributes.iterator", "GetEnumerator");
        configurator.mapField("com.itextpdf.styledxmlparser.jsoup.PortUtil.escapedSingleBracket", "EscapedSingleBracket");
        configurator.mapField("com.itextpdf.styledxmlparser.jsoup.PortUtil.signedNumberFormat", "SignedNumberFormat");
        configurator.mapMethod("com.itextpdf.styledxmlparser.jsoup.integration.ParseTest.getFile", "iText.StyledXmlParser.Jsoup.PortTestUtil.GetFile");
        configurator.removeMethod("com.itextpdf.styledxmlparser.jsoup.integration.ParseTest.getFile", true);
        configurator.removeMethod("com.itextpdf.styledxmlparser.jsoup.nodes.NodeTest.handleAbsOnFileUris");
        configurator.removeMethod("com.itextpdf.styledxmlparser.jsoup.nodes.NodeTest.handlesBaseUriBaseFails");
        configurator.mapStringLiteral("com.itextpdf.styledxmlparser.resolver.font.BasicFontProvider.HTML_TO_PDF_SHIPPED_FONT_RESOURCE_PATH", "iText.Html2Pdf.font.");
        configurator.mapMethod("com.itextpdf.styledxmlparser.css.pseudo.CssPseudoElementNode.AttributesStub.iterator", "GetEnumerator");
        configurator.addCustomUsingDeclaration("com.itextpdf.styledxmlparser.css.pseudo.CssPseudoElementNode", Arrays.asList("System.Collections"));
        configurator.mapMethod("com.itextpdf.styledxmlparser.css.page.PageMarginBoxContextNode.AttributesStub.iterator", "GetEnumerator");
        configurator.addCustomUsingDeclaration("com.itextpdf.styledxmlparser.css.page.PageMarginBoxContextNode", Arrays.asList("System.Collections"));

        configurator.mapStringLiteral("com.itextpdf.styledxmlparser.jsoup.integration.ParseTest.newsHref", "http://news.baidu.com/");

        configurator.addCustomMember("com.itextpdf.styledxmlparser.css.pseudo.CssPseudoElementNode.AttributesStub",
                SharpenConfigCustomMembers.getEnumerator);
        configurator.addCustomMember("com.itextpdf.styledxmlparser.css.page.PageMarginBoxContextNode.AttributesStub",
                SharpenConfigCustomMembers.getEnumerator);
        configurator.ignoreUsing("Java.IO");
    }

    @Override
    public void applySharpenOptions(OptionsConfigurator configurator) {

    }

    @Override
    public void applyConfigModuleSettings(ModulesConfigurator configurator) {

    }

    @Override
    public void setConfigModuleSettings(ModulesConfigurator modulesConfigurator) {

    }

    @Override
    public Collection<ModuleOption> getAvailableModuleSettings() {
        return Collections.EMPTY_SET;
    }

    @Override
    public Collection<String> getDependencies() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Collection<String> getIgnoredSourceFiles() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Collection<String> getIgnoredResources() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<SimpleImmutableEntry<String, String>> getOverwrittenResources() {
        return Collections.EMPTY_LIST;
    }
}
