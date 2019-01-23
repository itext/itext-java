/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.kernel.pdf.canvas.parser;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.source.PdfTokenizer;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.colors.CalGray;
import com.itextpdf.kernel.colors.CalRgb;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.colors.DeviceN;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.colors.IccBased;
import com.itextpdf.kernel.colors.Indexed;
import com.itextpdf.kernel.colors.Lab;
import com.itextpdf.kernel.colors.PatternColor;
import com.itextpdf.kernel.colors.Separation;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.geom.NoninvertibleTransformException;
import com.itextpdf.kernel.geom.Path;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfLiteral;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.canvas.CanvasTag;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
import com.itextpdf.kernel.pdf.canvas.parser.data.AbstractRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.data.ClippingPathInfo;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.ImageRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.data.PathRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;
import com.itextpdf.kernel.pdf.canvas.parser.util.PdfCanvasParser;
import com.itextpdf.kernel.pdf.colorspace.PdfCieBasedCs;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.pdf.colorspace.PdfPattern;
import com.itextpdf.kernel.pdf.colorspace.PdfSpecialCs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import static com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants.FillingRule;

/**
 * Processor for a PDF content stream.
 */
public class PdfCanvasProcessor {
    public static final String DEFAULT_OPERATOR = "DefaultOperator";

    /**
     * Listener that will be notified of render events
     */
    protected final IEventListener eventListener;

    /**
     * Cache supported events in case the user's {@link IEventListener#getSupportedEvents()} method is not very efficient
     */
    protected final Set<EventType> supportedEvents;

    protected Path currentPath = new Path();

    /**
     * Indicates whether the current clipping path should be modified by
     * intersecting it with the current path.
     */
    protected boolean isClip;

    /**
     * Specifies the filling rule which should be applied while calculating
     * new clipping path.
     */
    protected int clippingRule;

    /**
     * A map with all supported operators (PDF syntax).
     */
    private Map<String, IContentOperator> operators;

    /**
     * Resources for the content stream.
     * Current resources are always at the top of the stack.
     * Stack is needed in case if some "inner" content stream with it's own resources
     * is encountered (like Form XObject).
     */
    private Stack<PdfResources> resourcesStack;

    /**
     * Stack keeping track of the graphics state.
     */
    private final Stack<ParserGraphicsState> gsStack = new Stack<>();

    private Matrix textMatrix;
    private Matrix textLineMatrix;

    /**
     * A map with all supported XObject handlers
     */
    private Map<PdfName, IXObjectDoHandler> xobjectDoHandlers;

    /**
     * The font cache
     */
    private Map<Integer, WeakReference<PdfFont>> cachedFonts = new HashMap<>();

    /**
     * A stack containing marked content info.
     */
    private Stack<CanvasTag> markedContentStack = new Stack<>();

    /**
     * Creates a new PDF Content Stream Processor that will send its output to the
     * designated render listener.
     *
     * @param eventListener the {@link IEventListener} that will receive rendering notifications
     */
    public PdfCanvasProcessor(IEventListener eventListener) {
        this.eventListener = eventListener;
        this.supportedEvents = eventListener.getSupportedEvents();
        operators = new HashMap<>();
        populateOperators();
        xobjectDoHandlers = new HashMap<>();
        populateXObjectDoHandlers();
        reset();
    }

