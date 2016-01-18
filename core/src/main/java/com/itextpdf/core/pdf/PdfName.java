package com.itextpdf.core.pdf;

import com.itextpdf.basics.io.ByteBuffer;
import com.itextpdf.basics.io.OutputStream;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class PdfName extends PdfPrimitiveObject implements Comparable<PdfName> {

    private static final byte[] space = OutputStream.getIsoBytes("#20");                //  ' '
    private static final byte[] percent = OutputStream.getIsoBytes("#25");              //  '%'
    private static final byte[] leftParenthesis = OutputStream.getIsoBytes("#28");      //  '('
    private static final byte[] rightParenthesis = OutputStream.getIsoBytes("#29");     //  ')'
    private static final byte[] lessThan = OutputStream.getIsoBytes("#3c");             //  '<'
    private static final byte[] greaterThan = OutputStream.getIsoBytes("#3e");          //  '>'
    private static final byte[] leftSquare = OutputStream.getIsoBytes("#5b");           //  '['
    private static final byte[] rightSquare = OutputStream.getIsoBytes("#5d");          //  ']'
    private static final byte[] leftCurlyBracket = OutputStream.getIsoBytes("#7b");     //  '{'
    private static final byte[] rightCurlyBracket = OutputStream.getIsoBytes("#7d");    //  '}'
    private static final byte[] solidus = OutputStream.getIsoBytes("#2f");              //  '/'
    private static final byte[] numberSign = OutputStream.getIsoBytes("#23");           //  '#'

    public static final PdfName _3D = createDirectName("3D");
    public static final PdfName A = createDirectName("A");
    public static final PdfName A85 = createDirectName("A85");
    public static final PdfName AA = createDirectName("AA");
    public static final PdfName AbsoluteColorimetric = createDirectName("AbsoluteColorimetric");
    public static final PdfName AcroForm = createDirectName("AcroForm");
    public static final PdfName Action = createDirectName("Action");
    public static final PdfName ActualText = createDirectName("ActualText");
    public static final PdfName ADBE = createDirectName("ADBE");
    public static final PdfName Adbe_pkcs7_detached = createDirectName("adbe.pkcs7.detached");
    public static final PdfName Adbe_pkcs7_s4 = createDirectName("adbe.pkcs7.s4");
    public static final PdfName Adbe_pkcs7_s5 = createDirectName("adbe.pkcs7.s5");
    public static final PdfName Adbe_pkcs7_sha1 = createDirectName("adbe.pkcs7.sha1");
    public static final PdfName Adbe_x509_rsa_sha1 = createDirectName("adbe.x509.rsa_sha1");
    public static final PdfName Adobe_PPKLite = createDirectName("Adobe.PPKLite");
    public static final PdfName Adobe_PPKMS = createDirectName("Adobe.PPKMS");
    public static final PdfName Adobe_PubSec = createDirectName("Adobe.PubSec");
    public static final PdfName AESV2 = createDirectName("AESV2");
    public static final PdfName AESV3 = createDirectName("AESV3");
    public static final PdfName AFRelationship = createDirectName("AFRelationship");
    public static final PdfName After = createDirectName("After");
    public static final PdfName AHx = createDirectName("AHx");
    public static final PdfName AIS = createDirectName("AIS");
    public static final PdfName Alaw = createDirectName("ALaw");
    public static final PdfName All = createDirectName("All");
    public static final PdfName AllOff = createDirectName("AllOff");
    public static final PdfName AllOn = createDirectName("AllOn");
    public static final PdfName Alt = createDirectName("Alt");
    public static final PdfName Alternate = createDirectName("Alternate");
    public static final PdfName Alternates = createDirectName("Alternate");
    public static final PdfName AlternatePresentations = createDirectName("AlternatePresentations");
    public static final PdfName Alternative = createDirectName("Alternative");
    public static final PdfName AN = createDirectName("AN");
    public static final PdfName And = createDirectName("And");
    public static final PdfName Annot = createDirectName("Annot");
    public static final PdfName Annots = createDirectName("Annots");
    public static final PdfName Annotation = createDirectName("Annotation");
    public static final PdfName AnnotStates = createDirectName("AnnotStates");
    public static final PdfName AnyOff = createDirectName("AnyOff");
    public static final PdfName AnyOn = createDirectName("AnyOn");
    public static final PdfName AP = createDirectName("AP");
    public static final PdfName App = createDirectName("App");
    public static final PdfName ApplicationOctetStream = createDirectName("application/octet-stream");
    public static final PdfName ApplicationPdf = createDirectName("application/pdf");
    public static final PdfName Approved = createDirectName("Approved");
    public static final PdfName Art = createDirectName("Art");
    public static final PdfName ArtBox = createDirectName("ArtBox");
    public static final PdfName Artifact = createDirectName("Artifact");
    public static final PdfName AS = createDirectName("AS");
    public static final PdfName Ascent = createDirectName("Ascent");
    public static final PdfName ASCII85Decode = createDirectName("ASCII85Decode");
    public static final PdfName ASCIIHexDecode = createDirectName("ASCIIHexDecode");
    public static final PdfName AsIs = createDirectName("AsIs");
    public static final PdfName AuthEvent = createDirectName("AuthEvent");
    public static final PdfName Author = createDirectName("Author");
    public static final PdfName BackgroundColor = createDirectName("BackgroundColor");
    public static final PdfName BaseFont = createDirectName("BaseFont");
    public static final PdfName BaseEncoding = createDirectName("BaseEncoding");
    public static final PdfName BaselineShift = createDirectName("BaselineShift");
    public static final PdfName BaseVersion = createDirectName("BaseVersion");
    public static final PdfName B = createDirectName("B");
    public static final PdfName BBox = createDirectName("BBox");
    public static final PdfName BE = createDirectName("BE");
    public static final PdfName Before = createDirectName("Before");
    public static final PdfName BC = createDirectName("BC");
    public static final PdfName BG = createDirectName("BG");
    public static final PdfName BG2 = createDirectName("BG2");
    public static final PdfName BibEntry = createDirectName("BibEntry");
    public static final PdfName BitsPerComponent = createDirectName("BitsPerComponent");
    public static final PdfName BitsPerCoordinate = createDirectName("BitsPerCoordinate");
    public static final PdfName BitsPerFlag = createDirectName("BitsPerFlag");
    public static final PdfName BitsPerSample = createDirectName("BitsPerSample");
    public static final PdfName Bl = createDirectName("Bl");
    public static final PdfName BlackIs1 = createDirectName("BlackIs1");
    public static final PdfName BlackPoint = createDirectName("BlackPoint");
    public static final PdfName BleedBox = createDirectName("BleedBox");
    public static final PdfName Block = createDirectName("Block");
    public static final PdfName BlockAlign = createDirectName("BlockAlign");
    public static final PdfName BlockQuote = createDirectName("BlockQuote");
    public static final PdfName BM = createDirectName("BM");
    public static final PdfName Border = createDirectName("Border");
    public static final PdfName BorderColor = createDirectName("BorderColor");
    public static final PdfName BorderStyle = createDirectName("BorderStyle");
    public static final PdfName BorderThikness = createDirectName("BorderThikness");
    public static final PdfName Bounds = createDirectName("Bounds");
    public static final PdfName BS = createDirectName("BS");
    public static final PdfName Btn = createDirectName("Btn");
    public static final PdfName ByteRange = createDirectName("ByteRange");
    public static final PdfName C = createDirectName("C");
    public static final PdfName C0 = createDirectName("C0");
    public static final PdfName C1 = createDirectName("C1");
    public static final PdfName CA = createDirectName("CA");
    public static final PdfName ca = createDirectName("ca");
    public static final PdfName CalGray = createDirectName("CalGray");
    public static final PdfName CalRGB = createDirectName("CalRGB");
    public static final PdfName CapHeight = createDirectName("CapHeight");
    public static final PdfName Cap = createDirectName("Cap");
    public static final PdfName Caption = createDirectName("Caption");
    public static final PdfName Caret = createDirectName("Caret");
    public static final PdfName Catalog = createDirectName("Catalog");
    public static final PdfName Category = createDirectName("Category");
    public static final PdfName CCITTFaxDecode = createDirectName("CCITTFaxDecode");
    public static final PdfName Center = createDirectName("Center");
    public static final PdfName Cert = createDirectName("Cert");
    public static final PdfName Certs = createDirectName("Certs");
    public static final PdfName CF = createDirectName("CF");
    public static final PdfName CFM = createDirectName("CFM");
    public static final PdfName Ch = createDirectName("Ch");
    public static final PdfName CIDFontType0 = createDirectName("CIDFontType0");
    public static final PdfName CIDFontType2 = createDirectName("CIDFontType2");
    public static final PdfName CIDSet = createDirectName("CIDSet");
    public static final PdfName CIDSystemInfo = createDirectName("CIDSystemInfo");
    public static final PdfName CIDToGIDMap = createDirectName("CIDToGIDMap");
    public static final PdfName Circle = createDirectName("Circle");
    public static final PdfName CL = createDirectName("CL");
    public static final PdfName ClosedArrow = createDirectName("ClosedArrow");
    public static final PdfName CO = createDirectName("CO");
    public static final PdfName Code = createDirectName("Code");
    public static final PdfName ColSpan = createDirectName("ColSpan");
    public static final PdfName ColumnCount = createDirectName("ColumnCount");
    public static final PdfName ColumnGap = createDirectName("ColumnGap");
    public static final PdfName ColumnWidths = createDirectName("ColumnWidths");
    public static final PdfName ContactInfo = createDirectName("ContactInfo");
    public static final PdfName CharProcs = createDirectName("CharProcs");
    public static final PdfName Color = createDirectName("Color");
    public static final PdfName ColorBurn = createDirectName("ColorBurn");
    public static final PdfName ColorDodge = createDirectName("ColorDodge");
    public static final PdfName Colorants = createDirectName("Colorants");
    public static final PdfName Colors = createDirectName("Colors");
    public static final PdfName ColorSpace = createDirectName("ColorSpace");
    public static final PdfName ColorTransform = createDirectName("ColorTransform");
    public static final PdfName Columns = createDirectName("Columns");
    public static final PdfName Compatible = createDirectName("Compatible");
    public static final PdfName Confidential = createDirectName("Confidential");
    public static final PdfName Configs = createDirectName("Configs");
    public static final PdfName Contents = createDirectName("Contents");
    public static final PdfName Coords = createDirectName("Coords");
    public static final PdfName Count = createDirectName("Count");
    public static final PdfName CP = createDirectName("CP");
    public static final PdfName CRL = createDirectName("CRL");
    public static final PdfName CRLs = createDirectName("CRLs");
    public static final PdfName CreationDate = createDirectName("CreationDate");
    public static final PdfName Creator = createDirectName("Creator");
    public static final PdfName CreatorInfo = createDirectName("CreatorInfo");
    public static final PdfName CropBox = createDirectName("CropBox");
    public static final PdfName Crypt = createDirectName("Crypt");
    public static final PdfName CS = createDirectName("CS");
    public static final PdfName CT = createDirectName("CT");
    public static final PdfName D = createDirectName("D");
    public static final PdfName DA = createDirectName("DA");
    public static final PdfName Darken = createDirectName("Darken");
    public static final PdfName Dashed = createDirectName("Dashed");
    public static final PdfName Data = createDirectName("Data");
    public static final PdfName DCTDecode = createDirectName("DCTDecode");
    public static final PdfName Decimal = createDirectName("Decimal");
    public static final PdfName Decode = createDirectName("Decode");
    public static final PdfName DecodeParms = createDirectName("DecodeParms");
    public static final PdfName Default = createDirectName("Default");
    public static final PdfName DefaultCMYK = createDirectName("DefaultCMYK");
    public static final PdfName DefaultCryptFilter = createDirectName("DefaultCryptFilter");
    public static final PdfName DefaultGray = createDirectName("DefaultGray");
    public static final PdfName DefaultRGB = createDirectName("DefaultRGB");
    public static final PdfName Departmental = createDirectName("Departmental");
    public static final PdfName DescendantFonts = createDirectName("DescendantFonts");
    public static final PdfName Desc = createDirectName("Desc");
    public static final PdfName Descent = createDirectName("Descent");
    public static final PdfName Design = createDirectName("Design");
    public static final PdfName Dest = createDirectName("Dest");
    public static final PdfName DestOutputProfile = createDirectName("DestOutputProfile");
    public static final PdfName Dests = createDirectName("Dests");
    public static final PdfName DeviceCMY = createDirectName("DeviceCMY");
    public static final PdfName DeviceCMYK = createDirectName("DeviceCMYK");
    public static final PdfName DeviceGray = createDirectName("DeviceGray");
    public static final PdfName DeviceN = createDirectName("DeviceN");
    public static final PdfName DeviceRGB = createDirectName("DeviceRGB");
    public static final PdfName DeviceRGBK = createDirectName("DeviceRGBK");
    public static final PdfName Difference = createDirectName("Difference");
    public static final PdfName Differences = createDirectName("Differences");
    public static final PdfName Div = createDirectName("Div");
    public static final PdfName DigestLocation = createDirectName("DigestLocation");
    public static final PdfName DigestMethod = createDirectName("DigestMethod");
    public static final PdfName DigestValue = createDirectName("DigestValue");
    public static final PdfName DocMDP = createDirectName("DocMDP");
    public static final PdfName DocOpen = createDirectName("DocOpen");
    public static final PdfName DocTimeStamp = createDirectName("DocTimeStamp");
    public static final PdfName Document = createDirectName("Document");
    public static final PdfName Domain = createDirectName("Domain");
    public static final PdfName Dotted = createDirectName("Dotted");
    public static final PdfName Double = createDirectName("Double");
    public static final PdfName DP = createDirectName("DP");
    public static final PdfName DR = createDirectName("DR");
    public static final PdfName Draft = createDirectName("Draft");
    public static final PdfName DS = createDirectName("DS");
    public static final PdfName DSS = createDirectName("DSS");
    public static final PdfName DV = createDirectName("DV");
    public static final PdfName DW = createDirectName("DW");
    public static final PdfName E = createDirectName("E");
    public static final PdfName EF = createDirectName("EF");
    public static final PdfName EFF = createDirectName("EFF");
    public static final PdfName EFOpen = createDirectName("EFOpen");
    public static final PdfName EmbeddedFile = createDirectName("EmbeddedFile");
    public static final PdfName EmbeddedFiles = createDirectName("EmbeddedFiles");
    public static final PdfName Encode = createDirectName("Encode");
    public static final PdfName EncodedByteAlign = createDirectName("EncodedByteAlign");
    public static final PdfName Encoding = createDirectName("Encoding");
    public static final PdfName Encrypt = createDirectName("Encrypt");
    public static final PdfName EncryptMetadata = createDirectName("EncryptMetadata");
    public static final PdfName End = createDirectName("End");
    public static final PdfName EndIndent = createDirectName("EndIndent");
    public static final PdfName EndOfBlock = createDirectName("EndOfBlock");
    public static final PdfName EndOfLine = createDirectName("EndOfLine");
    public static final PdfName ESIC = createDirectName("ESIC");
    public static final PdfName ETSI_CAdES_DETACHED = createDirectName("ETSI.CAdES.detached");
    public static final PdfName ETSI_RFC3161 = createDirectName("ETSI.RFC3161");
    public static final PdfName Event = createDirectName("Event");
    public static final PdfName Exclude = createDirectName("Exclude");
    public static final PdfName Exclusion = createDirectName("Exclusion");
    public static final PdfName ExData = createDirectName("ExData");
    public static final PdfName Experimental = createDirectName("Experimental");
    public static final PdfName Expired = createDirectName("Expired");
    public static final PdfName Export = createDirectName("Export");
    public static final PdfName ExportState = createDirectName("ExportState");
    public static final PdfName Extend = createDirectName("Extend");
    public static final PdfName Extends = createDirectName("Extends");
    public static final PdfName Extensions = createDirectName("Extensions");
    public static final PdfName ExtensionLevel = createDirectName("ExtensionLevel");
    public static final PdfName ExtGState = createDirectName("ExtGState");
    public static final PdfName F = createDirectName("F");
    public static final PdfName False = createDirectName("false");
    public static final PdfName Ff = createDirectName("Ff");
    public static final PdfName FieldMDP = createDirectName("FieldMDP");
    public static final PdfName Fields = createDirectName("Fields");
    public static final PdfName Figure = createDirectName("Figure");
    public static final PdfName FileAttachment = createDirectName("FileAttachment");
    public static final PdfName Filespec = createDirectName("Filespec");
    public static final PdfName Filter = createDirectName("Filter");
    public static final PdfName FFilter = createDirectName("FFilter");
    public static final PdfName FDecodeParams = createDirectName("FDecodeParams");
    public static final PdfName Final = createDirectName("Final");
    public static final PdfName First = createDirectName("First");
    public static final PdfName FirstChar = createDirectName("FirstChar");
    public static final PdfName FirstPage = createDirectName("FirstPage");
    public static final PdfName Fit = createDirectName("Fit");
    public static final PdfName FitB = createDirectName("FitB");
    public static final PdfName FitBH = createDirectName("FitBH");
    public static final PdfName FitBV = createDirectName("FitBV");
    public static final PdfName FitH = createDirectName("FitH");
    public static final PdfName FitR = createDirectName("FitR");
    public static final PdfName FitV = createDirectName("FitV");
    public static final PdfName FixedPrint = createDirectName("FixedPrint");
    public static final PdfName FL = createDirectName("FL");
    public static final PdfName Flags = createDirectName("Flags");
    public static final PdfName FlateDecode = createDirectName("FlateDecode");
    public static final PdfName Fo = createDirectName("Fo");
    public static final PdfName Font = createDirectName("Font");
    public static final PdfName FontBBox = createDirectName("FontBBox");
    public static final PdfName FontDescriptor = createDirectName("FontDescriptor");
    public static final PdfName FontFamily = createDirectName("FontFamily");
    public static final PdfName FontFauxing = createDirectName("FontFauxing");
    public static final PdfName FontFile = createDirectName("FontFile");
    public static final PdfName FontFile2 = createDirectName("FontFile2");
    public static final PdfName FontFile3 = createDirectName("FontFile3");
    public static final PdfName FontMatrix = createDirectName("FontMatrix");
    public static final PdfName FontName = createDirectName("FontName");
    public static final PdfName FontWeight = createDirectName("FontWeight");
    public static final PdfName FontStretch = createDirectName("FontStretch");
    public static final PdfName ForComment = createDirectName("ForComment");
    public static final PdfName Form = createDirectName("Form");
    public static final PdfName ForPublicRelease = createDirectName("ForPublicRelease");
    public static final PdfName FormType = createDirectName("FormType");
    public static final PdfName FreeText = createDirectName("FreeText");
    public static final PdfName FreeTextCallout = createDirectName("FreeTextCallout");
    public static final PdfName FreeTextTypeWriter = createDirectName("FreeTextTypeWriter");
    public static final PdfName FS = createDirectName("FS");
    public static final PdfName Formula = createDirectName("Formula");
    public static final PdfName FT = createDirectName("FT");
    public static final PdfName Function = createDirectName("Function");
    public static final PdfName Functions = createDirectName("Functions");
    public static final PdfName FunctionType = createDirectName("FunctionType");
    public static final PdfName Gamma = createDirectName("Gamma");
    public static final PdfName GlyphOrientationVertical = createDirectName("GlyphOrientationVertical");
    public static final PdfName GoTo = createDirectName("GoTo");
    public static final PdfName GoTo3DView = createDirectName("GoTo3DView");
    public static final PdfName GoToE = createDirectName("GoToE");
    public static final PdfName GoToR = createDirectName("GoToR");
    public static final PdfName Graph = createDirectName("Graph");
    public static final PdfName Group = createDirectName("Group");
    public static final PdfName Groove = createDirectName("Groove");
    public static final PdfName GTS_PDFA1 = createDirectName("GTS_PDFA1");
    public static final PdfName H = createDirectName("H");
    public static final PdfName H1 = createDirectName("H1");
    public static final PdfName H2 = createDirectName("H2");
    public static final PdfName H3 = createDirectName("H3");
    public static final PdfName H4 = createDirectName("H4");
    public static final PdfName H5 = createDirectName("H5");
    public static final PdfName H6 = createDirectName("H6");
    public static final PdfName HalftoneType = createDirectName("HalftoneType");
    public static final PdfName HalftoneName = createDirectName("HalftoneName");
    public static final PdfName HardLight = createDirectName("HardLight");
    public static final PdfName Height = createDirectName("Height");
    public static final PdfName Hide = createDirectName("Hide");
    public static final PdfName Hidden = createDirectName("Hidden");
    public static final PdfName Highlight = createDirectName("Highlight");
    public static final PdfName HT = createDirectName("HT");
    public static final PdfName HTP = createDirectName("HTP");
    public static final PdfName Hue = createDirectName("Hue");
    public static final PdfName I = createDirectName("I");
    public static final PdfName IC = createDirectName("IC");
    public static final PdfName ICCBased = createDirectName("ICCBased");
    public static final PdfName ID = createDirectName("ID");
    public static final PdfName Identity = createDirectName("Identity");
    public static final PdfName Inset = createDirectName("Inset");
    public static final PdfName Image = createDirectName("Image");
    public static final PdfName ImageMask = createDirectName("ImageMask");
    public static final PdfName ImportData = createDirectName("ImportData");
    public static final PdfName Include = createDirectName("Include");
    public static final PdfName Index = createDirectName("Index");
    public static final PdfName Indexed = createDirectName("Indexed");
    public static final PdfName Info = createDirectName("Info");
    public static final PdfName Inline = createDirectName("Inline");
    public static final PdfName InlineAlign = createDirectName("InlineAlign");
    public static final PdfName Ink = createDirectName("Ink");
    public static final PdfName InkList = createDirectName("InkList");
    public static final PdfName Intent = createDirectName("Intent");
    public static final PdfName Interpolate = createDirectName("Interpolate");
    public static final PdfName IRT = createDirectName("IRT");
    public static final PdfName IsMap = createDirectName("IsMap");
    public static final PdfName ItalicAngle = createDirectName("ItalicAngle");
    public static final PdfName IT = createDirectName("IT");
    public static final PdfName JavaScript = createDirectName("JavaScript");
    public static final PdfName JBIG2Decode = createDirectName("JBIG2Decode");
    public static final PdfName JBIG2Globals = createDirectName("JBIG2Globals");
    public static final PdfName JPXDecode = createDirectName("JPXDecode");
    public static final PdfName JS = createDirectName("JS");
    public static final PdfName Justify = createDirectName("Justify");
    public static final PdfName K = createDirectName("K");
    public static final PdfName Keywords = createDirectName("Keywords");
    public static final PdfName Kids = createDirectName("Kids");
    public static final PdfName L = createDirectName("L");
    public static final PdfName Lab = createDirectName("Lab");
    public static final PdfName Lang = createDirectName("Lang");
    public static final PdfName Language = createDirectName("Language");
    public static final PdfName Last = createDirectName("Last");
    public static final PdfName LastChar = createDirectName("LastChar");
    public static final PdfName LastModified = createDirectName("LastModified");
    public static final PdfName LastPage = createDirectName("LastPage");
    public static final PdfName Launch = createDirectName("Launch");
    public static final PdfName Layout = createDirectName("Layout");
    public static final PdfName Lbl = createDirectName("Lbl");
    public static final PdfName LBody = createDirectName("LBody");
    public static final PdfName LC = createDirectName("LC");
    public static final PdfName Leading = createDirectName("Leading");
    public static final PdfName LE = createDirectName("LE");
    public static final PdfName Length = createDirectName("Length");
    public static final PdfName LI = createDirectName("LI");
    public static final PdfName Lighten = createDirectName("Lighten");
    public static final PdfName Limits = createDirectName("Limits");
    public static final PdfName Line = createDirectName("Line");
    public static final PdfName LineHeight = createDirectName("LineHeight");
    public static final PdfName LineThrough = createDirectName("LineThrough");
    public static final PdfName Link = createDirectName("Link");
    public static final PdfName List = createDirectName("List");
    public static final PdfName ListMode = createDirectName("ListMode");
    public static final PdfName ListNumbering = createDirectName("ListNumbering");
    public static final PdfName LJ = createDirectName("LJ");
    public static final PdfName LLE = createDirectName("LLE");
    public static final PdfName LLO = createDirectName("LLO");
    public static final PdfName Lock = createDirectName("Lock");
    public static final PdfName Locked = createDirectName("Locked");
    public static final PdfName Location = createDirectName("Location");
    public static final PdfName LowerAlpha = createDirectName("LowerAlpha");
    public static final PdfName LowerRoman = createDirectName("LowerRoman");
    public static final PdfName Luminosity = createDirectName("Luminosity");
    public static final PdfName LW = createDirectName("LW");
    public static final PdfName LZWDecode = createDirectName("LZWDecode");
    public static final PdfName M = createDirectName("M");
    public static final PdfName MacExpertEncoding = createDirectName("MacExpertEncoding");
    public static final PdfName MacRomanEncoding = createDirectName("MacRomanEncoding");
    public static final PdfName Marked = createDirectName("Marked");
    public static final PdfName MarkInfo = createDirectName("MarkInfo");
    public static final PdfName Markup = createDirectName("Markup");
    public static final PdfName MarkStyle = createDirectName("MarkStyle");
    public static final PdfName Mask = createDirectName("Mask");
    public static final PdfName Matrix = createDirectName("Matrix");
    public static final PdfName max = createDirectName("max");
    public static final PdfName MaxLen = createDirectName("MaxLen");
    public static final PdfName MCD = createDirectName("MCD");
    public static final PdfName MCID = createDirectName("MCID");
    public static final PdfName MCR = createDirectName("MCR");
    public static final PdfName Measure = createDirectName("Measure");
    public static final PdfName MediaBox = createDirectName("MediaBox");
    public static final PdfName MediaClip = createDirectName("MediaClip");
    public static final PdfName Metadata = createDirectName("Metadata");
    public static final PdfName Middle = createDirectName("Middle");
    public static final PdfName min = createDirectName("min");
    public static final PdfName Mix = createDirectName("Mix");
    public static final PdfName MissingWidth = createDirectName("MissingWidth");
    public static final PdfName MK = createDirectName("MK");
    public static final PdfName ML = createDirectName("ML");
    public static final PdfName MN = createDirectName("ML");
    public static final PdfName ModDate = createDirectName("ModDate");
    public static final PdfName Movie = createDirectName("Movie");
    public static final PdfName MR = createDirectName("MR");
    public static final PdfName MuLaw = createDirectName("muLaw");
    public static final PdfName Multiply = createDirectName("Multiply");
    public static final PdfName N = createDirectName("N");
    public static final PdfName Name = createDirectName("Name");
    public static final PdfName Named = createDirectName("Named");
    public static final PdfName Names = createDirectName("Names");
    public static final PdfName NeedAppearances = createDirectName("NeedAppearances");
    public static final PdfName NeedsRendering = createDirectName("NeedsRendering");
    public static final PdfName NewWindow = createDirectName("NewWindow");
    public static final PdfName Next = createDirectName("Next");
    public static final PdfName NextPage = createDirectName("NextPage");
    public static final PdfName NM = createDirectName("NM");
    public static final PdfName None = createDirectName("None");
    public static final PdfName NonStruct = createDirectName("NonStruct");
    public static final PdfName NoOp = createDirectName("NoOp");
    public static final PdfName Normal = createDirectName("Normal");
    public static final PdfName Not = createDirectName("Not");
    public static final PdfName NotApproved = createDirectName("NotApproved");
    public static final PdfName Note = createDirectName("Note");
    public static final PdfName NotForPublicRelease = createDirectName("NotForPublicRelease");
    public static final PdfName Nums = createDirectName("Nums");
    public static final PdfName O = createDirectName("O");
    public static final PdfName Obj = createDirectName("Obj");
    public static final PdfName OBJR = createDirectName("OBJR");
    public static final PdfName ObjStm = createDirectName("ObjStm");
    public static final PdfName OC = createDirectName("OC");
    public static final PdfName OCG = createDirectName("OCG");
    public static final PdfName OCGs = createDirectName("OCGs");
    public static final PdfName OCMD = createDirectName("OCMD");
    public static final PdfName OCProperties = createDirectName("OCProperties");
    public static final PdfName OCSP = createDirectName("OCSP");
    public static final PdfName OCSPs = createDirectName("OCSPs");
    public static final PdfName OE = createDirectName("OE");
    public static final PdfName OFF = createDirectName("OFF");
    public static final PdfName ON = createDirectName("ON");
    public static final PdfName OP = createDirectName("OP");
    public static final PdfName op = createDirectName("op");
    public static final PdfName Open = createDirectName("Open");
    public static final PdfName OpenAction = createDirectName("OpenAction");
    public static final PdfName OpenArrow = createDirectName("OpenArrow");
    public static final PdfName Operation = createDirectName("Operation");
    public static final PdfName OPI = createDirectName("OPI");
    public static final PdfName OPM = createDirectName("OPM");
    public static final PdfName Opt = createDirectName("Opt");
    public static final PdfName Or = createDirectName("Or");
    public static final PdfName Order = createDirectName("Order");
    public static final PdfName Ordering = createDirectName("Ordering");
    public static final PdfName Outlines = createDirectName("Outlines");
    public static final PdfName OutputCondition = createDirectName("OutputCondition");
    public static final PdfName OutputConditionIdentifier = createDirectName("OutputConditionIdentifier");
    public static final PdfName OutputIntents = createDirectName("OutputIntents");
    public static final PdfName Outset = createDirectName("Outset");
    public static final PdfName Overlay = createDirectName("Overlay");
    public static final PdfName OverlayText = createDirectName("OverlayText");
    public static final PdfName P = createDirectName("P");
    public static final PdfName PA = createDirectName("PA");
    public static final PdfName Padding = createDirectName("Padding");
    public static final PdfName Page = createDirectName("Page");
    public static final PdfName PageElement = createDirectName("PageElement");
    public static final PdfName PageMode = createDirectName("PageMode");
    public static final PdfName Pages = createDirectName("Pages");
    public static final PdfName PaintType = createDirectName("PaintType");
    public static final PdfName Panose = createDirectName("Panose");
    public static final PdfName Paperclip = createDirectName("Paperclip");
    public static final PdfName Params = createDirectName("Params");
    public static final PdfName Parent = createDirectName("Parent");
    public static final PdfName ParentTree = createDirectName("ParentTree");
    public static final PdfName Part = createDirectName("Part");
    public static final PdfName Pattern = createDirectName("Pattern");
    public static final PdfName PatternType = createDirectName("PatternType");
    public static final PdfName Perceptual = createDirectName("Perceptual");
    public static final PdfName Perms = createDirectName("Perms");
    public static final PdfName PCM = createDirectName("PCM");
    public static final PdfName Pdf_Version_1_2 = createDirectName("1.2");
    public static final PdfName Pdf_Version_1_3 = createDirectName("1.3");
    public static final PdfName Pdf_Version_1_4 = createDirectName("1.4");
    public static final PdfName Pdf_Version_1_5 = createDirectName("1.5");
    public static final PdfName Pdf_Version_1_6 = createDirectName("1.6");
    public static final PdfName Pdf_Version_1_7 = createDirectName("1.7");
    public static final PdfName Pg = createDirectName("Pg");
    public static final PdfName Placement = createDirectName("Placement");
    public static final PdfName Polygon = createDirectName("Polygon");
    public static final PdfName PolyLine = createDirectName("PolyLine");
    public static final PdfName Popup = createDirectName("Popup");
    public static final PdfName Predictor = createDirectName("Predictor");
    public static final PdfName Preferred = createDirectName("Preferred");
    public static final PdfName PreserveRB = createDirectName("PreserveRB");
    public static final PdfName PresSteps = createDirectName("PresSteps");
    public static final PdfName Prev = createDirectName("Prev");
    public static final PdfName PrevPage = createDirectName("PrevPage");
    public static final PdfName Print = createDirectName("Print");
    public static final PdfName PrinterMark = createDirectName("PrinterMark");
    public static final PdfName PrintState = createDirectName("PrintState");
    public static final PdfName Private = createDirectName("Private");
    public static final PdfName ProcSet = createDirectName("ProcSet");
    public static final PdfName Producer = createDirectName("Producer");
    public static final PdfName Prop_Build = createDirectName("Prop_Build");
    public static final PdfName Properties = createDirectName("Properties");
    public static final PdfName PS = createDirectName("PS");
    public static final PdfName Pushpin = createDirectName("PushPin");
    public static final PdfName Q = createDirectName("Q");
    public static final PdfName Quote = createDirectName("Quote");
    public static final PdfName QuadPoints = createDirectName("QuadPoints");
    public static final PdfName R = createDirectName("R");
    public static final PdfName Range = createDirectName("Range");
    public static final PdfName Raw = createDirectName("Raw");
    public static final PdfName RB = createDirectName("RB");
    public static final PdfName RBGroups = createDirectName("RBGroups");
    public static final PdfName RC = createDirectName("RC");
    public static final PdfName RD = createDirectName("RD");
    public static final PdfName Reason = createDirectName("Reason");
    public static final PdfName Recipients = createDirectName("Recipients");
    public static final PdfName Rect = createDirectName("Rect");
    public static final PdfName Redact = createDirectName("Redact");
    public static final PdfName Reference = createDirectName("Reference");
    public static final PdfName Registry = createDirectName("Registry");
    public static final PdfName RegistryName = createDirectName("RegistryName");
    public static final PdfName RelativeColorimetric = createDirectName("RelativeColorimetric");
    public static final PdfName Rendition = createDirectName("Rendition");
    public static final PdfName Repeat = createDirectName("Repeat");
    public static final PdfName ResetForm = createDirectName("ResetForm");
    public static final PdfName Requirements = createDirectName("Requirements");
    public static final PdfName Resources = createDirectName("Resources");
    public static final PdfName RI = createDirectName("RI");
    public static final PdfName Ridge = createDirectName("Ridge");
    public static final PdfName RO = createDirectName("RO");
    public static final PdfName RoleMap = createDirectName("RoleMap");
    public static final PdfName Root = createDirectName("Root");
    public static final PdfName Rotate = createDirectName("Rotate");
    public static final PdfName Rows = createDirectName("Rows");
    public static final PdfName RowSpan = createDirectName("RowSpan");
    public static final PdfName RP = createDirectName("RP");
    public static final PdfName RT = createDirectName("RT");
    public static final PdfName Ruby = createDirectName("Ruby");
    public static final PdfName RubyAlign = createDirectName("RubyAlign");
    public static final PdfName RubyPosition = createDirectName("RubyPosition");
    public static final PdfName RunLengthDecode = createDirectName("RunLengthDecode");
    public static final PdfName RV = createDirectName("RV");
    public static final PdfName Stream = createDirectName("Stream");
    public static final PdfName S = createDirectName("S");
    public static final PdfName SA = createDirectName("SA");
    public static final PdfName Saturation = createDirectName("Saturation");
    public static final PdfName Screen = createDirectName("Screen");
    public static final PdfName Sect = createDirectName("Sect");
    public static final PdfName Separation = createDirectName("Separation");
    public static final PdfName SeparationColorNames = createDirectName("SeparationColorNames");
    public static final PdfName Shading = createDirectName("Shading");
    public static final PdfName ShadingType = createDirectName("ShadingType");
    public static final PdfName SetOCGState = createDirectName("SetOCGState");
    public static final PdfName SetState = createDirectName("SetState");
    public static final PdfName Sig = createDirectName("Sig");
    public static final PdfName SigFieldLock = createDirectName("SigFieldLock");
    public static final PdfName SigFlags = createDirectName("SigFlags");
    public static final PdfName Signed = createDirectName("Signed");
    public static final PdfName SigRef = createDirectName("SigRef");
    public static final PdfName Size = createDirectName("Size");
    public static final PdfName SM = createDirectName("SM");
    public static final PdfName SMask = createDirectName("SMask");
    public static final PdfName SMaskInData = createDirectName("SMaskInData");
    public static final PdfName SoftLight = createDirectName("SoftLight");
    public static final PdfName Sold = createDirectName("Sold");
    public static final PdfName Solid = createDirectName("Solid");
    public static final PdfName Sound = createDirectName("Sound");
    public static final PdfName Source = createDirectName("Source");
    public static final PdfName Span = createDirectName("Span");
    public static final PdfName SpaceBefore = createDirectName("SpaceBefore");
    public static final PdfName SpaceAfter = createDirectName("SpaceAfter");
    public static final PdfName Square = createDirectName("Square");
    public static final PdfName Squiggly = createDirectName("Squiggly");
    public static final PdfName Stamp = createDirectName("Stamp");
    public static final PdfName Standard = createDirectName("Standard");
    public static final PdfName Start = createDirectName("Start");
    public static final PdfName StartIndent = createDirectName("StartIndent");
    public static final PdfName State = createDirectName("State");
    public static final PdfName StateModel = createDirectName("StateModel");
    public static final PdfName StdCF = createDirectName("StdCF");
    public static final PdfName StemV = createDirectName("StemV");
    public static final PdfName StemH = createDirectName("StemH");
    public static final PdfName StmF = createDirectName("StmF");
    public static final PdfName StrF = createDirectName("StrF");
    public static final PdfName StrikeOut = createDirectName("StrikeOut");
    public static final PdfName StructElem = createDirectName("StructElem");
    public static final PdfName StructParent = createDirectName("StructParent");
    public static final PdfName StructParents = createDirectName("StructParents");
    public static final PdfName StructTreeRoot = createDirectName("StructTreeRoot");
    public static final PdfName Style = createDirectName("Style");
    public static final PdfName SubFilter = createDirectName("SubFilter");
    public static final PdfName Subj = createDirectName("Subj");
    public static final PdfName Subject = createDirectName("Subject");
    public static final PdfName SubmitForm = createDirectName("SubmitForm");
    public static final PdfName Subtype = createDirectName("Subtype");
    public static final PdfName Subtype2 = createDirectName("Subtype2");
    public static final PdfName Supplement = createDirectName("Supplement");
    public static final PdfName Sy = createDirectName("Sy");
    public static final PdfName Symbol = new PdfName("Symbol");
    public static final PdfName Synchronous = createDirectName("Synchronous");
    public static final PdfName T = createDirectName("T");
    public static final PdfName Tag = createDirectName("Tag");
    public static final PdfName TBorderStyle = createDirectName("TBorderStyle");
    public static final PdfName Trans = createDirectName("Trans");
    public static final PdfName True = createDirectName("true");
    public static final PdfName Table = createDirectName("Table");
    public static final PdfName TBody = createDirectName("TBody");
    public static final PdfName TD = createDirectName("TD");
    public static final PdfName Text = createDirectName("Text");
    public static final PdfName TextAlign = createDirectName("TextAlign");
    public static final PdfName TextDecorationColor = createDirectName("TextDecorationColor");
    public static final PdfName TextDecorationThickness = createDirectName("TextDecorationThickness");
    public static final PdfName TextDecorationType = createDirectName("TextDecorationType");
    public static final PdfName TextIndent = createDirectName("TextIndent");
    public static final PdfName TF = createDirectName("TF");
    public static final PdfName TFoot = createDirectName("TFoot");
    public static final PdfName TH = createDirectName("TH");
    public static final PdfName THead = createDirectName("THead");
    public static final PdfName TI = createDirectName("TI");
    public static final PdfName TilingType = createDirectName("TilingType");
    public static final PdfName Title = createDirectName("Title");
    public static final PdfName TPadding = createDirectName("TPadding");
    public static final PdfName TrimBox = createDirectName("TrimBox");
    public static final PdfName TK = createDirectName("TK");
    public static final PdfName TM = createDirectName("TM");
    public static final PdfName TOC = createDirectName("TOC");
    public static final PdfName TOCI = createDirectName("TOCI");
    public static final PdfName TopSecret = createDirectName("TopSecret");
    public static final PdfName ToUnicode = createDirectName("ToUnicode");
    public static final PdfName TR = createDirectName("TR");
    public static final PdfName TR2 = createDirectName("TR2");
    public static final PdfName TransformMethod = createDirectName("TransformMethod");
    public static final PdfName TransformParams = createDirectName("TransformParams");
    public static final PdfName Transparency = createDirectName("Transparency");
    public static final PdfName TrapNet = createDirectName("TrapNet");
    public static final PdfName TrapRegions = createDirectName("TrapRegions");
    public static final PdfName TrapStyles = createDirectName("TrapStyles");
    public static final PdfName TrueType = createDirectName("TrueType");
    public static final PdfName TU = createDirectName("TU");
    public static final PdfName Tx = createDirectName("Tx");
    public static final PdfName Type = createDirectName("Type");
    public static final PdfName Type0 = createDirectName("Type0");
    public static final PdfName Type1 = createDirectName("Type1");
    public static final PdfName Type3 = createDirectName("Type3");
    public static final PdfName U = createDirectName("U");
    public static final PdfName UCR = createDirectName("UCR");
    public static final PdfName UR3 = createDirectName("UR3");
    public static final PdfName UCR2 = createDirectName("UCR2");
    public static final PdfName UE = createDirectName("UE");
    public static final PdfName UF = createDirectName("UF");
    public static final PdfName Underline = createDirectName("Underline");
    public static final PdfName Unspecified = createDirectName("Unspecified");
    public static final PdfName UpperAlpha = createDirectName("UpperAlpha");
    public static final PdfName UpperRoman = createDirectName("UpperRoman");
    public static final PdfName URI = createDirectName("URI");
    public static final PdfName URL = createDirectName("URL");
    public static final PdfName Usage = createDirectName("Usage");
    public static final PdfName UseOutlines = createDirectName("UseOutlines");
    public static final PdfName User = createDirectName("User");
    public static final PdfName UserUnit = createDirectName("UserUnit");
    public static final PdfName V = createDirectName("V");
    public static final PdfName V2 = createDirectName("V2");
    public static final PdfName VE = createDirectName("VE");
    public static final PdfName Version = createDirectName("Version");
    public static final PdfName Vertices = createDirectName("Vertices");
    public static final PdfName VerticesPerRow = createDirectName("VerticesPerRow");
    public static final PdfName View = createDirectName("View");
    public static final PdfName ViewState = createDirectName("ViewState");
    public static final PdfName VisiblePages = createDirectName("VisiblePages");
    public static final PdfName Volatile = createDirectName("Volatile");
    public static final PdfName Volume = createDirectName("Volume");
    public static final PdfName VRI = createDirectName("VRI");
    public static final PdfName W = createDirectName("W");
    public static final PdfName W2 = createDirectName("W2");
    public static final PdfName Warichu = createDirectName("Warichu");
    public static final PdfName Watermark = createDirectName("Watermark");
    public static final PdfName WhitePoint = createDirectName("WhitePoint");
    public static final PdfName Width = createDirectName("Width");
    public static final PdfName Widths = createDirectName("Widths");
    public static final PdfName Widget = createDirectName("Widget");
    public static final PdfName Win = createDirectName("Win");
    public static final PdfName WinAnsiEncoding = createDirectName("WinAnsiEncoding");
    public static final PdfName WritingMode = createDirectName("WritingMode");
    public static final PdfName WP = createDirectName("WP");
    public static final PdfName WT = createDirectName("WT");
    public static final PdfName X = createDirectName("X");
    public static final PdfName XFA = createDirectName("XFA");
    public static final PdfName XML = createDirectName("XML");
    public static final PdfName XObject = createDirectName("XObject");
    public static final PdfName XHeight = createDirectName("XHeight");
    public static final PdfName XRef = createDirectName("XRef");
    public static final PdfName XRefStm = createDirectName("XRefStm");
    public static final PdfName XStep = createDirectName("XStep");
    public static final PdfName XYZ = createDirectName("XYZ");
    public static final PdfName YStep = createDirectName("YStep");
    public static final PdfName ZapfDingbats = new PdfName("ZapfDingbats");
    public static final PdfName Zoom = createDirectName("Zoom");


    protected String value = null;

    /**
     * map strings to all known static names
     */
    public static Map<String, PdfName> staticNames;

    /**
     * Use reflection to cache all the static public final names so
     * future <code>PdfName</code> additions don't have to be "added twice".
     * A bit less efficient (around 50ms spent here on a 2.2ghz machine),
     *  but Much Less error prone.
     */

    static {
        Field[] fields = PdfName.class.getDeclaredFields();
        staticNames = new HashMap<String, PdfName>(fields.length);
        final int flags = Modifier.STATIC | Modifier.PUBLIC | Modifier.FINAL;
        try {
            for (Field field : fields) {
                if ((field.getModifiers() & flags) == flags && field.getType().equals(PdfName.class)) {
                    PdfName name = (PdfName) field.get(null);
                    staticNames.put(name.getValue(), name);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static PdfName createDirectName(String name) {
        return new PdfName(name, true);
    }

    public PdfName(String value) {
        super();
        this.value = value;
    }

    private PdfName(String value, boolean directOnly) {
        super(directOnly);
        this.value = value;
    }

    public PdfName(byte[] content) {
        super(content);
    }

    private PdfName() {
        super();
    }

    @Override
    public int getType() {
        return PdfObject.Name;
    }

    public String getValue() {
        if (value == null)
            generateValue();
        return value;
    }

    @Override
    public int compareTo(PdfName o) {
        if (value != null && o.value != null) {
            return value.compareTo(o.value);
        } else if (content != null && o.content != null) {
            return compareContent(o);
        } else
            return getValue().compareTo(o.getValue());
    }

    /**
     * Marks object to be saved as indirect.
     *
     * @param document a document the indirect reference will belong to.
     * @return object itself.
     */
    @SuppressWarnings("unchecked")
    @Override
    public PdfName makeIndirect(PdfDocument document) {
        return super.makeIndirect(document);
    }

    /**
     * Marks object to be saved as indirect.
     *
     * @param document a document the indirect reference will belong to.
     * @return object itself.
     */
    @SuppressWarnings("unchecked")
    @Override
    public PdfName makeIndirect(PdfDocument document, PdfIndirectReference reference) {
        return super.makeIndirect(document, reference);
    }

    /**
     * Copies object to a specified document.
     * Works only for objects that are read from existing document, otherwise an exception is thrown.
     *
     * @param document document to copy object to.
     * @return copied object.
     */
    @SuppressWarnings("unchecked")
    @Override
    public PdfName copyToDocument(PdfDocument document) {
        return super.copyToDocument(document, true);
    }

    /**
     * Copies object to a specified document.
     * Works only for objects that are read from existing document, otherwise an exception is thrown.
     *
     * @param document         document to copy object to.
     * @param allowDuplicating indicates if to allow copy objects which already have been copied.
     *                         If object is associated with any indirect reference and allowDuplicating is false then already existing reference will be returned instead of copying object.
     *                         If allowDuplicating is true then object will be copied and new indirect reference will be assigned.
     * @return copied object.
     */
    @SuppressWarnings("unchecked")
    @Override
    public PdfName copyToDocument(PdfDocument document, boolean allowDuplicating) {
        return super.copyToDocument(document, allowDuplicating);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PdfName pdfName = (PdfName) o;
        return this.compareTo(pdfName) == 0;
    }

    @Override
    public int hashCode() {
        return getValue().hashCode();
    }

    protected void generateValue() {
        StringBuilder buf = new StringBuilder();
        try {
            for (int k = 0; k < content.length; ++k) {
                char c = (char) content[k];
                if (c == '#') {
                    byte c1 = content[k + 1];
                    byte c2 = content[k + 2];
                    c = (char) ((ByteBuffer.getHex(c1) << 4) + ByteBuffer.getHex(c2));
                    k += 2;
                }
                buf.append(c);
            }
        } catch (IndexOutOfBoundsException e) {
            // empty on purpose
        }
        value = buf.toString();
    }

    @Override
    protected void generateContent() {
        int length = value.length();
        ByteBuffer buf = new ByteBuffer(length + 20);
        char c;
        char chars[] = value.toCharArray();
        for (int k = 0; k < length; k++) {
            c = (char) (chars[k] & 0xff);
            // Escape special characters
            switch (c) {
                case ' ':
                    buf.append(space);
                    break;
                case '%':
                    buf.append(percent);
                    break;
                case '(':
                    buf.append(leftParenthesis);
                    break;
                case ')':
                    buf.append(rightParenthesis);
                    break;
                case '<':
                    buf.append(lessThan);
                    break;
                case '>':
                    buf.append(greaterThan);
                    break;
                case '[':
                    buf.append(leftSquare);
                    break;
                case ']':
                    buf.append(rightSquare);
                    break;
                case '{':
                    buf.append(leftCurlyBracket);
                    break;
                case '}':
                    buf.append(rightCurlyBracket);
                    break;
                case '/':
                    buf.append(solidus);
                    break;
                case '#':
                    buf.append(numberSign);
                    break;
                default:
                    if (c >= 32 && c <= 126)
                        buf.append(c);
                    else {
                        buf.append('#');
                        if (c < 16)
                            buf.append('0');
                        buf.append(Integer.toString(c, 16));
                    }
                    break;
            }
        }
        content = buf.toByteArray();
    }

    @Override
    public String toString() {
        if (content != null)
            return "/" + new String(content);
        else
            return "/" + getValue();
    }

    @Override
    protected PdfName newInstance() {
        return new PdfName();
    }

    @Override
    protected void copyContent(PdfObject from, PdfDocument document) {
        super.copyContent(from, document);
        PdfName name = (PdfName) from;
        value = name.value;
    }
}
