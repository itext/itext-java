/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.forms.xfdf;

/**
 * Class containing constants to be used in xfdf processing.
 */
public final class XfdfConstants {

    private XfdfConstants() {
    }

    public static final String TEXT = "text";
    public static final String HIGHLIGHT = "highlight";
    public static final String UNDERLINE = "underline";
    public static final String STRIKEOUT = "strikeout";
    public static final String SQUIGGLY = "squiggly";
    public static final String LINE = "line";
    public static final String CIRCLE = "circle";
    public static final String SQUARE = "square";
    public static final String CARET = "caret";
    public static final String POPUP = "popup";
    public static final String POLYGON = "polygon";
    public static final String POLYLINE = "polyline";
    public static final String STAMP = "stamp";
    public static final String INK = "ink";
    public static final String FREETEXT = "freetext";
    public static final String FILEATTACHMENT = "fileattachment";
    public static final String SOUND = "sound";
    public static final String LINK = "link";
    public static final String REDACT = "redact";
    public static final String PROJECTION = "projection";
    public static final String PAGE = "page";
    public static final String COLOR = "color";
    public static final String DATE = "date";
    public static final String FLAGS = "flags";
    public static final String NAME = "name";
    public static final String RECT = "rect";
    public static final String TITLE = "title";
    public static final String CREATION_DATE = "creationdate";
    public static final String OPACITY = "opacity";
    public static final String SUBJECT = "subject";
    public static final String ICON = "icon";
    public static final String STATE = "state";
    public static final String STATE_MODEL = "statemodel";
    public static final String IN_REPLY_TO = "inreplyto";
    public static final String REPLY_TYPE = "replyType";
    public static final String CONTENTS = "contents";
    public static final String CONTENTS_RICHTEXT = "contents-richtext";
    public static final String EMPTY_F_LEMENT = "Empty f element, no href attribute found.";
    public static final String FIELDS = "fields";
    public static final String FIELD = "field";
    public static final String F = "f";
    public static final String HREF = "href";
    public static final String IDS = "ids";
    public static final String ANNOTS = "annots";
    public static final String ANNOT = "annot";
    public static final String VALUE = "value";
    public static final String COORDS = "coords";
    public static final String WIDTH = "width";
    public static final String DASHES = "dashes";
    public static final String STYLE = "style";
    public static final String INTERIOR_COLOR = "interior-color";
    public static final String FRINGE = "fringe";
    public static final String APPEARANCE = "appearance";
    public static final String JUSTIFICATION = "justification";
    public static final String INTENT = "intent";
    public static final String START = "start";
    public static final String END = "end";
    public static final String HEAD = "head";
    public static final String TAIL = "tail";
    public static final String LEADER_EXTENDED = "leaderExtended";
    public static final String LEADER_LENGTH = "leaderLength";
    public static final String CAPTION = "caption";
    public static final String LEADER_OFFSET = "leader-offset";
    public static final String CAPTION_STYLE = "caption-style";
    public static final String CAPTION_OFFSET_H = "caption-offset-h";
    public static final String CAPTION_OFFSET_V = "caption-offset-v";
    public static final String OPEN = "open";
    public static final String ORIGINAL = "original";
    public static final String MODIFIED = "modified";
    public static final String EMPTY_IDS_ELEMENT = "Empty ids element, original and/or modified id attributes not found.";
    public static final String EMPTY_FIELD_VALUE_ELEMENT = "Field has no value.";
    public static final String EMPTY_FIELD_NAME_ELEMENT = "Field has no name attribute.";
    public static final String ROTATION = "rotation";
    public static final String DEST = "Dest";
    public static final String FIT = "Fit";
    public static final String FIT_B = "FitB";
    public static final String FIT_H = "FitH";
    public static final String FIT_V = "FitV";
    public static final String FIT_BH = "FitBH";
    public static final String FIT_BV = "FitBV";
    public static final String FIT_R = "FitR";
    public static final String TOP = "Top";
    public static final String BOTTOM = "Bottom";
    public static final String RIGHT = "Right";
    public static final String LEFT = "Left";
    public static final String XYZ_CAPITAL = "XYZ";
    public static final String XYZ = "xyz";
    public static final String NAMED = "Named";
    public static final String LAUNCH = "Launch";
    public static final String ORIGINAL_NAME = "OriginalName";
    public static final String NEW_WINDOW = "NewWindow";
    public static final String GO_TO = "GoTo";
    public static final String GO_TO_R = "GoToR";
    public static final String FILE = "File";
    public static final String ON_ACTIVATION= "OnActivation";
    public static final String ACTION = "Action";
    public static final String URI = "URI";
    public static final String IS_MAP = "IsMap";
    public static final String INVISIBLE = "invisible";
    public static final String HIDDEN = "hidden";
    public static final String PRINT = "print";
    public static final String NO_ZOOM = "nozoom";
    public static final String NO_ROTATE = "norotate";
    public static final String NO_VIEW = "noview";
    public static final String READ_ONLY = "readonly";
    public static final String LOCKED = "locked";
    public static final String TOGGLE_NO_VIEW = "togglenoview";
    public static final String VERTICES = "vertices";
    public static final String PAGE_CAPITAL = "Page";
    public static final String BORDER_STYLE_ALT = "BorderStyleAlt";
    public static final String H_CORNER_RADIUS = "HCornerRadius";
    public static final String V_CORNER_RADIUS = "VCornerRadius";
    public static final String WIDTH_CAPITAL = "Width";
    public static final String DASH_PATTERN = "DashPattern";
    public static final String NAME_CAPITAL = "Name";
    public static final String DEFAULT_APPEARANCE = "defaultappearance";
    public static final String DEFAULT_STYLE = "defaultstyle";
    public static final String ATTRIBUTE_NAME_OR_VALUE_MISSING = "Attribute name or value are missing";
    public static final String PAGE_IS_MISSING = "Required Page attribute is missing.";
    public static final String UNSUPPORTED_ANNOTATION_ATTRIBUTE = "Unsupported attribute type";
}