    /**
     * Creates a new PDF Content Stream Processor that will send its output to the
     * designated render listener.
     * Also allows registration of custom IContentOperators that can influence
     * how (and whether or not) the PDF instructions will be parsed.
     *
     * @param eventListener              the {@link IEventListener} that will receive rendering notifications
     * @param additionalContentOperators an optional map of custom {@link IContentOperator}s for rendering instructions
     */
    public PdfCanvasProcessor(IEventListener eventListener, Map<String, IContentOperator> additionalContentOperators) {
        this(eventListener);
        for (Map.Entry<String, IContentOperator> entry : additionalContentOperators.entrySet()) {
            registerContentOperator(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Registers a Do handler that will be called when Do for the provided XObject subtype is encountered during content processing.
     * <br>
     * If you register a handler, it is a very good idea to pass the call on to the existing registered handler (returned by this call), otherwise you
     * may inadvertently change the internal behavior of the processor.
     *
     * @param xobjectSubType the XObject subtype this handler will process, or PdfName.DEFAULT for a catch-all handler
     * @param handler        the handler that will receive notification when the Do operator for the specified subtype is encountered
     * @return the existing registered handler, if any
     */
    public IXObjectDoHandler registerXObjectDoHandler(PdfName xobjectSubType, IXObjectDoHandler handler) {
        return xobjectDoHandlers.put(xobjectSubType, handler);
    }

    /**
     * Registers a content operator that will be called when the specified operator string is encountered during content processing.
     * <br>
     * If you register an operator, it is a very good idea to pass the call on to the existing registered operator (returned by this call), otherwise you
     * may inadvertently change the internal behavior of the processor.
     *
     * @param operatorString the operator id, or DEFAULT_OPERATOR for a catch-all operator
     * @param operator       the operator that will receive notification when the operator is encountered
     * @return the existing registered operator, if any
     */
    public IContentOperator registerContentOperator(String operatorString, IContentOperator operator) {
        return operators.put(operatorString, operator);
    }

    /**
     * Gets the {@link java.util.Collection} containing all the registered operators strings.
     *
     * @return {@link java.util.Collection} containing all the registered operators strings.
     */
    public Collection<String> getRegisteredOperatorStrings() {
        return new ArrayList<String>(operators.keySet());
    }

    /**
     * Resets the graphics state stack, matrices and resources.
     */
    public void reset() {
        gsStack.removeAllElements();
        gsStack.push(new ParserGraphicsState());
        textMatrix = null;
        textLineMatrix = null;
        resourcesStack = new Stack<>();
        isClip = false;
        currentPath = new Path();
    }

    /**
     * Gets the current {@link ParserGraphicsState}
     *
     * @return the current {@link ParserGraphicsState}
     */
    public ParserGraphicsState getGraphicsState() {
        return gsStack.peek();
    }

    /**
     * Processes PDF syntax.
     * <b>Note:</b> If you re-use a given {@link PdfCanvasProcessor}, you must call {@link PdfCanvasProcessor#reset()}
     *
     * @param contentBytes the bytes of a content stream
     * @param resources    the resources of the content stream. Must not be null.
     */
    public void processContent(byte[] contentBytes, PdfResources resources) {
        if (resources == null) {
            throw new PdfException(PdfException.ResourcesCannotBeNull);
        }
        this.resourcesStack.push(resources);
        PdfTokenizer tokeniser = new PdfTokenizer(new RandomAccessFileOrArray(new RandomAccessSourceFactory().createSource(contentBytes)));
        PdfCanvasParser ps = new PdfCanvasParser(tokeniser, resources);
        List<PdfObject> operands = new ArrayList<>();
        try {
            while (ps.parse(operands).size() > 0) {
                PdfLiteral operator = (PdfLiteral) operands.get(operands.size() - 1);
                invokeOperator(operator, operands);
            }
        } catch (IOException e) {
            throw new PdfException(PdfException.CannotParseContentStream, e);
        }

        this.resourcesStack.pop();

    }

    /**
     * Processes PDF syntax.
     *
     * <strong>Note:</strong> If you re-use a given {@link PdfCanvasProcessor}, you must call {@link PdfCanvasProcessor#reset()}
     *
     * @param page the page to process
     */
    public void processPageContent(PdfPage page) {
        initClippingPath(page);
        ParserGraphicsState gs = getGraphicsState();
        eventOccurred(new ClippingPathInfo(gs, gs.getClippingPath(), gs.getCtm()), EventType.CLIP_PATH_CHANGED);
        processContent(page.getContentBytes(), page.getResources());
    }

    /**
     * Accessor method for the {@link IEventListener} object maintained in this class.
     * Necessary for implementing custom ContentOperator implementations.
     *
     * @return the renderListener
     */
    public IEventListener getEventListener() {
        return eventListener;
    }

    /**
     * Loads all the supported graphics and text state operators in a map.
     */
    protected void populateOperators() {
        registerContentOperator(DEFAULT_OPERATOR, new IgnoreOperator());

        registerContentOperator("q", new PushGraphicsStateOperator());
        registerContentOperator("Q", new PopGraphicsStateOperator());
        registerContentOperator("cm", new ModifyCurrentTransformationMatrixOperator());

        registerContentOperator("Do", new DoOperator());

        registerContentOperator("BMC", new BeginMarkedContentOperator());
        registerContentOperator("BDC", new BeginMarkedContentDictionaryOperator());
        registerContentOperator("EMC", new EndMarkedContentOperator());

        if (supportedEvents == null || supportedEvents.contains(EventType.RENDER_TEXT)
                || supportedEvents.contains(EventType.RENDER_PATH)
                || supportedEvents.contains(EventType.CLIP_PATH_CHANGED)) {

            registerContentOperator("g", new SetGrayFillOperator());
            registerContentOperator("G", new SetGrayStrokeOperator());
            registerContentOperator("rg", new SetRGBFillOperator());
            registerContentOperator("RG", new SetRGBStrokeOperator());
            registerContentOperator("k", new SetCMYKFillOperator());
            registerContentOperator("K", new SetCMYKStrokeOperator());
            registerContentOperator("cs", new SetColorSpaceFillOperator());
            registerContentOperator("CS", new SetColorSpaceStrokeOperator());
            registerContentOperator("sc", new SetColorFillOperator());
            registerContentOperator("SC", new SetColorStrokeOperator());
            registerContentOperator("scn", new SetColorFillOperator());
            registerContentOperator("SCN", new SetColorStrokeOperator());
            registerContentOperator("gs", new ProcessGraphicsStateResourceOperator());
        }

        if (supportedEvents == null || supportedEvents.contains(EventType.RENDER_IMAGE)) {
            registerContentOperator("EI", new EndImageOperator());
        }

        if (supportedEvents == null || supportedEvents.contains(EventType.RENDER_TEXT)
                || supportedEvents.contains(EventType.BEGIN_TEXT)
                || supportedEvents.contains(EventType.END_TEXT)) {
            registerContentOperator("BT", new BeginTextOperator());
            registerContentOperator("ET", new EndTextOperator());
        }

        if (supportedEvents == null || supportedEvents.contains(EventType.RENDER_TEXT)) {
            SetTextCharacterSpacingOperator tcOperator = new SetTextCharacterSpacingOperator();
            registerContentOperator("Tc", tcOperator);
            SetTextWordSpacingOperator twOperator = new SetTextWordSpacingOperator();
            registerContentOperator("Tw", twOperator);
            registerContentOperator("Tz", new SetTextHorizontalScalingOperator());
            SetTextLeadingOperator tlOperator = new SetTextLeadingOperator();
            registerContentOperator("TL", tlOperator);
            registerContentOperator("Tf", new SetTextFontOperator());
            registerContentOperator("Tr", new SetTextRenderModeOperator());
            registerContentOperator("Ts", new SetTextRiseOperator());

            TextMoveStartNextLineOperator tdOperator = new TextMoveStartNextLineOperator();
            registerContentOperator("Td", tdOperator);
            registerContentOperator("TD", new TextMoveStartNextLineWithLeadingOperator(tdOperator, tlOperator));
            registerContentOperator("Tm", new TextSetTextMatrixOperator());
            TextMoveNextLineOperator tstarOperator = new TextMoveNextLineOperator(tdOperator);
            registerContentOperator("T*", tstarOperator);

            ShowTextOperator tjOperator = new ShowTextOperator();
            registerContentOperator("Tj", tjOperator);
            MoveNextLineAndShowTextOperator tickOperator = new MoveNextLineAndShowTextOperator(tstarOperator, tjOperator);
            registerContentOperator("'", tickOperator);
            registerContentOperator("\"", new MoveNextLineAndShowTextWithSpacingOperator(twOperator, tcOperator, tickOperator));
            registerContentOperator("TJ", new ShowTextArrayOperator());
        }

        if (supportedEvents == null || supportedEvents.contains(EventType.CLIP_PATH_CHANGED)
                || supportedEvents.contains(EventType.RENDER_PATH)) {
            registerContentOperator("w", new SetLineWidthOperator());
            registerContentOperator("J", new SetLineCapOperator());
            registerContentOperator("j", new SetLineJoinOperator());
            registerContentOperator("M", new SetMiterLimitOperator());
            registerContentOperator("d", new SetLineDashPatternOperator());

            int fillStroke = PathRenderInfo.FILL | PathRenderInfo.STROKE;
            registerContentOperator("m", new MoveToOperator());
            registerContentOperator("l", new LineToOperator());
            registerContentOperator("c", new CurveOperator());
            registerContentOperator("v", new CurveFirstPointDuplicatedOperator());
            registerContentOperator("y", new CurveFourhPointDuplicatedOperator());
            registerContentOperator("h", new CloseSubpathOperator());
            registerContentOperator("re", new RectangleOperator());
            registerContentOperator("S", new PaintPathOperator(PathRenderInfo.STROKE, -1, false));
            registerContentOperator("s", new PaintPathOperator(PathRenderInfo.STROKE, -1, true));
            registerContentOperator("f", new PaintPathOperator(PathRenderInfo.FILL, PdfCanvasConstants.FillingRule.NONZERO_WINDING, false));
            registerContentOperator("F", new PaintPathOperator(PathRenderInfo.FILL, PdfCanvasConstants.FillingRule.NONZERO_WINDING, false));
            registerContentOperator("f*", new PaintPathOperator(PathRenderInfo.FILL, PdfCanvasConstants.FillingRule.EVEN_ODD, false));
            registerContentOperator("B", new PaintPathOperator(fillStroke, PdfCanvasConstants.FillingRule.NONZERO_WINDING, false));
            registerContentOperator("B*", new PaintPathOperator(fillStroke, PdfCanvasConstants.FillingRule.EVEN_ODD, false));
            registerContentOperator("b", new PaintPathOperator(fillStroke, PdfCanvasConstants.FillingRule.NONZERO_WINDING, true));
            registerContentOperator("b*", new PaintPathOperator(fillStroke, PdfCanvasConstants.FillingRule.EVEN_ODD, true));
            registerContentOperator("n", new PaintPathOperator(PathRenderInfo.NO_OP, -1, false));
            registerContentOperator("W", new ClipPathOperator(PdfCanvasConstants.FillingRule.NONZERO_WINDING));
            registerContentOperator("W*", new ClipPathOperator(PdfCanvasConstants.FillingRule.EVEN_ODD));
        }
    }

    /**
     * Displays the current path.
     *
     * @param operation One of the possible combinations of {@link PathRenderInfo#STROKE}
     *                  and {@link PathRenderInfo#FILL} values or
     *                  {@link PathRenderInfo#NO_OP}
     * @param rule      Either {@link FillingRule#NONZERO_WINDING} or {@link FillingRule#EVEN_ODD}
     *                  In case it isn't applicable pass any <CODE>byte</CODE> value.
     */
    protected void paintPath(int operation, int rule) {
        ParserGraphicsState gs = getGraphicsState();
        PathRenderInfo renderInfo = new PathRenderInfo(this.markedContentStack, gs, currentPath, operation, rule, isClip, clippingRule);
        eventOccurred(renderInfo, EventType.RENDER_PATH);

        if (isClip) {
            isClip = false;
            gs.clip(currentPath, clippingRule);
            eventOccurred(new ClippingPathInfo(gs, gs.getClippingPath(), gs.getCtm()), EventType.CLIP_PATH_CHANGED);
        }

        currentPath = new Path();
    }

    /**
     * Invokes an operator.
     *
     * @param operator the PDF Syntax of the operator
     * @param operands a list with operands
     */
    protected void invokeOperator(PdfLiteral operator, List<PdfObject> operands) {
        IContentOperator op = operators.get(operator.toString());
        if (op == null) {
            op = operators.get(DEFAULT_OPERATOR);
        }
        op.invoke(this, operator, operands);
    }

    protected PdfStream getXObjectStream(PdfName xobjectName) {
        PdfDictionary xobjects = getResources().getResource(PdfName.XObject);
        return xobjects.getAsStream(xobjectName);
    }

    protected PdfResources getResources() {
        return resourcesStack.peek();
    }

    protected void populateXObjectDoHandlers() {
        registerXObjectDoHandler(PdfName.Default, new IgnoreXObjectDoHandler());
        registerXObjectDoHandler(PdfName.Form, new FormXObjectDoHandler());

        if (supportedEvents == null ||
                supportedEvents.contains(EventType.RENDER_IMAGE)) {
            registerXObjectDoHandler(PdfName.Image, new ImageXObjectDoHandler());
        }
    }

    /**
     * Creates a {@link PdfFont} object by a font dictionary. The font may have been cached in case it is an indirect object.
     *
     * @param fontDict
     * @return the font
     */
    protected PdfFont getFont(PdfDictionary fontDict) {
        if (fontDict.getIndirectReference() == null) {
            return PdfFontFactory.createFont(fontDict);
        } else {
            int n = fontDict.getIndirectReference().getObjNumber();
            WeakReference<PdfFont> fontRef = cachedFonts.get(n);
            PdfFont font = (PdfFont) (fontRef == null ? null : fontRef.get());
            if (font == null) {
                font = PdfFontFactory.createFont(fontDict);
                cachedFonts.put(n, new WeakReference<>(font));
            }
            return font;
        }
    }

    /**
     * Add to the marked content stack
     *
     * @param tag  the tag of the marked content
     * @param dict the PdfDictionary associated with the marked content
     */
    protected void beginMarkedContent(PdfName tag, PdfDictionary dict) {
        markedContentStack.push(new CanvasTag(tag).setProperties(dict));
    }

    /**
     * Remove the latest marked content from the stack.  Keeps track of the BMC, BDC and EMC operators.
     */
    protected void endMarkedContent() {
        markedContentStack.pop();
    }

    /**
     * Used to trigger beginTextBlock on the renderListener
     */
    private void beginText() {
        eventOccurred(null, EventType.BEGIN_TEXT);
    }

    /**
     * Used to trigger endTextBlock on the renderListener
     */
    private void endText() {
        eventOccurred(null, EventType.END_TEXT);
    }

    /**
     * This is a proxy to pass only those events to the event listener which are supported by it.
     *
     * @param data event data
     * @param type event type
     */
    protected void eventOccurred(IEventData data, EventType type) {
        if (supportedEvents == null || supportedEvents.contains(type)) {
            eventListener.eventOccurred(data, type);
        }
        if (data instanceof AbstractRenderInfo) {
            ((AbstractRenderInfo) data).releaseGraphicsState();
        }
    }

    /**
     * Displays text.
     *
     * @param string the text to display
     */
    private void displayPdfString(PdfString string) {
        TextRenderInfo renderInfo = new TextRenderInfo(string, getGraphicsState(), textMatrix, markedContentStack);
        textMatrix = new Matrix(renderInfo.getUnscaledWidth(), 0).multiply(textMatrix);
        eventOccurred(renderInfo, EventType.RENDER_TEXT);
    }

    /**
     * Displays an XObject using the registered handler for this XObject's subtype
     *
     * @param resourceName the name of the XObject to retrieve from the resource dictionary
     */
    private void displayXObject(PdfName resourceName) {
        PdfStream xobjectStream = getXObjectStream(resourceName);
        PdfName subType = xobjectStream.getAsName(PdfName.Subtype);
        IXObjectDoHandler handler = xobjectDoHandlers.get(subType);

        if (handler == null) {
            handler = xobjectDoHandlers.get(PdfName.Default);
        }

        handler.handleXObject(this, this.markedContentStack, xobjectStream, resourceName);
    }

    private void displayImage(Stack<CanvasTag> canvasTagHierarchy, PdfStream imageStream, PdfName resourceName, boolean isInline) {
        PdfDictionary colorSpaceDic = getResources().getResource(PdfName.ColorSpace);
        ImageRenderInfo renderInfo = new ImageRenderInfo(canvasTagHierarchy, getGraphicsState(), getGraphicsState().getCtm(),
                imageStream, resourceName, colorSpaceDic, isInline);
        eventOccurred(renderInfo, EventType.RENDER_IMAGE);
    }

    /**
     * Adjusts the text matrix for the specified adjustment value (see TJ operator in the PDF spec for information)
     *
     * @param tj the text adjustment
     */
    private void applyTextAdjust(float tj) {
        float adjustBy = -tj / 1000f * getGraphicsState().getFontSize() * (getGraphicsState().getHorizontalScaling() / 100f);

        textMatrix = new Matrix(adjustBy, 0).multiply(textMatrix);
    }

    private void initClippingPath(PdfPage page) {
        Path clippingPath = new Path();
        clippingPath.rectangle(page.getCropBox());
        getGraphicsState().setClippingPath(clippingPath);
    }

    /**
     * A handler that implements operator (unregistered).
     */
    private static class IgnoreOperator implements IContentOperator {
        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            // ignore the operator
        }
    }

    /**
     * A handler that implements operator (TJ). For more information see Table 51 ISO-32000-1
     */
    private static class ShowTextArrayOperator implements IContentOperator {
        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            PdfArray array = (PdfArray) operands.get(0);
            float tj = 0;
            for (PdfObject entryObj : array) {
                if (entryObj instanceof PdfString) {
                    processor.displayPdfString((PdfString) entryObj);
                    tj = 0;
                } else {
                    tj = ((PdfNumber) entryObj).floatValue();
                    processor.applyTextAdjust(tj);
                }
            }
        }
    }

    /**
     * A handler that implements operator ("). For more information see Table 51 ISO-32000-1
     */
    private static class MoveNextLineAndShowTextWithSpacingOperator implements IContentOperator {
        private final SetTextWordSpacingOperator setTextWordSpacing;
        private final SetTextCharacterSpacingOperator setTextCharacterSpacing;
        private final MoveNextLineAndShowTextOperator moveNextLineAndShowText;

        /**
         * Create new instance of this handler.
         *
         * @param setTextWordSpacing      the handler for Tw operator
         * @param setTextCharacterSpacing the handler for Tc operator
         * @param moveNextLineAndShowText the handler for ' operator
         */
        public MoveNextLineAndShowTextWithSpacingOperator(SetTextWordSpacingOperator setTextWordSpacing, SetTextCharacterSpacingOperator setTextCharacterSpacing, MoveNextLineAndShowTextOperator moveNextLineAndShowText) {
            this.setTextWordSpacing = setTextWordSpacing;
            this.setTextCharacterSpacing = setTextCharacterSpacing;
            this.moveNextLineAndShowText = moveNextLineAndShowText;
        }

        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            PdfNumber aw = (PdfNumber) operands.get(0);
            PdfNumber ac = (PdfNumber) operands.get(1);
            PdfString string = (PdfString) operands.get(2);

            List<PdfObject> twOperands = new ArrayList<PdfObject>(1);
            twOperands.add(0, aw);
            setTextWordSpacing.invoke(processor, null, twOperands);

            List<PdfObject> tcOperands = new ArrayList<PdfObject>(1);
            tcOperands.add(0, ac);
            setTextCharacterSpacing.invoke(processor, null, tcOperands);

            List<PdfObject> tickOperands = new ArrayList<PdfObject>(1);
            tickOperands.add(0, string);
            moveNextLineAndShowText.invoke(processor, null, tickOperands);
        }
    }

    /**
     * A handler that implements operator ('). For more information see Table 51 ISO-32000-1
     */
    private static class MoveNextLineAndShowTextOperator implements IContentOperator {
        private final TextMoveNextLineOperator textMoveNextLine;
        private final ShowTextOperator showText;

        /**
         * Creates the new instance of this handler
         *
         * @param textMoveNextLine the handler for T* operator
         * @param showText         the handler for Tj operator
         */
        public MoveNextLineAndShowTextOperator(TextMoveNextLineOperator textMoveNextLine, ShowTextOperator showText) {
            this.textMoveNextLine = textMoveNextLine;
            this.showText = showText;
        }

        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            textMoveNextLine.invoke(processor, null, new ArrayList<PdfObject>(0));
            showText.invoke(processor, null, operands);
        }
    }

    /**
     * A handler that implements operator (Tj). For more information see Table 51 ISO-32000-1
     */
    private static class ShowTextOperator implements IContentOperator {
        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            PdfString string = (PdfString) operands.get(0);

            processor.displayPdfString(string);
        }
    }


    /**
     * A handler that implements operator (T*). For more information see Table 51 ISO-32000-1
     */
    private static class TextMoveNextLineOperator implements IContentOperator {
        private final TextMoveStartNextLineOperator moveStartNextLine;

        public TextMoveNextLineOperator(TextMoveStartNextLineOperator moveStartNextLine) {
            this.moveStartNextLine = moveStartNextLine;
        }

        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            List<PdfObject> tdoperands = new ArrayList<PdfObject>(2);
            tdoperands.add(0, new PdfNumber(0));
            tdoperands.add(1, new PdfNumber(-processor.getGraphicsState().getLeading()));
            moveStartNextLine.invoke(processor, null, tdoperands);
        }
    }

    /**
     * A handler that implements operator (Tm). For more information see Table 51 ISO-32000-1
     */
    private static class TextSetTextMatrixOperator implements IContentOperator {
        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            float a = ((PdfNumber) operands.get(0)).floatValue();
            float b = ((PdfNumber) operands.get(1)).floatValue();
            float c = ((PdfNumber) operands.get(2)).floatValue();
            float d = ((PdfNumber) operands.get(3)).floatValue();
            float e = ((PdfNumber) operands.get(4)).floatValue();
            float f = ((PdfNumber) operands.get(5)).floatValue();

            processor.textLineMatrix = new Matrix(a, b, c, d, e, f);
            processor.textMatrix = processor.textLineMatrix;
        }
    }

    /**
     * A handler that implements operator (TD). For more information see Table 51 ISO-32000-1
     */
    private static class TextMoveStartNextLineWithLeadingOperator implements IContentOperator {
        private final TextMoveStartNextLineOperator moveStartNextLine;
        private final SetTextLeadingOperator setTextLeading;

        public TextMoveStartNextLineWithLeadingOperator(TextMoveStartNextLineOperator moveStartNextLine, SetTextLeadingOperator setTextLeading) {
            this.moveStartNextLine = moveStartNextLine;
            this.setTextLeading = setTextLeading;
        }

        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            float ty = ((PdfNumber) operands.get(1)).floatValue();

            List<PdfObject> tlOperands = new ArrayList<PdfObject>(1);
            tlOperands.add(0, new PdfNumber(-ty));
            setTextLeading.invoke(processor, null, tlOperands);
            moveStartNextLine.invoke(processor, null, operands);
        }
    }

    /**
     * A handler that implements operator (Td). For more information see Table 51 ISO-32000-1
     */
    private static class TextMoveStartNextLineOperator implements IContentOperator {
        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            float tx = ((PdfNumber) operands.get(0)).floatValue();
            float ty = ((PdfNumber) operands.get(1)).floatValue();

            Matrix translationMatrix = new Matrix(tx, ty);
            processor.textMatrix = translationMatrix.multiply(processor.textLineMatrix);
            processor.textLineMatrix = processor.textMatrix;
        }
    }

    /**
     * A handler that implements operator (Tf). For more information see Table 51 ISO-32000-1
     */
    private static class SetTextFontOperator implements IContentOperator {
        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            PdfName fontResourceName = (PdfName) operands.get(0);
            float size = ((PdfNumber) operands.get(1)).floatValue();

            PdfDictionary fontsDictionary = processor.getResources().getResource(PdfName.Font);
            PdfDictionary fontDict = fontsDictionary.getAsDictionary(fontResourceName);
            PdfFont font = null;
            font = processor.getFont(fontDict);

            processor.getGraphicsState().setFont(font);
            processor.getGraphicsState().setFontSize(size);

        }
    }

    /**
     * A handler that implements operator (Tr). For more information see Table 51 ISO-32000-1
     */
    private static class SetTextRenderModeOperator implements IContentOperator {
        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            PdfNumber render = (PdfNumber) operands.get(0);
            processor.getGraphicsState().setTextRenderingMode(render.intValue());
        }
    }

    /**
     * A handler that implements operator (Ts). For more information see Table 51 ISO-32000-1
     */
    private static class SetTextRiseOperator implements IContentOperator {
        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            PdfNumber rise = (PdfNumber) operands.get(0);
            processor.getGraphicsState().setTextRise(rise.floatValue());
        }
    }

    /**
     * A handler that implements operator (TL). For more information see Table 51 ISO-32000-1
     */
    private static class SetTextLeadingOperator implements IContentOperator {
        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            PdfNumber leading = (PdfNumber) operands.get(0);
            processor.getGraphicsState().setLeading(leading.floatValue());
        }
    }

    /**
     * A handler that implements operator (Tz). For more information see Table 51 ISO-32000-1
     */
    private static class SetTextHorizontalScalingOperator implements IContentOperator {
        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            PdfNumber scale = (PdfNumber) operands.get(0);
            processor.getGraphicsState().setHorizontalScaling(scale.floatValue());
        }
    }

    /**
     * A handler that implements operator (Tc). For more information see Table 51 ISO-32000-1
     */
    private static class SetTextCharacterSpacingOperator implements IContentOperator {
        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            PdfNumber charSpace = (PdfNumber) operands.get(0);
            processor.getGraphicsState().setCharSpacing(charSpace.floatValue());
        }
    }

    /**
     * A handler that implements operator (Tw). For more information see Table 51 ISO-32000-1
     */
    private static class SetTextWordSpacingOperator implements IContentOperator {
        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            PdfNumber wordSpace = (PdfNumber) operands.get(0);
            processor.getGraphicsState().setWordSpacing(wordSpace.floatValue());
        }
    }

    /**
     * A handler that implements operator (gs). For more information see Table 51 ISO-32000-1
     */
    private static class ProcessGraphicsStateResourceOperator implements IContentOperator {
        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            PdfName dictionaryName = (PdfName) operands.get(0);
            PdfDictionary extGState = processor.getResources().getResource(PdfName.ExtGState);
            if (extGState == null)
                throw new PdfException(PdfException.ResourcesDoNotContainExtgstateEntryUnableToProcessOperator1).setMessageParams(operator);
            PdfDictionary gsDic = extGState.getAsDictionary(dictionaryName);
            if (gsDic == null) {
                gsDic = extGState.getAsStream(dictionaryName);
                if (gsDic == null)
                    throw new PdfException(PdfException._1IsAnUnknownGraphicsStateDictionary).setMessageParams(dictionaryName);
            }
            // at this point, all we care about is the FONT entry in the GS dictionary TODO merge the whole gs dictionary
            PdfArray fontParameter = gsDic.getAsArray(PdfName.Font);
            if (fontParameter != null) {
                PdfFont font = processor.getFont(fontParameter.getAsDictionary(0));
                float size = fontParameter.getAsNumber(1).floatValue();

                processor.getGraphicsState().setFont(font);
                processor.getGraphicsState().setFontSize(size);
            }
        }
    }

    /**
     * A handler that implements operator (q). For more information see Table 51 ISO-32000-1
     */
    private static class PushGraphicsStateOperator implements IContentOperator {
        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            ParserGraphicsState gs = processor.gsStack.peek();
            ParserGraphicsState copy = new ParserGraphicsState(gs);
            processor.gsStack.push(copy);
        }
    }

    /**
     * A handler that implements operator (cm). For more information see Table 51 ISO-32000-1
     */
    private static class ModifyCurrentTransformationMatrixOperator implements IContentOperator {
        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            float a = ((PdfNumber) operands.get(0)).floatValue();
            float b = ((PdfNumber) operands.get(1)).floatValue();
            float c = ((PdfNumber) operands.get(2)).floatValue();
            float d = ((PdfNumber) operands.get(3)).floatValue();
            float e = ((PdfNumber) operands.get(4)).floatValue();
            float f = ((PdfNumber) operands.get(5)).floatValue();
            Matrix matrix = new Matrix(a, b, c, d, e, f);
            try {
                processor.getGraphicsState().updateCtm(matrix);
            } catch (PdfException exception) {
                if (!(exception.getCause() instanceof NoninvertibleTransformException)) {
                    throw exception;
                } else {
                    Logger logger = LoggerFactory.getLogger(PdfCanvasProcessor.class);
                    logger.error(MessageFormatUtil.format(LogMessageConstant.FAILED_TO_PROCESS_A_TRANSFORMATION_MATRIX));
                }
            }
        }
    }

    /**
     * Gets a color based on a list of operands and Color space.
     */
    private static Color getColor(PdfColorSpace pdfColorSpace, List<PdfObject> operands, PdfResources resources) {
        PdfObject pdfObject;
        if (pdfColorSpace.getPdfObject().isIndirectReference()) {
            pdfObject = ((PdfIndirectReference) pdfColorSpace.getPdfObject()).getRefersTo();
        } else {
            pdfObject = pdfColorSpace.getPdfObject();
        }

        if (pdfObject.isName()) {
            if (PdfName.DeviceGray.equals(pdfObject)) {
                return new DeviceGray(getColorants(operands)[0]);
            } else if (PdfName.Pattern.equals(pdfObject)) {
                if (operands.get(0) instanceof PdfName) {
                    PdfPattern pattern = resources.getPattern((PdfName) operands.get(0));
                    if (pattern != null) {
                        return new PatternColor(pattern);
                    }
                }
            }
            if (PdfName.DeviceRGB.equals(pdfObject)) {
                float[] c = getColorants(operands);
                return new DeviceRgb(c[0], c[1], c[2]);
            } else if (PdfName.DeviceCMYK.equals(pdfObject)) {
                float[] c = getColorants(operands);
                return new DeviceCmyk(c[0], c[1], c[2], c[3]);
            }
        } else if (pdfObject.isArray()) {
            PdfArray array = (PdfArray) pdfObject;
            PdfName csType = array.getAsName(0);
            if (PdfName.CalGray.equals(csType))
                return new CalGray((PdfCieBasedCs.CalGray) pdfColorSpace, getColorants(operands)[0]);
            else if (PdfName.CalRGB.equals(csType))
                return new CalRgb((PdfCieBasedCs.CalRgb) pdfColorSpace, getColorants(operands));
            else if (PdfName.Lab.equals(csType))
                return new Lab((PdfCieBasedCs.Lab) pdfColorSpace, getColorants(operands));
            else if (PdfName.ICCBased.equals(csType))
                return new IccBased((PdfCieBasedCs.IccBased) pdfColorSpace, getColorants(operands));
            else if (PdfName.Indexed.equals(csType))
                return new Indexed(pdfColorSpace, (int) getColorants(operands)[0]);
            else if (PdfName.Separation.equals(csType))
                return new Separation((PdfSpecialCs.Separation) pdfColorSpace, getColorants(operands)[0]);
            else if (PdfName.DeviceN.equals(csType))
                return new DeviceN((PdfSpecialCs.DeviceN) pdfColorSpace, getColorants(operands));
            else if (PdfName.Pattern.equals(csType)) {
                List<PdfObject> underlyingOperands = new ArrayList<>(operands);
                PdfObject patternName = underlyingOperands.remove(operands.size() - 2);
                PdfColorSpace underlyingCs = ((PdfSpecialCs.UncoloredTilingPattern) pdfColorSpace).getUnderlyingColorSpace();
                if (patternName instanceof PdfName) {
                    PdfPattern pattern = resources.getPattern((PdfName) patternName);
                    if (pattern instanceof PdfPattern.Tiling && !((PdfPattern.Tiling) pattern).isColored()) {
                        return new PatternColor((PdfPattern.Tiling) pattern, underlyingCs, getColorants(underlyingOperands));
                    }
                }
            }
        }
        return null;
    }

    /**
     * Gets a color based on a list of operands.
     */
    private static Color getColor(int nOperands, List<PdfObject> operands) {
        float[] c = new float[nOperands];
        for (int i = 0; i < nOperands; i++) {
            c[i] = ((PdfNumber) operands.get(i)).floatValue();
        }

        switch (nOperands) {
            case 1:
                return new DeviceGray(c[0]);
            case 3:
                return new DeviceRgb(c[0], c[1], c[2]);
            case 4:
                return new DeviceCmyk(c[0], c[1], c[2], c[3]);
        }
        return null;
    }

    private static float[] getColorants(List<PdfObject> operands) {
        float[] c = new float[operands.size() - 1];
        for (int i = 0; i < operands.size() - 1; i++) {
            c[i] = ((PdfNumber) operands.get(i)).floatValue();
        }
        return c;
    }

    /**
     * A handler that implements operator (Q). For more information see Table 51 ISO-32000-1
     */
    protected static class PopGraphicsStateOperator implements IContentOperator {
        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            processor.gsStack.pop();
            ParserGraphicsState gs = processor.getGraphicsState();
            processor.eventOccurred(new ClippingPathInfo(gs, gs.getClippingPath(), gs.getCtm()), EventType.CLIP_PATH_CHANGED);
        }
    }

    /**
     * A handler that implements operator (g). For more information see Table 51 ISO-32000-1
     */
    private static class SetGrayFillOperator implements IContentOperator {
        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            processor.getGraphicsState().setFillColor(getColor(1, operands));
        }
    }

    /**
     * A handler that implements operator (G). For more information see Table 51 ISO-32000-1
     */
    private static class SetGrayStrokeOperator implements IContentOperator {
        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            processor.getGraphicsState().setStrokeColor(getColor(1, operands));
        }
    }

    /**
     * A handler that implements operator (rg). For more information see Table 51 ISO-32000-1
     */
    private static class SetRGBFillOperator implements IContentOperator {
        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            processor.getGraphicsState().setFillColor(getColor(3, operands));
        }
    }

    /**
     * A handler that implements operator (RG). For more information see Table 51 ISO-32000-1
     */
    private static class SetRGBStrokeOperator implements IContentOperator {
        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            processor.getGraphicsState().setStrokeColor(getColor(3, operands));
        }
    }

    /**
     * A handler that implements operator (k). For more information see Table 51 ISO-32000-1
     */
    private static class SetCMYKFillOperator implements IContentOperator {
        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            processor.getGraphicsState().setFillColor(getColor(4, operands));
        }
    }

    /**
     * A handler that implements operator (K). For more information see Table 51 ISO-32000-1
     */
    private static class SetCMYKStrokeOperator implements IContentOperator {
        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            processor.getGraphicsState().setStrokeColor(getColor(4, operands));
        }
    }

    /**
     * A handler that implements operator (CS). For more information see Table 51 ISO-32000-1
     */
    private static class SetColorSpaceFillOperator implements IContentOperator {
        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            PdfColorSpace pdfColorSpace = determineColorSpace((PdfName) operands.get(0), processor);
            processor.getGraphicsState().setFillColor(Color.makeColor(pdfColorSpace));
        }

        static PdfColorSpace determineColorSpace(PdfName colorSpace, PdfCanvasProcessor processor) {
            PdfColorSpace pdfColorSpace = null;
            if (PdfColorSpace.directColorSpaces.contains(colorSpace)) {
                pdfColorSpace = PdfColorSpace.makeColorSpace(colorSpace);
            } else {
                PdfResources pdfResources = processor.getResources();
                PdfDictionary resourceColorSpace = pdfResources.getPdfObject().getAsDictionary(PdfName.ColorSpace);
                pdfColorSpace = PdfColorSpace.makeColorSpace(resourceColorSpace.get(colorSpace));
            }

            return pdfColorSpace;
        }
    }


    /**
     * A handler that implements operator (cs). For more information see Table 51 ISO-32000-1
     */
    private static class SetColorSpaceStrokeOperator implements IContentOperator {
        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            PdfColorSpace pdfColorSpace = SetColorSpaceFillOperator.determineColorSpace((PdfName) operands.get(0), processor);
            processor.getGraphicsState().setStrokeColor(Color.makeColor(pdfColorSpace));
        }
    }

    /**
     * A handler that implements operator (sc / scn). For more information see Table 51 ISO-32000-1
     */
    private static class SetColorFillOperator implements IContentOperator {
        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            processor.getGraphicsState().setFillColor(getColor(processor.getGraphicsState().getFillColor().getColorSpace(), operands, processor.getResources()));
        }
    }

    /**
     * A handler that implements operator (SC / SCN). For more information see Table 51 ISO-32000-1
     */
    private static class SetColorStrokeOperator implements IContentOperator {
        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            processor.getGraphicsState().setStrokeColor(getColor(processor.getGraphicsState().getStrokeColor().getColorSpace(), operands, processor.getResources()));
        }
    }

    /**
     * A handler that implements operator (BT). For more information see Table 51 ISO-32000-1
     */
    private static class BeginTextOperator implements IContentOperator {
        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            processor.textMatrix = new Matrix();
            processor.textLineMatrix = processor.textMatrix;
            processor.beginText();
        }
    }

    /**
     * A handler that implements operator (ET). For more information see Table 51 ISO-32000-1
     */
    private static class EndTextOperator implements IContentOperator {
        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            processor.textMatrix = null;
            processor.textLineMatrix = null;
            processor.endText();
        }
    }

    /**
     * A handler that implements operator (BMC). For more information see Table 51 ISO-32000-1
     */
    private static class BeginMarkedContentOperator implements IContentOperator {
        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor,
                           PdfLiteral operator, List<PdfObject> operands) {
            processor.beginMarkedContent((PdfName) operands.get(0), new PdfDictionary());
        }

    }

    /**
     * A handler that implements operator (BDC). For more information see Table 51 ISO-32000-1
     */
    private static class BeginMarkedContentDictionaryOperator implements IContentOperator {
        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor,
                           PdfLiteral operator, List<PdfObject> operands) {

            PdfObject properties = operands.get(1);

            processor.beginMarkedContent((PdfName) operands.get(0), getPropertiesDictionary(properties, processor.getResources()));
        }

        PdfDictionary getPropertiesDictionary(PdfObject operand1, PdfResources resources) {
            if (operand1.isDictionary())
                return (PdfDictionary) operand1;

            PdfName dictionaryName = ((PdfName) operand1);
            PdfDictionary properties = resources.getResource(PdfName.Properties);
            if (null == properties) {
                Logger logger = LoggerFactory.getLogger(PdfCanvasProcessor.class);
                logger.warn(MessageFormatUtil.format(LogMessageConstant.PDF_REFERS_TO_NOT_EXISTING_PROPERTY_DICTIONARY, PdfName.Properties));
                return null;
            }
            PdfDictionary propertiesDictionary = properties.getAsDictionary(dictionaryName);
            if (null == propertiesDictionary) {
                Logger logger = LoggerFactory.getLogger(PdfCanvasProcessor.class);
                logger.warn(MessageFormatUtil.format(LogMessageConstant.PDF_REFERS_TO_NOT_EXISTING_PROPERTY_DICTIONARY, dictionaryName));
                return null;
            }
            return properties.getAsDictionary(dictionaryName);
        }
    }

    /**
     * A handler that implements operator (EMC). For more information see Table 51 ISO-32000-1
     */
    private static class EndMarkedContentOperator implements IContentOperator {
        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor,
                           PdfLiteral operator, List<PdfObject> operands) {
            processor.endMarkedContent();
        }
    }

    /**
     * A handler that implements operator (Do). For more information see Table 51 ISO-32000-1
     */
    private static class DoOperator implements IContentOperator {
        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            PdfName resourceName = (PdfName) operands.get(0);
            processor.displayXObject(resourceName);
        }
    }

    /**
     * A handler that implements operator (EI). For more information see Table 51 ISO-32000-1
     * BI and ID operators are parsed along with this operator.
     * This not a usual operator, it will have a single operand, which will be a PdfStream object which
     * encapsulates inline image dictionary and bytes
     */
    private static class EndImageOperator implements IContentOperator {
        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            PdfStream imageStream = (PdfStream) operands.get(0);
            processor.displayImage(processor.markedContentStack, imageStream, null, true);
        }
    }

    /**
     * A handler that implements operator (w). For more information see Table 51 ISO-32000-1
     */
    private static class SetLineWidthOperator implements IContentOperator {
        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral oper, List<PdfObject> operands) {
            float lineWidth = ((PdfNumber) operands.get(0)).floatValue();
            processor.getGraphicsState().setLineWidth(lineWidth);
        }
    }

    /**
     * A handler that implements operator (J). For more information see Table 51 ISO-32000-1
     */
    private static class SetLineCapOperator implements IContentOperator {

        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral oper, List<PdfObject> operands) {
            int lineCap = ((PdfNumber) operands.get(0)).intValue();
            processor.getGraphicsState().setLineCapStyle(lineCap);
        }
    }

    /**
     * A handler that implements operator (j). For more information see Table 51 ISO-32000-1
     */
    private static class SetLineJoinOperator implements IContentOperator {

        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral oper, List<PdfObject> operands) {
            int lineJoin = ((PdfNumber) operands.get(0)).intValue();
            processor.getGraphicsState().setLineJoinStyle(lineJoin);
        }
    }

    /**
     * A handler that implements operator (M). For more information see Table 51 ISO-32000-1
     */
    private static class SetMiterLimitOperator implements IContentOperator {

        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral oper, List<PdfObject> operands) {
            float miterLimit = ((PdfNumber) operands.get(0)).floatValue();
            processor.getGraphicsState().setMiterLimit(miterLimit);
        }
    }

    /**
     * A handler that implements operator (d). For more information see Table 51 ISO-32000-1
     */
    private static class SetLineDashPatternOperator implements IContentOperator {

        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral oper, List<PdfObject> operands) {
            processor.getGraphicsState().setDashPattern(new PdfArray(Arrays.asList(operands.get(0), operands.get(1))));
        }
    }

    /**
     * An XObject subtype handler for FORM
     */
    private static class FormXObjectDoHandler implements IXObjectDoHandler {

        public void handleXObject(PdfCanvasProcessor processor, Stack<CanvasTag> canvasTagHierarchy, PdfStream xObjectStream, PdfName xObjectName) {

            PdfDictionary resourcesDic = xObjectStream.getAsDictionary(PdfName.Resources);
            PdfResources resources;
            if (resourcesDic == null) {
                resources = processor.getResources();
            } else {
                resources = new PdfResources(resourcesDic);
            }

            // we read the content bytes up here so if it fails we don't leave the graphics state stack corrupted
            // this is probably not necessary (if we fail on this, probably the entire content stream processing
            // operation should be rejected
            byte[] contentBytes;
            contentBytes = xObjectStream.getBytes();
            final PdfArray matrix = xObjectStream.getAsArray(PdfName.Matrix);

            new PushGraphicsStateOperator().invoke(processor, null, null);

            if (matrix != null) {
                float a = matrix.getAsNumber(0).floatValue();
                float b = matrix.getAsNumber(1).floatValue();
                float c = matrix.getAsNumber(2).floatValue();
                float d = matrix.getAsNumber(3).floatValue();
                float e = matrix.getAsNumber(4).floatValue();
                float f = matrix.getAsNumber(5).floatValue();
                Matrix formMatrix = new Matrix(a, b, c, d, e, f);
                processor.getGraphicsState().updateCtm(formMatrix);
            }

            processor.processContent(contentBytes, resources);

            new PopGraphicsStateOperator().invoke(processor, null, null);
        }
    }

    /**
     * An XObject subtype handler for IMAGE
     */
    private static class ImageXObjectDoHandler implements IXObjectDoHandler {

        public void handleXObject(PdfCanvasProcessor processor, Stack<CanvasTag> canvasTagHierarchy, PdfStream xObjectStream, PdfName resourceName) {
            processor.displayImage(canvasTagHierarchy, xObjectStream, resourceName,false);
        }
    }

    /**
     * An XObject subtype handler that does nothing
     */
    private static class IgnoreXObjectDoHandler implements IXObjectDoHandler {
        public void handleXObject(PdfCanvasProcessor processor, Stack<CanvasTag> canvasTagHierarchy, PdfStream xObjectStream, PdfName xObjectName) {
            // ignore XObject subtype
        }
    }

    /**
     * A handler that implements operator (m). For more information see Table 51 ISO-32000-1
     */
    private static class MoveToOperator implements IContentOperator {

        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            float x = ((PdfNumber) operands.get(0)).floatValue();
            float y = ((PdfNumber) operands.get(1)).floatValue();
            processor.currentPath.moveTo(x, y);
        }
    }

    /**
     * A handler that implements operator (l). For more information see Table 51 ISO-32000-1
     */
    private static class LineToOperator implements IContentOperator {

        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            float x = ((PdfNumber) operands.get(0)).floatValue();
            float y = ((PdfNumber) operands.get(1)).floatValue();
            processor.currentPath.lineTo(x, y);
        }
    }

    /**
     * A handler that implements operator (c). For more information see Table 51 ISO-32000-1
     */
    private static class CurveOperator implements IContentOperator {

        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            float x1 = ((PdfNumber) operands.get(0)).floatValue();
            float y1 = ((PdfNumber) operands.get(1)).floatValue();
            float x2 = ((PdfNumber) operands.get(2)).floatValue();
            float y2 = ((PdfNumber) operands.get(3)).floatValue();
            float x3 = ((PdfNumber) operands.get(4)).floatValue();
            float y3 = ((PdfNumber) operands.get(5)).floatValue();
            processor.currentPath.curveTo(x1, y1, x2, y2, x3, y3);
        }
    }

    /**
     * A handler that implements operator (v). For more information see Table 51 ISO-32000-1
     */
    private static class CurveFirstPointDuplicatedOperator implements IContentOperator {

        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            float x2 = ((PdfNumber) operands.get(0)).floatValue();
            float y2 = ((PdfNumber) operands.get(1)).floatValue();
            float x3 = ((PdfNumber) operands.get(2)).floatValue();
            float y3 = ((PdfNumber) operands.get(3)).floatValue();
            processor.currentPath.curveTo(x2, y2, x3, y3);
        }
    }

    /**
     * A handler that implements operator (y). For more information see Table 51 ISO-32000-1
     */
    private static class CurveFourhPointDuplicatedOperator implements IContentOperator {

        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            float x1 = ((PdfNumber) operands.get(0)).floatValue();
            float y1 = ((PdfNumber) operands.get(1)).floatValue();
            float x3 = ((PdfNumber) operands.get(2)).floatValue();
            float y3 = ((PdfNumber) operands.get(3)).floatValue();
            processor.currentPath.curveFromTo(x1, y1, x3, y3);
        }
    }

    /**
     * A handler that implements operator (h). For more information see Table 51 ISO-32000-1
     */
    private static class CloseSubpathOperator implements IContentOperator {

        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            processor.currentPath.closeSubpath();
        }
    }

    /**
     * A handler that implements operator (re). For more information see Table 51 ISO-32000-1
     */
    private static class RectangleOperator implements IContentOperator {

        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            float x = ((PdfNumber) operands.get(0)).floatValue();
            float y = ((PdfNumber) operands.get(1)).floatValue();
            float w = ((PdfNumber) operands.get(2)).floatValue();
            float h = ((PdfNumber) operands.get(3)).floatValue();
            processor.currentPath.rectangle(x, y, w, h);
        }
    }

    /**
     * A handler that implements operator (S, s, f, F, f*, B, B*, b, b*). For more information see Table 51 ISO-32000-1
     */
    private static class PaintPathOperator implements IContentOperator {

        private int operation;
        private int rule;
        private boolean close;

        /**
         * Constructs PainPath object.
         *
         * @param operation One of the possible combinations of {@link PathRenderInfo#STROKE}
         *                  and {@link PathRenderInfo#FILL} values or
         *                  {@link PathRenderInfo#NO_OP}
         * @param rule      Either {@link FillingRule#NONZERO_WINDING} or {@link FillingRule#EVEN_ODD}
         *                  In case it isn't applicable pass any value.
         * @param close     Indicates whether the path should be closed or not.
         */
        public PaintPathOperator(int operation, int rule, boolean close) {
            this.operation = operation;
            this.rule = rule;
            this.close = close;
        }

        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            if (close) {
                processor.currentPath.closeSubpath();
            }

            processor.paintPath(operation, rule);
        }
    }

    /**
     * A handler that implements operator (W, W*). For more information see Table 51 ISO-32000-1
     */
    private static class ClipPathOperator implements IContentOperator {

        private int rule;

        public ClipPathOperator(int rule) {
            this.rule = rule;
        }

        /**
         * {@inheritDoc}
         */
        public void invoke(PdfCanvasProcessor processor, PdfLiteral operator, List<PdfObject> operands) {
            processor.isClip = true;
            processor.clippingRule = rule;
        }
    }
}
